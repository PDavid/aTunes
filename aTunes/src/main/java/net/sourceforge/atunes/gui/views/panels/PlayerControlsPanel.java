/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.playerControls.EqualizerButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NextButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NormalizationButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.ProgressSlider;
import net.sourceforge.atunes.gui.views.controls.playerControls.RepeatButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.SecondaryControl;
import net.sourceforge.atunes.gui.views.controls.playerControls.ShuffleButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.StopButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeSlider;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * The player controls panel.
 */
/**
 * @author alex
 *
 */
public final class PlayerControlsPanel extends JPanel {

    private static final long serialVersionUID = -8647737014195638177L;

    /************************************************ PANEL CONSTANTS ******************************************************/

    /**
     * Size of main controls by standard layout (not Substance)
     */
    public static final Dimension PLAY_PREVIOUS_NEXT_BUTTONS_SIZE = new Dimension(40, 40);
    
    /**
     * Size of main controls by standard layout (not Substance)
     */
    public static final Dimension DEFAULT_BUTTONS_SIZE = new Dimension(34, 34);

    /** Size of play / pause button */
    public static final Dimension PLAY_BUTTON_SIZE = Context.getBean(ILookAndFeelManager.class).getCurrentLookAndFeel().isCustomPlayerControlsSupported() ? new Dimension(45, 45)
            : PLAY_PREVIOUS_NEXT_BUTTONS_SIZE;

    /** Size of previous and next buttons */
    public static final Dimension PREVIOUS_NEXT_BUTTONS_SIZE = Context.getBean(ILookAndFeelManager.class).getCurrentLookAndFeel().isCustomPlayerControlsSupported() ? new Dimension(62, 30)
            : PLAY_PREVIOUS_NEXT_BUTTONS_SIZE;

    /** Size of stop and mute buttons */
    public static final Dimension STOP_MUTE_BUTTONS_SIZE = Context.getBean(ILookAndFeelManager.class).getCurrentLookAndFeel().isCustomPlayerControlsSupported() ? new Dimension(30, 26)
            : DEFAULT_BUTTONS_SIZE;

    /** Size of shuffle, repeat, ... buttons */
    public static final Dimension OTHER_BUTTONS_SIZE = new Dimension(30, 30);

    /** Height of progress bar when has no ticks */
    private static final int PROGRESS_BAR_NO_TICKS_HEIGHT = 26;

    /** Height of progress bar when has ticks */
    private static final int PROGRESS_BAR_TICKS_HEIGHT = 40;
    
	/**
	 * Minimum width of progress bar to be shown at bottom
	 */
	private static final int PROGRESS_BAR_BOTTOM_MINIMUM_SIZE = 300;

    /************************************************ PANEL CONSTANTS ******************************************************/

    private ShuffleButton shuffleButton;
    private RepeatButton repeatButton;
    private EqualizerButton karaokeButton;
    private NormalizationButton normalizeButton;
    private PreviousButton previousButton;
    private PlayPauseButton playButton;
    private StopButton stopButton;
    private NextButton nextButton;
    private MuteButton volumeButton;
    private VolumeSlider volumeSlider;
    private boolean playing;
    private ProgressSlider progressSlider;
    private JPanel secondaryControls;
    private FilterPanel filterPanel;

    private IState state;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    /**
     * Instantiates a new player controls panel.
     * @param state
     * @param lookAndFeelManager
     */
    public PlayerControlsPanel(IState state, ILookAndFeelManager lookAndFeelManager) {
        super(new GridBagLayout());
        this.state = state;
        this.lookAndFeelManager = lookAndFeelManager;
        addContent();
        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Adds the content.
     */
    private void addContent() {
    	JPanel topProgressSliderContainer = new JPanel(new BorderLayout());
    	JPanel bottomProgressSliderContainer = new JPanel(new BorderLayout());
    	bottomProgressSliderContainer.addComponentListener(new BottomProgressSliderPanelComponentAdapter(bottomProgressSliderContainer, topProgressSliderContainer));
    	
        progressSlider = new ProgressSlider();
        JPanel mainControls = getMainControlsPanel();
        JPanel secondaryControls = getSecondaryControls();
        filterPanel = new FilterPanel(state, lookAndFeelManager);
        
        GridBagConstraints c = new GridBagConstraints();
        
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 5;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 40, 0, 40);
        add(topProgressSliderContainer, c);
                
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0.5;
        c.insets = new Insets(0, 10, 5, 0);
        c.fill = GridBagConstraints.BOTH;
        add(mainControls, c);
        
        c.gridx = 2;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        add(secondaryControls, c);
        
        c.gridx = 3;
        c.weightx = 1;
        c.insets = new Insets(0, 20, 0, 20);
        c.fill = GridBagConstraints.BOTH;
        add(bottomProgressSliderContainer, c);
        
        c.gridx = 4;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 0, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        add(filterPanel, c);
    }

    public ProgressSlider getProgressSlider() {
        return progressSlider;
    }

    public void setProgress(long time, long remainingTime) {
        progressSlider.setProgress(time, remainingTime);
    }

    public MuteButton getVolumeButton() {
        return volumeButton;
    }

    public JSlider getVolumeSlider() {
        return volumeSlider;
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
        getProgressSlider().setPaintLabels(showTicks);
        getProgressSlider().setPaintTicks(showTicks);
        getProgressSlider().setPreferredSize(new Dimension(getProgressSlider().getPreferredSize().width, showTicks ? PROGRESS_BAR_TICKS_HEIGHT : PROGRESS_BAR_NO_TICKS_HEIGHT));
    }

    private JPanel getMainControlsPanel() {
        previousButton = new PreviousButton(PREVIOUS_NEXT_BUTTONS_SIZE, lookAndFeelManager);
        playButton = new PlayPauseButton(PLAY_BUTTON_SIZE, lookAndFeelManager);
        stopButton = new StopButton(STOP_MUTE_BUTTONS_SIZE, lookAndFeelManager);
        nextButton = new NextButton(PREVIOUS_NEXT_BUTTONS_SIZE, lookAndFeelManager);
        volumeButton = new MuteButton(STOP_MUTE_BUTTONS_SIZE, state, lookAndFeelManager);
        volumeButton.setText("");
        volumeSlider = new VolumeSlider(state);
        JPanel panel = getPanelWithPlayerControls(stopButton, previousButton, playButton, nextButton, volumeButton, volumeSlider, lookAndFeelManager);
        // add a small border to separate from other components
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        return panel;
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
     * @param lookAndFeelManager
     * @return
     */
    public static JPanel getPanelWithPlayerControls(StopButton stopButton, PreviousButton previousButton, PlayPauseButton playButton, NextButton nextButton, MuteButton volumeButton, JSlider volumeSlider, ILookAndFeelManager lookAndFeelManager) {
        return lookAndFeelManager.getCurrentLookAndFeel().isCustomPlayerControlsSupported() ? 
        		getCustomPlayerControls(stopButton, previousButton, playButton, nextButton, volumeButton, volumeSlider) : 
        		getStandardPlayerControls(stopButton, previousButton, playButton, nextButton, volumeButton, volumeSlider);
    }

    /**
     * Returns custom panel with controls (used by Substance LAF)
     * 
     * @param stopButton
     * @param previousButton
     * @param playButton
     * @param nextButton
     * @param volumeButton
     * @param volumeSlider
     * @return
     */
    private static JPanel getCustomPlayerControls(StopButton stopButton, PreviousButton previousButton, PlayPauseButton playButton, NextButton nextButton, MuteButton volumeButton, JSlider volumeSlider) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
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
        if (volumeButton != null && volumeSlider != null) {
            c.gridx = 4;
            c.insets = new Insets(0, -7, 0, 0);
            panel.add(volumeButton, c);
            c.gridx = 5;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.insets = new Insets(0, -1, 3, 0);
            panel.add(volumeSlider, c);
        }
        return panel;
    }

    /**
     * Returns standard panel with controls
     * 
     * @param stopButton
     * @param previousButton
     * @param playButton
     * @param nextButton
     * @param volumeButton
     * @param volumeSlider
     * @return
     */
    private static JPanel getStandardPlayerControls(StopButton stopButton, PreviousButton previousButton, PlayPauseButton playButton, NextButton nextButton, MuteButton volumeButton, JSlider volumeSlider) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        setButton(panel, stopButton, c);
        c.gridx = 1;
        setButton(panel, previousButton, c);
        c.gridx = 2;
        setButton(panel, playButton, c);
        c.gridx = 3;
        setButton(panel, nextButton, c);
        if (volumeButton != null && volumeSlider != null) {
            c.gridx = 4;
            panel.add(volumeButton, c);
            c.gridx = 5;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            c.insets = new Insets(0, 5, 0, 0);
            panel.add(volumeSlider, c);
        }
        return panel;
    }

    private JPanel getSecondaryControls() {
        if (secondaryControls == null) {
            shuffleButton = new ShuffleButton(lookAndFeelManager);
            repeatButton = new RepeatButton(lookAndFeelManager);
            karaokeButton = new EqualizerButton(lookAndFeelManager);
            normalizeButton = new NormalizationButton(lookAndFeelManager);

            secondaryControls = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.insets = new Insets(0, 5, 0, 0);
            secondaryControls.add(shuffleButton, c);
            c.gridx = 1;
            c.insets = new Insets(0, 1, 0, 0);
            secondaryControls.add(repeatButton, c);
            c.gridx = 2;
            secondaryControls.add(karaokeButton, c);
            c.gridx = 3;
            secondaryControls.add(normalizeButton, c);
        }
        return secondaryControls;
    }

    /**
     * Adds a secondary control
     * 
     * @param button
     */
    public void addSecondaryControl(Action action) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = getSecondaryControls().getComponentCount();
        c.gridy = 0;
        c.insets = new Insets(0, 1, 0, 0);
        JToggleButton button = new SecondaryControl(action, lookAndFeelManager);
        getSecondaryControls().add(button, c);
        getSecondaryControls().repaint();
    }
    
    /**
     * Hides or shows advanced controls
     * @param show
     */
    public void showAdvancedPlayerControls(boolean show) {
    	karaokeButton.setVisible(show);
    	normalizeButton.setVisible(show);
    }

	/**
	 * @return the filterPanel
	 */
	public FilterPanel getFilterPanel() {
		return filterPanel;
	}
	
	private final class BottomProgressSliderPanelComponentAdapter extends ComponentAdapter {
		
		private final JPanel bottomProgressSliderPanel;
		private final JPanel topProgressSliderPanel;
		private Boolean showProgressOnTop = null;

		private BottomProgressSliderPanelComponentAdapter(JPanel bottomProgressSliderPanel, JPanel topProgressSliderPanel) {
			this.bottomProgressSliderPanel = bottomProgressSliderPanel;
			this.topProgressSliderPanel = topProgressSliderPanel;
		}

		@Override
		public void componentResized(ComponentEvent e) {
			boolean showOnTop = bottomProgressSliderPanel.getWidth() < PROGRESS_BAR_BOTTOM_MINIMUM_SIZE;

			if (showProgressOnTop == null || showProgressOnTop != showOnTop) {
				if (showOnTop) {
					bottomProgressSliderPanel.remove(progressSlider);
					progressSlider.setLayout();
					topProgressSliderPanel.add(progressSlider, BorderLayout.CENTER);
				} else {
					topProgressSliderPanel.remove(progressSlider);
					progressSlider.setLayout();
					bottomProgressSliderPanel.add(progressSlider, BorderLayout.CENTER);
				}
				showProgressOnTop = showOnTop;
			}
		}
	}

}