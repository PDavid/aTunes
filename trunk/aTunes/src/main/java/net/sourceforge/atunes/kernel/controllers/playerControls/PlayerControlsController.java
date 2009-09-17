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

package net.sourceforge.atunes.kernel.controllers.playerControls;

import java.awt.Font;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class PlayerControlsController.
 */
public class PlayerControlsController extends PanelController<PlayerControlsPanel> {

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
     *            the panel
     */
    public PlayerControlsController(PlayerControlsPanel panel) {
        super(panel);
        addBindings();
        addStateBindings();
    }

    @Override
    protected void addBindings() {
        PlayerControlsListener listener = new PlayerControlsListener(getPanelControlled(), this);

        getPanelControlled().getProgressBar().addMouseListener(listener);
        //bind a mouse wheel listener
        getPanelControlled().getVolumeSlider().addMouseWheelListener(listener);
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Sets the max duration.
     * 
     * @param length
     *            the new length
     */
    public void setAudioObjectLength(long length) {
        getLogger().debug(LogCategories.CONTROLLER, Long.toString(length));

        getPanelControlled().getProgressBar().setMaximum((int) length);
        setupProgressTicks(length);
    }

    /**
     * Sets the playing.
     * 
     * @param playing
     *            the new playing
     */
    public void setPlaying(boolean playing) {
        getPanelControlled().setPlaying(playing);
    }

    /**
     * Sets the slidable.
     * 
     * @param slidable
     *            the new slidable
     */
    public void setSlidable(boolean slidable) {
        getPanelControlled().getProgressBar().setEnabled(slidable);
    }

    /**
     * Sets the time.
     * 
     * @param timePlayed
     *            the time played
     * @param totalTime
     *            the total time
     */
    public void setCurrentAudioObjectTimePlayed(long timePlayed, long totalTime) {
        long remainingTime = totalTime - timePlayed;
        if (timePlayed == 0) {
            getPanelControlled().getRemainingTime().setText(StringUtils.milliseconds2String(timePlayed));
        } else {
            getPanelControlled().getRemainingTime().setText(remainingTime > 0 ? StringUtils.getString("- ", StringUtils.milliseconds2String(remainingTime)) : "-");
        }

        getPanelControlled().getTime().setText(StringUtils.milliseconds2String(timePlayed));
        getPanelControlled().getProgressBar().setValue((int) timePlayed);
    }

    /**
     * Sets the volume.
     * 
     * @param value
     *            the new volume
     */
    public void setVolume(int value) {
        getPanelControlled().getVolumeSlider().setValue(value);
        getPanelControlled().getVolumeLevel().setText(StringUtils.getString(String.valueOf(value), " %"));
    }

    /**
     * Gets the position in percent.
     */
    public float getPostionInPercent() {
        JSlider progressBar = getPanelControlled().getProgressBar();
        int max = progressBar.getMaximum();
        int pos = progressBar.getValue();

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

        JSlider progressBar = getPanelControlled().getProgressBar();

        //avoid NullPointerException 
        progressBar.setLabelTable(null);

        progressBar.setPaintTicks(ApplicationState.getInstance().isShowTicks());
        progressBar.setMajorTickSpacing(majorTickSpacing);
        progressBar.setMinorTickSpacing(minorTickSpacing);

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
        JSlider progressBar = getPanelControlled().getProgressBar();
        progressBar.setPaintLabels(ApplicationState.getInstance().isShowTicks() && ticksLabels.size() > 0);
        if (ticksLabels.size() > 0) {
            progressBar.setLabelTable(ticksLabels);
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
        label.setFont(new Font(currentFont.getFontName(), Font.ITALIC, Math.max(currentFont.getSize() - 2, 8)));
        return label;
    }
}
