/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.player.PlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngineCapability;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.audio.CueTrack;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.FileNameUtils;

/**
 * Engine for MPlayer
 */
public class MPlayerEngine extends PlayerEngine {

    /**
     * Command to be executed on Linux systems to launch mplayer. Mplayer should
     * be in $PATH
     */
    private static String LINUX_COMMAND = "mplayer";

    /**
     * Command to be executed on Windows systems to launch mplayer. Mplayer is
     * in "win_tools" dir, inside aTunes package
     */
    private static final String WIN_COMMAND = "win_tools/mplayer.exe";

    /** Command to be executed on Mac systems to launch mplayer. */
    private static final String MACOS_COMMAND = "mac_tools/mplayer";

    /**
     * Command to be executed on Solaris systems to launch mplayer. Note the
     * workaround with the options - Java6 on Solaris Express appears to require
     * these options added separately.
     */
    private static final String SOLARIS_COMMAND = "mplayer";

    /** The Constant SOLARISOPTAO. */
    private static final String SOLARISOPTAO = "-ao";
    /** The Constant SOLARISOPTTYPE. */
    private static final String SOLARISOPTTYPE = "sun"; // Arguments for mplayer
    /** Argument to not display more information than needed. */
    private static final String QUIET = "-quiet";
    /** Argument to control mplayer through commands. */
    private static final String SLAVE = "-slave";
    /** Argument to pass mplayer a play list. */
    private static final String PLAYLIST = "-playlist";
    /** Arguments to filter audio output. */
    private static final String AUDIO_FILTER = "-af";
    private static final String VOLUME_NORM = "volnorm";
    private static final String KARAOKE = "karaoke";
    private static final String EQUALIZER = "equalizer=";
    private static final String CACHE = "-cache";
    private static final String CACHE_SIZE = "500";
    private static final String CACHE_MIN = "-cache-min";
    private static final String CACHE_FILL_SIZE_IN_PERCENT = "7.0";
    private static final String PREFER_IPV4 = "-prefer-ipv4";

    private Process process;
    private MPlayerCommandWriter commandWriter;
    private MPlayerOutputReader mPlayerOutputReader;
    private MPlayerErrorReader mPlayerErrorReader;
    /** The current fade away process running */
    private FadeAwayRunnable currentFadeAwayRunnable = null;
    /** A cue track is playing (different computation required) */
    private boolean isCueTrack = false;
    /** The start position of the cue track */
    private int cueTrackStartPosition;
    /** The duration of the cue track */
    private long cueTrackDuration;
    /** The total time of the audio file (to which the cue sheet links) */
    private long audioFileDuration;

    public MPlayerEngine() {
        commandWriter = new MPlayerCommandWriter(null);
    }

    @Override
    public boolean isEngineAvailable() {
        if (SystemProperties.OS != OperatingSystem.WINDOWS) {
            InputStream in = null;
            try {
                Process p = new ProcessBuilder(LINUX_COMMAND).start();
                in = p.getInputStream();
                byte[] buffer = new byte[4096];
                while (in.read(buffer) >= 0) {
                    ;
                }

                int code = p.waitFor();
                if (code != 0) {
                    return false;
                }
            } catch (Exception e) {
                if (SystemProperties.OS == OperatingSystem.MACOSX && !LINUX_COMMAND.equals(MACOS_COMMAND)) {
                    logger.info(LogCategories.PLAYER, "Mac OS X: mplayer not found, trying in mac_tools");
                    LINUX_COMMAND = MACOS_COMMAND;
                    return isEngineAvailable();
                }
                return false;

            } finally {
                ClosingUtils.close(in);
            }
        }
        return true;
    }

    @Override
    protected void pausePlayback() {
        commandWriter.sendPauseCommand();
    }

    @Override
    protected void resumePlayback() {
        commandWriter.sendResumeCommand();
        /*
         * Mplayer volume problem workaround If player was paused, set volume
         * again as it could be changed when paused
         */
        commandWriter.sendVolumeCommand(ApplicationState.getInstance().getVolume());
        /*
         * End Mplayer volume problem workaround
         */
    }

    @Override
    protected void startPlayback(AudioObject audioObjectToPlay, AudioObject audioObject) {
        try {
            // If there is a fade away working, stop it inmediately
            if (currentFadeAwayRunnable != null) {
                currentFadeAwayRunnable.finish();
            }

            // Send stop command in order to try to avoid two mplayer
            // instaces are running at the same time
            commandWriter.sendStopCommand();

            // Start the play process
            process = getProcess(audioObjectToPlay);
            commandWriter = new MPlayerCommandWriter(process);
            // Output reader needs original audio object, specially when cacheFilesBeforePlaying is true, as
            // statistics must be applied over original audio object, not the cached one
            mPlayerOutputReader = MPlayerOutputReader.newInstance(this, process, audioObject);
            mPlayerOutputReader.start();
            mPlayerErrorReader = new MPlayerErrorReader(this, process, audioObjectToPlay);
            mPlayerErrorReader.start();
            commandWriter.sendGetDurationCommand();

            setVolume(ApplicationState.getInstance().getVolume());

        } catch (Exception e) {
            stopCurrentAudioObject(false);
            handlePlayerEngineError(e);
        }
    }

    @Override
    protected void stopPlayback(boolean userStopped, boolean useFadeAway) {
        if (!isEnginePlaying()) {
            return;
        }

        if (useFadeAway && !isPaused()) {
            // If there is a fade away process working don't create
            // a new process
            if (currentFadeAwayRunnable != null) {
                return;
            }
            mPlayerErrorReader.interrupt();
            currentFadeAwayRunnable = new FadeAwayRunnable(process, ApplicationState.getInstance().getVolume(), this);
            Thread t = new Thread(currentFadeAwayRunnable);
            // Start fade away process
            t.start();
        } else {
            commandWriter.sendStopCommand();
            // If there is a fade away process stop inmediately
            if (currentFadeAwayRunnable != null) {
                currentFadeAwayRunnable.finish();
            }
            process = null;
            mPlayerErrorReader = null;
            mPlayerOutputReader = null;
            commandWriter.setProcess(null);
            setCurrentAudioObjectPlayedTime(0);
        }
    }

    /**
     * Called when finished fade away
     */
    protected void finishedFadeAway() {
        // NOTE: interrupting output reader means closing standard input
        // of mplayer process, so process is finished
        mPlayerOutputReader.interrupt();
        process = null;
        mPlayerErrorReader = null;
        mPlayerOutputReader = null;
        commandWriter.setProcess(null);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setTime(0);
            }
        });
        // No fade away process working
        currentFadeAwayRunnable = null;
    }

    protected void setTime(int time) {
        super.setCurrentAudioObjectPlayedTime(time);
    }

    @Override
    protected void seekTo(double position) {
        double p = position;
        if (isCueTrack) {
            p = (cueTrackStartPosition + (cueTrackDuration * position)) / audioFileDuration;
        }
        commandWriter.sendSeekCommand(p);
    }

    @Override
    public void finishPlayer() {
        stopCurrentAudioObject(false);
        logger.info(LogCategories.PLAYER, "Stopping player");
    }

    @Override
    public boolean isEnginePlaying() {
        return process != null && !isPaused();
    }

    @Override
    public void applyMuteState(boolean mute) {
        logger.debug(LogCategories.PLAYER, Boolean.toString(mute));

        commandWriter.sendMuteCommand();

        // volume must be applied again because of the volume bug
        setVolume(ApplicationState.getInstance().getVolume());

        // MPlayer bug: paused, demute, muted -> starts playing
        if (isPaused() && !mute) {
            commandWriter.sendPauseCommand();
            logger.debug(LogCategories.PLAYER, "MPlayer bug (paused, demute, muted -> starts playing) workaround applied");
        }
    }

    @Override
    public void setVolume(int volume) {
        // MPlayer bug: paused, volume change -> starts playing
        // If is paused, volume will be sent to mplayer when user resumes playback
        if (!isPaused() && !isMuteEnabled()) {
            commandWriter.sendVolumeCommand(volume);
        }
    }

    /**
     * Returns a mplayer process to play an audiofile.
     * 
     * @param audioObject
     *            audio object which should be played
     * 
     * @return mplayer process
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private Process getProcess(AudioObject audioObject) throws IOException {

        ProcessBuilder pb = new ProcessBuilder();
        List<String> command = new ArrayList<String>();

        boolean isRemoteAudio = !(audioObject instanceof AudioFile || (audioObject instanceof PodcastFeedEntry
                && ApplicationState.getInstance().isUseDownloadedPodcastFeedEntries() && ((PodcastFeedEntry) audioObject).isDownloaded()));

        command.add(getProcessNameForOS());
        if (SystemProperties.OS == OperatingSystem.SOLARIS) {
            command.add(SOLARISOPTAO);
            command.add(SOLARISOPTTYPE);
        }
        command.add(QUIET);
        command.add(SLAVE);

        // PREFER_IPV4 for radios and podcast entries
        if (isRemoteAudio) {
            command.add(PREFER_IPV4);
        }

        // If a radio has a playlist url add playlist command
        if (audioObject instanceof Radio && ((Radio) audioObject).hasPlaylistUrl()) {
            command.add(PLAYLIST);
        }

        // url
        boolean shortPathName = ApplicationState.getInstance().isUseShortPathNames() && SystemProperties.OS == OperatingSystem.WINDOWS && audioObject instanceof AudioFile;
        String url;
        if (audioObject instanceof PodcastFeedEntry && !isRemoteAudio) {
            url = PodcastFeedHandler.getInstance().getDownloadPath((PodcastFeedEntry) audioObject);
            if (ApplicationState.getInstance().isUseShortPathNames() && SystemProperties.OS == OperatingSystem.WINDOWS) {
                shortPathName = true;
            }
        } else {
            url = audioObject.getUrl();
            if (!isRemoteAudio && AudioFile.isCueFile(((AudioFile) audioObject).getFile())) {
                isCueTrack = true;
                url = ((CueTrack) audioObject).getAudioFileName();
                cueTrackStartPosition = ((CueTrack) audioObject).getTrackStartPositionAsInt();
                cueTrackDuration = ((CueTrack) audioObject).getDuration();
                audioFileDuration = ((CueTrack) audioObject).getTotalDuration();
                command.add("-ss");
                command.add(String.valueOf(cueTrackStartPosition));
                if (((CueTrack) audioObject).getTrackEndPosition() != null) { //it is not the last track
                    command.add("-endpos");
                    command.add(((CueTrack) audioObject).getTrackEndPosition());
                }
            }
        }
        if (shortPathName) {
            String shortPath = FileNameUtils.getShortPathNameW(url);
            command.add(shortPath != null && !shortPath.isEmpty() ? shortPath : url);
        } else {
            if (url.startsWith("http")) {

                // proxy
                StringBuilder proxy = new StringBuilder();
                ProxyBean proxyBean = ApplicationState.getInstance().getProxy();
                if (proxyBean != null && proxyBean.getType().equals(ProxyBean.HTTP_PROXY)) {
                    //String user = proxyBean.getUser();
                    //String password = proxyBean.getPassword();
                    String proxyUrl = proxyBean.getUrl();
                    int port = proxyBean.getPort();

                    proxy.append("http_proxy://");
                    //proxy.append(!user.isEmpty() ? user : "");
                    //proxy.append(!user.isEmpty() && !password.isEmpty() ? ":" : "");
                    //proxy.append(!user.isEmpty() && !password.isEmpty() ? password : "");
                    //proxy.append(!user.isEmpty() ? "@" : "");
                    proxy.append(proxyUrl);
                    proxy.append(port != 0 ? ":" : "");
                    proxy.append(port != 0 ? port : "");
                    proxy.append("/");
                }
                proxy.append(url);

                command.add(proxy.toString());
            } else {
                command.add(url);
            }
        }

        // Cache for radios and podcast entries
        if (isRemoteAudio) {
            command.add(CACHE);
            command.add(CACHE_SIZE);
            command.add(CACHE_MIN);
            command.add(CACHE_FILL_SIZE_IN_PERCENT);
        }

        boolean isKaraokeEnabled = ApplicationState.getInstance().isKaraoke();
        //float[] eualizer = getEqualizer();
        if ((audioObject instanceof AudioFile && getEqualizer().getEqualizerValues() != null) || isSoundNormalizationEnabled() || isKaraokeEnabled) {
            command.add(AUDIO_FILTER);
        }

        // normalization
        if (isSoundNormalizationEnabled()) {
            command.add(VOLUME_NORM);
        }

        // Build equalizer command. Mplayer uses 10 bands
        if (audioObject instanceof AudioFile && getEqualizer().getEqualizerValues() != null && !isKaraokeEnabled) {
            float[] equalizer = getEqualizer().getEqualizerValues();
            command.add(EQUALIZER + equalizer[0] + ":" + equalizer[1] + ":" + equalizer[2] + ":" + equalizer[3] + ":" + equalizer[4] + ":" + equalizer[5] + ":" + equalizer[6]
                    + ":" + equalizer[7] + ":" + equalizer[8] + ":" + equalizer[9]);
        }

        // karaoke
        if (isKaraokeEnabled) {
            command.add(KARAOKE);
        }

        logger.debug(LogCategories.PLAYER, command.toArray(new String[command.size()]));
        return pb.command(command).start();
    }

    /**
     * Returns string command to call mplayer.
     * 
     * @return the process name for os
     */
    private String getProcessNameForOS() {
        if (SystemProperties.OS == OperatingSystem.WINDOWS) {
            return WIN_COMMAND;
        } else if (SystemProperties.OS == OperatingSystem.LINUX) {
            return LINUX_COMMAND;
        } else if (SystemProperties.OS == OperatingSystem.SOLARIS) {
            return SOLARIS_COMMAND;
        } else {
            return MACOS_COMMAND;
        }
    }

    @Override
    protected void initializePlayerEngine() {
        super.initializePlayerEngine();
        MPlayerPositionThread positionThread = new MPlayerPositionThread(this);
        positionThread.start();
    }

    /**
     * Gets the command writer.
     * 
     * @return the command writer
     */
    MPlayerCommandWriter getCommandWriter() {
        return commandWriter;
    }

    @Override
    public boolean supportsCapability(PlayerEngineCapability capability) {
        return EnumSet.of(PlayerEngineCapability.EQUALIZER, PlayerEngineCapability.EQUALIZER_CHANGE, PlayerEngineCapability.STREAMING, PlayerEngineCapability.PROXY,
                PlayerEngineCapability.KARAOKE, PlayerEngineCapability.NORMALIZATION).contains(capability);
    }

    @Override
    public void applyEqualization(float[] values) {
        // Mplayer does not support equalizer change
        // workaround:
        // we can stop/restart the current playing song to
        // its last position when users applied the EQ
        // test to avoid non desired startup of player
        if (isEnginePlaying()) {
            stopStartSeekToCurrentPosition();
        }
    }

    @Override
    public void applyNormalization() {
        // same comment as above, but for normalization mode
        if (isEnginePlaying()) {
            stopStartSeekToCurrentPosition();
        }
    }

    @Override
    public float[] transformEqualizerValues(float[] values) {
        return values;
    }

    protected void setCurrentLength(long currentDuration) {
        super.setCurrentAudioObjectLength(currentDuration);
    }

    /**
     * Checks if playback is paused.
     * 
     * @return true, if is paused
     */
    protected boolean isPlaybackPaused() {
        return isPaused();
    }

    protected void notifyRadioOrPodcastFeedEntry() {
        super.notifyRadioOrPodcastFeedEntryStarted();
    }

    protected boolean isMute() {
        return super.isMuteEnabled();
    }

    @Override
    protected String getEngineName() {
        return "MPlayer";
    }

    @Override
    protected void killPlayer() {
        commandWriter.sendStopCommand();
    }

    private void stopStartSeekToCurrentPosition() {
        float cent = ((float) getCurrentAudioObjectPlayedTime() / getCurrentAudioObjectLength());
        finishPlayer();
        playCurrentAudioObject(true);
        seekCurrentAudioObject(cent);
    }

}
