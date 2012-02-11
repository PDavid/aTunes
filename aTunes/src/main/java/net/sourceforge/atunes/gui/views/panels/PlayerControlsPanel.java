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

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.playerControls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NextButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.SecondaryControl;
import net.sourceforge.atunes.gui.views.controls.playerControls.SecondaryToggleControl;
import net.sourceforge.atunes.gui.views.controls.playerControls.StopButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeSlider;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayerControlsPanel;
import net.sourceforge.atunes.model.IProgressSlider;
import net.sourceforge.atunes.model.IState;

/**
 * The player controls panel.
 */
/**
 * @author alex
 *
 */
public final class PlayerControlsPanel extends JPanel implements IPlayerControlsPanel {

    private static final long serialVersionUID = -8647737014195638177L;

    private SecondaryControl equalizerButton;
    private SecondaryToggleControl normalizeButton;
    private PlayPauseButton playButton;
    private MuteButton volumeButton;
    private VolumeSlider volumeSlider;
    private IProgressSlider progressSlider;
    private JPanel secondaryControls;

    private IState state;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    /**
     * Instantiates a new player controls panel.
     */
    public PlayerControlsPanel() {
        super(new GridBagLayout());
    }
    
    /**
     * @param state
     */
    public void setState(IState state) {
		this.state = state;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * Adds the content.
     */
    public void initialize() {
    	JPanel topProgressSliderContainer = new JPanel(new BorderLayout());
    	JPanel bottomProgressSliderContainer = new JPanel(new BorderLayout());
    	progressSlider = Context.getBean(IProgressSlider.class);
    	bottomProgressSliderContainer.addComponentListener(new PlayerControlsPanelBottomProgressSliderPanelComponentAdapter(progressSlider, bottomProgressSliderContainer, topProgressSliderContainer));

    	JPanel mainControls = getMainControlsPanel();
        JPanel secondaryControls = getSecondaryControls();
        secondaryControls.setMinimumSize(mainControls.getPreferredSize());
        secondaryControls.setPreferredSize(mainControls.getPreferredSize());
        secondaryControls.setMaximumSize(mainControls.getPreferredSize());
        mainControls.setMaximumSize(mainControls.getPreferredSize());
        
        
//        mainControls.setBorder(BorderFactory.createLineBorder(Color.red));
//        secondaryControls.setBorder(BorderFactory.createLineBorder(Color.red));
//        bottomProgressSliderContainer.setBorder(BorderFactory.createLineBorder(Color.red));
        
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
        c.weightx = 1;
        c.insets = new Insets(5, 70, 8, 70);
        c.fill = GridBagConstraints.BOTH;
        add(bottomProgressSliderContainer, c);

        c.gridx = 3;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 0, 0, 10);
        add(secondaryControls, c);
        
                
        GuiUtils.applyComponentOrientation(this);
    }

    @Override
	public IProgressSlider getProgressSlider() {
        return progressSlider;
    }

    @Override
	public void setProgress(long time, long remainingTime) {
        progressSlider.setProgress(time, remainingTime);
    }
    
    /**
     * Updates volume controls with the volume level
     * @param volume
     */
    @Override
	public void setVolume(int volume) {
        volumeSlider.setValue(volume);
        volumeButton.updateIcon(state);
    }

    @Override
	public void setPlaying(boolean playing) {
        playButton.setPlaying(playing);
    }

    private JPanel getMainControlsPanel() {
        PreviousButton previousButton = new PreviousButton(PlayerControlsSize.PREVIOUS_NEXT_BUTTONS_SIZE, lookAndFeelManager);
        playButton = new PlayPauseButton(PlayerControlsSize.PLAY_BUTTON_SIZE, lookAndFeelManager);
        StopButton stopButton = new StopButton(PlayerControlsSize.STOP_MUTE_BUTTONS_SIZE, lookAndFeelManager);
        NextButton nextButton = new NextButton(PlayerControlsSize.PREVIOUS_NEXT_BUTTONS_SIZE, lookAndFeelManager);
        volumeButton = Context.getBean("volumeButton", MuteButton.class);
        volumeButton.setText("");
        volumeSlider = Context.getBean("volumeSlider", VolumeSlider.class);
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
        if (lookAndFeelManager.getCurrentLookAndFeel().isCustomPlayerControlsSupported()) { 
        	return new CustomPlayerControlsBuilder().getCustomPlayerControls(stopButton, previousButton, playButton, nextButton, volumeButton, volumeSlider);
        } else {
        	return new StandardPlayerControlsBuilder().getStandardPlayerControls(stopButton, previousButton, playButton, nextButton, volumeButton, volumeSlider);
        }
    }

    private JPanel getSecondaryControls() {
        if (secondaryControls == null) {
            equalizerButton = (SecondaryControl)Context.getBean("equalizerButton");
            normalizeButton = (SecondaryToggleControl)Context.getBean("normalizeButton");

            secondaryControls = Context.getBean(SecondaryPlayerControlsBuilder.class).getSecondaryControls();
        }
        return secondaryControls;
    }

    /**
     * Adds a secondary control
     * 
     * @param button
     */
    @Override
	public void addSecondaryControl(Action action) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = getSecondaryControls().getComponentCount();
        c.gridy = 0;
        c.insets = new Insets(0, 1, 0, 0);
        JButton button = new SecondaryControl(action);
        button.setPreferredSize((Dimension)Context.getBean("secondaryControlSize"));
        getSecondaryControls().add(button, c);
        getSecondaryControls().repaint();
    }

    /**
     * Adds a secondary toggle control
     * 
     * @param button
     */
    @Override
	public void addSecondaryToggleControl(Action action) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = getSecondaryControls().getComponentCount();
        c.gridy = 0;
        c.insets = new Insets(0, 1, 0, 0);
        JToggleButton button = new SecondaryToggleControl(action);
        button.setPreferredSize((Dimension)Context.getBean("secondaryControlSize"));
        getSecondaryControls().add(button, c);
        getSecondaryControls().repaint();
    }

    /**
     * Hides or shows advanced controls
     * @param show
     */
    @Override
	public void showAdvancedPlayerControls(boolean show) {
    	equalizerButton.setVisible(show);
    	normalizeButton.setVisible(show);
    }

	@Override
	public JPanel getSwingComponent() {
		return this;
	}
}