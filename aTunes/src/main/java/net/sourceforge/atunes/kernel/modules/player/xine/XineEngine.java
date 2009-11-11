/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.player.xine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngineCapability;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;

import org.libxinejna.Xine;
import org.libxinejna.XineController;
import org.libxinejna.XineEventListener;

/**
 * Engine for Xine player
 * 
 * @author <a href="mailto:Helbrass@gmail.com">Aekold Helbrass</a>
 */
public class XineEngine extends PlayerEngine {

    XineController xineController;
    private Timer durationUpdater;
    final Object xineLock = new Object();

    @Override
    protected boolean isEngineAvailable() {
        if (SystemProperties.OS == OperatingSystem.WINDOWS) {
            return false;
        } else {
            try {
                String v = Xine.getLibXineVersion();
                info("Engine version: " + v);
                if (v != null && v.trim().length() > 0) {
                    return true;
                }
            } catch (Throwable th) {
                // catching of Throwable is bad practice, but there are too many possible exceptions
                // with only result - Xine is not available
            }
            return false;
        }
    }

    @Override
    protected void pausePlayback() {
        if (xineController != null) {
            xineController.pause();
        }
    }

    @Override
    protected void resumePlayback() {
        if (xineController != null) {
            xineController.resume();
        }
    }

    @Override
    protected void startPlayback(final AudioObject audioObjectToPlay, final AudioObject audioObject) {

        boolean valid = true;

        if (xineController == null) {
            info("Creating xineController for playback");
            xineController = Xine.getXine().createController();
            xineController.registerXineEventListener(new XineListener());
        } else {
            info("Stopping before new playback");
            internalStop();
        }
        info("Opening stream and setting xine params");

        xineController.open(audioObjectToPlay.getUrl());

        xineController.setVolume(ApplicationState.getInstance().getVolume());

        // Apply equalizer
        applyEqualization(getEqualizer().getEqualizerValues());

        int streamLength = xineController.getStreamLength();
        if (audioObjectToPlay instanceof PodcastFeedEntry || audioObjectToPlay instanceof Radio) {
            if (!xineController.hasAudio()) {
                info("No audio found, go to next track");
                valid = false;
            } else {
                // logic from init() of PodcastFeedEntryMPlayerOutputReader
                long duration = audioObjectToPlay.getDuration() * 1000;
                setCurrentAudioObjectLength(duration);
                notifyRadioOrPodcastFeedEntryStarted();
            }
        } else {
            // Check if stream length is 0 which may indicate a wrong audio object, and then skip
            if (streamLength <= 0) {
                valid = false;
            }
            setCurrentAudioObjectLength(streamLength);
        }

        if (valid) {
            startPlayback(0);
            info("Starting duration thread");
            durationUpdater = new Timer(250, new ActionListener() {
                int prevPosition;

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isEnginePlaying()) {
                        int s = -1;
                        synchronized (xineLock) {
                            s = xineController.getStreamPos();
                        }
                        // TODO perform better checks. May be >= is better here, but there is some bug with seek
                        if (s >= 0) {
                            prevPosition = s;
                            setTime(prevPosition);
                            //if ((audioObjectToPlay instanceof PodcastFeedEntry || audioObjectToPlay instanceof Radio) && s < 1000) {
                            if (s < 1000) {
                                GuiHandler.getInstance().updateStatusBar(audioObjectToPlay);
                                GuiHandler.getInstance().updateTitleBar(audioObjectToPlay);
                            }
                        }
                    }
                }
            });
            durationUpdater.start();
        } else {
            currentAudioObjectFinished();
        }
    }

    protected void setTime(int time) {
        super.setCurrentAudioObjectPlayedTime(time);
    }

    @Override
    protected void stopPlayback(final boolean userStopped, boolean useFadeAway) {
        if (xineController == null) {
            return;
        }
        stopDurationUpdater();
        if (useFadeAway) {
            info("Fading away...");
            new Thread("FadeAway") {

                @Override
                public void run() {
                    // xineController.getVolume() always returns 0 ??
                    // Using volume value from app instead of xine
                    int vol = ApplicationState.getInstance().getVolume();
                    int i = 0;
                    while (i < 50 && vol > 0) {
                        vol = vol - 2;
                        vol = vol < 0 ? 0 : vol;
                        xineController.setVolume(vol);
                        try {
                            Thread.sleep(25);
                        } catch (Exception e) {
                            // Nothing to do
                        }
                        i++;
                    }

                    internalStop();
                    // Volume restored after stop
                    // If we don't do this, volume is 0 in next audio object
                    xineController.setVolume(ApplicationState.getInstance().getVolume());
                }

            }.start();
        } else {
            info("Stopping without fade");
            internalStop();
        }
    }

    @Override
    protected void seekTo(double position) {
        if (xineController != null) {
            startPlayback(percentToXine(position));
        }
    }

    @Override
    public void finishPlayer() {
        info("Finishing");
        try {
            if (xineController != null) {
                synchronized (xineLock) {
                    stopDurationUpdater();
                    xineController.dispose();
                    xineController = null;
                }
            }
            Xine.getXine().dispose();
        } catch (UnsatisfiedLinkError le) {
            info("Error catched: " + le);
        } catch (Exception e) {
            info("Error catched: " + e);
        }
    }

    @Override
    public boolean isEnginePlaying() {
        if (xineController != null) {
            synchronized (xineLock) {
                return xineController.isPlaying();
            }
        }
        return false;
    }

    @Override
    public void applyMuteState(boolean mute) {
        if (mute) {
            setVolume(0);
        } else {
            setVolume(ApplicationState.getInstance().getVolume());
        }
    }

    @Override
    public void setVolume(int volume) {
        if (xineController != null) {
            xineController.setVolume(volume);
        }
    }

    //	@Override
    //	public boolean isPaused() {
    //		if (xineController != null) {
    //			return xineController.isPaused();
    //		}
    //		return false;
    //	}

    protected void internalStop() {
        info("Internal stop");
        if (xineController != null) {
            stopDurationUpdater();
            if (xineController.isPlaying() || xineController.isPaused()) {
                synchronized (xineLock) {
                    xineController.stop();
                }
            }
        }
    }

    private int percentToXine(double percent) {
        if (percent == 0) {
            return 0;
        }
        return (int) (65535 * percent);
    }

    private void startPlayback(final int startPosition) {
        info("Starting playback");
        try {
            xineController.start(startPosition, 0);
        } catch (Exception e) {
            info("Xine encountered an error: " + e);
            currentAudioObjectFinished();
        }
    }

    private void stopDurationUpdater() {
        if (durationUpdater != null) {
            durationUpdater.stop();
            durationUpdater = null;
        }
    }

    protected void info(String info) {
        logger.info(LogCategories.PLAYER, "Xine: " + info);
    }

    protected void error(Exception o) {
        logger.error(LogCategories.PLAYER, o);
    }

    protected class XineListener implements XineEventListener {

        public void handleXineEvent(int eventID, byte[] data) {
            if (eventID == 1) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            currentAudioObjectFinished();
                        }
                    });
                } catch (InterruptedException e) {
                    error(e);
                } catch (InvocationTargetException e) {
                    error(e);
                }
            }
        }
    }

    @Override
    public boolean supportsCapability(PlayerEngineCapability capability) {
        return EnumSet.of(PlayerEngineCapability.EQUALIZER, PlayerEngineCapability.EQUALIZER_CHANGE, PlayerEngineCapability.STREAMING).contains(capability);
    }

    @Override
    public void applyEqualization(float[] values) {
        if (xineController != null && values != null) {
            values = transformEqualizerValues(values);
            for (int i = 0, p = 18; i < values.length; i++, p++) {
                xineController.setParam(p, (int) values[i]);
            }
        }
    }

    @Override
    public float[] transformEqualizerValues(float[] eq) {
        if (eq == null) {
            return null;
        }
        float[] result = new float[eq.length];
        float one = 100f / 12f;
        for (int i = 0; i < eq.length; i++) {
            if (eq[i] == 0) {
                result[i] = 0;
            } else {
                result[i] = (int) (one * eq[i]);
            }
        }
        return result;
    }

    @Override
    protected String getEngineName() {
        return "Xine";
    }

    @Override
    protected void killPlayer() {
        if (xineController != null) {
            synchronized (xineLock) {
                stopDurationUpdater();
                xineController.dispose();
                xineController = null;
            }
        }
        Xine.getXine().dispose();
    }

    @Override
    public void applyNormalization() {
        // TODO normalization must be applied here
    }
}
