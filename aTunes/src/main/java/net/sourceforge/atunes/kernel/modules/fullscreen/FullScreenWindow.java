/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomWindow;
import net.sourceforge.atunes.gui.views.controls.FullScreenNextButton;
import net.sourceforge.atunes.gui.views.controls.FullScreenPlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.FullScreenPreviousButton;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Window to show a flow of covers
 * 
 * @author alex
 * 
 */
public final class FullScreenWindow extends AbstractCustomWindow {

	private static final long serialVersionUID = 3422799994808333945L;

	private CoverFlow covers;

	/** The text label. */
	private JLabel textLabel;

	/** The text label 2 */
	private JLabel textLabel2;

	/** The options. */
	private JPopupMenu options;

	private FullScreenPreviousButton previousButton;

	/** The play button. */
	private FullScreenPlayPauseButton playButton;

	private JPanel controlsPanel;

	/** The playing. */
	private boolean playing;

	private Timer hideMouseTimer;

	/**
	 * Audio Objects to show in full screen
	 */
	private List<IAudioObject> objects;

	private final IFrame frame;

	private IOSManager osManager;

	private ILookAndFeelManager lookAndFeelManager;

	private Dimension screenSize;

	private FullScreenNextButton nextButton;

	private JMenuItem selectBackground;

	private FullScreenBackgroundPanel backgroundPanel;

	private JMenuItem removeBackground;

	private JMenuItem exitFullScreen;

	private IBeanFactory beanFactory;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param screenSize
	 */
	public void setScreenSize(final Dimension screenSize) {
		this.screenSize = screenSize;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Instantiates a new full screen dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public FullScreenWindow(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame.getFrame(), 0, 0, controlsBuilder);
		this.frame = frame;
	}

	/**
	 * Initializes window
	 */
	public void initializeDialog() {
		setLocation(0, 0);
		setAlwaysOnTop(true);
		setContent();
	}

	/**
	 * Activates timer to hide mouse cursor
	 */
	void activateTimer() {
		setCursor(Cursor.getDefaultCursor());
		this.controlsPanel.setVisible(true);
		if (this.hideMouseTimer != null) {
			this.hideMouseTimer.restart();
		} else {
			this.hideMouseTimer = new Timer(5000, new HideMouseActionListener(
					this, this.controlsPanel, this.options));
		}
	}

	/**
	 * Checks if is playing.
	 * 
	 * @return true, if is playing
	 */
	public boolean isPlaying() {
		return this.playing;
	}

	/**
	 * Sets the audio objects
	 * 
	 * @param objects
	 */
	public void setAudioObjects(final List<IAudioObject> objects) {
		if (objects == null || objects.isEmpty()) {
			this.textLabel.setText("");
			this.textLabel2.setText("");
			return;
		}

		this.objects = objects;

		if (isVisible()) {
			updateWindow();
		}
	}

	private void setText(final IAudioObject audioObject) {
		// No object
		if (audioObject == null) {
			this.textLabel.setText("");
			this.textLabel2.setText("");
		} else if (audioObject instanceof IRadio) {
			this.textLabel.setText(((IRadio) audioObject).getName());
			this.textLabel2.setText(((IRadio) audioObject).getUrl());
		} else if (audioObject instanceof IPodcastFeedEntry) {
			this.textLabel
					.setText(((IPodcastFeedEntry) audioObject).getTitle());
			this.textLabel2.setText(((IPodcastFeedEntry) audioObject)
					.getPodcastFeed().getName());
		} else {
			this.textLabel.setText(audioObject.getTitleOrFileName());
			this.textLabel2.setText(audioObject
					.getArtist(this.unknownObjectChecker));
		}
	}

	/**
	 * Sets the background.
	 * 
	 * @param file
	 *            the new background
	 */
	void setBackground(final File file) {
		if (file == null) {
			this.backgroundPanel.setBackgroundImage(null);
		} else {
			try {
				this.backgroundPanel.setBackgroundImage(ImageIO.read(file));
			} catch (IOException e) {
				Logger.error(e);
			}
		}
	}

	/**
	 * Sets the content.
	 */
	private void setContent() {
		this.backgroundPanel = new FullScreenBackgroundPanel();
		add(this.backgroundPanel);

		setOptions();

		this.textLabel = getTextLabel();
		this.textLabel2 = getTextLabel2();

		JPanel textAndControlsPanel = getTextAndControlsPanel();

		textAndControlsPanel.setAlignmentX(0.0f);
		textAndControlsPanel.setAlignmentY(1f);

		this.covers.setAlignmentX(0.0f);
		this.covers.setAlignmentY(0.8f);

		this.backgroundPanel.add(textAndControlsPanel);
		this.backgroundPanel.add(this.covers);
	}

	/**
	 * @param covers
	 */
	public void setCovers(final CoverFlow covers) {
		this.covers = covers;
	}

	/**
	 * 
	 */
	private void setOptions() {
		this.options = new JPopupMenu(I18nUtils.getString("OPTIONS"));
		this.selectBackground = new JMenuItem(
				I18nUtils.getString("SELECT_BACKGROUND"));
		this.removeBackground = new JMenuItem(
				I18nUtils.getString("REMOVE_BACKGROUND"));
		this.exitFullScreen = new JMenuItem(I18nUtils.getString("CLOSE"));
		this.options.add(this.selectBackground);
		this.options.add(this.removeBackground);
		this.options.add(this.exitFullScreen);
	}

	/**
	 * @param lookAndFeelManager
	 * @return
	 */
	private JPanel getTextAndControlsPanel() {
		JPanel textAndControlsPanel = new JPanel(new GridLayout(2, 1));
		textAndControlsPanel.setOpaque(false);

		JPanel textPanel = new JPanel(new GridBagLayout());
		textPanel.setOpaque(false);

		JPanel buttonsPanel = new JPanel(new FlowLayout());
		buttonsPanel.setOpaque(false);

		this.previousButton = new FullScreenPreviousButton(getPlayerIconSize(),
				getPlayerIconResized(Images
						.getImage(Images.PREVIOUS_FULL_SCREEN)),
				this.beanFactory.getBean("previousAction", Action.class));
		this.playButton = new FullScreenPlayPauseButton(
				getMainPlayerIconSize(),
				getMainPlayerIconResized(Images
						.getImage(Images.PLAY_FULL_SCREEN)),
				getMainPlayerIconResized(Images
						.getImage(Images.PAUSE_FULL_SCREEN)),
				this.beanFactory.getBean("playAction", Action.class));
		this.nextButton = new FullScreenNextButton(getPlayerIconSize(),
				getPlayerIconResized(Images.getImage(Images.NEXT_FULL_SCREEN)),
				this.beanFactory.getBean("nextAction", Action.class));

		buttonsPanel.add(this.previousButton);
		buttonsPanel.add(this.playButton);
		buttonsPanel.add(this.nextButton);

		setPanels(textAndControlsPanel, textPanel, buttonsPanel);
		textAndControlsPanel.add(this.controlsPanel);
		textAndControlsPanel.setMaximumSize(new Dimension(
				this.screenSize.width, this.screenSize.height / 3));
		return textAndControlsPanel;
	}

	private ImageIcon getMainPlayerIconResized(final ImageIcon icon) {
		int iconSize = this.screenSize.height / 12;
		return ImageUtils
				.scaleImageBicubic(icon.getImage(), iconSize, iconSize);
	}

	private ImageIcon getPlayerIconResized(final ImageIcon icon) {
		int iconSize = this.screenSize.height / 16;
		return ImageUtils
				.scaleImageBicubic(icon.getImage(), iconSize, iconSize);
	}

	private Dimension getMainPlayerIconSize() {
		return new Dimension(this.screenSize.height / 12,
				this.screenSize.height / 12);
	}

	private Dimension getPlayerIconSize() {
		return new Dimension(this.screenSize.height / 16,
				this.screenSize.height / 16);
	}

	/**
	 * @return
	 */
	public FullScreenBackgroundPanel getBackgroundPanel() {
		return this.backgroundPanel;
	}

	/**
	 * @return
	 */
	public FullScreenPreviousButton getPreviousButton() {
		return this.previousButton;
	}

	/**
	 * @return
	 */
	public FullScreenPlayPauseButton getPlayButton() {
		return this.playButton;
	}

	/**
	 * @return
	 */
	public FullScreenNextButton getNextButton() {
		return this.nextButton;
	}

	/**
	 * @param textAndControlsPanel
	 * @param textPanel
	 * @param buttonsPanel
	 */
	private void setPanels(final JPanel textAndControlsPanel,
			final JPanel textPanel, final JPanel buttonsPanel) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 20, 0);
		c.anchor = GridBagConstraints.NORTH;
		textPanel.add(this.textLabel, c);
		c.gridy = 1;
		textPanel.add(this.textLabel2, c);

		textAndControlsPanel.add(textPanel);

		this.controlsPanel = new JPanel(new GridBagLayout());
		this.controlsPanel.setOpaque(false);

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridwidth = 3;
		c.insets = new Insets(20, 0, 5, 0);
		this.controlsPanel.add(buttonsPanel, c);
	}

	private JLabel getTextLabel2() {
		JLabel label = new JLabel();
		label.setFont(this.lookAndFeelManager.getCurrentLookAndFeel()
				.getFullScreenLine2Font());
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	private JLabel getTextLabel() {
		JLabel label = new JLabel();
		label.setFont(this.lookAndFeelManager.getCurrentLookAndFeel()
				.getFullScreenLine1Font());
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	/**
	 * @return
	 */
	public JMenuItem getExitFullScreen() {
		return this.exitFullScreen;
	}

	/**
	 * Sets the full screen.
	 * 
	 * @param fullscreen
	 * @param frame
	 */
	private void setFullScreen(final boolean fullscreen, final IFrame frame) {
		this.osManager.setFullScreen(this, fullscreen, frame);
	}

	/**
	 * Sets the playing.
	 * 
	 * @param playing
	 *            the new playing
	 */
	public void setPlaying(final boolean playing) {
		this.playing = playing;
		this.playButton.setPlaying(playing);
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) {
			updateWindow();
			activateTimer();
		}
		setFullScreen(visible, this.frame);
	}

	/**
	 * Updates the window with the current objects
	 */
	private void updateWindow() {
		setText(this.objects.get(2));
		this.covers.paint(this.objects);
	}

	/**
	 * @return the options
	 */
	public JPopupMenu getOptions() {
		return this.options;
	}

	/**
	 * @return the covers
	 */
	public CoverFlow getCovers() {
		return this.covers;
	}

	/**
	 * @return the selectBackground
	 */
	public JMenuItem getSelectBackground() {
		return this.selectBackground;
	}

	/**
	 * @return the removeBackground
	 */
	public JMenuItem getRemoveBackground() {
		return this.removeBackground;
	}

}