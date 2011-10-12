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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.draganddrop.TreeNavigationTransferHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTablePanel;
import net.sourceforge.atunes.model.INavigationTreePanel;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
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
	
	private IFilterHandler filterHandler;

	@Override
	protected void initHandler() {
		this.filterHandler = Context.getBean(IFilterHandler.class);
	}
	
    @Override
    public void applicationStarted(List<IAudioObject> playList) {
        showNavigationTree(getState().isShowNavigationTree());
        applyNavigationTableVisibility(getState().isShowNavigationTree() && getState().isShowNavigationTable());

        // Navigation Panel View
        getNavigationController().setNavigationView(getState().getNavigationView(), false); 
        
        getNavigationController().getNavigationTreePanel().enableDragAndDrop(new TreeNavigationTransferHandler());
    }

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

    @Override
	public INavigationView getCurrentView() {
        return getView(getViewByName(getState().getNavigationView()));
    }

    @Override
	public ViewMode getCurrentViewMode() {
        return getCurrentView().getCurrentViewMode();
    }

    @Override
	public INavigationView getView(Class<? extends INavigationView> navigationViewClass) {
        return getNavigationViewsMap().get(navigationViewClass);
    }
    
    @Override
	public void refreshCurrentView() {
        getCurrentView().refreshView(getState().getViewMode(),
                filterHandler.isFilterSelected(getTreeFilter()) ? filterHandler.getFilter() : null);
    }

    @Override
	public void refreshView(Class<? extends INavigationView> navigationViewClass) {
        if (getView(navigationViewClass).equals(getCurrentView())) {
            getView(navigationViewClass).refreshView(getState().getViewMode(),
                    filterHandler.isFilterSelected(getTreeFilter()) ? filterHandler.getFilter() : null);
        }
    }

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
            getNavigationViews().add((INavigationView) getBean(IPluginsHandler.class).getNewInstance(plugin));
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

    @Override
	public IFilter getTableFilter() {
        return tableFilter;
    }

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
        	INavigationTreePanel treePanel = getFrame().getNavigationTreePanel();
            INavigationTablePanel tablePanel = getFrame().getNavigationTablePanel();
            navigationController = new NavigationController(treePanel, tablePanel, getState(), getOsManager(), this, getBean(ITaskService.class), getBean(ILookAndFeelManager.class), getBean(IRepositoryHandler.class), getBean(IFilterHandler.class));
        }
        return navigationController;
    }

	@Override
	public void refreshNavigationTable() {
		getNavigationController().refreshTable();
	}

	@Override
	public List<IAudioObject> getFilesSelectedInNavigator() {
		return getNavigationController().getFilesSelectedInNavigator();
	}

	@Override
	public void setNavigationView(String name) {
		getNavigationController().setNavigationView(name);		
	}

	@Override
	public ISearch openSearchDialog(ISearchDialog dialog, boolean b) {
		return getNavigationController().openSearchDialog(dialog, b);
	}

	private void updateTableContent(JTree tree) {
		getNavigationController().updateTableContent(tree);
	}
	
	@Override
	public void updateViewTable() {
		updateTableContent(getCurrentView().getTree());
	}

	@Override
	public List<? extends IAudioObject> getAudioObjectsForTreeNode(Class<? extends INavigationView> class1, DefaultMutableTreeNode objectDragged) {
		return getNavigationController().getAudioObjectsForTreeNode(class1, objectDragged);
	}

	@Override
	public IAudioObject getSelectedAudioObjectInNavigationTable() {
		return getNavigationController().getAudioObjectInNavigationTable(getFrame().getNavigationTable().getSelectedRow());
	}

	@Override
	public List<IAudioObject> getSelectedAudioObjectsInNavigationTable() {
		List<IAudioObject> result = new ArrayList<IAudioObject>();
		for (int i = 0; i < getFrame().getNavigationTable().getSelectedRows().length; i++) {
			result.add(getNavigationController().getAudioObjectInNavigationTable(i));
		}
		return result;
	}

	@Override
	public IAudioObject getAudioObjectInNavigationTable(int row) {
		return getNavigationController().getAudioObjectInNavigationTable(row);
	}
	
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
	
    @Override
	public void showNavigationTree(boolean show) {    	
        getState().setShowNavigationTree(show);

    	// Disable or enable actions
        for (INavigationView navigationView : getNavigationViews()) {
        	navigationView.getActionToShowView().setEnabled(show);
        }
    	
        getFrame().showNavigationTree(show);
        // Depending if is visible or not filtering is allowed or not
        filterHandler.setFilterEnabled(getTreeFilter(), show);
        
        applyNavigationTableVisibility(show && getState().isShowNavigationTable());
    }
    
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
        filterHandler.setFilterEnabled(getTableFilter(), show);
        
        updateTableContent(getCurrentView().getTree());
    }

	@Override
	public void selectArtist(String artist) {
		getCurrentView().selectArtist(getCurrentView().getCurrentViewMode(), artist);		
	}
	
    @Override
	public void selectAudioObject(IAudioObject audioObject){
    	getCurrentView().selectAudioObject(getCurrentView().getCurrentViewMode(), audioObject);
    }

	@Override
	public boolean isActionOverTree() {
		return getNavigationController().isActionOverTree();
	}
	
	@Override
	public void setNavigationViews(List<INavigationView> navigationViews) {
		this.navigationViews = navigationViews;
	}
}
