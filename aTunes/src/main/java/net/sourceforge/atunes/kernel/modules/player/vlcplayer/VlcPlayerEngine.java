/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.player.vlcplayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.PropertyResourceBundle;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngineCapability;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;

public class VlcPlayerEngine extends PlayerEngine {

    /**
     * The engine name
     */
    private static final String ENGINE_NAME = "VLC";
    /**
     * Command to be executed on Linux systems to launch vlc.
     */
    private static String LINUX_COMMAND = "vlc";
    /**
     * Command to be executed on Windows systems to launch vlc. VLC is in
     * "win_tools/vlc" dir, inside aTunes package
     */
    private static final String WIN_COMMAND = Constants.WINDOWS_TOOLS_DIR + "/vlc/vlc.exe";
    /**
     * Command to be executed on Mac systems to launch vlc. It requires a copy
     * of VLC.app in the mac_tools dir
     **/
    private static String MACOS_COMMAND = Constants.MAC_TOOLS_DIR + "/VLC.app/Contents/MacOS/VLC";
    /**
     * Command to be executed on Solaris systems to launch vlc TO TEST !!
     * */
    private static final String SOLARIS_COMMAND = "vlc";
    /** Arguments to control vlc through commands. */
    private static final String INTERFACE = "-I";
    private static final String RC = "rc";
    protected static final String REMOTE_HOST = "127.0.0.1";
    protected static int REMOTE_PORT = 8888;
    private static final String RC_HOST = "--rc-host=" + REMOTE_HOST + ":" + String.valueOf(REMOTE_PORT);
    /**
     * Argument to not display more information than needed. Depends on OS
     */
    private static final String WINDOWS_ARG_QUIET = "--rc-quiet";
    private static final String MACOS_ARG_QUIET = "--rc-fake-tty";
    private static final String LINUX_ARG_QUIET = "--rc-fake-tty";
    private static final String SOLARIS_ARG_QUIET = "--rc-fake-tty";
    /** Argument to setup the volume. */
    private static final String VOLUME = "--volume=";
    /** Arguments to filter audio output. */
    private static final String AUDIO_FILTER = "--audio-filter=";
    private static final String VOLUME_NORM = "normvol";
    //the default value in vlc is 2.0 (really strong) 
    private static final String VOLUME_NORM_LEVEL = "--norm-max-level=7.0";
    private static final String EQUALIZER = "equalizer";
    //the default value in vlc is 12 
    private static final String EQUALIZER_PREAMP = "--equalizer-preamp=12";
    private static final String EQUALIZER_BANDS = "--equalizer-bands=";
    private static final String HTTP_PROXY = "--http-proxy=";
    private static final String SOCKS_PROXY = "--socks=";
    private static final String HTTP_CACHE = "--http-caching=500";
    /** Argument to make vlc verbose. */
    private static final String VERBOSE = "-vvv";

    private Process process;
    private VlcPlayerCommandWriter commandWriter;
    private VlcOutputReader vlcOutputReader;
    /** The actual duration of media **/
    private long duration = 0;

    /**
     * static initialization
     */
    static {
        try {
            PropertyResourceBundle vlcConfig = new PropertyResourceBundle(VlcPlayerEngine.class.getResourceAsStream(Constants.VLC_CONFIG_FILE));
            REMOTE_PORT = Integer.valueOf(vlcConfig.getString("vlc.telnet.client.port")).intValue();
        } catch (IOException ioe) {
            logger.internalError(ioe);
        }
    }

    /**
     * Constructor
     */
    public VlcPlayerEngine() {
        commandWriter = new VlcPlayerCommandWriter(null, null, null);
    }

    @Override
    public boolean isEngineAvailable() {
        if (SystemProperties.OS != OperatingSystem.WINDOWS) {
            InputStream in = null;
            try {
                //launching a process without catching an exception is enough to accept it 
                Process p = new ProcessBuilder(LINUX_COMMAND).start();
                in = p.getInputStream();
                p.destroy();

            } catch (Exception e) {
                if (SystemProperties.OS == OperatingSystem.MACOSX) {

                    if (!LINUX_COMMAND.equals(MACOS_COMMAND)) {
                        //logger.info(LogCategories.PLAYER, "Mac OS X: vlc player not found, giving a try with linux installation dir");
                        LINUX_COMMAND = MACOS_COMMAND;
                        return isEngineAvailable();
                    }
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

        //little pause required between this 2 commands 
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            // nothing special to do here
        }

        if (isMuteEnabled()) {
            commandWriter.sendMuteCommand();
        } else {
            commandWriter.sendVolumeCommand(ApplicationState.getInstance().getVolume());
        }
    }

    @Override
    protected void startPlayback(AudioObject audioObjectToPlay, AudioObject audioObject) {
        try {

            // Start the play process	
            process = getProcess(audioObjectToPlay);
            vlcOutputReader = new VlcOutputReader(this, process);
            vlcOutputReader.start();

            //starts the command writer
            //creates also a telnet client to pass and read command.
            commandWriter = new VlcPlayerCommandWriter(this, process, audioObject);
            commandWriter.start();

        } catch (Exception e) {
            stopCurrentAudioObject(false);
            handlePlayerEngineError(e);
        }
    }

    @Override
    protected void stopPlayback(boolean userStopped, boolean useFadeAway) {

        if (commandWriter != null) {
            commandWriter.sendStopCommand();
        }

        //terminate the vlc process
        while (true) {
            try {
                if (process.exitValue() == 0) {
                    break;
                }
            } catch (Exception e) {
                if (process != null) {
                    process.destroy();
                }
                break;
            }
        }

        vlcOutputReader = null;
        commandWriter = null;
        process = null;
        setCurrentAudioObjectPlayedTime(0);
    }

    protected void setTime(int time) {
        super.setCurrentAudioObjectPlayedTime(time);
    }

    @Override
    protected void seekTo(double position) {
        //VLC needs a position in seconds
        if (duration > 0 && duration < Integer.MAX_VALUE) {
            position = position * duration;
            commandWriter.sendSeekCommand(new Double(position / 1000).intValue());
        }
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
        if (mute) {
            commandWriter.sendMuteCommand();
        } else {
            commandWriter.sendVolumeCommand(ApplicationState.getInstance().getVolume());
        }
    }

    @Override
    public void setVolume(int volume) {
        if (!isMuteEnabled() && commandWriter != null) {
            commandWriter.sendVolumeCommand(volume);
        }
    }

    @Override
    public boolean supportsCapability(PlayerEngineCapability capability) {
        return EnumSet.of(PlayerEngineCapability.EQUALIZER, PlayerEngineCapability.EQUALIZER_CHANGE, PlayerEngineCapability.NORMALIZATION).contains(capability);
    }

    @Override
    public void applyEqualization(float[] values) {
        if (isEnginePlaying()) {
            stopStartSeekToCurrentPosition();
        }
    }

    @Override
    public void applyNormalization() {
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
        this.duration = currentDuration;
    }

    /**
     * Checks if playback is paused.
     * 
     * @return true, if is paused
     */
    protected boolean isPlaybackPaused() {
        return super.isPaused();
    }

    @Override
    protected String getEngineName() {
        return ENGINE_NAME;
    }

    @Override
    protected void killPlayer() {
        if (commandWriter != null) {
            commandWriter.sendStopCommand();
        }
    }

    private void stopStartSeekToCurrentPosition() {
        //need a pause between the start and sending the seek command
        //avoid the freeze of the gui.
        new Thread(new Runnable() {
            @Override
            public void run() {
                float cent = ControllerProxy.getInstance().getPlayerControlsController().getPostionInPercent();
                finishPlayer();
                //mute till we have reach the seek position
                boolean isMute = ApplicationState.getInstance().isMuteEnabled();
                ApplicationState.getInstance().setMuteEnabled(true);
                playCurrentAudioObject(true);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    //nothing special to do
                    //e.printStackTrace();
                }
                seekCurrentAudioObject(cent);
                if (!isMute) {
                    ApplicationState.getInstance().setMuteEnabled(false);
                    applyMuteState(false);
                }
            }
        }).start();
    }

    protected VlcPlayerCommandWriter getCommandWriter() {
        return commandWriter;
    }

    protected VlcOutputReader getVlcOutputReader() {
        return vlcOutputReader;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    /**
     * Returns a vlc process to play an audiofile.
     * 
     * @param audioObject
     *            audio object which should be played
     * 
     * @return vlc process
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected Process getProcess(AudioObject audioObject) throws IOException {

        ProcessBuilder pb = new ProcessBuilder();

        List<String> command = new ArrayList<String>();

        command.add(getProcessNameForOS());
        command.add(VERBOSE);
        command.add(INTERFACE);
        command.add(RC);
        command.add(RC_HOST);
        command.add(getQuietModeArg());

        int volume = 0;
        if (!isMuteEnabled()) {
            volume = ApplicationState.getInstance().getVolume() * VlcPlayerCommandWriter.VOLUME_FACTOR;
        }
        command.add(VOLUME + volume);

        //setup the audio filters
        List<String> audioFilters = new ArrayList<String>();
        //equalizer
        String equalizerBands = "";
        if ((getEqualizer().getEqualizerValues() != null && supportsCapability(PlayerEngineCapability.EQUALIZER))) {
            audioFilters.add(EQUALIZER);
            float[] equalizer = getEqualizer().getEqualizerValues();
            equalizerBands = EQUALIZER_BANDS + buildEqualizerBands(equalizer);
        }

        // normalization
        if (isSoundNormalizationEnabled() && supportsCapability(PlayerEngineCapability.NORMALIZATION)) {
            audioFilters.add(VOLUME_NORM);
        }

        //pass the commands
        if (audioFilters.size() > 0) {
            command.add(AUDIO_FILTER + buildAudioFiltersParameter(audioFilters));
        }
        if (audioFilters.contains(EQUALIZER)) {
            command.add(equalizerBands);
            command.add(EQUALIZER_PREAMP);
        }
        if (audioFilters.contains(VOLUME_NORM)) {
            command.add(VOLUME_NORM_LEVEL);
        }

        //get the dowloaded podcast if it was
        String url = audioObject.getUrl();
        if (audioObject instanceof PodcastFeedEntry && ((PodcastFeedEntry) audioObject).isDownloaded()) {
            url = PodcastFeedHandler.getInstance().getDownloadPath((PodcastFeedEntry) audioObject);
        }

        //proxy setup
        if (url.startsWith("http")) {
            String proxyParam = buildProxyParam(url);
            if (!proxyParam.equals("")) {
                command.add(proxyParam);
            }
            command.add(HTTP_CACHE);
        }

        command.add(url);

        logger.info(LogCategories.PLAYER, "VLC process args : " + command);

        return pb.command(command).start();

    }

    //---------- HELPERS ----------------

    private String buildProxyParam(String url) {

        StringBuilder proxy = new StringBuilder();
        ProxyBean proxyBean = ApplicationState.getInstance().getProxy();
        if (proxyBean != null && proxyBean.getType().equals(ProxyBean.HTTP_PROXY)) {
            //String user = proxyBean.getUser();
            //String password = proxyBean.getPassword();
            String proxyUrl = proxyBean.getUrl();
            int port = proxyBean.getPort();

            //proxy.append("--http-proxy=");
            //proxy.append(!user.isEmpty() ? user : "");
            //proxy.append(!user.isEmpty() && !password.isEmpty() ? ":" : "");
            //proxy.append(!user.isEmpty() && !password.isEmpty() ? password : "");
            //proxy.append(!user.isEmpty() ? "@" : "");
            if (!proxyUrl.startsWith("http://")) {
                proxy.append("http://");
            }
            proxy.append(proxyUrl);
            proxy.append(port != 0 ? ":" + port : ":80");
            return HTTP_PROXY + proxy;

        } else if (proxyBean != null && proxyBean.getType().equals(ProxyBean.SOCKS_PROXY)) {
            String proxyUrl = proxyBean.getUrl();
            int port = proxyBean.getPort();

            proxy.append(proxyUrl);
            proxy.append(port != 0 ? ":" + port : ":80");
            return SOCKS_PROXY + proxy;

        }
        return "";
    }

    private String buildEqualizerBands(float[] equalizer) {    	
        StringBuilder bands = new StringBuilder();
        for (int i = 0; i < equalizer.length; i++) {
            bands.append(String.valueOf(new Float(equalizer[i]).intValue()));
            if (i != equalizer.length - 1) {
                bands.append(" ");
            }
        }
        return bands.toString();
    }

    private String buildAudioFiltersParameter(List<String> audioFilters) {
        StringBuilder audioFiltersParam = new StringBuilder();
        for (int i = 0; i < audioFilters.size(); i++) {
            audioFiltersParam.append(audioFilters.get(i));
            if (i != audioFilters.size() - 1) {
                audioFiltersParam.append(":");
            }
        }
        return audioFiltersParam.toString();
    }

    /**
     * Returns string command to call vlc.
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

    /**
     * Returns string command to call vlc in quiet mode. Apparently depends on
     * the os
     * 
     * @return the process name for os
     */
    private String getQuietModeArg() {
        if (SystemProperties.OS == OperatingSystem.WINDOWS) {
            return WINDOWS_ARG_QUIET;
        } else if (SystemProperties.OS == OperatingSystem.LINUX) {
            return LINUX_ARG_QUIET;
        } else if (SystemProperties.OS == OperatingSystem.SOLARIS) {
            return SOLARIS_ARG_QUIET;
        } else {
            return MACOS_ARG_QUIET;
        }
    }

}
