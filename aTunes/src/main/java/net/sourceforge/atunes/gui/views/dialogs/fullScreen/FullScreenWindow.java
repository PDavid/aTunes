/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.CustomWindow;
import net.sourceforge.atunes.gui.views.controls.playerControls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.NextButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.StopButton;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeLevel;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeSlider;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class FullScreenWindow extends CustomWindow {

    private static final long serialVersionUID = 3422799994808333945L;

    private CoverFlow covers;

    /** The text label. */
    private JLabel textLabel;

    /** The text label 2 */
    private JLabel textLabel2;

    /** The progress bar. */
    private JSlider progressBar;

    /** The time. */
    private JLabel time;

    /** The remaining time. */
    private JLabel remainingTime;

    /** The options. */
    private JPopupMenu options;

    /** The exit full screen. */
    private JMenuItem exitFullScreen;

    /** The select background. */
    private JMenuItem selectBackground;

    /** The remove background. */
    private JMenuItem removeBackground;

    /** The previous button. */
    private PreviousButton previousButton;

    /** The play button. */
    private PlayPauseButton playButton;

    /** The stop button. */
    private StopButton stopButton;

    /** The next button. */
    private NextButton nextButton;

    /** The mute button */
    private MuteButton muteButton;

    /** The volume slider */
    private VolumeSlider volumeSlider;

    /** The volume level */
    private VolumeLevel volumeLevel;

    private JPanel textPanel;

    private JPanel controlsPanel;

    /** The playing. */
    private boolean playing;

    /** The background. */
    private ImageIcon background;

    /** The key adapter. */
    private KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_F11) {
                setVisible(false);
            }
        }
    };

    private Timer hideMouseTimer;

    private MouseListener clickListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            activateTimer();
        }
    };

    private MouseMotionListener moveListener = new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            activateTimer();
        }
    };

    private MouseListener showMenuListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                options.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    };

    /**
     * Audio Objects to show in full screen
     */
    private List<AudioObject> objects;

    /**
     * Instantiates a new full screen dialog.
     * 
     * @param owner
     *            the owner
     */
    public FullScreenWindow(JFrame owner) {
        super(owner, 0, 0);
        setLocation(0, 0);
        setAlwaysOnTop(true);
        setContent();
        addKeyListener(keyAdapter);
        File backgroundFile = null;
        if (ApplicationState.getInstance().getFullScreenBackground() != null) {
            backgroundFile = new File(ApplicationState.getInstance().getFullScreenBackground());
            if (!backgroundFile.exists()) {
                backgroundFile = null;
            }
        }
        if (backgroundFile == null) {
            background = null;
        } else {
            setBackground(backgroundFile);
        }
        GuiUtils.applyComponentOrientation(this);

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
                    setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Images.getImage(Images.EMPTY).getImage(), new Point(0, 0), "invisibleCursor"));
                    if (options.isVisible()) {
                        options.setVisible(false);
                    }
                }
            });
        }
    }

    /**
     * Gets the inset for progress bar.
     * 
     * @return the inset for progress bar
     */
    private int getInsetForProgressBar() {
        return GuiUtils.getDeviceWidth() / 4;
    }

    /**
     * Gets the remaining time.
     * 
     * @return the remainingTime
     */
    public JLabel getRemainingTime() {
        return remainingTime;
    }

    /**
     * Gets the time.
     * 
     * @return the time
     */
    public JLabel getTime() {
        return time;
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
    public void setAudioObjects(List<AudioObject> objects) {
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

    private void setText(AudioObject audioObject) {
        // No object
        if (audioObject == null) {
            textLabel.setText("");
            textLabel2.setText("");
        } else if (audioObject instanceof Radio) {
            progressBar.setEnabled(false);
            textLabel.setText(((Radio) audioObject).getName());
            textLabel2.setText(((Radio) audioObject).getUrl());
        } else if (audioObject instanceof PodcastFeedEntry) {
            progressBar.setEnabled(false);
            textLabel.setText(((PodcastFeedEntry) audioObject).getTitle());
            textLabel2.setText(((PodcastFeedEntry) audioObject).getPodcastFeed().getName());
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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        try {
            background = ImageUtils.scaleImageBicubic(ImageIO.read(file), screenSize.width, screenSize.height);
            ApplicationState.getInstance().setFullScreenBackground(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        final JPanel panel = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 109708757849271173L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background == null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setPaint(new GradientPaint(0, 0, Color.BLACK, 0, this.getHeight(), Color.BLACK));
                    g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                } else {
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    g.drawImage(ImageUtils.resize(background, screenSize.width, screenSize.height).getImage(), 0, 0, this);
                }
            }
        };
        panel.setBackground(Color.black);
        add(panel);

        options = new JPopupMenu(I18nUtils.getString("OPTIONS"));
        options.addKeyListener(keyAdapter);
        options.addMouseListener(clickListener);

        panel.addMouseListener(showMenuListener);

        selectBackground = new JMenuItem(I18nUtils.getString("SELECT_BACKGROUND"));

        selectBackground.addActionListener(new ActionListener() {
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
            }
        });

        removeBackground = new JMenuItem(I18nUtils.getString("REMOVE_BACKGROUND"));
        removeBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                background = null;
                ApplicationState.getInstance().setFullScreenBackground(null);
                FullScreenWindow.this.invalidate();
                FullScreenWindow.this.repaint();
            }
        });
        exitFullScreen = new JMenuItem(I18nUtils.getString("CLOSE"));
        exitFullScreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        options.add(selectBackground);
        options.add(removeBackground);
        options.add(exitFullScreen);

        previousButton = new PreviousButton(PlayerControlsPanel.PREVIOUS_NEXT_BUTTONS_SIZE);
        playButton = new PlayPauseButton(PlayerControlsPanel.PLAY_BUTTON_SIZE);
        stopButton = new StopButton(PlayerControlsPanel.STOP_MUTE_BUTTONS_SIZE);
        nextButton = new NextButton(PlayerControlsPanel.PREVIOUS_NEXT_BUTTONS_SIZE);
        muteButton = new MuteButton(PlayerControlsPanel.STOP_MUTE_BUTTONS_SIZE);
        muteButton.setText("");
        volumeSlider = new VolumeSlider();
        volumeLevel = new VolumeLevel();

        previousButton.addMouseListener(clickListener);
        playButton.addMouseListener(clickListener);
        stopButton.addMouseListener(clickListener);
        nextButton.addMouseListener(clickListener);
        muteButton.addMouseListener(clickListener);
        volumeSlider.addMouseListener(clickListener);
        volumeLevel.addMouseListener(clickListener);

        covers = new CoverFlow();
        Dimension coverSize = new Dimension(GuiUtils.getDeviceWidth(), GuiUtils.getDeviceHeight() * 5 / 7);
        covers.setMinimumSize(coverSize);
        covers.setMaximumSize(coverSize);
        covers.setPreferredSize(coverSize);

        covers.addMouseListener(clickListener);
        covers.addMouseListener(showMenuListener);
        covers.addMouseMotionListener(moveListener);

        textLabel = new JLabel();
        textLabel.setFont(Fonts.getFullScreenLine1Font());
        textLabel.setForeground(Color.WHITE);

        textLabel2 = new JLabel();
        textLabel2.setFont(Fonts.getContextInformationBigFont());
        textLabel2.setForeground(Color.WHITE);

        time = new JLabel("0:00");
        time.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));

        remainingTime = new JLabel("0:00");
        remainingTime.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));

        progressBar = new JSlider();
        progressBar.setOpaque(false);
        progressBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (progressBar.isEnabled()) {
                    int widthClicked = e.getPoint().x;
                    int widthOfProgressBar = progressBar.getSize().width;
                    double perCent = (double) widthClicked / (double) widthOfProgressBar;
                    PlayerHandler.getInstance().seekCurrentAudioObject(perCent);
                }
            }
        });
        progressBar.addKeyListener(keyAdapter);
        progressBar.setToolTipText(I18nUtils.getString("CLICK_TO_SEEK"));
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        progressBar.setFocusable(false);

        JPanel textAndControlsPanel = new JPanel(new GridLayout(2, 1));
        textAndControlsPanel.setOpaque(false);

        textPanel = new JPanel(new GridBagLayout());
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
        int inset = getInsetForProgressBar();

        c.gridy = 0;
        c.insets = new Insets(0, inset, 0, 10);
        c.gridwidth = 1;
        controlsPanel.add(time, c);
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        controlsPanel.add(progressBar, c);
        c.gridx = 2;
        c.insets = new Insets(0, 10, 0, inset);
        c.weightx = 0;
        controlsPanel.add(remainingTime, c);

        JPanel buttonsPanel = PlayerControlsPanel.getPanelWithPlayerControls(stopButton, previousButton, playButton, nextButton, muteButton, volumeSlider, volumeLevel);

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
     * 
     * @param fullscreen
     *            if fullscreen mode should be set
     */
    private void setFullScreen(boolean fullscreen) {
        if (SystemProperties.OS.isOldWindows() && !SystemProperties.OS.isWindowsVista()) {
            if (fullscreen) {
                setSize(GuiUtils.getDeviceWidth(), GuiUtils.getDeviceHeight());
            }
        } else {
            // Get in which screen is application and set full screen in that screen
            GraphicsDevice graphicsDevice = GuiUtils.getGraphicsDeviceForLocation(GuiHandler.getInstance().getFrame().getLocation());
            graphicsDevice.setFullScreenWindow(fullscreen ? this : null);
        }
    }

    /**
     * Sets the max duration.
     * 
     * @param maxDuration
     *            the new max duration
     */
    public void setAudioObjectLength(long maxDuration) {
        progressBar.setMaximum((int) maxDuration);
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
        if (time == 0) {
            this.remainingTime.setText(StringUtils.milliseconds2String(time));
        } else {
            this.remainingTime.setText(remainingTime1 > 0 ? StringUtils.getString("- ", StringUtils.milliseconds2String(remainingTime1)) : "-");
        }

        this.time.setText(StringUtils.milliseconds2String(time));
        progressBar.setValue((int) time);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateWindow();
            activateTimer();
        }
        setFullScreen(visible);
    }

    public void setVolume(int volume) {
        volumeSlider.setValue(volume);
        volumeLevel.setText(StringUtils.getString(String.valueOf(volume), " %"));
    }

    /**
     * Updates the window with the current objects
     */
    private void updateWindow() {
        setText(objects.get(2));
        covers.paint(objects);
    }
}
