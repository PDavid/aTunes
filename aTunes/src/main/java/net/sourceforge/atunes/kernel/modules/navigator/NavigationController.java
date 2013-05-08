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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import net.sourceforge.atunes.gui.ColumnDecorator;
import net.sourceforge.atunes.gui.NavigationTableColumnModel;
import net.sourceforge.atunes.gui.NavigationTableModel;
import net.sourceforge.atunes.gui.views.controls.ColumnSetPopupMenu;
import net.sourceforge.atunes.gui.views.controls.ColumnSetRowSorter;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
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
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTablePanel;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationTreePanel;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Controller to manage
 * 
 * @author alex
 * 
 */
public final class NavigationController implements IAudioFilesRemovedListener,
		IController {

	private INavigationTreePanel navigationTreePanel;

	private INavigationTablePanel navigationTablePanel;

	/** The popupmenu caller. */
	private JComponent popupMenuCaller;

	private ColumnSetPopupMenu columnSetPopupMenu;

	/** The timer. */
	private Timer timer;

	private IColumnSet navigatorColumnSet;

	private INavigationHandler navigationHandler;

	private IFilter navigationTableFilter;

	private IFilterHandler filterHandler;

	private ITable navigationTable;

	private NavigationTableColumnModel navigationTableColumnModel;

	private NavigationTableModel navigationTableModel;

	private IRepositoryHandler repositoryHandler;

	private IFilter navigationTreeFilter;

	private IStateNavigation stateNavigation;

	private IBeanFactory beanFactory;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param navigationTablePanel
	 */
	public void setNavigationTablePanel(
			final INavigationTablePanel navigationTablePanel) {
		this.navigationTablePanel = navigationTablePanel;
	}

	/**
	 * @param navigationTableFilter
	 */
	public void setNavigationTableFilter(final IFilter navigationTableFilter) {
		this.navigationTableFilter = navigationTableFilter;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param navigationTableColumnModel
	 */
	public void setNavigationTableColumnModel(
			final NavigationTableColumnModel navigationTableColumnModel) {
		this.navigationTableColumnModel = navigationTableColumnModel;
	}

	/**
	 * @param navigationTableModel
	 */
	public void setNavigationTableModel(
			final NavigationTableModel navigationTableModel) {
		this.navigationTableModel = navigationTableModel;
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
		this.repositoryHandler.addAudioFilesRemovedListener(this);
	}

	/**
	 * @return navigation tree panel
	 */
	protected INavigationTreePanel getNavigationTreePanel() {
		return this.navigationTreePanel;
	}

	@Override
	public void addBindings() {
		this.beanFactory.getBean("navigatorTableColumnDecorator",
				ColumnDecorator.class).decorate(true);

		new ColumnSetRowSorter(this.navigationTable.getSwingComponent(),
				this.navigationTableModel, this.navigationTableColumnModel);

		// Bind column set popup menu
		this.columnSetPopupMenu = (ColumnSetPopupMenu) this.controlsBuilder
				.createColumnSetPopupMenu(
						this.navigationTable.getSwingComponent(),
						this.navigationTableColumnModel,
						this.navigationTableModel);

		// Add tree selection listeners to all views
		for (INavigationView view : this.navigationHandler.getNavigationViews()) {
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
		NavigationTreeMouseListener treeMouseListener = this.beanFactory
				.getBean(NavigationTreeMouseListener.class);
		NavigationTreeToolTipListener tooltipListener = this.beanFactory
				.getBean(NavigationTreeToolTipListener.class);
		for (INavigationView view : this.navigationHandler.getNavigationViews()) {
			view.getTree().addMouseListener(treeMouseListener);
			view.getTree().addMouseListener(tooltipListener);
			view.getTree().addMouseMotionListener(tooltipListener);
			view.getTreeScrollPane().addMouseWheelListener(tooltipListener);
		}

		this.navigationTable.addMouseListener(this.beanFactory
				.getBean(NavigationTableMouseListener.class));
	}

	@Override
	public void addStateBindings() {
	}

	/**
	 * @return the timer
	 */
	Timer getToolTipTimer() {
		if (this.timer == null) {
			this.timer = new Timer(0,
					this.beanFactory
							.getBean(ExtendedToolTipActionListener.class));
		}
		return this.timer;
	}

	/**
	 * Get files of all selected elements in navigator.
	 * 
	 * @return the files selected in navigator
	 */
	public List<IAudioObject> getFilesSelectedInNavigator() {
		List<IAudioObject> files = new ArrayList<IAudioObject>();
		if (getPopupMenuCaller() instanceof JTable) {
			int[] rows = this.navigationTable.getSelectedRows();
			files.addAll(((NavigationTableModel) this.navigationTable
					.getModel()).getAudioObjectsAt(rows));
		} else if (getPopupMenuCaller() instanceof NavigationTree) {
			List<ITreeNode> nodes = this.navigationHandler.getCurrentView()
					.getTree().getSelectedNodes();
			for (ITreeNode node : nodes) {
				if (node.getUserObject() instanceof ITreeObject) {
					files.addAll(getAudioObjectsForTreeNode(
							this.navigationHandler.getCurrentView().getClass(),
							node));
				}
			}
		}
		return files;
	}

	/**
	 * Gets the audio object in navigation table for selected row
	 * 
	 * @return the song in navigation table
	 */
	public IAudioObject getAudioObjectInNavigationTable() {
		return getAudioObjectInNavigationTable(this.navigationTable
				.getSelectedRow());
	}

	/**
	 * @return audio objects in all selected rows
	 */
	public List<IAudioObject> getAudioObjectsInNavigationTable() {
		List<IAudioObject> result = new ArrayList<IAudioObject>();
		for (int i = 0; i < this.navigationTable.getSelectedRows().length; i++) {
			result.add(getAudioObjectInNavigationTable(i));
		}
		return result;
	}

	/**
	 * Gets the audio object in navigation table for selected row
	 * 
	 * @param row
	 * @return
	 */
	public IAudioObject getAudioObjectInNavigationTable(final int row) {
		return ((NavigationTableModel) this.navigationTable.getModel())
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
	public List<IAudioObject> getAudioObjectsForTreeNode(
			final Class<? extends INavigationView> navigationViewClass,
			final ITreeNode node) {
		String treeFilter = this.filterHandler
				.getFilterText(this.navigationTreeFilter);
		String tableFilter = this.filterHandler
				.getFilterText(this.navigationTableFilter);

		List<IAudioObject> audioObjects = this.navigationHandler.getView(
				navigationViewClass).getAudioObjectForTreeNode(node,
				this.stateNavigation.getViewMode(), treeFilter, tableFilter);

		IColumnSet columnSet = this.navigationHandler.getCurrentView()
				.getCustomColumnSet();
		if (columnSet == null) {
			columnSet = this.navigatorColumnSet;
		}

		IColumn<?> columnSorted = columnSet.getSortedColumn();
		if (columnSorted != null) {
			net.sourceforge.atunes.utils.Timer t = new net.sourceforge.atunes.utils.Timer();
			t.start();
			try {
				Collections.sort(audioObjects, columnSorted.getComparator());
			} catch (IllegalArgumentException e) {
				// Try to find column causing
				// "java.lang.IllegalArgumentException: Comparison method violates its general contract!"
				// with Java 7
				List<String> values = new ArrayList<String>();
				for (IAudioObject audioObject : audioObjects) {
					Object value = columnSorted.getValueFor(audioObject, 0);
					if (value != null) {
						values.add(value.toString());
					} else {
						values.add("null");
					}
				}
				throw new IllegalArgumentException(StringUtils.getString(
						"Column: ", columnSorted.getColumnName(), " Values: ",
						Arrays.toString(values.toArray())), e);
			}
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
		((NavigationTableModel) this.navigationTable.getModel())
				.refresh(TableModelEvent.UPDATE);
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
		Class<? extends INavigationView> navigationView = this.navigationHandler
				.getViewByName(view);
		if (navigationView == null) {
			navigationView = RepositoryNavigationView.class;
		}

		if (saveNavigationView) {
			this.stateNavigation.setNavigationView(navigationView.getName());
		}

		getNavigationTreePanel().showNavigationView(
				this.navigationHandler.getView(navigationView));
		this.navigationTablePanel.showNavigationView(this.navigationHandler
				.getView(navigationView));

		// Allow view mode controls if navigation view supports them and tree is
		// visible
		boolean viewModeSupported = this.navigationHandler.getView(
				navigationView).isViewModeSupported()
				&& this.stateNavigation.isShowNavigationTree();

		this.beanFactory.getBean(ShowAlbumsInNavigatorAction.class).setEnabled(
				viewModeSupported);
		this.beanFactory.getBean(ShowArtistsInNavigatorAction.class)
				.setEnabled(viewModeSupported);
		this.beanFactory.getBean(ShowFoldersInNavigatorAction.class)
				.setEnabled(viewModeSupported);
		this.beanFactory.getBean(ShowGenresInNavigatorAction.class).setEnabled(
				viewModeSupported);
		this.beanFactory.getBean(ShowYearsInNavigatorAction.class).setEnabled(
				viewModeSupported);

		// Change column set
		boolean useDefaultNavigatorColumns = this.navigationHandler.getView(
				navigationView).isUseDefaultNavigatorColumnSet();
		IColumnSet columnSet = null;
		if (useDefaultNavigatorColumns) {
			columnSet = this.navigatorColumnSet;
		} else {
			columnSet = this.navigationHandler.getView(navigationView)
					.getCustomColumnSet();
		}

		((NavigationTableModel) this.navigationTable.getModel())
				.setColumnSet(columnSet);
		((NavigationTableColumnModel) this.navigationTable.getColumnModel())
				.setColumnSet(columnSet);

		this.navigationHandler.refreshCurrentView();

		// Allow arrange columns if view uses default column set
		this.columnSetPopupMenu
				.enableArrangeColumns(useDefaultNavigatorColumns);

		showNodesContentInNavigationTable(navigationView,
				this.navigationHandler.getCurrentView().getTree());
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
			((NavigationTableModel) this.navigationTable.getModel())
					.setSongs(songs);
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
		if (!this.stateNavigation.isShowNavigationTable()) {
			return;
		}

		// Avoid events when changes on a tree different than the one which is
		// visible
		if (tree != this.navigationHandler.getCurrentView().getTree()) {
			return;
		}

		showNodesContentInNavigationTable(this.navigationHandler
				.getCurrentView().getClass(), this.navigationHandler
				.getCurrentView().getTree());
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
		return this.popupMenuCaller;
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
		this.navigationHandler.refreshCurrentView();
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
