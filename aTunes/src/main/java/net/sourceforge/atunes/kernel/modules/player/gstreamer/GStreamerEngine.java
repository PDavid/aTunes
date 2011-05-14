/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.player.gstreamer;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngineCapability;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;

import org.gstreamer.Bus;
import org.gstreamer.ElementFactory;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.elements.PlayBin;

//TODO:  radio?, podcast?, proxy?, normalization?, karaoke?
public class GStreamerEngine extends AbstractPlayerEngine {

    private PlayBin playBin;
    private Runnable remainingTimeRunnable;
    private ScheduledFuture<?> scheduledFuture;

    public GStreamerEngine() {
        try {
            Gst.init("AudioPlayer", new String[] {});
            playBin = new PlayBin("AudioPlayer");
            playBin.setVideoSink(ElementFactory.make("fakesink", "videosink"));
        } catch (Throwable e) {
            getLogger().error(LogCategories.PLAYER, "GStreamer is not supported");
        }

        remainingTimeRunnable = new Runnable() {

            @Override
            public void run() {
                final long playedTime = playBin.queryPosition().toMillis();
                final long length = playBin.queryDuration().toMillis();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        GStreamerEngine.this.setCurrentAudioObjectPlayedTime(playedTime);
                        if (length > 0) {
                            GStreamerEngine.this.setCurrentAudioObjectLength(length);
                        }
                    }
                });
            }
        };

    }

    @Override
    protected void applyEqualization(float[] values) {
    }

    @Override
    public void applyMuteState(boolean mute) {
        if (mute) {
            playBin.setVolumePercent(0);
        } else {
            playBin.setVolumePercent(ApplicationState.getInstance().getVolume());
        }
    }

    @Override
    public void applyNormalization() {
    }

    @Override
    protected void finishPlayer() {
        playBin.stop();
    }

    @Override
    protected String getEngineName() {
        return "GStreamer";
    }

    @Override
    protected boolean isEngineAvailable() {
        try {
            Gst.init();
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isEnginePlaying() {
        return playBin.isPlaying();
    }

    @Override
    protected void killPlayer() {
        playBin.dispose();
    }

    @Override
    protected void pausePlayback() {
        playBin.pause();
    }

    @Override
    protected void resumePlayback() {
        playBin.play();
    }

    @Override
    protected void seekTo(long milliseconds) {
        playBin.seek(milliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public void setVolume(int perCent) {
        if (!isMuteEnabled()) {
            playBin.setVolumePercent(perCent);
        }
    }

    @Override
    protected void startPlayback(AudioObject audioObjectToPlay, AudioObject audioObject) {

        if (audioObjectToPlay instanceof AudioFile) {
            playBin.setInputFile(new File(audioObjectToPlay.getUrl()));
        } else {
            try {
                playBin.setURI(new URI(audioObjectToPlay.getUrl()));
            } catch (URISyntaxException e) {
                getLogger().error(LogCategories.PLAYER, e);
            }
        }
        playBin.getBus().connect(new Bus.EOS() {
            @Override
            public void endOfStream(GstObject arg0) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        currentAudioObjectFinished(true);
                    }
                });
            }
        });
        playBin.play();
        ScheduledExecutorService scheduledExecutorService = Gst.getScheduledExecutorService();
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(remainingTimeRunnable, 0, 100, TimeUnit.MILLISECONDS);

        GuiHandler.getInstance().updateStatusBar(audioObjectToPlay);
        GuiHandler.getInstance().updateTitleBar(audioObjectToPlay);
    }

    @Override
    protected void stopPlayback(boolean userStopped, boolean useFadeAway) {
        stopTimeRunnable();
        playBin.stop();
    }

    private void stopTimeRunnable() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }

    @Override
    public boolean supportsCapability(PlayerEngineCapability capability) {
        return false;
    }

    @Override
    protected float[] transformEqualizerValues(float[] values) {
        return values;
    }

}
