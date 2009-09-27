/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.views.controls.playerControls.KaraokeButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NextButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NormalizationButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.RepeatButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.ShuffleButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.StopButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeLevel;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeSlider;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class PlayerControlsPanel.
 */
public class PlayerControlsPanel extends JPanel {

    private static final long serialVersionUID = -8647737014195638177L;

    /************************************************ PANEL CONSTANTS ******************************************************/

    /** Size of play / pause button */
    public static final Dimension PLAY_BUTTON_SIZE = new Dimension(45, 45);

    /** Size of previous and next buttons */
    public static final Dimension PREVIOUS_NEXT_BUTTONS_SIZE = new Dimension(62, 30);

    /** Size of stop and mute buttons */
    public static final Dimension STOP_MUTE_BUTTONS_SIZE = new Dimension(30, 26);

    /** Size of shuffle, repeat, ... buttons */
    public static final Dimension OTHER_BUTTONS_SIZE = new Dimension(25, 23);

    /** Minimum size of this panel to show progress bar at bottom */
    private static final int PROGRESS_BAR_BOTTOM_MINIMUM_SIZE = 270;

    /** Width of progress bar when it's placed at bottom of panel */
    private static final int PROGRESS_BAR_WIDTH_AT_BOTTOM = 190;

    /** Height of progress bar when has no ticks */
    private static final int PROGRESS_BAR_NO_TICKS_HEIGHT = 26;

    /** Height of progress bar when has ticks */
    private static final int PROGRESS_BAR_TICKS_HEIGHT = 40;

    /** Total margin (top and bottom) of player controls */
    private static final int PLAYER_CONTROLS_VERTICAL_MARGIN = 10;

    /************************************************ PANEL CONSTANTS ******************************************************/

    protected ShuffleButton shuffleButton;
    protected RepeatButton repeatButton;
    protected KaraokeButton karaokeButton;
    protected NormalizationButton normalizeButton;
    protected PreviousButton previousButton;
    protected PlayPauseButton playButton;
    protected StopButton stopButton;
    protected NextButton nextButton;
    protected MuteButton volumeButton;
    protected VolumeSlider volumeSlider;
    protected VolumeLevel volumeLevel;
    boolean playing;
    ProgressSlider progressSlider;

    /**
     * Instantiates a new player controls panel.
     */
    public PlayerControlsPanel() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder());
        addContent();
        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Adds the content.
     */
    protected void addContent() {
        final JPanel topProgressSliderPanel = new JPanel(new BorderLayout());
        final JPanel bottomProgressSliderPanel = new JPanel(new BorderLayout());
        bottomProgressSliderPanel.addComponentListener(new ComponentAdapter() {

            Boolean showProgressOnTop = null;

            @Override
            public void componentResized(ComponentEvent e) {
                boolean showOnTop = bottomProgressSliderPanel.getWidth() < PROGRESS_BAR_BOTTOM_MINIMUM_SIZE;

                if (showProgressOnTop == null || showProgressOnTop != showOnTop) {
                    if (showOnTop) {
                        bottomProgressSliderPanel.remove(progressSlider);
                        topProgressSliderPanel.add(progressSlider, BorderLayout.CENTER);
                    } else {
                        topProgressSliderPanel.remove(progressSlider);
                        bottomProgressSliderPanel.add(progressSlider, BorderLayout.WEST);
                    }
                    progressSlider.setExpandable(showOnTop);
                    showProgressOnTop = showOnTop;
                }
            }
        });

        progressSlider = new ProgressSlider();

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(10, PLAY_BUTTON_SIZE.height + PLAYER_CONTROLS_VERTICAL_MARGIN));
        JPanel mainControlsPanel = getMainControlsPanel();
        bottomPanel.add(mainControlsPanel, BorderLayout.WEST);
        bottomPanel.add(bottomProgressSliderPanel, BorderLayout.CENTER);
        bottomPanel.add(getSecondaryControls(), BorderLayout.EAST);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.insets = new Insets(5, 5, 0, 5);
        c.fill = GridBagConstraints.BOTH;
        add(topProgressSliderPanel, c);
        c.gridy = 1;
        c.insets = new Insets(0, 5, 0, 5);
        add(bottomPanel, c);
    }

    public JSlider getProgressBar() {
        return progressSlider.getProgressBar();
    }

    public JLabel getRemainingTime() {
        return progressSlider.getRemainingTime();
    }

    public JLabel getTime() {
        return progressSlider.getTime();
    }

    public JToggleButton getVolumeButton() {
        return volumeButton;
    }

    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    public JLabel getVolumeLevel() {
        return volumeLevel;
    }

    public NormalizationButton getNormalizeButton() {
        return normalizeButton;
    }

    public boolean isPlaying() {
        return playing;
    }

    protected static void setButton(JPanel panel, JComponent b, GridBagConstraints c) {
        panel.add(b, c);
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        playButton.setPlaying(playing);
    }

    public void setShowTicksAndLabels(boolean showTicks) {
        getProgressBar().setPaintLabels(showTicks);
        getProgressBar().setPaintTicks(showTicks);
        getProgressBar().setPreferredSize(new Dimension(getProgressBar().getPreferredSize().width, showTicks ? PROGRESS_BAR_TICKS_HEIGHT : PROGRESS_BAR_NO_TICKS_HEIGHT));
    }

    private JPanel getMainControlsPanel() {
        previousButton = new PreviousButton(PREVIOUS_NEXT_BUTTONS_SIZE);
        playButton = new PlayPauseButton(PLAY_BUTTON_SIZE);
        stopButton = new StopButton(STOP_MUTE_BUTTONS_SIZE);
        nextButton = new NextButton(PREVIOUS_NEXT_BUTTONS_SIZE);
        volumeButton = new MuteButton(STOP_MUTE_BUTTONS_SIZE);
        volumeButton.setText("");
        volumeSlider = new VolumeSlider();
        volumeLevel = new VolumeLevel();
        return getPanelWithPlayerControls(stopButton, previousButton, playButton, nextButton, volumeButton, volumeSlider, volumeLevel);
    }

    /**
     * Return a panel with all player buttons. This method is shared with full
     * screen window
     * 
     * @param stopButton
     * @param previousButton
     * @param playButton
     * @param nextButton
     * @param volumeButton
     * @param volumeSlider
     * @param volumeLevel
     * @return
     */
    public static JPanel getPanelWithPlayerControls(StopButton stopButton, PreviousButton previousButton, PlayPauseButton playButton, NextButton nextButton, MuteButton volumeButton, JSlider volumeSlider, JLabel volumeLevel) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 0, 0, 0);
        setButton(panel, stopButton, c);
        c.gridx = 1;
        c.insets = new Insets(0, -6, 0, 0);
        setButton(panel, previousButton, c);
        c.gridx = 2;
        c.insets = new Insets(-1, -16, 0, 0);
        setButton(panel, playButton, c);
        c.gridx = 3;
        c.insets = new Insets(0, -16, 0, 0);
        setButton(panel, nextButton, c);
        if (volumeButton != null && volumeSlider != null && volumeLevel != null) {
            c.gridx = 4;
            c.insets = new Insets(0, -7, 0, 0);
            panel.add(volumeButton, c);
            c.gridx = 5;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.insets = new Insets(0, 0, 3, 0);
            panel.add(volumeSlider, c);
            c.gridy = 1;
            c.gridx = 5;
            c.fill = GridBagConstraints.NONE;
            c.insets = new Insets(-20, 16, 0, 0);
            c.anchor = GridBagConstraints.WEST;
            panel.add(volumeLevel, c);
        }
        return panel;
    }

    private JPanel getSecondaryControls() {
        shuffleButton = new ShuffleButton();
        repeatButton = new RepeatButton();
        karaokeButton = new KaraokeButton();
        normalizeButton = new NormalizationButton();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 0);
        panel.add(shuffleButton, c);
        c.gridx = 1;
        c.insets = new Insets(0, 1, 0, 0);
        panel.add(repeatButton, c);
        c.gridx = 2;
        panel.add(karaokeButton, c);
        c.gridx = 3;
        panel.add(normalizeButton, c);

        return panel;
    }

    static class ProgressSlider extends JPanel {

        private static final long serialVersionUID = 8921834666233975274L;

        protected JLabel time;
        protected JLabel remainingTime;
        protected JSlider progressBar;

        ProgressSlider() {
            super(new GridBagLayout());
            time = new JLabel("0:00");
            time.setHorizontalAlignment(SwingConstants.CENTER);

            progressBar = new JSlider();
            progressBar.setToolTipText(I18nUtils.getString("CLICK_TO_SEEK"));
            progressBar.setMinimum(0);
            progressBar.setValue(0);
            progressBar.setFocusable(false);
            progressBar.setPreferredSize(new Dimension(PROGRESS_BAR_WIDTH_AT_BOTTOM, PROGRESS_BAR_NO_TICKS_HEIGHT));

            remainingTime = new JLabel("0:00");
            remainingTime.setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(false);

            setExpandable(true);
        }

        public JLabel getTime() {
            return time;
        }

        public JLabel getRemainingTime() {
            return remainingTime;
        }

        public JSlider getProgressBar() {
            return progressBar;
        }

        public void setExpandable(boolean expandable) {
            removeAll();

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.insets = new Insets(1, 10, 0, 0);
            add(time, c);
            c.gridx = 1;
            c.weightx = expandable ? 1 : 0;
            c.weighty = 1;
            c.insets = new Insets(1, 5, 0, 5);
            c.fill = GridBagConstraints.HORIZONTAL;
            add(progressBar, c);
            c.gridx = 2;
            c.weightx = 0;
            c.insets = new Insets(1, 0, 0, 5);
            c.fill = GridBagConstraints.NONE;
            add(remainingTime, c);

        }
    }
}
