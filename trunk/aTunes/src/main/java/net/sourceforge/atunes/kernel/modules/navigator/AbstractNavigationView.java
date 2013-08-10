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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.NavigationTableModel;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.decorators.NavigationViewTreeCellRendererCode;
import net.sourceforge.atunes.kernel.actions.ActionWithColorMutableIcon;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.model.ITreeGeneratorFactory;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Common code for navigation views
 * 
 * @author alex
 * 
 */
public abstract class AbstractNavigationView implements INavigationView {

	/**
	 * Scroll pane used for tree
	 */
	private JScrollPane scrollPane;

	/**
	 * Action associated to show this navigation view
	 */
	private ActionWithColorMutableIcon action;

	private INavigationHandler navigationHandler;

	/**
	 * Decorators used in view
	 */
	private List<AbstractTreeCellDecorator<?, ?>> decorators;

	private ITable navigationTable;

	private IStateNavigation stateNavigation;

	private IControlsBuilder controlsBuilder;

	private IBeanFactory beanFactory;

	private JPanel overlayPanel;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return bean factory
	 */
	protected IBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

	/**
	 * @param navigationTable
	 */
	public void setNavigationTable(final ITable navigationTable) {
		this.navigationTable = navigationTable;
	}

	@Override
	public abstract String getTitle();

	@Override
	public abstract IColorMutableImageIcon getIcon();

	@Override
	public abstract String getTooltip();

	@Override
	public abstract NavigationTree getTree();

	/**
	 * Return decorators to be used in view
	 * 
	 * @return
	 */
	protected final List<AbstractTreeCellDecorator<?, ?>> getTreeCellDecorators() {
		return this.decorators;
	}

	/**
	 * @param decorators
	 */
	public void setDecorators(
			final List<AbstractTreeCellDecorator<?, ?>> decorators) {
		this.decorators = decorators;
	}

	@Override
	public abstract JPopupMenu getTreePopupMenu();

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	@Override
	public ITreeGeneratorFactory getTreeGeneratorFactory() {
		return this.beanFactory.getBean(ITreeGeneratorFactory.class);
	}

	@Override
	public JPopupMenu getTablePopupMenu() {
		// By default table popup is the same of tree
		return getTreePopupMenu();
	}

	@Override
	public final JScrollPane getTreeScrollPane() {
		if (this.scrollPane == null) {
			this.scrollPane = this.controlsBuilder.createScrollPane(getTree());
		}
		return this.scrollPane;
	}

	@Override
	public final JPanel getOverlayPanel() {
		if (this.overlayPanel == null) {
			this.overlayPanel = new JPanel(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createLineBorder(GuiUtils
					.getBorderColor()));
			panel.setLayout(new GridBagLayout());
			JTextPane text = getControlsBuilder().createReadOnlyTextPane(
					getOverlayText());
			JScrollPane scroll = getControlsBuilder().createScrollPane(text);
			scroll.setBorder(BorderFactory.createEmptyBorder());
			Action action = getOverlayAction();
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5, 5, 5, 5);
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			panel.add(scroll, c);
			if (action != null) {
				c.gridy = 1;
				c.weightx = 0;
				c.weighty = 0;
				c.fill = GridBagConstraints.NONE;
				c.insets = new Insets(0, 5, 5, 5);
				panel.add(new JButton(action), c);
			}
			c = new GridBagConstraints();
			c.weightx = 0.5;
			c.weighty = 0.5;
			c.insets = new Insets(20, 20, 20, 20);
			c.fill = GridBagConstraints.BOTH;
			this.overlayPanel.add(panel, c);
		}
		return this.overlayPanel;
	}

	/**
	 * Returns the data to be shown in the view. It depends on the view mode
	 * 
	 * @param viewMode
	 * @return
	 */
	protected abstract Map<String, ?> getViewData(ViewMode viewMode);

	@Override
	public final void refreshView(final ViewMode viewMode,
			final String treeFilter) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				// Get selected rows before refresh
				List<IAudioObject> selectedObjects = ((NavigationTableModel) AbstractNavigationView.this.navigationTable
						.getModel())
						.getAudioObjectsAt(AbstractNavigationView.this.navigationTable
								.getSelectedRows());

				// Call to refresh tree
				Logger.debug("Refreshing ", this.getClass().getName());
				refreshTree(viewMode, treeFilter);
				Logger.debug("Refreshing ", this.getClass().getName(), " done");

				// Set the same selected audio objects as before refreshing
				for (IAudioObject audioObject : selectedObjects) {
					int indexOfAudioObject = ((NavigationTableModel) AbstractNavigationView.this.navigationTable
							.getModel()).getAudioObjects().indexOf(audioObject);
					if (indexOfAudioObject != -1) {
						AbstractNavigationView.this.navigationTable
								.addRowSelectionInterval(indexOfAudioObject,
										indexOfAudioObject);
					}
				}

				// Update overlay state to show or hide
				updateOverlayState();
			}
		});
	}

	private void updateOverlayState() {
		// Hide temporarily until adjusting visibility to avoid glitches of
		// overlay layout
		getTree().setVisible(false);
		getOverlayPanel().setVisible(false);

		boolean needsToBeVisible = overlayNeedsToBeVisible();
		if (!getOverlayPanel().isVisible() && needsToBeVisible) {
		}
		getTree().setVisible(!needsToBeVisible);
		getOverlayPanel().setVisible(needsToBeVisible);
		getOverlayPanel().invalidate();
		getOverlayPanel().repaint();
	}

	/**
	 * Refresh tree view
	 * 
	 * @param viewMode
	 * @param treeFilter
	 */
	protected abstract void refreshTree(ViewMode viewMode, String treeFilter);

	@Override
	public abstract boolean isViewModeSupported();

	@Override
	public abstract boolean isUseDefaultNavigatorColumnSet();

	@Override
	public abstract IColumnSet getCustomColumnSet();

	@Override
	public final void updateTreePopupMenuWithTreeSelection(final MouseEvent e) {
		this.beanFactory.getBean(NavigatorActionsStateController.class)
				.updateTreePopupMenuWithTreeSelection(
						getTree().isRowSelected(0),
						getTreePopupMenu().getComponents(),
						getTree().getSelectedNodes());
	}

	@Override
	public final void updateTablePopupMenuWithTableSelection(
			final ITable table, final MouseEvent e) {
		this.beanFactory
				.getBean(NavigatorActionsStateController.class)
				.updateTablePopupMenuWithTableSelection(
						getTree().isRowSelected(0),
						getTablePopupMenu().getComponents(),
						((NavigationTableModel) this.navigationTable.getModel())
								.getAudioObjectsAt(table.getSelectedRows()));
	}

	@Override
	public ViewMode getCurrentViewMode() {
		return this.stateNavigation.getViewMode();
	}

	/**
	 * Return selected objects in this navigation view
	 * 
	 * @return
	 */
	@Override
	public List<IAudioObject> getSelectedAudioObjects() {
		List<IAudioObject> selectedInTable = ((NavigationTableModel) this.navigationTable
				.getModel()).getAudioObjectsAt(this.navigationTable
				.getSelectedRows());
		if (selectedInTable.isEmpty()) {
			List<ITreeNode> nodes = getTree().getSelectedNodes();
			List<IAudioObject> audioObjectsSelected = new ArrayList<IAudioObject>();
			if (!CollectionUtils.isEmpty(nodes)) {
				for (ITreeNode node : nodes) {
					audioObjectsSelected.addAll(this.navigationHandler
							.getAudioObjectsForTreeNode(this.getClass(), node));
				}
			}
			return audioObjectsSelected;
		}
		return selectedInTable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITreeObject<? extends IAudioObject>> getSelectedTreeObjects() {
		// Get objects selected before refreshing tree
		List<ITreeObject<? extends IAudioObject>> objectsSelected = new ArrayList<ITreeObject<? extends IAudioObject>>();
		List<ITreeNode> nodes = getTree().getSelectedNodes();

		// If any node was selected
		if (!CollectionUtils.isEmpty(nodes)) {
			for (ITreeNode node : nodes) {
				Object obj = node.getUserObject();
				if (obj instanceof ITreeObject) {
					objectsSelected
							.add((ITreeObject<? extends IAudioObject>) obj);
				}
			}
		}

		return objectsSelected;
	}

	/**
	 * Returns all TreeObject instances expanded in a tree
	 * 
	 * @param tree
	 * @param root
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected final List<ITreeObject<? extends IAudioObject>> getTreeObjectsExpanded(
			final INavigationTree tree) {
		// Get objects expanded before refreshing tree
		List<ITreeObject<? extends IAudioObject>> objectsExpanded = new ArrayList<ITreeObject<? extends IAudioObject>>();
		List<?> expandedDescendants = tree.getExpandedDescendants();

		if (!CollectionUtils.isEmpty(expandedDescendants)) {
			for (Object obj : expandedDescendants) {
				if (obj instanceof ITreeObject) {
					objectsExpanded
							.add((ITreeObject<? extends IAudioObject>) obj);
				}
			}
		}

		return objectsExpanded;
	}

	@Override
	public final ActionWithColorMutableIcon getActionToShowView() {
		if (this.action == null) {
			this.action = new ActionWithColorMutableIcon(getTitle()) {

				private static final long serialVersionUID = 2895222205333520899L;

				@Override
				protected void executeAction() {
					AbstractNavigationView.this.navigationHandler
							.setNavigationView(AbstractNavigationView.this
									.getClass().getName());
				}

				@Override
				public IColorMutableImageIcon getIcon(
						final ILookAndFeel lookAndFeel) {
					return new IColorMutableImageIcon() {

						@Override
						public ImageIcon getIcon(final Color paint) {
							return AbstractNavigationView.this.getIcon()
									.getIcon(paint);
						}
					};
				}
			};

			this.action.putValue(Action.SHORT_DESCRIPTION, getTitle());
		}
		return this.action;
	}

	/**
	 * Returns tree renderer used
	 * 
	 * @return
	 */
	protected final TreeCellRenderer getTreeRenderer() {
		return this.controlsBuilder
				.getTreeCellRenderer(new NavigationViewTreeCellRendererCode(
						getTreeCellDecorators()));
	}

	@Override
	public String toString() {
		return getTitle();
	}

	@Override
	public void selectAudioObject(final ViewMode currentViewMode,
			final IAudioObject audioObject) {

	}

	@Override
	public void selectArtist(final ViewMode currentViewMode, final String artist) {
	}

	protected final IControlsBuilder getControlsBuilder() {
		return this.controlsBuilder;
	}
}
