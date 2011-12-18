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

package net.sourceforge.atunes.gui.views.dialogs.fullScreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomWindow;
import net.sourceforge.atunes.gui.views.controls.playerControls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NextButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.StopButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeSlider;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsSize;
import net.sourceforge.atunes.kernel.modules.player.ProgressBarSeekListener;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IProgressSlider;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;

public final class FullScreenWindow extends AbstractCustomWindow {

    private final class SelectBackgroundActionListener implements
			ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		    JFileChooser fileChooser = new JFileChooser();
		    fileChooser.setFileFilter(new FileFilter() {
		        @Override
		        public boolean accept(File pathname) {
		            if (pathname.isDirectory()) {
		                return true;
		            }
		            String fileName = pathname.getName().toUpperCase();
		            return fileName.endsWith("JPG") || fileName.endsWith("JPEG") || fileName.endsWith("PNG");
		        }

		        @Override
		        public String getDescription() {
		            return I18nUtils.getString("IMAGES");
		        }
		    });
		    if (fileChooser.showOpenDialog(FullScreenWindow.this) == JFileChooser.APPROVE_OPTION) {
		        File selectedBackground = fileChooser.getSelectedFile();
		        setBackground(selectedBackground);
		        FullScreenWindow.this.invalidate();
		        FullScreenWindow.this.repaint();
		    }
			setVisible(true);
		}
	}

	private static final long serialVersionUID = 3422799994808333945L;

    private CoverFlow covers;

    /** The text label. */
    private JLabel textLabel;

    /** The text label 2 */
    private JLabel textLabel2;

    private IProgressSlider progressSlider;
    
    /** The options. */
    private JPopupMenu options;

    /** The play button. */
    private PlayPauseButton playButton;

    /** The volume slider */
    private VolumeSlider volumeSlider;

    private JPanel controlsPanel;

    /** The playing. */
    private boolean playing;

    /** The background. */
    private transient Image background;

    /** The key adapter. */
    private transient KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_F11) {
                setVisible(false);
            }
        }
    };

    private Timer hideMouseTimer;

    private transient MouseListener clickListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            activateTimer();
        }
    };

    private transient MouseMotionListener moveListener = new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            activateTimer();
        }
    };

    private transient MouseListener showMenuListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (GuiUtils.isSecondaryMouseButton(e)) {
                options.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    };

    /**
     * Audio Objects to show in full screen
     */
    private List<IAudioObject> objects;
    
    private IState state;
    
    private IFrame frame;
    
    private IOSManager osManager;
    
    private IPlayerHandler playerHandler;

    /**
     * Instantiates a new full screen dialog.
     * 
     * @param owner
     * @param state
     * @param frame
     * @param osManager
     * @param lookAndFeelManager
     * @param playerHandler
     */
    public FullScreenWindow(JFrame owner, IState state, IFrame frame, IOSManager osManager, ILookAndFeelManager lookAndFeelManager, IPlayerHandler playerHandler) {
        super(owner, 0, 0);
        this.state = state;
        this.frame = frame;
        this.osManager = osManager;
        this.playerHandler = playerHandler;
        setLocation(0, 0);
        setAlwaysOnTop(true);
        setContent(lookAndFeelManager);
        addKeyListener(keyAdapter);
        File backgroundFile = null;
        if (state.getFullScreenBackground() != null) {
            backgroundFile = new File(state.getFullScreenBackground());
            if (!backgroundFile.exists()) {
                backgroundFile = null;
            }
        }
        if (backgroundFile == null) {
            background = null;
        } else {
            setBackground(backgroundFile);
        }

        addMouseMotionListener(moveListener);

        addMouseListener(clickListener);
    }

    void activateTimer() {
        setCursor(Cursor.getDefaultCursor());
        controlsPanel.setVisible(true);
        if (hideMouseTimer != null) {
            hideMouseTimer.restart();
        } else {
            hideMouseTimer = new Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    controlsPanel.setVisible(false);
                    setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR), new Point(0, 0), "invisibleCursor"));
                    if (options.isVisible()) {
                        options.setVisible(false);
                    }
                }
            });
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
            progressSlider.setEnabled(false);
            textLabel.setText(((IRadio) audioObject).getName());
            textLabel2.setText(((IRadio) audioObject).getUrl());
        } else if (audioObject instanceof IPodcastFeedEntry) {
        	progressSlider.setEnabled(false);
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
        try {
            background = ImageIO.read(file);
            FullScreenWindow.this.state.setFullScreenBackground(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the content.
     * @param lookAndFeelManager 
     */
    private void setContent(ILookAndFeelManager lookAndFeelManager) {
        final JPanel panel = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 109708757849271173L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background == null && g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setPaint(new GradientPaint(0, 0, Color.BLACK, 0, this.getHeight(), Color.BLACK));
                    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                } else {
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    g.drawImage(ImageUtils.scaleBufferedImageBicubic(background, screenSize.width, screenSize.height), 0, 0, this);
                }
            }
        };
        panel.setBackground(Color.black);
        add(panel);

        options = new JPopupMenu(I18nUtils.getString("OPTIONS"));
        options.addKeyListener(keyAdapter);
        options.addMouseListener(clickListener);

        panel.addMouseListener(showMenuListener);

        JMenuItem selectBackground = new JMenuItem(I18nUtils.getString("SELECT_BACKGROUND"));

        selectBackground.addActionListener(new SelectBackgroundActionListener());

        JMenuItem removeBackground = new JMenuItem(I18nUtils.getString("REMOVE_BACKGROUND"));
        removeBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                background = null;
                FullScreenWindow.this.state.setFullScreenBackground(null);
                FullScreenWindow.this.invalidate();
                FullScreenWindow.this.repaint();
            }
        });
        JMenuItem exitFullScreen = new JMenuItem(I18nUtils.getString("CLOSE"));
        exitFullScreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        options.add(selectBackground);
        options.add(removeBackground);
        options.add(exitFullScreen);

        PreviousButton previousButton = new PreviousButton(PlayerControlsSize.PREVIOUS_NEXT_BUTTONS_SIZE, lookAndFeelManager);
        playButton = new PlayPauseButton(PlayerControlsSize.PLAY_BUTTON_SIZE, lookAndFeelManager);
        StopButton stopButton = new StopButton(PlayerControlsSize.STOP_MUTE_BUTTONS_SIZE, lookAndFeelManager);
        NextButton nextButton = new NextButton(PlayerControlsSize.PREVIOUS_NEXT_BUTTONS_SIZE, lookAndFeelManager);
        MuteButton muteButton = Context.getBean("volumeButton", MuteButton.class);
        muteButton.setText("");
        volumeSlider = new VolumeSlider(state, playerHandler);

        previousButton.addMouseListener(clickListener);
        playButton.addMouseListener(clickListener);
        stopButton.addMouseListener(clickListener);
        nextButton.addMouseListener(clickListener);
        muteButton.addMouseListener(clickListener);
        volumeSlider.addMouseListener(clickListener);

        covers = new CoverFlow();
        Dimension coverSize = new Dimension(GuiUtils.getDeviceWidth(), GuiUtils.getDeviceHeight() * 5 / 7);
        covers.setMinimumSize(coverSize);
        covers.setMaximumSize(coverSize);
        covers.setPreferredSize(coverSize);

        covers.addMouseListener(clickListener);
        covers.addMouseListener(showMenuListener);
        covers.addMouseMotionListener(moveListener);

        textLabel = new JLabel();
        textLabel.setFont(lookAndFeelManager.getCurrentLookAndFeel().getFullScreenLine1Font());
        textLabel.setForeground(Color.WHITE);

        textLabel2 = new JLabel();
        textLabel2.setFont(lookAndFeelManager.getCurrentLookAndFeel().getContextInformationBigFont());
        textLabel2.setForeground(Color.WHITE);

        progressSlider = Context.getBean(IProgressSlider.class);
        ProgressBarSeekListener seekListener = new ProgressBarSeekListener(progressSlider, playerHandler);
        progressSlider.addMouseListener(seekListener);        
        progressSlider.addKeyListener(keyAdapter);
        progressSlider.setOpaque(false);

        JPanel textAndControlsPanel = new JPanel(new GridLayout(2, 1));
        textAndControlsPanel.setOpaque(false);

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);

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
        controlsPanel.add(progressSlider.getSwingComponent(), c);

        JPanel buttonsPanel = PlayerControlsPanel.getPanelWithPlayerControls(stopButton, previousButton, playButton, nextButton, muteButton, volumeSlider, lookAndFeelManager);

        c.gridx = 0;
        c.gridwidth = 3;
        c.gridy = 1;
        c.insets = new Insets(20, 0, 5, 0);
        controlsPanel.add(buttonsPanel, c);

        textAndControlsPanel.add(controlsPanel);

        panel.add(covers, BorderLayout.CENTER);
        panel.add(textAndControlsPanel, BorderLayout.SOUTH);

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
     * Sets the max duration.
     * 
     * @param maxDuration
     *            the new max duration
     */
    public void setAudioObjectLength(long maxDuration) {
        progressSlider.setMaximum((int) maxDuration);
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

    /**
     * Sets the time.
     * 
     * @param time
     *            the time
     * @param totalTime
     *            the total time
     */
    public void setCurrentAudioObjectPlayedTime(final long time, final long totalTime) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setCurrentAudioObjectPlayedTimeEDT(time, totalTime);
                }
            });
        } else {
            setCurrentAudioObjectPlayedTimeEDT(time, totalTime);
        }
    }

    private void setCurrentAudioObjectPlayedTimeEDT(long time, long totalTime) {
        long remainingTime1 = totalTime - time;
        progressSlider.setProgress(time, time == 0 ? time : remainingTime1);
        progressSlider.setValue((int) time);
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

    public void setVolume(int volume) {
        volumeSlider.setValue(volume);
    }

    /**
     * Updates the window with the current objects
     */
    private void updateWindow() {
        setText(objects.get(2));
        covers.paint(objects, osManager);
    }    
}