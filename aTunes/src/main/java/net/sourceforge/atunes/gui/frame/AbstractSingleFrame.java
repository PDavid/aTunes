/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.frame;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.images.DeviceImageIcon;
import net.sourceforge.atunes.gui.images.NewImageIcon;
import net.sourceforge.atunes.gui.images.RssImageIcon;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.dialogs.UpdateDialog;
import net.sourceforge.atunes.gui.views.menus.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTablePanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTreePanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;
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

    private static final int MARGIN = 100;
    
    private FrameState frameState;

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
    private ContextPanel contextPanel;
    private PlayerControlsPanel playerControls;
    private JXStatusBar statusBar;

    /**
     * Used to retrieve JSplitPane divider size of current look and feel
     */
    protected static int defaultDividerSize;
    
    private WindowAdapter fullFrameStateListener;

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

        // Set window location
        Point windowLocation = null;
        if (frameState.getXPosition() >= 0 && frameState.getYPosition() >= 0) {
            windowLocation = new Point(frameState.getXPosition(), frameState.getYPosition());
        }
        
        if (windowLocation == null) {
        	// Setting location centered in screen according to default size
        	Dimension defSize = getDefaultWindowSize();
            windowLocation = new Point((GuiUtils.getDeviceWidth() - defSize.width) / 2, (GuiUtils.getDeviceHeight() - defSize.height) / 2);
        }
        
        if (windowLocation != null) {
            setLocation(windowLocation);
        }

        // Set OS-dependent frame configuration
        OsManager.setupFrame(this);

        // Set window state listener
        addWindowStateListener(getWindowStateListener());
        addWindowFocusListener(getWindowStateListener());
        addComponentListener(new ComponentAdapter() {
			
        	private java.util.Timer timer;
        	
			@Override
			public void componentResized(ComponentEvent event) {				
				saveState(event);
			}
			
			@Override
			public void componentMoved(ComponentEvent event) {
				saveState(event);
			}
			
			private void saveState(final ComponentEvent event) {
				final int width = GuiHandler.getInstance().getWindowSize().width;
				final int height = GuiHandler.getInstance().getWindowSize().height;
				
				if (isVisible() && width != 0 && height != 0) {
					if (timer != null) {
						timer.cancel();
						timer = null;
					}
					timer = new java.util.Timer();
					timer.schedule(new TimerTask() {
						
						@Override
						public void run() {
							FrameState state = ApplicationState.getInstance().getFrameState(GuiHandler.getInstance().getFrame().getClass());
							state.setXPosition(event.getComponent().getX());
							state.setYPosition(event.getComponent().getY());
							state.setMaximized(GuiHandler.getInstance().isMaximized());
							state.setWindowWidth(width);
							state.setWindowHeight(height);
							ApplicationState.getInstance().setFrameState(GuiHandler.getInstance().getFrame().getClass(), state);
						}
					}, 1000);
				}
			}
		});

        // Create frame content
        setContentPane(getContentPanel());

        // Add menu bar
        ApplicationMenuBar bar = getAppMenuBar();
        setJMenuBar(bar);

        // Apply component orientation
        GuiUtils.applyComponentOrientation(this);
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
                        PlayListHandler.getInstance().scrollPlayList(false);
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
            contextPanel.setMinimumSize(getContextPanelMinimumSize());
            contextPanel.setPreferredSize(getContextPanelPreferredSize());
            contextPanel.setMaximumSize(getContextPanelMaximumSize());
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
            navigationTreePanel.setMinimumSize(getNavigationTreePanelMinimumSize());
            navigationTreePanel.setPreferredSize(getNavigationTreePanelPreferredSize());
            navigationTreePanel.setMaximumSize(getNavigationTreePanelMaximumSize());
        }
        return navigationTreePanel;
    }

    @Override
    public NavigationTablePanel getNavigationTablePanel() {
        if (navigationTablePanel == null) {
            navigationTablePanel = new NavigationTablePanel();
            navigationTablePanel.setMinimumSize(getNavigationTablePanelMinimumSize());
            navigationTablePanel.setPreferredSize(getNavigationTablePanelPreferredSize());
            navigationTablePanel.setMaximumSize(getNavigationTablePanelMaximumSize());
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
            playListPanel.setMinimumSize(getPlayListPanelMinimumSize());
            playListPanel.setPreferredSize(getPlayListPanelPreferredSize());
            playListPanel.setMaximumSize(getPlayListPanelMaximumSize());
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
            statusBarDeviceLabel = new JLabel(DeviceImageIcon.getIcon());
        }
        return statusBarDeviceLabel;
    }

    private JLabel getStatusBarNewPodcastEntriesLabel() {
        if (statusBarNewPodcastEntriesLabel == null) {
            statusBarNewPodcastEntriesLabel = new JLabel(RssImageIcon.getSmallIcon());
            statusBarNewPodcastEntriesLabel.setToolTipText(I18nUtils.getString("NEW_PODCAST_ENTRIES"));
            statusBarNewPodcastEntriesLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showNewPodcastFeedEntriesInfo(false);
                    NavigationHandler.getInstance().setNavigationView(PodcastNavigationView.class.getName());
                }
            });
        }
        return statusBarNewPodcastEntriesLabel;
    }

    JLabel getStatusBarNewVersionLabel() {
        if (statusBarNewVersionLabel == null) {
            statusBarNewVersionLabel = new JLabel(NewImageIcon.getIcon());
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
    void setWindowSize() {
        setMinimumSize(getWindowMinimumSize());
        if (ApplicationState.getInstance().getFrameState(GuiHandler.getInstance().getFrame().getClass()).isMaximized()) {
            setWindowSizeMaximized();
        } else {
            Dimension dimension = null;
            if (ApplicationState.getInstance().getFrameState(GuiHandler.getInstance().getFrame().getClass()).getWindowWidth() != 0 && 
                ApplicationState.getInstance().getFrameState(GuiHandler.getInstance().getFrame().getClass()).getWindowHeight() != 0) {
                dimension = new Dimension(ApplicationState.getInstance().getFrameState(GuiHandler.getInstance().getFrame().getClass()).getWindowWidth(), 
                		ApplicationState.getInstance().getFrameState(GuiHandler.getInstance().getFrame().getClass()).getWindowHeight());
            }
            if (dimension == null) {
            	dimension = getDefaultWindowSize();
            }
            if (dimension != null) {
                setSize(dimension);
            }
        }
    }
    
    /**
     * Calculates default window size
     * @return
     */
    private Dimension getDefaultWindowSize() {
        // Set size always according to main device dimension 
        // Avoid create a frame too big: if device width is greater than 2000 pixels then use half width
    	return new Dimension((GuiUtils.getDeviceWidth() > 2000 ? GuiUtils.getDeviceWidth() / 2 : GuiUtils.getDeviceWidth()) - MARGIN, GuiUtils.getDeviceHeight() - MARGIN);
    }
    
    private void setWindowSizeMaximized() {
        Dimension screen = getToolkit().getScreenSize();
        setSize(screen.width - MARGIN, screen.height - MARGIN);
        setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    @Override
    public void showStatusBar(boolean show) {
        getStatusBar().setVisible(show);
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
            updateDialog = new UpdateDialog(version, this.getFrame());
        } else {
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
    
    protected void storeFrameState() {
   		ApplicationState.getInstance().setFrameState(GuiHandler.getInstance().getFrame().getClass(), frameState);
    }

    protected final void applyVisibility(final boolean show, String s, Component c, final JSplitPane sp) {
    	// Get divider split location before its changed when showing component
    	final int location = frameState.getSplitPaneDividerPos(s);
        c.setVisible(show);
        // Depending on visibility, set divider size, so if panel is not shown, its divider is hidden too 
       	sp.setDividerSize(show ? defaultDividerSize : 0);
        if (show) {
        	SwingUtilities.invokeLater(new Runnable() {
        		@Override
        		public void run() {
            		if (location > 0) {
            			applySplitPaneDividerPosition(sp, location, 0);
            		}
        		}
        	});

        	SwingUtilities.invokeLater(new Runnable() {

            	@Override
            	public void run() {
            		if (getExtendedState() != Frame.MAXIMIZED_BOTH) {
            			// if not maximized, if panel size is less than its minimum size, then change window size
     					testComponentSize(sp, sp.getLeftComponent());
            		}
            	}
            });

        	SwingUtilities.invokeLater(new Runnable() {

            	@Override
            	public void run() {
            		if (getExtendedState() != Frame.MAXIMIZED_BOTH) {
            			// if not maximized, if panel size is less than its minimum size, then change window size
            			testComponentSize(sp, sp.getRightComponent());
            		}
            	}
            });
        }
    }

	protected void testComponentSize(JSplitPane sp, Component c) {
		Dimension minimumSize = c.getMinimumSize();
		Dimension actualSize = c.getSize();
		int minimumSizeComponent = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? minimumSize.width : minimumSize.height;
		int actualSizeComponent = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? actualSize.width : actualSize.height;
		if (actualSizeComponent < minimumSizeComponent) {
			int newWidth = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? getSize().width + (minimumSizeComponent - actualSizeComponent) : getSize().width;
			int newHeight = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? getSize().height : getSize().height + (minimumSizeComponent - actualSizeComponent);
			getLogger().info(LogCategories.DESKTOP, "Changing window size to : ", newWidth , "x", newHeight);
			setSize(newWidth, newHeight);
		}
	}
	

    protected void applySplitPaneDividerPosition(JSplitPane splitPane, int location, double relPos) {
        if (location != 0) {
            splitPane.setDividerLocation(location);
        } else {
            splitPane.setDividerLocation(relPos);
        }
    }
    
    @Override
    public void applicationStarted(FrameState frameState) {
    	// Setting window size after frame is visible avoids using workarounds to set extended state in Linux
    	// and work both in Windows and Linux
        setWindowSize();
    	setupSplitPaneDividerPosition(frameState);
    }
    
    /**
     * Returns minimum size of context panel
     * @return
     */
    protected abstract Dimension getContextPanelMinimumSize();
    
    /**
     * Returns preferred size of context panel
     * @return
     */
    protected abstract Dimension getContextPanelPreferredSize();

    /**
     * Returns maximum size of context panel
     * @return
     */
    protected abstract Dimension getContextPanelMaximumSize();

    /**
     * Returns minimum size for navigation tree panel
     * @return
     */
    protected abstract Dimension getNavigationTreePanelMinimumSize();

    /**
     * Returns preferred size for navigation tree panel
     * @return
     */
    protected abstract Dimension getNavigationTreePanelPreferredSize();

    /**
     * Returns maximum size for navigation tree panel
     * @return
     */
    protected abstract Dimension getNavigationTreePanelMaximumSize();

    /**
     * Returns navigation table panel minimum size
     * @return
     */
    protected abstract Dimension getNavigationTablePanelMinimumSize();

    /**
     * Returns navigation table panel preferred size
     * @return
     */
    protected abstract Dimension getNavigationTablePanelPreferredSize();

    /**
     * Returns navigation table panel maximum size
     * @return
     */
    protected abstract Dimension getNavigationTablePanelMaximumSize();

    /**
     * Returns minimum size of play list panel
     * @return
     */
    protected abstract Dimension getPlayListPanelMinimumSize();

    /**
     * Returns preferred size of play list panel
     * @return
     */
    protected abstract Dimension getPlayListPanelPreferredSize();

    /**
     * Returns maximum size of play list panel
     * @return
     */
    protected abstract Dimension getPlayListPanelMaximumSize();

    /**
     * Returns minimum size of properties panel
     * @return
     */
    protected abstract Dimension getPropertiesPanelMinimumSize();

    /**
     * Returns preferred size of properties panel
     * @return
     */
    protected abstract Dimension getPropertiesPanelPreferredSize();

    /**
     * Returns maximum size of properties panel
     * @return
     */
    protected abstract Dimension getPropertiesPanelMaximumSize();

    /**
     * Returns minimum size of window
     * @return
     */
    protected abstract Dimension getWindowMinimumSize();
}
