/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.gui.NavigationTableModel;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.decorators.NavigationViewTreeCellRendererCode;
import net.sourceforge.atunes.kernel.actions.ActionWithColorMutableIcon;
import net.sourceforge.atunes.kernel.actions.CustomAbstractAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
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

    private ITreeGeneratorFactory treeGeneratorFactory;

    private ILookAndFeelManager lookAndFeelManager;

    private ITable navigationTable;

    private IStateNavigation stateNavigation;

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
	return decorators;
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

    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(
	    final ILookAndFeelManager lookAndFeelManager) {
	this.lookAndFeelManager = lookAndFeelManager;
    }

    /**
     * @param treeGeneratorFactory
     */
    public void setTreeGeneratorFactory(
	    final ITreeGeneratorFactory treeGeneratorFactory) {
	this.treeGeneratorFactory = treeGeneratorFactory;
    }

    @Override
    public ITreeGeneratorFactory getTreeGeneratorFactory() {
	return treeGeneratorFactory;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
	// By default table popup is the same of tree
	return getTreePopupMenu();
    }

    @Override
    public final JScrollPane getTreeScrollPane() {
	if (scrollPane == null) {
	    scrollPane = lookAndFeelManager.getCurrentLookAndFeel()
		    .getTreeScrollPane(getTree());
	}
	return scrollPane;
    }

    /**
     * Returns the data to be shown in the view. It depends on the view mode
     * 
     * @param viewMode
     * @return
     */
    protected abstract Map<String, ?> getViewData(ViewMode viewMode);

    @Override
    public void refreshView(final ViewMode viewMode, final String treeFilter) {
	// Get selected rows before refresh
	List<IAudioObject> selectedObjects = ((NavigationTableModel) navigationTable
		.getModel()).getAudioObjectsAt(navigationTable
		.getSelectedRows());

	// Call to refresh tree
	Logger.debug("Refreshing ", this.getClass().getName());
	refreshTree(viewMode, treeFilter);
	Logger.debug("Refreshing ", this.getClass().getName(), " done");

	// Set the same selected audio objects as before refreshing
	for (IAudioObject audioObject : selectedObjects) {
	    int indexOfAudioObject = ((NavigationTableModel) navigationTable
		    .getModel()).getAudioObjects().indexOf(audioObject);
	    if (indexOfAudioObject != -1) {
		navigationTable.addRowSelectionInterval(indexOfAudioObject,
			indexOfAudioObject);
	    }
	}
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
	List<ITreeNode> nodes = getTree().getSelectedNodes();
	for (Component c : getTreePopupMenu().getComponents()) {
	    updateMenuComponent(getTree().isRowSelected(0), nodes, c);
	}
    }

    @Override
    public final void updateTablePopupMenuWithTableSelection(
	    final ITable table, final MouseEvent e) {
	List<IAudioObject> selection = ((NavigationTableModel) navigationTable
		.getModel()).getAudioObjectsAt(table.getSelectedRows());
	for (Component c : getTablePopupMenu().getComponents()) {
	    updateTableMenuComponent(getTree().isRowSelected(0), selection, c);
	}
    }

    /**
     * @param rootSelected
     * @param selection
     * @param c
     */
    private void updateMenuComponent(final boolean rootSelected,
	    final List<ITreeNode> selection, final Component c) {
	if (c != null) {
	    if (c instanceof JMenu) {
		for (int i = 0; i < ((JMenu) c).getItemCount(); i++) {
		    updateMenuComponent(rootSelected, selection,
			    ((JMenu) c).getItem(i));
		}
	    } else if (c instanceof JMenuItem) {
		updateMenuItem(rootSelected, selection, (JMenuItem) c);
	    }
	}
    }

    /**
     * @param rootSelected
     * @param selection
     * @param c
     */
    private void updateTableMenuComponent(final boolean rootSelected,
	    final List<IAudioObject> selection, final Component c) {
	if (c != null) {
	    if (c instanceof JMenu) {
		for (int i = 0; i < ((JMenu) c).getItemCount(); i++) {
		    updateTableMenuComponent(rootSelected, selection,
			    ((JMenu) c).getItem(i));
		}
	    } else if (c instanceof JMenuItem) {
		updateTableMenuItem(rootSelected, selection, (JMenuItem) c);
	    }
	}
    }

    /**
     * @param rootSelected
     * @param selection
     * @param menuItem
     */
    private void updateMenuItem(final boolean rootSelected,
	    final List<ITreeNode> selection, final JMenuItem menuItem) {
	Action a = menuItem.getAction();
	if (a instanceof CustomAbstractAction) {
	    a.setEnabled(((CustomAbstractAction) a)
		    .isEnabledForNavigationTreeSelection(rootSelected,
			    selection));
	}
    }

    /**
     * @param rootSelected
     * @param selection
     * @param menuItem
     */
    private void updateTableMenuItem(final boolean rootSelected,
	    final List<IAudioObject> selection, final JMenuItem menuItem) {
	Action a = menuItem.getAction();
	if (a instanceof CustomAbstractAction) {
	    a.setEnabled(((CustomAbstractAction) a)
		    .isEnabledForNavigationTableSelection(selection));
	}
    }

    @Override
    public ViewMode getCurrentViewMode() {
	return stateNavigation.getViewMode();
    }

    /**
     * Return selected objects in this navigation view
     * 
     * @return
     */
    @Override
    public List<IAudioObject> getSelectedAudioObjects() {
	List<IAudioObject> selectedInTable = ((NavigationTableModel) navigationTable
		.getModel()).getAudioObjectsAt(navigationTable
		.getSelectedRows());
	if (selectedInTable.isEmpty()) {
	    List<ITreeNode> nodes = getTree().getSelectedNodes();
	    List<IAudioObject> audioObjectsSelected = new ArrayList<IAudioObject>();
	    if (!CollectionUtils.isEmpty(nodes)) {
		for (ITreeNode node : nodes) {
		    audioObjectsSelected.addAll(navigationHandler
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
	if (action == null) {
	    action = new ActionWithColorMutableIcon(getTitle()) {

		private static final long serialVersionUID = 2895222205333520899L;

		@Override
		protected void executeAction() {
		    navigationHandler
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

	    action.putValue(Action.SHORT_DESCRIPTION, getTitle());
	}
	return action;
    }

    /**
     * Returns tree renderer used
     * 
     * @return
     */
    protected final TreeCellRenderer getTreeRenderer() {
	return lookAndFeelManager.getCurrentLookAndFeel()
		.getTreeCellRenderer(
			new NavigationViewTreeCellRendererCode(
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

    /**
     * @return look and feel manager
     */
    protected ILookAndFeelManager getLookAndFeelManager() {
	return lookAndFeelManager;
    }
}
