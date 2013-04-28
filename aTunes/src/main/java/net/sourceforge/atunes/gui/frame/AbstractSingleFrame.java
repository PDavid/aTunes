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
import net.sourceforge.atunes.gui.views.controls.CustomStatusBar;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.model.ApplicationVersion;
import net.sourceforge.atunes.model.IContextPanelsContainer;
import net.sourceforge.atunes.model.IControlsBuilder;
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
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.model.IUpdateDialog;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

import org.jdesktop.swingx.JXStatusBar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The standard frame
 */
abstract class AbstractSingleFrame extends AbstractCustomFrame implements
		net.sourceforge.atunes.model.IFrame, ApplicationContextAware {

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

	private IOSManager osManager;
	private INavigationHandler navigationHandler;
	private ILookAndFeelManager lookAndFeelManager;
	private IUIHandler uiHandler;

	private ApplicationContext context;

	private IStateUI stateUI;

	private IStateContext stateContext;

	/**
	 * @param stateContext
	 */
	@Override
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
	 * @param stateUI
	 */
	@Override
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	@Override
	public void setApplicationContext(
			final ApplicationContext applicationContext) {
		this.context = applicationContext;
	}

	/**
	 * @return
	 */
	IStateUI getStateUI() {
		return this.stateUI;
	}

	/**
	 * @return
	 */
	protected final ApplicationContext getContext() {
		return this.context;
	}

	@Override
	public void create(final IFrameState frameState) {
		this.frameState = frameState;

		setLocation(getContext().getBean(WindowLocationCalculator.class)
				.getWindowLocation(this.stateUI.getFramePosition()));

		// Set OS-dependent frame configuration
		this.osManager.setupFrame(this);

		getContext().getBean(FrameListenersDecorator.class).decorate(this);

		// Create frame content
		setContent();

		// Add menu bar
		addMenuBar();

		// Apply component orientation
		getContext().getBean(IControlsBuilder.class).applyComponentOrientation(
				this);
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
		this.uiHandler.finish();
		super.dispose();
	}

	/**
	 * This method is called from the OSXAdapter
	 */
	public void about() {
		this.uiHandler.showAboutDialog();
	}

	@Override
	public IMenuBar getAppMenuBar() {
		return this.appMenuBar;
	}

	protected IContextPanelsContainer getContextPanel() {
		if (this.contextPanelContainer == null) {
			this.contextPanelContainer = this.context
					.getBean(IContextPanelsContainer.class);
			this.contextPanelContainer
					.setMinimumSize(getContextPanelMinimumSize());
			this.contextPanelContainer
					.setPreferredSize(getContextPanelPreferredSize());
			this.contextPanelContainer
					.setMaximumSize(getContextPanelMaximumSize());
			if (!this.stateContext.isUseContext()) {
				this.contextPanelContainer.setVisible(false);
			}
		}
		return this.contextPanelContainer;
	}

	/**
	 * Gets the center status bar.
	 * 
	 * @return the center status bar
	 */
	private JLabel getCenterStatusBar() {
		if (this.centerStatusBar == null) {
			this.centerStatusBar = new JLabel(" ");
		}
		return this.centerStatusBar;
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
		return this.navigationTreePanel;
	}

	/**
	 * @param navigationTreePanel
	 */
	public void setNavigationTreePanel(
			final INavigationTreePanel navigationTreePanel) {
		this.navigationTreePanel = navigationTreePanel;
		this.navigationTreePanel
				.setMinimumSize(getNavigationTreePanelMinimumSize());
		this.navigationTreePanel
				.setPreferredSize(getNavigationTreePanelPreferredSize());
		this.navigationTreePanel
				.setMaximumSize(getNavigationTreePanelMaximumSize());
	}

	/**
	 * @return navigation table panel
	 */
	protected INavigationTablePanel getNavigationTablePanel() {
		return this.navigationTablePanel;
	}

	/**
	 * @param navigationTablePanel
	 */
	public void setNavigationTablePanel(
			final INavigationTablePanel navigationTablePanel) {
		this.navigationTablePanel = navigationTablePanel;
		this.navigationTablePanel
				.setMinimumSize(getNavigationTablePanelMinimumSize());
		this.navigationTablePanel
				.setPreferredSize(getNavigationTablePanelPreferredSize());
		this.navigationTablePanel
				.setMaximumSize(getNavigationTablePanelMaximumSize());
	}

	/**
	 * @return player controls panel
	 */
	protected IPlayerControlsPanel getPlayerControls() {
		return this.playerControls;
	}

	/**
	 * @param playerControls
	 */
	public void setPlayerControls(final IPlayerControlsPanel playerControls) {
		this.playerControls = playerControls;
	}

	/**
	 * Get play list panel
	 * 
	 * @return
	 */
	protected IPlayListPanel getPlayListPanel() {
		if (this.playListPanel == null) {
			this.playListPanel = this.context.getBean(IPlayListPanel.class);
			this.playListPanel.setMinimumSize(getPlayListPanelMinimumSize());
			this.playListPanel
					.setPreferredSize(getPlayListPanelPreferredSize());
			this.playListPanel.setMaximumSize(getPlayListPanelMaximumSize());
		}
		return this.playListPanel;
	}

	/**
	 * Gets the progress bar.
	 * 
	 * @return the progress bar
	 */
	@Override
	public JProgressBar getProgressBar() {
		if (this.progressBar == null) {
			this.progressBar = new JProgressBar();
			this.progressBar.setBorder(BorderFactory.createEmptyBorder());
		}
		return this.progressBar;
	}

	/**
	 * Gets the right status bar.
	 * 
	 * @return the right status bar
	 */
	private JLabel getRightStatusBar() {
		if (this.rightStatusBar == null) {
			this.rightStatusBar = new JLabel(" ");
		}
		return this.rightStatusBar;
	}

	/**
	 * Gets the status bar.
	 * 
	 * @return the status bar
	 */
	protected final JXStatusBar getStatusBar() {
		if (this.statusBar == null) {
			this.statusBar = new CustomStatusBar(this.lookAndFeelManager);
			JXStatusBar.Constraint c = new JXStatusBar.Constraint(
					JXStatusBar.Constraint.ResizeBehavior.FILL);
			this.statusBar.add(getCenterStatusBar(), c);
			this.statusBar.add(getRightStatusBar(), c);
			c = new JXStatusBar.Constraint(
					JXStatusBar.Constraint.ResizeBehavior.FIXED);
			this.statusBar.add(getProgressBar(), c);
		}
		return this.statusBar;
	}

	/**
	 * Gets the status bar image label.
	 * 
	 * @return the status device label
	 */
	private JLabel getStatusBarDeviceLabel() {
		if (this.statusBarDeviceLabel == null) {
			this.statusBarDeviceLabel = new JLabel(this.context.getBean(
					"deviceIcon", IIconFactory.class).getIcon(
					this.lookAndFeelManager.getCurrentLookAndFeel()
							.getPaintForSpecialControls()));
		}
		return this.statusBarDeviceLabel;
	}

	private JLabel getStatusBarNewPodcastEntriesLabel() {
		if (this.statusBarNewPodcastEntriesLabel == null) {
			this.statusBarNewPodcastEntriesLabel = new JLabel(this.context
					.getBean("rssSmallIcon", IIconFactory.class).getIcon(
							this.lookAndFeelManager.getCurrentLookAndFeel()
									.getPaintForSpecialControls()));
			this.statusBarNewPodcastEntriesLabel.setToolTipText(I18nUtils
					.getString("NEW_PODCAST_ENTRIES"));
			this.statusBarNewPodcastEntriesLabel
					.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(final MouseEvent e) {
							showNewPodcastFeedEntriesInfo(false);
							AbstractSingleFrame.this.navigationHandler
									.setNavigationView(PodcastNavigationView.class
											.getName());
						}
					});
		}
		return this.statusBarNewPodcastEntriesLabel;
	}

	private JLabel getStatusBarNewVersionLabel() {
		if (this.statusBarNewVersionLabel == null) {
			this.statusBarNewVersionLabel = new JLabel(this.context.getBean(
					"newIcon", IIconFactory.class).getIcon(
					this.lookAndFeelManager.getCurrentLookAndFeel()
							.getPaintForSpecialControls()));
			this.statusBarNewVersionLabel.setToolTipText(I18nUtils
					.getString("NEW_VERSION_AVAILABLE"));
			this.statusBarNewVersionLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					AbstractSingleFrame.this.updateDialog.showDialog();
					showNewVersionInfo(false, null);
				}
			});
		}
		return this.statusBarNewVersionLabel;
	}

	@Override
	public void setCenterStatusBarText(final String text, final String toolTip) {
		getCenterStatusBar().setText(text);
		getCenterStatusBar().setToolTipText(toolTip);
	}

	@Override
	public void setRightStatusBarText(final String text, final String toolTip) {
		getRightStatusBar().setText(text);
		getRightStatusBar().setToolTipText(toolTip);
	}

	@Override
	public void setStatusBarDeviceLabelText(final String text) {
		getStatusBarDeviceLabel().setText(text);
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		// TODO
		/*
		 * We need this because when switching from LTR to RTL component
		 * orientation (or vice versa) the navigation panel width is too big
		 * because of an unknown reason
		 */
		Timer t = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (Toolkit.getDefaultToolkit().getScreenSize().width + 15 < getSize().width) {
					getContext().getBean(WindowSizeCalculator.class)
							.setWindowSize(AbstractSingleFrame.this,
									AbstractSingleFrame.this.stateUI);
				}
			}
		});
		t.setRepeats(false);
		t.start();
	}

	@Override
	public void showStatusBar(final boolean show) {
		getStatusBar().setVisible(show);
	}

	@Override
	public void showDeviceInfo(final boolean show) {
		if (show) {
			JXStatusBar.Constraint c = new JXStatusBar.Constraint(
					JXStatusBar.Constraint.ResizeBehavior.FIXED);
			this.statusBar.add(getStatusBarDeviceLabel(), c);
		} else {
			this.statusBar.remove(getStatusBarDeviceLabel());
		}
		getStatusBarDeviceLabel().setVisible(show);
		this.statusBar.validate();
		this.statusBar.repaint();
	}

	@Override
	public void showNewPodcastFeedEntriesInfo(final boolean show) {
		if (show) {
			if (Arrays.asList(this.statusBar.getComponents()).contains(
					getStatusBarNewPodcastEntriesLabel())) {
				return;
			}
			JXStatusBar.Constraint c = new JXStatusBar.Constraint(
					JXStatusBar.Constraint.ResizeBehavior.FIXED);
			this.statusBar.add(getStatusBarNewPodcastEntriesLabel(), c);
		} else {
			this.statusBar.remove(getStatusBarNewPodcastEntriesLabel());
		}
		getStatusBarNewPodcastEntriesLabel().setVisible(show);
		this.statusBar.validate();
		this.statusBar.repaint();
	}

	@Override
	public void showNewVersionInfo(final boolean show,
			final ApplicationVersion version) {
		if (show && version == null) {
			throw new IllegalArgumentException();
		}
		if (show) {
			if (Arrays.asList(this.statusBar.getComponents()).contains(
					getStatusBarNewVersionLabel())) {
				return;
			}
			JXStatusBar.Constraint c = new JXStatusBar.Constraint(
					JXStatusBar.Constraint.ResizeBehavior.FIXED);
			this.statusBar.add(getStatusBarNewVersionLabel(), c);
			this.updateDialog = this.context.getBean(IUpdateDialog.class);
			this.updateDialog.initialize(version);
		} else {
			this.statusBar.remove(getStatusBarNewVersionLabel());
			this.updateDialog = null;
		}
		getStatusBarNewVersionLabel().setVisible(show);
		this.statusBar.validate();
		this.statusBar.repaint();
	}

	@Override
	public IFrameState getFrameState() {
		return this.frameState;
	}

	protected void storeFrameState() {
		this.stateUI.setFrameState(getClass(), this.frameState);
	}

	protected final void applyVisibility(final boolean show, final String s,
			final Component c, final JSplitPane sp) {
		// Get divider split location before its changed when showing component
		final int location = this.frameState.getSplitPaneDividerPos(s);
		c.setVisible(show);
		// Depending on visibility, set divider size, so if panel is not shown,
		// its divider is hidden too
		sp.setDividerSize(show ? getControlsBuilder().getSplitPaneDividerSize()
				: 0);
		if (show) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					applySplitPaneDividerPosition(
							sp,
							location,
							getDefaultSplitPaneRelativePositions() != null ? getDefaultSplitPaneRelativePositions()
									.get(s) : 0.5);
				}
			});

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (getExtendedState() != Frame.MAXIMIZED_BOTH) {
						// if not maximized, if panel size is less than its
						// minimum size, then change window size
						testComponentSize(sp, sp.getLeftComponent());
					}
				}
			});

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					if (getExtendedState() != Frame.MAXIMIZED_BOTH) {
						// if not maximized, if panel size is less than its
						// minimum size, then change window size
						testComponentSize(sp, sp.getRightComponent());
					}
				}
			});
		}
	}

	protected void testComponentSize(final JSplitPane sp, final Component c) {
		Dimension minimumSize = c.getMinimumSize();
		Dimension actualSize = c.getSize();
		int minimumSizeComponent = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? minimumSize.width
				: minimumSize.height;
		int actualSizeComponent = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? actualSize.width
				: actualSize.height;
		// Don't test component size if actual size is 0, means it's not visible
		// yet
		if (actualSizeComponent > 0
				&& actualSizeComponent < minimumSizeComponent) {
			int newWidth = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? getSize().width
					+ (minimumSizeComponent - actualSizeComponent)
					: getSize().width;
			int newHeight = sp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT ? getSize().height
					: getSize().height
							+ (minimumSizeComponent - actualSizeComponent);
			Logger.info("Changing window size to : ", newWidth, "x", newHeight);
			setSize(newWidth, newHeight);
		}
	}

	protected void applySplitPaneDividerPosition(final JSplitPane splitPane,
			final int location, final double relPos) {
		if (location != 0) {
			splitPane.setDividerLocation(location);
		} else {
			splitPane.setDividerLocation(relPos);
		}
	}

	@Override
	public void applicationStarted(final IFrameState frameState) {
		// Setting window size after frame is visible avoids using workarounds
		// to set extended state in Linux
		// and work both in Windows and Linux
		getContext().getBean(WindowSizeCalculator.class).setWindowSize(
				AbstractSingleFrame.this, this.stateUI);
		setupSplitPaneDividerPosition(frameState);
	}

	@Override
	public final void showProgressBar(final boolean indeterminate,
			final String text) {
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
	 * 
	 * @return
	 */
	protected abstract Dimension getContextPanelMinimumSize();

	/**
	 * Returns preferred size of context panel
	 * 
	 * @return
	 */
	protected abstract Dimension getContextPanelPreferredSize();

	/**
	 * Returns maximum size of context panel
	 * 
	 * @return
	 */
	protected abstract Dimension getContextPanelMaximumSize();

	/**
	 * Returns minimum size for navigation tree panel
	 * 
	 * @return
	 */
	protected abstract Dimension getNavigationTreePanelMinimumSize();

	/**
	 * Returns preferred size for navigation tree panel
	 * 
	 * @return
	 */
	protected abstract Dimension getNavigationTreePanelPreferredSize();

	/**
	 * Returns maximum size for navigation tree panel
	 * 
	 * @return
	 */
	protected abstract Dimension getNavigationTreePanelMaximumSize();

	/**
	 * Returns navigation table panel minimum size
	 * 
	 * @return
	 */
	protected abstract Dimension getNavigationTablePanelMinimumSize();

	/**
	 * Returns navigation table panel preferred size
	 * 
	 * @return
	 */
	protected abstract Dimension getNavigationTablePanelPreferredSize();

	/**
	 * Returns navigation table panel maximum size
	 * 
	 * @return
	 */
	protected abstract Dimension getNavigationTablePanelMaximumSize();

	/**
	 * Returns minimum size of play list panel
	 * 
	 * @return
	 */
	protected abstract Dimension getPlayListPanelMinimumSize();

	/**
	 * Returns preferred size of play list panel
	 * 
	 * @return
	 */
	protected abstract Dimension getPlayListPanelPreferredSize();

	/**
	 * Returns maximum size of play list panel
	 * 
	 * @return
	 */
	protected abstract Dimension getPlayListPanelMaximumSize();

	/**
	 * Returns minimum size of window
	 * 
	 * @return
	 */
	protected abstract Dimension getWindowMinimumSize();

	/**
	 * @param uiHandler
	 */
	public void setUiHandler(final IUIHandler uiHandler) {
		this.uiHandler = uiHandler;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param appMenuBar
	 */
	public void setAppMenuBar(final IMenuBar appMenuBar) {
		this.appMenuBar = appMenuBar;
	}

	@Override
	public void showNavigationTableFilter(final boolean show) {
		getNavigationTablePanel().showNavigationTableFilter(show);
	}

	@Override
	public void showNavigationTree(final boolean show) {
		// Call both panels so must hide or show different controls
		GuiUtils.callInEventDispatchThreadLater(new Runnable() {
			@Override
			public void run() {
				getNavigationTablePanel().showNavigationTree(show);
			}
		});
	}
}
