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
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomWindow;
import net.sourceforge.atunes.gui.views.controls.playerControls.FullScreenNextButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.FullScreenPlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.FullScreenPreviousButton;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;

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
    
    private IFrame frame;
    
    private IOSManager osManager;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private Dimension screenSize;

	private FullScreenNextButton nextButton;

	private JMenuItem selectBackground;
	
	private FullScreenBackgroundPanel backgroundPanel;

	private JMenuItem removeBackground;

	private JMenuItem exitFullScreen;
    
    /**
     * @param screenSize
     */
    public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
	}
    
    /**
     * @param osManager
     */
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * Instantiates a new full screen dialog.
     * @param frame
     */
    public FullScreenWindow(IFrame frame) {
        super(frame.getFrame(), 0, 0);
        this.frame = frame;
    }
    
    /**
     * Initializes window
     */
    public void initialize() {
        setLocation(0, 0);
        setAlwaysOnTop(true);
        setContent();
    }

    /**
     * Activates timer to hide mouse cursor
     */
    void activateTimer() {
        setCursor(Cursor.getDefaultCursor());
        controlsPanel.setVisible(true);
        if (hideMouseTimer != null) {
            hideMouseTimer.restart();
        } else {
            hideMouseTimer = new Timer(5000, new HideMouseActionListener(this, controlsPanel, options));
        }
    }

    /**
     * Checks if is playing.
     * 
     * @return true, if is playing
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Sets the audio object.
     * 
     * @param audioObject
     *            the new audio object
     */
    public void setAudioObjects(List<IAudioObject> objects) {
        if (objects == null || objects.isEmpty()) {
            textLabel.setText("");
            textLabel2.setText("");
            return;
        }

        this.objects = objects;

        if (isVisible()) {
            updateWindow();
        }
    }

    private void setText(IAudioObject audioObject) {
        // No object
        if (audioObject == null) {
            textLabel.setText("");
            textLabel2.setText("");
        } else if (audioObject instanceof IRadio) {
            textLabel.setText(((IRadio) audioObject).getName());
            textLabel2.setText(((IRadio) audioObject).getUrl());
        } else if (audioObject instanceof IPodcastFeedEntry) {
            textLabel.setText(((IPodcastFeedEntry) audioObject).getTitle());
            textLabel2.setText(((IPodcastFeedEntry) audioObject).getPodcastFeed().getName());
        } else {
            textLabel.setText(audioObject.getTitleOrFileName());
            textLabel2.setText(audioObject.getArtist());
        }
    }

    /**
     * Sets the background.
     * 
     * @param file
     *            the new background
     */
    void setBackground(File file) {
    	if (file == null) {
    		backgroundPanel.setBackgroundImage(null);
    	} else {
    		try {
    			backgroundPanel.setBackgroundImage(ImageIO.read(file));
    		} catch (IOException e) {
    			Logger.error(e);
    		}
    	}
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        backgroundPanel = new FullScreenBackgroundPanel();
        add(backgroundPanel);

        setOptions();

        textLabel = getTextLabel();
        textLabel2 = getTextLabel2();

        JPanel textAndControlsPanel = getTextAndControlsPanel();
        
        textAndControlsPanel.setAlignmentX(0.0f);
        textAndControlsPanel.setAlignmentY(1f);

        covers.setAlignmentX(0.0f);
        covers.setAlignmentY(0.8f);

        backgroundPanel.add(textAndControlsPanel);
        backgroundPanel.add(covers);
    }

    /**
     * @param covers
     */
    public void setCovers(CoverFlow covers) {
		this.covers = covers;
	}
    
	/**
	 * 
	 */
	private void setOptions() {
		options = new JPopupMenu(I18nUtils.getString("OPTIONS"));
        selectBackground = new JMenuItem(I18nUtils.getString("SELECT_BACKGROUND"));
        removeBackground = new JMenuItem(I18nUtils.getString("REMOVE_BACKGROUND"));
        exitFullScreen = new JMenuItem(I18nUtils.getString("CLOSE"));
        options.add(selectBackground);
        options.add(removeBackground);
        options.add(exitFullScreen);
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
        
        previousButton = new FullScreenPreviousButton(getPlayerIconSize(), getPlayerIconResized(Images.getImage(Images.PREVIOUS_FULL_SCREEN)));
        playButton = new FullScreenPlayPauseButton(getMainPlayerIconSize(), getMainPlayerIconResized(Images.getImage(Images.PLAY_FULL_SCREEN)), getMainPlayerIconResized(Images.getImage(Images.PAUSE_FULL_SCREEN)));
        nextButton = new FullScreenNextButton(getPlayerIconSize(), getPlayerIconResized(Images.getImage(Images.NEXT_FULL_SCREEN)));
        
        buttonsPanel.add(previousButton);
        buttonsPanel.add(playButton);
        buttonsPanel.add(nextButton);
        
        setPanels(textAndControlsPanel, textPanel, buttonsPanel);
        textAndControlsPanel.add(controlsPanel);
        textAndControlsPanel.setMaximumSize(new Dimension(screenSize.width, screenSize.height / 3));
		return textAndControlsPanel;
	}
	
	private ImageIcon getMainPlayerIconResized(ImageIcon icon) {
		int iconSize = screenSize.height / 12;
		return ImageUtils.scaleImageBicubic(icon.getImage(), iconSize, iconSize);
	}

	private ImageIcon getPlayerIconResized(ImageIcon icon) {
		int iconSize = screenSize.height / 16;
		return ImageUtils.scaleImageBicubic(icon.getImage(), iconSize, iconSize);
	}
	
	private Dimension getMainPlayerIconSize() {
		return new Dimension(screenSize.height / 12, screenSize.height / 12);
	}

	private Dimension getPlayerIconSize() {
		return new Dimension(screenSize.height / 16, screenSize.height / 16);
	}

    /**
     * @return
     */
    public FullScreenBackgroundPanel getBackgroundPanel() {
		return backgroundPanel;
	}
    
    /**
     * @return
     */
    public FullScreenPreviousButton getPreviousButton() {
		return previousButton;
	}
    
    /**
     * @return
     */
    public FullScreenPlayPauseButton getPlayButton() {
		return playButton;
	}
    
    /**
     * @return
     */
    public FullScreenNextButton getNextButton() {
		return nextButton;
	}
    
	/**
	 * @param textAndControlsPanel
	 * @param textPanel
	 * @param buttonsPanel
	 */
	private void setPanels(JPanel textAndControlsPanel, JPanel textPanel, JPanel buttonsPanel) {
		GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 20, 0);
        c.anchor = GridBagConstraints.NORTH;
        textPanel.add(textLabel, c);
        c.gridy = 1;
        textPanel.add(textLabel2, c);

        textAndControlsPanel.add(textPanel);

        controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setOpaque(false);

        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 3;
        c.insets = new Insets(20, 0, 5, 0);
        controlsPanel.add(buttonsPanel, c);
	}

	/**
	 * @param lookAndFeelManager
	 */
	private JLabel getTextLabel2() {
		JLabel label = new JLabel();
        label.setFont(lookAndFeelManager.getCurrentLookAndFeel().getFullScreenLine2Font());
        label.setForeground(Color.WHITE);
        return label;
	}

	/**
	 * @param lookAndFeelManager
	 */
	private JLabel getTextLabel() {
		JLabel label = new JLabel();
        label.setFont(lookAndFeelManager.getCurrentLookAndFeel().getFullScreenLine1Font());
        label.setForeground(Color.WHITE);
        return label;
	}

	/**
	 * @return
	 */
	public JMenuItem getExitFullScreen() {
		return exitFullScreen;
	}

    /**
     * Sets the full screen.
     * @param fullscreen
     * @param frame
     */
    private void setFullScreen(boolean fullscreen, IFrame frame) {
    	osManager.setFullScreen(this, fullscreen, frame);
    }

    /**
     * Sets the playing.
     * 
     * @param playing
     *            the new playing
     */
    public void setPlaying(boolean playing) {
        this.playing = playing;
        playButton.setPlaying(playing);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateWindow();
            activateTimer();
        }
        setFullScreen(visible, frame);
    }

    /**
     * Updates the window with the current objects
     */
    private void updateWindow() {
        setText(objects.get(2));
        covers.paint(objects);
    }

	/**
	 * @return the options
	 */
	public JPopupMenu getOptions() {
		return options;
	}

	/**
	 * @return the covers
	 */
	public CoverFlow getCovers() {
		return covers;
	}

	/**
	 * @return the selectBackground
	 */
	public JMenuItem getSelectBackground() {
		return selectBackground;
	}

	/**
	 * @return the removeBackground
	 */
	public JMenuItem getRemoveBackground() {
		return removeBackground;
	}

}