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

package net.sourceforge.atunes.kernel.modules.player;

import java.awt.EventQueue;
import java.awt.Font;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

final class PlayerControlsController extends AbstractSimpleController<PlayerControlsPanel> {

    private static final int SECONDS_10 = 10000;
    private static final int SECONDS_30 = 30000;
    private static final int MINUTES_1 = 60000;
    private static final int MINUTES_2 = 120000;
    private static final int MINUTES_5 = 300000;
    private static final int MINUTES_10 = 600000;
    private static final int MINUTES_30 = 1800000;

    /**
     * Instantiates a new player controls controller.
     * 
     * @param panel
     * @param state
     */
    PlayerControlsController(PlayerControlsPanel panel, IState state) {
        super(panel, state);
        addBindings();
        addStateBindings();
    }

    @Override
	public void addBindings() {
        ProgressBarSeekListener seekListener = new ProgressBarSeekListener(getComponentControlled().getProgressSlider());        
        getComponentControlled().getProgressSlider().addMouseListener(seekListener);
    }

    /**
     * Sets the max duration.
     * 
     * @param length
     *            the new length
     */
    void setAudioObjectLength(long length) {
        Logger.debug(Long.toString(length));

        getComponentControlled().getProgressSlider().setMaximum((int) length);
        setupProgressTicks(length);
    }

    /**
     * Sets the playing.
     * 
     * @param playing
     *            the new playing
     */
    void setPlaying(boolean playing) {
        getComponentControlled().setPlaying(playing);
    }

    /**
     * Sets the slidable.
     * 
     * @param slidable
     *            the new slidable
     */
    void setSlidable(boolean slidable) {
        getComponentControlled().getProgressSlider().setEnabled(slidable);
    }

    /**
     * Sets the time.
     * 
     * @param timePlayed
     *            the time played
     * @param totalTime
     *            the total time
     */
    void setCurrentAudioObjectTimePlayed(final long timePlayed, final long totalTime) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setCurrentAudioObjectTimePlayedEDT(timePlayed, totalTime);
                }
            });
        } else {
            setCurrentAudioObjectTimePlayedEDT(timePlayed, totalTime);
        }
    }

    private void setCurrentAudioObjectTimePlayedEDT(long timePlayed, long totalTime) {
        long remainingTime = totalTime - timePlayed;
        getComponentControlled().setProgress(timePlayed, timePlayed == 0 ? timePlayed : remainingTime);
        getComponentControlled().getProgressSlider().setValue((int) timePlayed);
    }

    /**
     * Sets the volume.
     * 
     * @param value
     *            the new volume
     */
    void setVolume(int value) {
        getComponentControlled().getVolumeSlider().setValue(value);
        getComponentControlled().getVolumeButton().updateIcon(getState());
    }

    /**
     * Gets the position in percent.
     */
    float getPostionInPercent() {
        int max = getComponentControlled().getProgressSlider().getMaximum();
        int pos = getComponentControlled().getProgressSlider().getValue();

        float floatPercent = 0;

        if (max > 0 && pos >= 0) {
            int intPercent = pos * 100 / max;
            floatPercent = intPercent / 100f;
        }
        return floatPercent;
    }

    /**
     * Setup ticks spacing
     * 
     * @param length
     */
    private void setupProgressTicks(long length) {

        int minorTickSpacing = SECONDS_10;
        int majorTickSpacing = SECONDS_30;

        if (length > MINUTES_10 && length <= MINUTES_30) {
            minorTickSpacing = SECONDS_30;
            majorTickSpacing = MINUTES_1;

        } else if (length > MINUTES_30) {
            minorTickSpacing = MINUTES_1;
            majorTickSpacing = MINUTES_5;
        }

        //avoid NullPointerException 
        getComponentControlled().getProgressSlider().setLabelTable(null);

        getComponentControlled().getProgressSlider().setPaintTicks(getState().isShowTicks());
        getComponentControlled().getProgressSlider().setMajorTickSpacing(majorTickSpacing);
        getComponentControlled().getProgressSlider().setMinorTickSpacing(minorTickSpacing);

        setupTicksLabels(length);

    }

    /**
     * Setup ticks labels
     * 
     * @param length
     */
    private void setupTicksLabels(long length) {
        Hashtable<Integer, JLabel> ticksLabels = new Hashtable<Integer, JLabel>();

        for (int k = 0; k < length; k++) {

            if (length < MINUTES_1) {
                if (k % SECONDS_10 == 0 && k != 0) {
                    ticksLabels.put(k, getLabelForDuration(k));
                }

            } else if (length > MINUTES_1 && length <= MINUTES_10) {
                if (k % MINUTES_1 == 0 && k != 0) {
                    ticksLabels.put(k, getLabelForDuration(k));
                }

            } else if (length > MINUTES_10 && length <= MINUTES_30) {
                if (k % MINUTES_2 == 0 && k != 0) {
                    ticksLabels.put(k, getLabelForDuration(k));
                }

            } else {
                if (k % MINUTES_10 == 0 && k != 0) {
                    ticksLabels.put(k, getLabelForDuration(k));
                }
            }
        }
        getComponentControlled().getProgressSlider().setPaintLabels(getState().isShowTicks() && ticksLabels.size() > 0);
        if (ticksLabels.size() > 0) {
        	getComponentControlled().getProgressSlider().setLabelTable(ticksLabels);
        }

    }

    /**
     * Get label for duration
     * 
     * @param duration
     * @return the label for duration
     */
    private JLabel getLabelForDuration(int unit) {
        String duration = StringUtils.milliseconds2String(unit);
        JLabel label = new JLabel(duration, SwingConstants.CENTER);
        Font currentFont = label.getFont();
        label.setFont(new Font(currentFont.getFontName(), currentFont.getStyle(), Math.max(currentFont.getSize() - 3, 7)));
        return label;
    }    
    
    /**
     * Updates controls when playing given audio object
     * @param audioObject
     */
    void updatePlayerControls(IAudioObject audioObject) {
        // Disable slider if audio object is a radio or podcast feed entry
        boolean b = audioObject.isSeekable();
        if (b && audioObject instanceof IPodcastFeedEntry) {
            b = getState().isUseDownloadedPodcastFeedEntries();
        }
        setSlidable(b);
    }
}
