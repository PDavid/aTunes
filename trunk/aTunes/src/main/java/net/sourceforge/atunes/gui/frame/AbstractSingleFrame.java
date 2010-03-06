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
package net.sourceforge.atunes.gui.frame;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.OSXAdapter;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.bars.ToolBar;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.dialogs.UpdateDialog;
import net.sourceforge.atunes.gui.views.menus.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.AudioObjectPropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTablePanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTreePanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.JXStatusBar;

/**
 * The standard frame
 */
abstract class AbstractSingleFrame extends CustomFrame implements net.sourceforge.atunes.gui.frame.Frame {

    private static final long serialVersionUID = 1L;

    public static final int NAVIGATION_TREE_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 280);
    public static final int NAVIGATION_TREE_HEIGHT = GuiUtils.getComponentWidthForResolution(1280, 280);

    public static final int NAVIGATION_TABLE_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 280);
    public static final int NAVIGATION_TABLE_HEIGHT = GuiUtils.getComponentWidthForResolution(1280, 280);

    public static final int CONTEXT_PANEL_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 295);

    public static final int AUDIO_OBJECT_PROPERTIES_PANEL_HEIGHT = 100;

    public static final int PLAY_LIST_PANEL_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 490);

    public static final int MARGIN = 100;

    private FrameState frameState;
    private FrameState oldFrameState;

    private NavigationTreePanel navigationTreePanel;
    private NavigationTablePanel navigationTablePanel;
    private JLabel leftStatusBar;
    private JLabel centerStatusBar;
    private JLabel rightStatusBar;
    private JLabel statusBarDeviceLabel;
    private JLabel statusBarNewPodcastEntriesLabel;
    private JLabel statusBarNewVersionLabel;
    private UpdateDialog updateDialog;
    private JProgressBar progressBar;
    private ApplicationMenuBar appMenuBar;
    private PlayListPanel playListPanel;
    private AudioObjectPropertiesPanel propertiesPanel;
    private ContextPanel contextPanel;
    private PlayerControlsPanel playerControls;
    private JXStatusBar statusBar;
    private ToolBar toolBar;

    private WindowAdapter fullFrameStateListener;

    private Timer statusBarNewVersionInfoTimer = new Timer(1000, new ActionListener() {
        private boolean b;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (b) {
                getStatusBarNewVersionLabel().setIcon(Images.getImage(Images.CHECK_FOR_UPDATES));
            } else {
                getStatusBarNewVersionLabel().setIcon(Images.getImage(Images.CHECK_FOR_UPDATES_BW));
            }
            b = !b;
        }
    });

    private Logger logger;

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    /**
     * Instantiates a new standard frame.
     */
    public AbstractSingleFrame() {
        super();
    }

    @Override
    public void create(FrameState frameState) {
        this.frameState = frameState;
        this.oldFrameState = new FrameState(frameState);

        // Set frame basic attributes
        setWindowSize();

        // Set window location
        Point windowLocation = null;
        if (ApplicationState.getInstance().getWindowXPosition() >= 0 && ApplicationState.getInstance().getWindowYPosition() >= 0) {
            windowLocation = new Point(ApplicationState.getInstance().getWindowXPosition(), ApplicationState.getInstance().getWindowYPosition());
        }
        if (windowLocation != null) {
            setLocation(windowLocation);
        } else {
            setLocationRelativeTo(null);
        }

        // Mac OS -code
        if (SystemProperties.OS == OperatingSystem.MACOSX) {
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("dispose", (Class[]) null));
                OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[]) null));
            } catch (Exception e) {
                getLogger().error(LogCategories.STANDARD_FRAME, e);
            }
        }

        // Set window state listener
        addWindowStateListener(getWindowStateListener());
        addWindowFocusListener(getWindowStateListener());

        // Create frame content
        setContentPane(getContentPanel());

        // Apply component orientation
        GuiUtils.applyComponentOrientation(this);

        setupSplitPaneDividerPosition(frameState);
    }

    protected abstract void setupSplitPaneDividerPosition(FrameState frameState);

    /**
     * Gets the window state listener.
     * 
     * @return the window state listener
     */
    private WindowAdapter getWindowStateListener() {
        if (fullFrameStateListener == null) {
            fullFrameStateListener = new WindowAdapter() {

                @Override
                public void windowStateChanged(WindowEvent e) {
                    if (e.getNewState() == Frame.ICONIFIED) {
                        if (ApplicationState.getInstance().isShowSystemTray()) {
                            AbstractSingleFrame.this.setVisible(false);
                        }
                        getLogger().debug(LogCategories.DESKTOP, "Window Iconified");
                    } else if (e.getNewState() != Frame.ICONIFIED) {
                        getLogger().debug(LogCategories.DESKTOP, "Window Deiconified");
                        ControllerProxy.getInstance().getPlayListController().scrollPlayList(false);
                    }
                }
            };
        }
        return fullFrameStateListener;
    }

    @Override
    public void dispose() {
        GuiHandler.getInstance().finish();
        super.dispose();
    }

    /**
     * This method is called from the OSXAdapter
     */
    public void about() {
        GuiHandler.getInstance().showAboutDialog();
    }

    @Override
    public ApplicationMenuBar getAppMenuBar() {
        if (appMenuBar == null) {
            appMenuBar = new ApplicationMenuBar();
        }
        return appMenuBar;
    }

    @Override
    public ContextPanel getContextPanel() {
        if (contextPanel == null) {
            contextPanel = new ContextPanel();
            contextPanel.setPreferredSize(new Dimension(CONTEXT_PANEL_WIDTH, 1));
            if (!ApplicationState.getInstance().isUseContext()) {
                contextPanel.setVisible(false);
            }
        }
        return contextPanel;
    }

    /**
     * Gets the center status bar.
     * 
     * @return the center status bar
     */
    private JLabel getCenterStatusBar() {
        if (centerStatusBar == null) {
            centerStatusBar = new JLabel(" ");
        }
        return centerStatusBar;
    }

    /**
     * Gets the content panel.
     * 
     * @return the content panel
     */
    protected abstract Container getContentPanel();

    @Override
    public JFrame getFrame() {
        return this;
    }

    /**
     * Gets the left status bar.
     * 
     * @return the left status bar
     */
    private JLabel getLeftStatusBar() {
        if (leftStatusBar == null) {
            leftStatusBar = new JLabel(I18nUtils.getString("STOPPED"));
        }
        return leftStatusBar;
    }

    @Override
    public NavigationTreePanel getNavigationTreePanel() {
        if (navigationTreePanel == null) {
            navigationTreePanel = new NavigationTreePanel();
            navigationTreePanel.setPreferredSize(new Dimension(NAVIGATION_TREE_WIDTH, NAVIGATION_TREE_HEIGHT));
        }
        return navigationTreePanel;
    }

    @Override
    public NavigationTablePanel getNavigationTablePanel() {
        if (navigationTablePanel == null) {
            navigationTablePanel = new NavigationTablePanel();
            navigationTablePanel.setPreferredSize(new Dimension(NAVIGATION_TABLE_WIDTH, NAVIGATION_TABLE_HEIGHT));
        }
        return navigationTablePanel;
    }

    @Override
    public PlayerControlsPanel getPlayerControls() {
        if (playerControls == null) {
            playerControls = new PlayerControlsPanel();
        }
        return playerControls;
    }

    @Override
    public PlayListPanel getPlayListPanel() {
        if (playListPanel == null) {
            playListPanel = new PlayListPanel();
            playListPanel.setMinimumSize(new Dimension(PLAY_LIST_PANEL_WIDTH, 1));
            playListPanel.setPreferredSize(new Dimension(PLAY_LIST_PANEL_WIDTH, 1));
        }
        return playListPanel;
    }

    @Override
    public PlayListTable getPlayListTable() {
        return getPlayListPanel().getPlayListTable();
    }

    /**
     * Gets the progress bar.
     * 
     * @return the progress bar
     */
    @Override
    public JProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = new JProgressBar();
            progressBar.setBorder(BorderFactory.createEmptyBorder());
        }
        return progressBar;
    }

    @Override
    public AudioObjectPropertiesPanel getPropertiesPanel() {
        if (propertiesPanel == null) {
            propertiesPanel = new AudioObjectPropertiesPanel();
            propertiesPanel.setPreferredSize(new Dimension(1, AUDIO_OBJECT_PROPERTIES_PANEL_HEIGHT));
            if (!ApplicationState.getInstance().isShowAudioObjectProperties()) {
                propertiesPanel.setVisible(false);
            }
        }
        return propertiesPanel;
    }

    /**
     * Gets the right status bar.
     * 
     * @return the right status bar
     */
    private JLabel getRightStatusBar() {
        if (rightStatusBar == null) {
            rightStatusBar = new JLabel(" ");
        }
        return rightStatusBar;
    }

    /**
     * Gets the status bar.
     * 
     * @return the status bar
     */
    protected final JXStatusBar getStatusBar() {
        if (statusBar == null) {
            statusBar = new JXStatusBar();
            JXStatusBar.Constraint c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
            statusBar.add(getLeftStatusBar(), c);
            c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
            statusBar.add(getCenterStatusBar(), c);
            c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
            statusBar.add(getRightStatusBar(), c);
            c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusBar.add(getProgressBar(), c);
        }
        return statusBar;
    }

    /**
     * Gets the status bar image label.
     * 
     * @return the status device label
     */
    private JLabel getStatusBarDeviceLabel() {
        if (statusBarDeviceLabel == null) {
            statusBarDeviceLabel = new JLabel(Images.getImage(Images.DEVICE));
        }
        return statusBarDeviceLabel;
    }

    private JLabel getStatusBarNewPodcastEntriesLabel() {
        if (statusBarNewPodcastEntriesLabel == null) {
            statusBarNewPodcastEntriesLabel = new JLabel(Images.getImage(Images.RSS_LITTLE));
            statusBarNewPodcastEntriesLabel.setToolTipText(I18nUtils.getString("NEW_PODCAST_ENTRIES"));
            statusBarNewPodcastEntriesLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showNewPodcastFeedEntriesInfo(false);
                    ControllerProxy.getInstance().getNavigationController().setNavigationView(PodcastNavigationView.class.getName());
                }
            });
        }
        return statusBarNewPodcastEntriesLabel;
    }

    JLabel getStatusBarNewVersionLabel() {
        if (statusBarNewVersionLabel == null) {
            statusBarNewVersionLabel = new JLabel(Images.getImage(Images.CHECK_FOR_UPDATES));
            statusBarNewVersionLabel.setToolTipText(I18nUtils.getString("NEW_VERSION_AVAILABLE"));
            statusBarNewVersionLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    updateDialog.setVisible(true);
                    updateDialog.toFront();
                    showNewVersionInfo(false, null);
                }
            });
        }
        return statusBarNewVersionLabel;
    }

    @Override
    public ToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new ToolBar();
        }
        return toolBar;
    }

    @Override
    public void setCenterStatusBarText(String text, String toolTip) {
        getCenterStatusBar().setText(text);
        getCenterStatusBar().setToolTipText(toolTip);
    }

    @Override
    public void setLeftStatusBarText(String text, String toolTip) {
        getLeftStatusBar().setText(text);
        getLeftStatusBar().setToolTipText(toolTip);
    }

    @Override
    public void setRightStatusBarText(String text, String toolTip) {
        getRightStatusBar().setText(text);
        getRightStatusBar().setToolTipText(toolTip);
    }

    @Override
    public void setStatusBarDeviceLabelText(String text) {
        getStatusBarDeviceLabel().setText(text);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        // TODO Workaround for JRE bug 6365898 (will be fixed in Java 7)
        if (visible && SystemProperties.OS == OperatingSystem.LINUX && ApplicationState.getInstance().isMaximized()) {
            setWindowSizeMaximized();
        }
        //TODO
        /*
         * We need this because when switching from LTR to RTL component
         * orientation (or vice versa) the navigation panel width is too big
         * because of an unknown reason
         */
        Timer t = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Toolkit.getDefaultToolkit().getScreenSize().width + 15 < getSize().width) {
                    setWindowSize();
                }
            }
        });
        t.setRepeats(false);
        t.start();
    }

    /**
     * Sets the window size.
     */
    public void setWindowSize() {
        setMinimumSize(new Dimension(655, 410));
        if (ApplicationState.getInstance().isMaximized()) {
            setWindowSizeMaximized();
        } else {
            Dimension d = null;
            if (ApplicationState.getInstance().getWindowWidth() != 0 && ApplicationState.getInstance().getWindowHeight() != 0) {
                d = new Dimension(ApplicationState.getInstance().getWindowWidth(), ApplicationState.getInstance().getWindowHeight());
            }
            if (d != null) {
                setSize(d);
            } else {
                // Set size always according to main device dimension 
                // Avoid create a frame too big: if device width is greater than 2000 pixels then use half width
                setSize((GuiUtils.getDeviceWidth() > 2000 ? GuiUtils.getDeviceWidth() / 2 : GuiUtils.getDeviceWidth()) - MARGIN, GuiUtils.getDeviceHeight() - MARGIN);
            }
        }

    }

    private void setWindowSizeMaximized() {
        Dimension screen = getToolkit().getScreenSize();
        setSize(screen.width - MARGIN, screen.height - MARGIN);
        setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    @Override
    public void showSongProperties(boolean show) {
        getPropertiesPanel().setVisible(show);
    }

    @Override
    public void showStatusBar(boolean show) {
        getStatusBar().setVisible(show);
    }

    @Override
    public void showToolBar(boolean show) {
        getToolBar().setVisible(show);
    }

    @Override
    public void showDeviceInfo(boolean show) {
        if (show) {
            JXStatusBar.Constraint c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusBar.add(getStatusBarDeviceLabel(), c);
        } else {
            statusBar.remove(getStatusBarDeviceLabel());
        }
        getStatusBarDeviceLabel().setVisible(show);
        statusBar.validate();
        statusBar.repaint();
    }

    @Override
    public void showNewPodcastFeedEntriesInfo(boolean show) {
        if (show) {
            if (Arrays.asList(statusBar.getComponents()).contains(getStatusBarNewPodcastEntriesLabel())) {
                return;
            }
            JXStatusBar.Constraint c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusBar.add(getStatusBarNewPodcastEntriesLabel(), c);
        } else {
            statusBar.remove(getStatusBarNewPodcastEntriesLabel());
        }
        getStatusBarNewPodcastEntriesLabel().setVisible(show);
        statusBar.validate();
        statusBar.repaint();
    }

    @Override
    public void showNewVersionInfo(boolean show, ApplicationVersion version) {
        if (show && version == null) {
            throw new IllegalArgumentException();
        }
        if (show) {
            if (Arrays.asList(statusBar.getComponents()).contains(getStatusBarNewVersionLabel())) {
                return;
            }
            JXStatusBar.Constraint c = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusBar.add(getStatusBarNewVersionLabel(), c);
            statusBarNewVersionInfoTimer.start();
            updateDialog = new UpdateDialog(version, this.getFrame());
        } else {
            statusBarNewVersionInfoTimer.stop();
            statusBar.remove(getStatusBarNewVersionLabel());
            updateDialog = null;
        }
        getStatusBarNewVersionLabel().setVisible(show);
        statusBar.validate();
        statusBar.repaint();
    }

    @Override
    public FrameState getFrameState() {
        return frameState;
    }

    protected final void applyVisibility(boolean show, String s, Component c, JSplitPane sp) {
        if (!show) {
            oldFrameState.putSplitPaneDividerPos(s, getFrameState().getSplitPaneDividerPos(s));
        }
        boolean b = c.isVisible();
        c.setVisible(show);
        if (show && !b) {
            applySplitPaneDividerPosition(sp, oldFrameState.getSplitPaneDividerPos(s), 0);
        }
    }

    protected static void applySplitPaneDividerPosition(JSplitPane splitPane, int location, double relPos) {
        if (location != 0) {
            splitPane.setDividerLocation(location);
        } else {
            splitPane.setDividerLocation(relPos);
        }
    }
}
