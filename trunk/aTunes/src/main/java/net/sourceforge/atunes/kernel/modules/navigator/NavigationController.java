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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.sourceforge.atunes.gui.AbstractCommonColumnModel;
import net.sourceforge.atunes.gui.ColumnRenderers;
import net.sourceforge.atunes.gui.NavigationTableColumnModel;
import net.sourceforge.atunes.gui.NavigationTableModel;
import net.sourceforge.atunes.gui.views.controls.ColumnSetPopupMenu;
import net.sourceforge.atunes.gui.views.controls.ColumnSetRowSorter;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.actions.ShowAlbumsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowArtistsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowFoldersInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowGenresInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowYearsInNavigatorAction;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IController;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationTreePanel;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Controller to manage
 * 
 * @author alex
 * 
 */
public final class NavigationController implements IAudioFilesRemovedListener,
	IController, ApplicationContextAware {

    private INavigationTreePanel navigationTreePanel;

    /** The current extended tool tip content. */
    private volatile Object currentExtendedToolTipContent;

    /** The album tool tip. */
    private ExtendedToolTip extendedToolTip;

    /** The popupmenu caller. */
    private JComponent popupMenuCaller;

    private ColumnSetPopupMenu columnSetPopupMenu;

    /** The timer. */
    private Timer timer;

    private IColumnSet navigatorColumnSet;

    private INavigationHandler navigationHandler;

    private ILookAndFeelManager lookAndFeelManager;

    private IFilterHandler filterHandler;

    private ITable navigationTable;

    private IRepositoryHandler repositoryHandler;

    private IFilter navigationTreeFilter;

    private IStateNavigation stateNavigation;

    private IBeanFactory beanFactory;

    private IUnknownObjectChecker unknownObjectChecker;

    private IPlayListHandler playListHandler;

    /**
     * @param playListHandler
     */
    public void setPlayListHandler(final IPlayListHandler playListHandler) {
	this.playListHandler = playListHandler;
    }

    /**
     * @param unknownObjectChecker
     */
    public void setUnknownObjectChecker(
	    final IUnknownObjectChecker unknownObjectChecker) {
	this.unknownObjectChecker = unknownObjectChecker;
    }

    /**
     * @param beanFactory
     */
    public void setBeanFactory(final IBeanFactory beanFactory) {
	this.beanFactory = beanFactory;
    }

    /**
     * @param stateNavigation
     */
    public void setStateNavigation(final IStateNavigation stateNavigation) {
	this.stateNavigation = stateNavigation;
    }

    @Override
    public void setApplicationContext(
	    final ApplicationContext applicationContext) {
	timer = new Timer(0,
		applicationContext.getBean(ExtendedToolTipActionListener.class));
    }

    /**
     * @param navigationTreeFilter
     */
    public void setNavigationTreeFilter(final IFilter navigationTreeFilter) {
	this.navigationTreeFilter = navigationTreeFilter;
    }

    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
	this.repositoryHandler = repositoryHandler;
    }

    /**
     * @param navigationTreePanel
     */
    public void setNavigationTreePanel(
	    final INavigationTreePanel navigationTreePanel) {
	this.navigationTreePanel = navigationTreePanel;
    }

    /**
     * @param navigationTable
     */
    public void setNavigationTable(final ITable navigationTable) {
	this.navigationTable = navigationTable;
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
     * @param filterHandler
     */
    public void setFilterHandler(final IFilterHandler filterHandler) {
	this.filterHandler = filterHandler;
    }

    /**
     * @param navigatorColumnSet
     */
    public void setNavigatorColumnSet(final IColumnSet navigatorColumnSet) {
	this.navigatorColumnSet = navigatorColumnSet;
    }

    /**
     * Initializes controller
     */
    public void initialize() {
	addBindings();
	repositoryHandler.addAudioFilesRemovedListener(this);
    }

    /**
     * @return navigation tree panel
     */
    protected INavigationTreePanel getNavigationTreePanel() {
	return navigationTreePanel;
    }

    @Override
    public void addBindings() {
	NavigationTableModel model = new NavigationTableModel();
	navigationTable.setModel(model);
	AbstractCommonColumnModel columnModel = beanFactory
		.getBean(NavigationTableColumnModel.class);
	columnModel.setModel(model);
	navigationTable.setColumnModel(columnModel);
	ColumnRenderers.addRenderers(navigationTable.getSwingComponent(),
		columnModel, lookAndFeelManager.getCurrentLookAndFeel());

	new ColumnSetRowSorter(navigationTable.getSwingComponent(), model,
		columnModel);

	// Bind column set popup menu
	columnSetPopupMenu = new ColumnSetPopupMenu(
		navigationTable.getSwingComponent(), columnModel);

	// Add tree selection listeners to all views
	for (INavigationView view : navigationHandler.getNavigationViews()) {
	    view.getTree().getSwingComponent()
		    .addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(final TreeSelectionEvent e) {
			    updateTableContent((NavigationTree) e.getSource());
			}
		    });
	}

	// Add tree mouse listeners to all views
	// Add tree tool tip listener to all views
	NavigationTreeMouseListener treeMouseListener = new NavigationTreeMouseListener(
		this, navigationTable, stateNavigation, navigationHandler,
		playListHandler);
	NavigationTreeToolTipListener tooltipListener = new NavigationTreeToolTipListener(
		this, stateNavigation, navigationHandler);
	for (INavigationView view : navigationHandler.getNavigationViews()) {
	    view.getTree().addMouseListener(treeMouseListener);
	    view.getTree().addMouseListener(tooltipListener);
	    view.getTree().addMouseMotionListener(tooltipListener);
	    view.getTreeScrollPane().addMouseWheelListener(tooltipListener);
	}

	navigationTable.addMouseListener(new NavigationTableMouseListener(this,
		navigationTable, navigationHandler, playListHandler));
    }

    @Override
    public void addStateBindings() {
    }

    /**
     * @return the timer
     */
    public Timer getToolTipTimer() {
	return timer;
    }

    /**
     * Gets the album tool tip.
     * 
     * @return the album tool tip
     */
    public ExtendedToolTip getExtendedToolTip() {
	if (extendedToolTip == null) {
	    JDialog.setDefaultLookAndFeelDecorated(false);
	    extendedToolTip = new ExtendedToolTip(
		    lookAndFeelManager.getCurrentLookAndFeel(),
		    unknownObjectChecker);
	    JDialog.setDefaultLookAndFeelDecorated(lookAndFeelManager
		    .getCurrentLookAndFeel().isDialogUndecorated());
	}
	return extendedToolTip;
    }

    /**
     * Get files of all selected elements in navigator.
     * 
     * @return the files selected in navigator
     */
    public List<IAudioObject> getFilesSelectedInNavigator() {
	List<IAudioObject> files = new ArrayList<IAudioObject>();
	if (getPopupMenuCaller() instanceof JTable) {
	    int[] rows = navigationTable.getSelectedRows();
	    files.addAll(((NavigationTableModel) navigationTable.getModel())
		    .getAudioObjectsAt(rows));
	} else if (getPopupMenuCaller() instanceof NavigationTree) {
	    List<ITreeNode> nodes = navigationHandler.getCurrentView()
		    .getTree().getSelectedNodes();
	    for (ITreeNode node : nodes) {
		if (node.getUserObject() instanceof ITreeObject) {
		    files.addAll(getAudioObjectsForTreeNode(navigationHandler
			    .getCurrentView().getClass(), node));
		}
	    }
	}
	return files;
    }

    /**
     * Gets the last album tool tip content.
     * 
     * @return the last album tool tip content
     */
    public Object getCurrentExtendedToolTipContent() {
	return currentExtendedToolTipContent;
    }

    /**
     * Gets the audio object in navigation table.
     * 
     * @param row
     *            the row
     * 
     * @return the song in navigation table
     */
    public IAudioObject getAudioObjectInNavigationTable(final int row) {
	return ((NavigationTableModel) navigationTable.getModel())
		.getAudioObjectAt(row);
    }

    /**
     * Returns audio objects selected by the given node in the given navigation
     * view
     * 
     * @param navigationViewClass
     * @param node
     * @return
     */
    public List<? extends IAudioObject> getAudioObjectsForTreeNode(
	    final Class<? extends INavigationView> navigationViewClass,
	    final ITreeNode node) {
	List<? extends IAudioObject> audioObjects = navigationHandler.getView(
		navigationViewClass).getAudioObjectForTreeNode(node,
		stateNavigation.getViewMode(),
		filterHandler.getFilterText(navigationTreeFilter));

	IColumnSet columnSet = navigationHandler.getCurrentView()
		.getCustomColumnSet();
	if (columnSet == null) {
	    columnSet = navigatorColumnSet;
	}

	IColumn<?> columnSorted = columnSet.getSortedColumn();
	if (columnSorted != null) {
	    net.sourceforge.atunes.utils.Timer t = new net.sourceforge.atunes.utils.Timer();
	    t.start();
	    Collections.sort(audioObjects, columnSorted.getComparator(false));
	    Logger.debug("Navigation table sort: ", t.stop(), " seconds");
	}
	return audioObjects;
    }

    @Override
    public void notifyReload() {
    }

    /**
     * Refresh table.
     */
    public void refreshTable() {
	((NavigationTableModel) navigationTable.getModel())
		.refresh(TableModelEvent.UPDATE);
    }

    /**
     * Sets the current album tool tip content.
     * 
     * @param currentAlbumToolTipContent
     *            the new current album tool tip content
     */
    public void setCurrentExtendedToolTipContent(
	    final Object currentAlbumToolTipContent) {
	this.currentExtendedToolTipContent = currentAlbumToolTipContent;
	getExtendedToolTip().setSizeToFitImage(currentAlbumToolTipContent);
    }

    /**
     * Sets the navigation view and optionally saves navigation view
     * 
     * @param view
     *            the new navigation view
     * 
     * @param saveNavigationView
     */
    public void setNavigationView(final String view,
	    final boolean saveNavigationView) {
	Class<? extends INavigationView> navigationView = navigationHandler
		.getViewByName(view);
	if (navigationView == null) {
	    navigationView = RepositoryNavigationView.class;
	}

	if (saveNavigationView) {
	    stateNavigation.setNavigationView(navigationView.getName());
	}

	getNavigationTreePanel().showNavigationView(
		navigationHandler.getView(navigationView));

	boolean viewModeSupported = navigationHandler.getView(navigationView)
		.isViewModeSupported();
	beanFactory.getBean(ShowAlbumsInNavigatorAction.class).setEnabled(
		viewModeSupported);
	beanFactory.getBean(ShowArtistsInNavigatorAction.class).setEnabled(
		viewModeSupported);
	beanFactory.getBean(ShowFoldersInNavigatorAction.class).setEnabled(
		viewModeSupported);
	beanFactory.getBean(ShowGenresInNavigatorAction.class).setEnabled(
		viewModeSupported);
	beanFactory.getBean(ShowYearsInNavigatorAction.class).setEnabled(
		viewModeSupported);

	// Change column set
	boolean useDefaultNavigatorColumns = navigationHandler.getView(
		navigationView).isUseDefaultNavigatorColumnSet();
	IColumnSet columnSet = null;
	if (useDefaultNavigatorColumns) {
	    columnSet = navigatorColumnSet;
	} else {
	    columnSet = navigationHandler.getView(navigationView)
		    .getCustomColumnSet();
	}

	((NavigationTableModel) navigationTable.getModel())
		.setColumnSet(columnSet);
	((NavigationTableColumnModel) navigationTable.getColumnModel())
		.setColumnSet(columnSet);

	navigationHandler.refreshCurrentView();

	// Allow arrange columns if view uses default column set
	columnSetPopupMenu.enableArrangeColumns(useDefaultNavigatorColumns);

	showNodesContentInNavigationTable(navigationView, navigationHandler
		.getCurrentView().getTree());
    }

    /**
     * @param navigationView
     * @param tree
     */
    private void showNodesContentInNavigationTable(
	    final Class<? extends INavigationView> navigationView,
	    final INavigationTree tree) {
	List<ITreeNode> nodes = tree.getSelectedNodes();
	if (!CollectionUtils.isEmpty(nodes)) {
	    List<IAudioObject> songs = new ArrayList<IAudioObject>();
	    for (ITreeNode node : nodes) {
		songs.addAll(getAudioObjectsForTreeNode(navigationView, node));
	    }
	    ((NavigationTableModel) navigationTable.getModel()).setSongs(songs);
	}
    }

    /**
     * Sets the navigation view and saves state
     * 
     * @param view
     */
    public void setNavigationView(final String view) {
	setNavigationView(view, true);
    }

    /**
     * Updates table contents when user selects a tree node or the table filter
     * changes
     * 
     * @param tree
     *            the tree
     */
    protected void updateTableContent(final INavigationTree tree) {
	// If navigation table is not shown then don't update it
	if (!stateNavigation.isShowNavigationTable()) {
	    return;
	}

	// Avoid events when changes on a tree different than the one which is
	// visible
	if (tree != navigationHandler.getCurrentView().getTree()) {
	    return;
	}

	showNodesContentInNavigationTable(navigationHandler.getCurrentView()
		.getClass(), navigationHandler.getCurrentView().getTree());
    }

    /**
     * Open search dialog.
     * 
     * @param dialog
     *            the dialog
     * @param setAsDefaultVisible
     *            the set as default visible
     * 
     * @return the search
     */
    public ISearch openSearchDialog(final ISearchDialog dialog,
	    final boolean setAsDefaultVisible) {
	dialog.setSetAsDefaultVisible(setAsDefaultVisible);
	dialog.showDialog();
	return dialog.getResult();
    }

    protected JComponent getPopupMenuCaller() {
	return popupMenuCaller;
    }

    /**
     * @param popupMenuCaller
     *            the popupMenuCaller to set
     */
    public void setPopupMenuCaller(final JComponent popupMenuCaller) {
	this.popupMenuCaller = popupMenuCaller;
    }

    @Override
    public void audioFilesRemoved(final List<ILocalAudioObject> audioFiles) {
	navigationHandler.refreshCurrentView();
    }

    /**
     * Returns true if last action has been performed in tree
     * 
     * @return
     */
    public boolean isActionOverTree() {
	return getPopupMenuCaller() instanceof JTree;
    }
}
