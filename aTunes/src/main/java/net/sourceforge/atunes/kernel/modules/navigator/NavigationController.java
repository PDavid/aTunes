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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.AbstractCommonColumnModel;
import net.sourceforge.atunes.gui.ColumnRenderers;
import net.sourceforge.atunes.gui.NavigationTableColumnModel;
import net.sourceforge.atunes.gui.NavigationTableModel;
import net.sourceforge.atunes.gui.views.controls.ColumnSetPopupMenu;
import net.sourceforge.atunes.gui.views.controls.ColumnSetRowSorter;
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.actions.ShowAlbumsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowArtistsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowFoldersInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowGenresInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowYearsInNavigatorAction;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioFilesRemovedListener;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IController;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTreePanel;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.Logger;

final class NavigationController implements IAudioFilesRemovedListener, IController {

    private final class ExtendedToolTipActionListener implements ActionListener {
    	
		private final class GetAndSetImageSwingWorker extends SwingWorker<ImageIcon, Void> {
			
			private final Object currentObject;

			private GetAndSetImageSwingWorker(Object currentObject) {
				this.currentObject = currentObject;
			}

			@Override
			protected ImageIcon doInBackground() throws Exception {
			    // Get image for albums
		        if (currentObject instanceof ITreeObject) {
		        	if (currentObject instanceof Artist) {
		        		Artist a = (Artist) currentObject;
		                Image img = Context.getBean(IWebServicesHandler.class).getArtistImage(a.getName());
		                if (img != null) {
		                    return new ImageIcon(img);
		                }
		                return null;
		        	} else if (currentObject instanceof Album) {
		        		return audioObjectImageLocator.getImage((Album)currentObject, ImageSize.SIZE_MAX);
		        	}
		        }
		        return null;
			}

			@Override
			protected void done() {
			    try {
			        // Set image in tooltip when done (tooltip can be not visible then)
			        if (currentExtendedToolTipContent != null && currentExtendedToolTipContent.equals(currentObject)) {
			            getExtendedToolTip().setImage(get());
			        }
			    } catch (InterruptedException e) {
			        Logger.error(e);
			    } catch (ExecutionException e) {
			        Logger.error(e);
			    }
			}
		}

		@Override
        public void actionPerformed(ActionEvent arg0) {
            getExtendedToolTip().setVisible(true);

            final Object currentObject = currentExtendedToolTipContent;
            SwingWorker<ImageIcon, Void> getAndSetImage = new GetAndSetImageSwingWorker(currentObject);
            getAndSetImage.execute();

        }
	}

    private INavigationTreePanel navigationTreePanel;

    /** The current extended tool tip content. */
    private volatile Object currentExtendedToolTipContent;

    /** The album tool tip. */
    private ExtendedToolTip extendedToolTip;

    /** The popupmenu caller. */
    private JComponent popupMenuCaller;

    /** The column model */
    private AbstractCommonColumnModel columnModel;

    private ColumnSetPopupMenu columnSetPopupMenu;

    /** The timer. */
    private Timer timer = new Timer(0, new ExtendedToolTipActionListener());

    /**
     * State of app
     */
    private IState state;
    
    private IColumnSet navigatorColumnSet;
    
    INavigationHandler navigationHandler;
    
    private ITaskService taskService;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private IFilterHandler filterHandler;
    
    private ITable navigationTable;
    
    private IAudioObjectImageLocator audioObjectImageLocator;
    
    /**
     * Instantiates a new navigation controller.
     * @param treePanel
     * @param navigationTable
     * @param state
     * @param navigationHandler
     * @param taskService
     * @param lookAndFeelManager
     * @param repositoryHandler
     * @param filterHandler
     * @param audioObjectImageLocator
     */
    NavigationController(INavigationTreePanel treePanel, ITable navigationTable, IState state, INavigationHandler navigationHandler, ITaskService taskService, ILookAndFeelManager lookAndFeelManager, IRepositoryHandler repositoryHandler, IFilterHandler filterHandler, IAudioObjectImageLocator audioObjectImageLocator) {
        this.navigationTreePanel = treePanel;
        this.navigationTable = navigationTable;
        this.state = state;
        this.navigationHandler = navigationHandler;
        this.taskService = taskService;
        this.lookAndFeelManager = lookAndFeelManager;
        this.filterHandler = filterHandler;
        this.audioObjectImageLocator = audioObjectImageLocator;
        addBindings();
        repositoryHandler.addAudioFilesRemovedListener(this);
        this.navigatorColumnSet = (IColumnSet) Context.getBean("navigatorColumnSet");
    }

    protected INavigationTreePanel getNavigationTreePanel() {
        return navigationTreePanel;
    }

    @Override
    public void addBindings() {
        NavigationTableModel model = new NavigationTableModel();
        navigationTable.setModel(model);
        columnModel = new NavigationTableColumnModel(navigationTable, state, navigationHandler, taskService, lookAndFeelManager.getCurrentLookAndFeel());
        navigationTable.setColumnModel(columnModel);
        ColumnRenderers.addRenderers(navigationTable.getSwingComponent(), columnModel, lookAndFeelManager.getCurrentLookAndFeel());

        new ColumnSetRowSorter(navigationTable.getSwingComponent(), model, columnModel);

        // Bind column set popup menu
        columnSetPopupMenu = new ColumnSetPopupMenu(navigationTable.getSwingComponent(), columnModel);

        // Add tree selection listeners to all views
        for (INavigationView view : navigationHandler.getNavigationViews()) {
            view.getTree().addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    updateTableContent((JTree) e.getSource());
                }
            });
        }

        // Add tree mouse listeners to all views
        // Add tree tool tip listener to all views
        NavigationTreeMouseListener treeMouseListener = new NavigationTreeMouseListener(this, navigationTable, state, navigationHandler);
        NavigationTreeToolTipListener tooltipListener = new NavigationTreeToolTipListener(this, state, navigationHandler);
        for (INavigationView view : navigationHandler.getNavigationViews()) {
            view.getTree().addMouseListener(treeMouseListener);
            view.getTree().addMouseListener(tooltipListener);
            view.getTree().addMouseMotionListener(tooltipListener);
            view.getTreeScrollPane().addMouseWheelListener(tooltipListener);
        }

        navigationTable.addMouseListener(new NavigationTableMouseListener(this, navigationTable, navigationHandler));
        
        if (lookAndFeelManager.getCurrentLookAndFeel().customComboBoxRenderersSupported()) {
        	navigationTreePanel.getTreeComboBox().setRenderer(lookAndFeelManager.getCurrentLookAndFeel().getListCellRenderer(new NavigationTreePanelCustomComboBoxRenderer(lookAndFeelManager)));
        }
    }
    
    /**
     * Enable navigation tree combo listener
     */
    protected void enableNavigationTreeComboListener() {
        // Add combo listener
        navigationTreePanel.getTreeComboBox().addItemListener(new NavigationTreeComboListener(navigationHandler));       
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
            extendedToolTip = new ExtendedToolTip(lookAndFeelManager.getCurrentLookAndFeel());
            JDialog.setDefaultLookAndFeelDecorated(lookAndFeelManager.getCurrentLookAndFeel().isDialogUndecorated());
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
            files.addAll(((NavigationTableModel) navigationTable.getModel()).getAudioObjectsAt(rows));
        } else if (getPopupMenuCaller() instanceof JTree) {
            TreePath[] paths = navigationHandler.getCurrentView().getTree().getSelectionPaths();
            for (TreePath path : paths) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (treeNode.getUserObject() instanceof ITreeObject) {
                    files.addAll(getAudioObjectsForTreeNode(navigationHandler.getCurrentView().getClass(),
                            treeNode));
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
    public IAudioObject getAudioObjectInNavigationTable(int row) {
        return ((NavigationTableModel) navigationTable.getModel()).getAudioObjectAt(row);
    }

    /**
     * Returns audio objects selected by the given node in the given navigation
     * view
     * 
     * @param navigationViewClass
     * @param node
     * @return
     */
    public List<? extends IAudioObject> getAudioObjectsForTreeNode(Class<? extends INavigationView> navigationViewClass, DefaultMutableTreeNode node) {
        List<? extends IAudioObject> audioObjects = navigationHandler.getView(navigationViewClass).getAudioObjectForTreeNode(node, state.getViewMode(),
                filterHandler.isFilterSelected(navigationHandler.getTreeFilter()) ? filterHandler.getFilter() : null);

        IColumnSet columnSet = navigationHandler.getCurrentView().getCustomColumnSet();
        if (columnSet == null) {
            columnSet = navigatorColumnSet;
        }

        IColumn columnSorted = columnSet.getSortedColumn();
        if (columnSorted != null) {
            Collections.sort(audioObjects, columnSorted.getComparator(false));
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
        ((NavigationTableModel) navigationTable.getModel()).refresh(TableModelEvent.UPDATE);
    }

    /**
     * Sets the current album tool tip content.
     * 
     * @param currentAlbumToolTipContent
     *            the new current album tool tip content
     */
    @SuppressWarnings("unchecked")
	public void setCurrentExtendedToolTipContent(Object currentAlbumToolTipContent) {
        this.currentExtendedToolTipContent = currentAlbumToolTipContent;
        getExtendedToolTip().setSizeToFitImage(currentAlbumToolTipContent instanceof ITreeObject && ((ITreeObject<? extends IAudioObject>) currentAlbumToolTipContent).isExtendedToolTipImageSupported());
    }

    /**
     * Sets the navigation view and optionally saves navigation view
     * 
     * @param view
     *            the new navigation view
     *            
     * @param saveNavigationView
     */
    public void setNavigationView(String view, boolean saveNavigationView) {
        Class<? extends INavigationView> navigationView = navigationHandler.getViewByName(view);
        if (navigationView == null) {
            navigationView = RepositoryNavigationView.class;
        }
        
        if (saveNavigationView) {
        	state.setNavigationView(navigationView.getName());
        }
        
        getNavigationTreePanel().showNavigationView(navigationHandler.getView(navigationView));

        boolean viewModeSupported = navigationHandler.getView(navigationView).isViewModeSupported();
        Context.getBean(ShowAlbumsInNavigatorAction.class).setEnabled(viewModeSupported);
        Context.getBean(ShowArtistsInNavigatorAction.class).setEnabled(viewModeSupported);
        Context.getBean(ShowFoldersInNavigatorAction.class).setEnabled(viewModeSupported);
        Context.getBean(ShowGenresInNavigatorAction.class).setEnabled(viewModeSupported);
        Context.getBean(ShowYearsInNavigatorAction.class).setEnabled(viewModeSupported);

        // Change column set
        boolean useDefaultNavigatorColumns = navigationHandler.getView(navigationView).isUseDefaultNavigatorColumnSet();
        IColumnSet columnSet = null;
        if (useDefaultNavigatorColumns) {
            columnSet = navigatorColumnSet;
        } else {
            columnSet = navigationHandler.getView(navigationView).getCustomColumnSet();
        }

        ((NavigationTableModel) navigationTable.getModel()).setColumnSet(columnSet);
        ((NavigationTableColumnModel) navigationTable.getColumnModel()).setColumnSet(columnSet);

        navigationHandler.refreshCurrentView();

        // Allow arrange columns if view uses default column set
        columnSetPopupMenu.enableArrangeColumns(useDefaultNavigatorColumns);

        JTree tree = navigationHandler.getCurrentView().getTree();

        if (tree.getSelectionPath() != null) {
            ((NavigationTableModel) navigationTable.getModel()).setSongs(getAudioObjectsForTreeNode(navigationView, (DefaultMutableTreeNode) (tree
                    .getSelectionPath().getLastPathComponent())));
        }
    }

    /**
     * Sets the navigation view and saves state
     * @param view
     */
    public void setNavigationView(String view) {
    	setNavigationView(view, true);
    }
    
    /**
     * Updates table contents when user selects a tree node or the table filter
     * changes
     * 
     * @param tree
     *            the tree
     */
    protected void updateTableContent(JTree tree) {
        // If navigation table is not shown then don't update it
        if (!state.isShowNavigationTable()) {
            return;
        }

        // Avoid events when changes on a tree different than the one which is visible
        if (tree != navigationHandler.getCurrentView().getTree()) {
            return;
        }

        TreePath[] paths = tree.getSelectionPaths();

        if (paths != null) {
            List<IAudioObject> audioObjects = new ArrayList<IAudioObject>();
            for (TreePath element : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) (element.getLastPathComponent());
                audioObjects.addAll(getAudioObjectsForTreeNode(navigationHandler.getViewByName(state.getNavigationView()), node));
            }

            // Filter objects
            audioObjects = filterNavigationTable(audioObjects);

            ((NavigationTableModel) navigationTable.getModel()).setSongs(audioObjects);
        }
    }

    /**
     * Returns a filtered list of audio objects using the current table filter
     * 
     * @param audioObjects
     * @return
     */
    private List<IAudioObject> filterNavigationTable(List<IAudioObject> audioObjects) {
        if (!filterHandler.isFilterSelected(navigationHandler.getTableFilter())) {
            return audioObjects;
        }

        if (navigationHandler.getCurrentView().isUseDefaultNavigatorColumnSet()) {
            // Use column set filtering
            return navigatorColumnSet.filterAudioObjects(audioObjects, filterHandler.getFilter());
        } else {
            // Use custom filter
            return navigationHandler.getCurrentView().getCustomColumnSet().filterAudioObjects(audioObjects, filterHandler.getFilter());
        }
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
    public ISearch openSearchDialog(ISearchDialog dialog, boolean setAsDefaultVisible) {
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
    public void setPopupMenuCaller(JComponent popupMenuCaller) {
        this.popupMenuCaller = popupMenuCaller;
    }

    @Override
    public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
    	navigationHandler.refreshCurrentView();
    }

	/**
	 * Returns true if last action has been performed in tree
	 * @return
	 */
	public boolean isActionOverTree() {
		return getPopupMenuCaller() instanceof JTree;
	}
}
