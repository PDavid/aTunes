/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.gui.frame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.OSXAdapter;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.bars.ToolBar;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.dialogs.UpdateDialog;
import net.sourceforge.atunes.gui.views.menus.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.AudioObjectPropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
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
public class StandardFrame extends CustomFrame implements net.sourceforge.atunes.gui.frame.Frame {

    private static final long serialVersionUID = 1L;

    public static final int NAVIGATION_PANEL_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 280);
    public static final int NAVIGATION_PANEL_MINIMUM_WIDTH = NAVIGATION_PANEL_WIDTH - 80;
    public static final int NAVIGATION_PANEL_MAXIMUM_WIDTH = NAVIGATION_PANEL_WIDTH + 50;
    public static final int CONTEXT_PANEL_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 295);
    public static final int CONTEXT_PANEL_MINIMUM_WIDTH = CONTEXT_PANEL_WIDTH - 50;
    public static final int FILE_PROPERTIES_PANEL_HEIGHT = 100;
    public static final int PLAY_LIST_PANEL_WIDTH = GuiUtils.getComponentWidthForResolution(1280, 490);
    public static final int NAVIGATOR_SPLIT_PANE_DIVIDER_LOCATION = GuiUtils.getComponentHeightForResolution(1024, 612);
    private static final int SPLIT_PANE_DEFAULT_DIVIDER_SIZE = 10;
    public static final int MARGIN = 100;

    private JSplitPane leftVerticalSplitPane;
    private JSplitPane rightVerticalSplitPane;
    private JLabel leftStatusBar;
    private JLabel centerStatusBar;
    private JLabel rightStatusBar;
    private JLabel statusBarDeviceLabel;
    private JLabel statusBarNewPodcastEntriesLabel;
    private JLabel statusBarNewVersionLabel;
    UpdateDialog updateDialog;
    private JProgressBar progressBar;
    private ApplicationMenuBar appMenuBar;
    private NavigationPanel navigationPanel;
    private PlayListPanel playListPanel;
    private AudioObjectPropertiesPanel propertiesPanel;
    private ContextPanel contextPanel;
    private PlayerControlsPanel playerControls;
    private JXStatusBar statusBar;
    private ToolBar toolBar;

    private WindowAdapter fullFrameStateListener;

    Timer statusBarNewVersionInfoTimer = new Timer(1000, new ActionListener() {
        private boolean b;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (b) {
                getStatusBarNewVersionLabel().setIcon(ImageLoader.getImage(ImageLoader.CHECK_FOR_UPDATES));
            } else {
                getStatusBarNewVersionLabel().setIcon(ImageLoader.getImage(ImageLoader.CHECK_FOR_UPDATES_BW));
            }
            b = !b;
        }
    });

    Logger logger = new Logger();

    /**
     * Instantiates a new standard frame.
     */
    public StandardFrame() {
        super();
    }

    @Override
    public void create() {
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
                logger.error(LogCategories.STANDARD_FRAME, e);
            }
        }

        // Set window state listener
        addWindowStateListener(getWindowStateListener());
        addWindowFocusListener(getWindowStateListener());

        // Create frame content
        setContentPane(getContentPanel());

        // Apply component orientation
        GuiUtils.applyComponentOrientation(this);
    }

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
                            StandardFrame.this.setVisible(false);
                        }
                        logger.debug(LogCategories.DESKTOP, "Window Iconified");
                    } else if (e.getNewState() != Frame.ICONIFIED) {
                        logger.debug(LogCategories.DESKTOP, "Window Deiconified");
                        ControllerProxy.getInstance().getPlayListController().scrollPlayList(false);
                    }
                }
            };
        }
        return fullFrameStateListener;
    }

    @Override
    public void dispose() {
        VisualHandler.getInstance().finish();
        super.dispose();
    }

    /**
     * This method is called from the OSXAdapter
     */
    public void about() {
        VisualHandler.getInstance().showAboutDialog();
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
            contextPanel.setMinimumSize(new Dimension(CONTEXT_PANEL_MINIMUM_WIDTH, 1));
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
    private Container getContentPanel() {
        // Main Container
        JPanel panel = new JPanel(new GridBagLayout());

        // Main Split Pane			
        leftVerticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Create menu bar
        setJMenuBar(getAppMenuBar());

        GridBagConstraints c = new GridBagConstraints();

        // Play List, File Properties, Context panel
        JPanel nonNavigatorPanel = new JPanel(new BorderLayout());
        rightVerticalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightVerticalSplitPane.setBorder(BorderFactory.createEmptyBorder());
        rightVerticalSplitPane.setResizeWeight(1);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        centerPanel.add(getPlayListPanel(), c);
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;
        centerPanel.add(getPropertiesPanel(), c);
        c.gridy = 2;
        centerPanel.add(getPlayerControls(), c);

        // JSplitPane does not support component orientation, so we must do this manually
        // -> http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4265389
        if (GuiUtils.getComponentOrientation().isLeftToRight()) {
            rightVerticalSplitPane.add(centerPanel);
            rightVerticalSplitPane.add(getContextPanel());
        } else {
            rightVerticalSplitPane.add(getContextPanel());
            rightVerticalSplitPane.add(centerPanel);
        }

        nonNavigatorPanel.add(rightVerticalSplitPane, BorderLayout.CENTER);

        // Navigation Panel
        // JSplitPane does not support component orientation, so we must do this manually
        // -> http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4265389
        if (GuiUtils.getComponentOrientation().isLeftToRight()) {
            leftVerticalSplitPane.add(getNavigationPanel());
            leftVerticalSplitPane.add(nonNavigatorPanel);
            leftVerticalSplitPane.setResizeWeight(0.2);
        } else {
            leftVerticalSplitPane.add(nonNavigatorPanel);
            leftVerticalSplitPane.add(getNavigationPanel());
            rightVerticalSplitPane.setResizeWeight(0.2);
        }

        c.gridx = 0;
        c.gridy = 0;
        panel.add(getToolBar(), c);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(leftVerticalSplitPane, c);

        c.gridy = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(getStatusBar(), c);

        return panel;
    }

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

    /**
     * Gets the left vertical split pane.
     * 
     * @return the left vertical split pane
     */
    public JSplitPane getLeftVerticalSplitPane() {
        return leftVerticalSplitPane;
    }

    @Override
    public NavigationPanel getNavigationPanel() {
        if (navigationPanel == null) {
            navigationPanel = new NavigationPanel();
            navigationPanel.setPreferredSize(new Dimension(NAVIGATION_PANEL_WIDTH, 1));
            navigationPanel.setMinimumSize(new Dimension(NAVIGATION_PANEL_MINIMUM_WIDTH, 1));
            navigationPanel.setMaximumSize(new Dimension(NAVIGATION_PANEL_MAXIMUM_WIDTH, 1));

            // If must be hidden, hide directly
            if (!ApplicationState.getInstance().isShowNavigationPanel()) {
                navigationPanel.setVisible(false);
            }
        }
        return navigationPanel;
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
            propertiesPanel.setPreferredSize(new Dimension(1, FILE_PROPERTIES_PANEL_HEIGHT));
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
     * Gets the right vertical split pane.
     * 
     * @return the right vertical split pane
     */
    public JSplitPane getRightVerticalSplitPane() {
        return rightVerticalSplitPane;
    }

    /**
     * Gets the status bar.
     * 
     * @return the status bar
     */
    public JXStatusBar getStatusBar() {
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
            statusBarDeviceLabel = new JLabel(ImageLoader.getImage(ImageLoader.DEVICE));
        }
        return statusBarDeviceLabel;
    }

    private JLabel getStatusBarNewPodcastEntriesLabel() {
        if (statusBarNewPodcastEntriesLabel == null) {
            statusBarNewPodcastEntriesLabel = new JLabel(ImageLoader.getImage(ImageLoader.RSS_LITTLE));
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
            statusBarNewVersionLabel = new JLabel(ImageLoader.getImage(ImageLoader.CHECK_FOR_UPDATES));
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
    public void setCenterStatusBar(String text, String toolTip) {
        getCenterStatusBar().setText(text);
        getCenterStatusBar().setToolTipText(toolTip);
    }

    @Override
    public void setLeftStatusBarText(String text, String toolTip) {
        getLeftStatusBar().setText(text);
        getLeftStatusBar().setToolTipText(toolTip);
    }

    /**
     * Sets the left vertical split pane divider location and set window size.
     * 
     * @param location
     *            the new left vertical split pane divider location and set
     *            window size
     */
    public void setLeftVerticalSplitPaneDividerLocationAndSetWindowSize(int location) {
        leftVerticalSplitPane.setDividerLocation(location);
        setWindowSize();
    }

    @Override
    public void setRightStatusBar(String text, String toolTip) {
        getRightStatusBar().setText(text);
        getRightStatusBar().setToolTipText(toolTip);
    }

    /**
     * Sets the right vertical split pane divider location and set window size.
     * 
     * @param location
     *            the new right vertical split pane divider location and set
     *            window size
     */
    public void setRightVerticalSplitPaneDividerLocationAndSetWindowSize(int location) {
        rightVerticalSplitPane.setDividerLocation(location);
        setWindowSize();
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
    public void showContextPanel(boolean show, boolean changeSize) {
        boolean wasVisible = getContextPanel().isVisible();
        getContextPanel().setVisible(show);
        if (!wasVisible && show) {
            int panelWidth = playListPanel.getWidth();
            int rightDividerLocation = ApplicationState.getInstance().getRightVerticalSplitPaneDividerLocation();
            if (rightDividerLocation != 0 && rightDividerLocation < (panelWidth - StandardFrame.CONTEXT_PANEL_WIDTH)) {
                rightVerticalSplitPane.setDividerLocation(ApplicationState.getInstance().getRightVerticalSplitPaneDividerLocation());
            } else {
                rightVerticalSplitPane.setDividerLocation(rightVerticalSplitPane.getSize().width - StandardFrame.CONTEXT_PANEL_WIDTH);
            }
            panelWidth = panelWidth - StandardFrame.CONTEXT_PANEL_WIDTH;
            if (panelWidth < PLAY_LIST_PANEL_WIDTH && changeSize) {
                int diff = PLAY_LIST_PANEL_WIDTH - panelWidth;
                // If window is almost as big as device, move left vertical split pane to the left
                if (getSize().width + diff > GuiUtils.getDeviceWidth()) {
                    leftVerticalSplitPane.setDividerLocation(leftVerticalSplitPane.getLocation().x - diff);
                } else {
                    // Resize window
                    setSize(getSize().width + diff, getSize().height);
                }
            }
        } else if (!show) {
            // Save panel width
            ApplicationState.getInstance().setRightVerticalSplitPaneDividerLocation(getRightVerticalSplitPane().getDividerLocation());
        }
        if (show) {
            rightVerticalSplitPane.setDividerSize(SPLIT_PANE_DEFAULT_DIVIDER_SIZE);
        } else {
            rightVerticalSplitPane.setDividerSize(0);
        }
    }

    @Override
    public void showNavigationPanel(boolean show, boolean changeSize) {
        getNavigationPanel().setVisible(show);
        if (show) {
            int playListWidth = playListPanel.getWidth();

            getLeftVerticalSplitPane().setDividerLocation(ApplicationState.getInstance().getLeftVerticalSplitPaneDividerLocation());
            playListWidth = playListWidth - StandardFrame.NAVIGATION_PANEL_WIDTH;
            if (playListWidth < PLAY_LIST_PANEL_WIDTH && changeSize) {
                int diff = PLAY_LIST_PANEL_WIDTH - playListWidth;
                setSize(getSize().width + diff, getSize().height);
            }
            getLeftVerticalSplitPane().setDividerSize(SPLIT_PANE_DEFAULT_DIVIDER_SIZE);
        } else {
            // Save panel width
            ApplicationState.getInstance().setLeftVerticalSplitPaneDividerLocation(getLeftVerticalSplitPane().getDividerLocation());
            getLeftVerticalSplitPane().setDividerSize(0);
        }
    }

    @Override
    public void showNavigationTable(boolean show) {
        getNavigationPanel().getNavigationTableContainer().setVisible(show);
        if (show) {
            super.setVisible(show);
            getNavigationPanel().getSplitPane().setDividerLocation(ApplicationState.getInstance().getLeftHorizontalSplitPaneDividerLocation());
            getNavigationPanel().getSplitPane().setDividerSize(SPLIT_PANE_DEFAULT_DIVIDER_SIZE);
        } else {
            // Save location
            ApplicationState.getInstance().setLeftHorizontalSplitPaneDividerLocation(getNavigationPanel().getSplitPane().getDividerLocation());
            getNavigationPanel().getSplitPane().setDividerSize(0);
        }
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
    }

}
