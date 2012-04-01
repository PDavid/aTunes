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

package net.sourceforge.atunes.gui.frame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomFrame;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.model.ApplicationVersion;
import net.sourceforge.atunes.model.IContextPanelsContainer;
import net.sourceforge.atunes.model.IFrameState;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IMenuBar;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTablePanel;
import net.sourceforge.atunes.model.INavigationTreePanel;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListPanel;
import net.sourceforge.atunes.model.IPlayerControlsPanel;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.model.IUpdateDialog;
import net.sourceforge.atunes.model.IWindowListener;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

import org.jdesktop.swingx.JXStatusBar;
import org.springframework.context.ApplicationContext;

/**
 * The standard frame
 */
abstract class AbstractSingleFrame extends AbstractCustomFrame implements net.sourceforge.atunes.model.IFrame {

	private static final long serialVersionUID = 5221630053432272337L;
	
    private IFrameState frameState;

    private INavigationTreePanel navigationTreePanel;
    private INavigationTablePanel navigationTablePanel;
    private JLabel centerStatusBar;
    private JLabel rightStatusBar;
    private JLabel statusBarDeviceLabel;
    private JLabel statusBarNewPodcastEntriesLabel;
    private JLabel statusBarNewVersionLabel;
    private IUpdateDialog updateDialog;
    private JProgressBar progressBar;
    private IMenuBar appMenuBar;
    private IPlayListPanel playListPanel;
    private IContextPanelsContainer contextPanelContainer;
    private IPlayerControlsPanel playerControls;
    private JXStatusBar statusBar;

    private IState state;
    private IOSManager osManager;
    private INavigationHandler navigationHandler;
    private ILookAndFeelManager lookAndFeelManager;
    private IUIHandler uiHandler;
    
    private ITaskService taskService;

    private ApplicationContext context;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
    	this.context = applicationContext;
    }

    /**
     * @return
     */
    IState getAppState() {
    	return state;
    }
    
    /**
     * @return
     */
    protected final ApplicationContext getContext() {
		return context;
	}
    
    @Override
    public void create(IFrameState frameState) {
        this.frameState = frameState;

        setLocation(getContext().getBean(WindowLocationCalculator.class).getWindowLocation(frameState));

        // Set OS-dependent frame configuration
        osManager.setupFrame(this);

        FrameListenersDecorator decorator = new FrameListenersDecorator(this, taskService, state, context.getBeansOfType(IWindowListener.class).values());
        decorator.decorate();

        // Create frame content
        setContent();

        // Add menu bar
        addMenuBar();

        // Apply component orientation
        GuiUtils.applyComponentOrientation(this);
    }

	/**
	 * Adds menu bar
	 */
	private void addMenuBar() {
		IMenuBar bar = getAppMenuBar();
        bar.initialize();
        setJMenuBar(bar.getSwingComponent());
	}

    protected abstract void setupSplitPaneDividerPosition(IFrameState frameState);

    @Override
    public void dispose() {
    	uiHandler.finish();
        super.dispose();
    }

    /**
     * This method is called from the OSXAdapter
     */
    public void about() {
    	uiHandler.showAboutDialog();
    }

    @Override
    public IMenuBar getAppMenuBar() {
        return appMenuBar;
    }

    protected IContextPanelsContainer getContextPanel() {
        if (contextPanelContainer == null) {
            contextPanelContainer = context.getBean(IContextPanelsContainer.class);
            contextPanelContainer.setMinimumSize(getContextPanelMinimumSize());
            contextPanelContainer.setPreferredSize(getContextPanelPreferredSize());
            contextPanelContainer.setMaximumSize(getContextPanelMaximumSize());
            if (!state.isUseContext()) {
                contextPanelContainer.setVisible(false);
            }
        }
        return contextPanelContainer;
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
    protected abstract void setContent();

    @Override
    public JFrame getFrame() {
        return this;
    }

    /**
     * @return navigation tree panel
     */
    protected INavigationTreePanel getNavigationTreePanel() {
        return navigationTreePanel;
    }
    
    /**
     * @param navigationTreePanel
     */
    public void setNavigationTreePanel(INavigationTreePanel navigationTreePanel) {
		this.navigationTreePanel = navigationTreePanel;
		this.navigationTreePanel.setMinimumSize(getNavigationTreePanelMinimumSize());
		this.navigationTreePanel.setPreferredSize(getNavigationTreePanelPreferredSize());
		this.navigationTreePanel.setMaximumSize(getNavigationTreePanelMaximumSize());
	}

    /**
     * @return navigation table panel
     */
    protected INavigationTablePanel getNavigationTablePanel() {
        return navigationTablePanel;
    }
    
    /**
     * @param navigationTablePanel
     */
    public void setNavigationTablePanel(INavigationTablePanel navigationTablePanel) {
		this.navigationTablePanel = navigationTablePanel;
		this.navigationTablePanel.setMinimumSize(getNavigationTablePanelMinimumSize());
		this.navigationTablePanel.setPreferredSize(getNavigationTablePanelPreferredSize());
		this.navigationTablePanel.setMaximumSize(getNavigationTablePanelMaximumSize());
	}

    /**
     * @return player controls panel
     */
    protected IPlayerControlsPanel getPlayerControls() {
        return playerControls;
    }
    
    /**
     * @param playerControls
     */
    public void setPlayerControls(IPlayerControlsPanel playerControls) {
		this.playerControls = playerControls;
	}

    /**
     * Get play list panel
     * @return
     */
    protected IPlayListPanel getPlayListPanel() {
        if (playListPanel == null) {
            playListPanel = context.getBean(IPlayListPanel.class);
            playListPanel.setMinimumSize(getPlayListPanelMinimumSize());
            playListPanel.setPreferredSize(getPlayListPanelPreferredSize());
            playListPanel.setMaximumSize(getPlayListPanelMaximumSize());
        }
        return playListPanel;
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
            statusBar.add(getCenterStatusBar(), c);
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
            statusBarDeviceLabel = new JLabel(context.getBean("deviceIcon", IIconFactory.class).getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls()));
        }
        return statusBarDeviceLabel;
    }

    private JLabel getStatusBarNewPodcastEntriesLabel() {
        if (statusBarNewPodcastEntriesLabel == null) {
            statusBarNewPodcastEntriesLabel = new JLabel(context.getBean("rssSmallIcon", IIconFactory.class).getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls()));
            statusBarNewPodcastEntriesLabel.setToolTipText(I18nUtils.getString("NEW_PODCAST_ENTRIES"));
            statusBarNewPodcastEntriesLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showNewPodcastFeedEntriesInfo(false);
                    navigationHandler.setNavigationView(PodcastNavigationView.class.getName());
                }
            });
        }
        return statusBarNewPodcastEntriesLabel;
    }

    private JLabel getStatusBarNewVersionLabel() {
        if (statusBarNewVersionLabel == null) {
            statusBarNewVersionLabel = new JLabel(context.getBean("newIcon", IIconFactory.class).getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls()));
            statusBarNewVersionLabel.setToolTipText(I18nUtils.getString("NEW_VERSION_AVAILABLE"));
            statusBarNewVersionLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    updateDialog.showDialog();
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
                	getContext().getBean(WindowSizeCalculator.class).setWindowSize(AbstractSingleFrame.this, state);
                }
            }
        });
        t.setRepeats(false);
        t.start();
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
            updateDialog = context.getBean(IUpdateDialog.class);
            updateDialog.initialize(version);
        } else {
            statusBar.remove(getStatusBarNewVersionLabel());
            updateDialog = null;
        }
        getStatusBarNewVersionLabel().setVisible(show);
        statusBar.validate();
        statusBar.repaint();
    }

    @Override
    public IFrameState getFrameState() {
        return frameState;
    }
    
    protected void storeFrameState() {
   		state.setFrameState(getClass(), frameState);
    }

    protected final void applyVisibility(final boolean show, final String s, Component c, final JSplitPane sp) {
    	// Get divider split location before its changed when showing component
    	final int location = frameState.getSplitPaneDividerPos(s);
        c.setVisible(show);
        // Depending on visibility, set divider size, so if panel is not shown, its divider is hidden too 
       	sp.setDividerSize(show ? lookAndFeelManager.getCurrentLookAndFeel().getSplitPaneDividerSize() : 0);
        if (show) {
        	SwingUtilities.invokeLater(new Runnable() {
        		@Override
        		public void run() {
            		applySplitPaneDividerPosition(sp, location, getDefaultSplitPaneRelativePositions() != null ? 
            								getDefaultSplitPaneRelativePositions().get(s) : 0.5);
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
		// Don't test component size if actual size is 0, means it's not visible yet
		if (actualSizeComponent > 0 && actualSizeComponent < minimumSizeComponent) {
			int newWidth = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? getSize().width + (minimumSizeComponent - actualSizeComponent) : getSize().width;
			int newHeight = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? getSize().height : getSize().height + (minimumSizeComponent - actualSizeComponent);
			Logger.info("Changing window size to : ", newWidth , "x", newHeight);
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
    public void applicationStarted(IFrameState frameState) {
    	// Setting window size after frame is visible avoids using workarounds to set extended state in Linux
    	// and work both in Windows and Linux
    	getContext().getBean(WindowSizeCalculator.class).setWindowSize(AbstractSingleFrame.this, state);
    	setupSplitPaneDividerPosition(frameState);
    }
    
    @Override
    public final void showProgressBar(boolean indeterminate, String text) {
        if (getProgressBar() != null) {
            getProgressBar().setVisible(true);
            getProgressBar().setIndeterminate(indeterminate);
            if (!indeterminate) {
                getProgressBar().setMinimum(0);
                getProgressBar().setValue(0);
            }
            getProgressBar().setToolTipText(text);
        }
    }

    @Override
    public final void hideProgressBar() {
        if (getProgressBar() != null) {
            getProgressBar().setVisible(false);
        }
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
     * Returns minimum size of window
     * @return
     */
    protected abstract Dimension getWindowMinimumSize();
    
    /**
     * @param taskService
     */
    public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}
    
    /**
     * @param uiHandler
     */
    public void setUiHandler(IUIHandler uiHandler) {
		this.uiHandler = uiHandler;
	}
    
    @Override
    public void setState(IState state) {
    	this.state = state;
    }
    
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * @return
     */
    protected ILookAndFeelManager getLookAndFeelManager() {
		return lookAndFeelManager;
	}
    
    /**
     * @param appMenuBar
     */
    public void setAppMenuBar(IMenuBar appMenuBar) {
		this.appMenuBar = appMenuBar;
	}
}
