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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.views.panels.NavigationTablePanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTreePanel;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.draganddrop.TreeNavigationTransferHandler;
import net.sourceforge.atunes.kernel.modules.filter.FilterHandler;
import net.sourceforge.atunes.kernel.modules.plugins.PluginsHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

public final class NavigationHandler extends AbstractHandler implements PluginListener, INavigationHandler {

    private List<INavigationView> navigationViews;

	/**
     * Filter for navigation table
     */
    private IFilter tableFilter = new IFilter() {

        @Override
        public String getName() {
            return "NAVIGATION_TABLE";
        }

        @Override
        public String getDescription() {
            return I18nUtils.getString("NAVIGATION_TABLE");
        }

        @Override
        public void applyFilter(String filter) {
            getNavigationController().updateTableContent(getCurrentView().getTree());
        }
    };

    /**
     * Filter for tree
     */
    private IFilter treeFilter = new IFilter() {

        @Override
        public String getName() {
            return "NAVIGATION_TREE";
        };

        @Override
        public String getDescription() {
            return I18nUtils.getString("NAVIGATOR");
        };

        @Override
        public void applyFilter(String filter) {
            refreshCurrentView();
        };
    };

	private NavigationController navigationController;

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
        showNavigationTree(getState().isShowNavigationTree());
        applyNavigationTableVisibility(getState().isShowNavigationTree() && getState().isShowNavigationTable());

        // Navigation Panel View
        getNavigationController().setNavigationView(getState().getNavigationView(), false); 
        
        
        TreeNavigationTransferHandler treeNavigationTransferHandler = new TreeNavigationTransferHandler();
        getNavigationController().getNavigationTreePanel().setTransferHandler(treeNavigationTransferHandler);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getNavigationViews()
	 */
    @Override
	public List<INavigationView> getNavigationViews() {
        return navigationViews;
    }

    /**
     * Builds a map containing classes of navigation view as keys and references
     * to instances as values
     * 
     * @return
     */
    private Map<Class<? extends INavigationView>, INavigationView> getNavigationViewsMap() {
        Map<Class<? extends INavigationView>, INavigationView> navigationViewsMap = new HashMap<Class<? extends INavigationView>, INavigationView>();
        for (INavigationView view : getNavigationViews()) {
            navigationViewsMap.put(view.getClass(), view);
        }
        return navigationViewsMap;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getCurrentView()
	 */
    @Override
	public INavigationView getCurrentView() {
        return getView(getViewByName(getState().getNavigationView()));
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getCurrentViewMode()
	 */
    @Override
	public ViewMode getCurrentViewMode() {
        return getCurrentView().getCurrentViewMode();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getView(java.lang.Class)
	 */
    @Override
	public INavigationView getView(Class<? extends INavigationView> navigationViewClass) {
        return getNavigationViewsMap().get(navigationViewClass);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#refreshCurrentView()
	 */
    @Override
	public void refreshCurrentView() {
        getCurrentView().refreshView(getState().getViewMode(),
                FilterHandler.getInstance().isFilterSelected(getTreeFilter()) ? FilterHandler.getInstance().getFilter() : null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#refreshView(java.lang.Class)
	 */
    @Override
	public void refreshView(Class<? extends INavigationView> navigationViewClass) {
        if (getView(navigationViewClass).equals(getCurrentView())) {
            getView(navigationViewClass).refreshView(getState().getViewMode(),
                    FilterHandler.getInstance().isFilterSelected(getTreeFilter()) ? FilterHandler.getInstance().getFilter() : null);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getViewByName(java.lang.String)
	 */
    @Override
	public Class<? extends INavigationView> getViewByName(String className) {
        if (className == null) {
            return null;
        }
        for (Class<? extends INavigationView> viewFromMap : getNavigationViewsMap().keySet()) {
            if (viewFromMap.getName().equals(className)) {
                return viewFromMap;
            }
        }

        // If class is not found (maybe the view was a plugin and has been removed, return default view)
        return RepositoryNavigationView.class;
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            getNavigationViews().add((INavigationView) PluginsHandler.getInstance().getNewInstance(plugin));
            // Set tress
            getNavigationController().getNavigationTreePanel().updateTrees();
        } catch (PluginSystemException e) {
            Logger.error(e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> views) {
        // Remove all views
        for (Plugin view : views) {
            getNavigationViews().remove(view);
        }
    }

    @Override
    public void applicationStateChanged(IState newState) {
        // TODO: Remove refreshing explicitly radio view
        refreshView(RadioNavigationView.class);
        refreshCurrentView();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getTableFilter()
	 */
    @Override
	public IFilter getTableFilter() {
        return tableFilter;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getTreeFilter()
	 */
    @Override
	public IFilter getTreeFilter() {
        return treeFilter;
    }
    
    /**
     * Gets the navigation controller.
     * 
     * @return the navigation controller
     */
    private NavigationController getNavigationController() {
        if (navigationController == null) {
            NavigationTreePanel treePanel = getFrame().getNavigationTreePanel();
            NavigationTablePanel tablePanel = getFrame().getNavigationTablePanel();
            navigationController = new NavigationController(treePanel, tablePanel, getState(), getOsManager(), this, getBean(ITaskService.class));
        }
        return navigationController;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#refreshNavigationTable()
	 */
	@Override
	public void refreshNavigationTable() {
		getNavigationController().refreshTable();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getFilesSelectedInNavigator()
	 */
	@Override
	public List<IAudioObject> getFilesSelectedInNavigator() {
		return getNavigationController().getFilesSelectedInNavigator();
	}

	protected JTable getNavigationTable() {
		return getNavigationController().getNavigationTablePanel().getNavigationTable();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#setNavigationView(java.lang.String)
	 */
	@Override
	public void setNavigationView(String name) {
		getNavigationController().setNavigationView(name);		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#openSearchDialog(net.sourceforge.atunes.model.ISearchDialog, boolean)
	 */
	@Override
	public ISearch openSearchDialog(ISearchDialog dialog, boolean b) {
		return getNavigationController().openSearchDialog(dialog, b);
	}

	private void updateTableContent(JTree tree) {
		getNavigationController().updateTableContent(tree);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#updateViewTable()
	 */
	@Override
	public void updateViewTable() {
		updateTableContent(getCurrentView().getTree());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getAudioObjectsForTreeNode(java.lang.Class, javax.swing.tree.DefaultMutableTreeNode)
	 */
	@Override
	public List<? extends IAudioObject> getAudioObjectsForTreeNode(Class<? extends INavigationView> class1, DefaultMutableTreeNode objectDragged) {
		return getNavigationController().getAudioObjectsForTreeNode(class1, objectDragged);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getSelectedAudioObjectInNavigationTable()
	 */
	@Override
	public IAudioObject getSelectedAudioObjectInNavigationTable() {
		return getNavigationController().getAudioObjectInNavigationTable(getNavigationTable().getSelectedRow());
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getSelectedAudioObjectsInNavigationTable()
	 */
	@Override
	public List<IAudioObject> getSelectedAudioObjectsInNavigationTable() {
		List<IAudioObject> result = new ArrayList<IAudioObject>();
		for (int i = 0; i < getNavigationTable().getSelectedRows().length; i++) {
			result.add(getNavigationController().getAudioObjectInNavigationTable(i));
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#getAudioObjectInNavigationTable(int)
	 */
	@Override
	public IAudioObject getAudioObjectInNavigationTable(int row) {
		return getNavigationController().getAudioObjectInNavigationTable(row);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#repositoryReloaded()
	 */
	@Override
	public void repositoryReloaded() {
    	// Calling inside invokeLater will cause ConcurrentModificationException
    	refreshCurrentView();
	}

	@Override
	public void favoritesChanged() {
        // Update all views
        refreshCurrentView();
	}
	
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#showNavigationTree(boolean)
	 */
    @Override
	public void showNavigationTree(boolean show) {    	
        getState().setShowNavigationTree(show);

    	// Disable or enable actions
        for (INavigationView navigationView : getNavigationViews()) {
        	navigationView.getActionToShowView().setEnabled(show);
        }
    	
        getFrame().showNavigationTree(show);
        // Depending if is visible or not filtering is allowed or not
        FilterHandler.getInstance().setFilterEnabled(getTreeFilter(), show);
        
        applyNavigationTableVisibility(show && getState().isShowNavigationTable());
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#showNavigationTable(boolean)
	 */
    @Override
	public void showNavigationTable(boolean show) {
        getState().setShowNavigationTable(show);
        applyNavigationTableVisibility(show);
    }
    
    /**
     * Used to show or hide navigation table when changing tree visibility
     * @param show
     */
    private void applyNavigationTableVisibility(boolean show) {
    	getFrame().showNavigationTable(show);
        // Depending if is visible or not filtering is allowed or not
        FilterHandler.getInstance().setFilterEnabled(getTableFilter(), show);
        
        updateTableContent(getCurrentView().getTree());
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#selectArtist(java.lang.String)
	 */
	@Override
	public void selectArtist(String artist) {
		getCurrentView().selectArtist(getCurrentView().getCurrentViewMode(), artist);		
	}
	
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#selectAudioObject(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public void selectAudioObject(IAudioObject audioObject){
    	getCurrentView().selectAudioObject(getCurrentView().getCurrentViewMode(), audioObject);
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#isActionOverTree()
	 */
	@Override
	public boolean isActionOverTree() {
		return getNavigationController().isActionOverTree();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler#setNavigationViews(java.util.List)
	 */
	@Override
	public void setNavigationViews(List<INavigationView> navigationViews) {
		this.navigationViews = navigationViews;
	}
}
