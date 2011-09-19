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

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.views.panels.NavigationTablePanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTreePanel;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.draganddrop.TreeNavigationTransferHandler;
import net.sourceforge.atunes.kernel.modules.filter.FilterHandler;
import net.sourceforge.atunes.kernel.modules.plugins.PluginsHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

public final class NavigationHandler extends AbstractHandler implements PluginListener {

    /**
     * Singleton instance
     */
    private static NavigationHandler instance;

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

    /**
     * Getter of singleton instance
     * 
     * @return
     */
    public static NavigationHandler getInstance() {
        if (instance == null) {
            instance = new NavigationHandler();
        }
        return instance;
    }

    private NavigationHandler() {
    }

    @Override
    public void applicationFinish() {
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void initHandler() {
    	navigationViews = (List<INavigationView>) getBean("navigationViews");
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
        showNavigationTree(getState().isShowNavigationTree());
        applyNavigationTableVisibility(getState().isShowNavigationTree() && getState().isShowNavigationTable());

        // Navigation Panel View
        getNavigationController().setNavigationView(getState().getNavigationView(), false); 
        
        
        TreeNavigationTransferHandler treeNavigationTransferHandler = new TreeNavigationTransferHandler();
        getNavigationController().getNavigationTreePanel().setTransferHandler(treeNavigationTransferHandler);
    }

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

    public INavigationView getCurrentView() {
        return getView(getViewByName(getState().getNavigationView()));
    }

    public ViewMode getCurrentViewMode() {
        return getCurrentView().getCurrentViewMode();
    }

    public INavigationView getView(Class<? extends INavigationView> navigationViewClass) {
        return getNavigationViewsMap().get(navigationViewClass);
    }
    
    /**
     * Refreshes current view to update data shown
     */
    public void refreshCurrentView() {
        getCurrentView().refreshView(getState().getViewMode(),
                FilterHandler.getInstance().isFilterSelected(getTreeFilter()) ? FilterHandler.getInstance().getFilter() : null);
    }

    /**
     * Refreshes given view. To avoid unnecessary actions, given view is only
     * refreshed if it's the current view
     * 
     * @param navigationViewClass
     */
    public void refreshView(Class<? extends INavigationView> navigationViewClass) {
        if (getView(navigationViewClass).equals(getCurrentView())) {
            getView(navigationViewClass).refreshView(getState().getViewMode(),
                    FilterHandler.getInstance().isFilterSelected(getTreeFilter()) ? FilterHandler.getInstance().getFilter() : null);
        }
    }

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

    public int indexOf(Class<? extends INavigationView> view) {
        return getNavigationViews().indexOf(getNavigationViewsMap().get(view));
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

    /**
     * @return the tableFilter
     */
    public IFilter getTableFilter() {
        return tableFilter;
    }

    /**
     * @return the treeFilter
     */
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
            navigationController = new NavigationController(treePanel, tablePanel, getState(), getOsManager());
        }
        return navigationController;
    }

	public void refreshNavigationTable() {
		getNavigationController().refreshTable();
	}

	public List<IAudioObject> getFilesSelectedInNavigator() {
		return getNavigationController().getFilesSelectedInNavigator();
	}

	public boolean sameParentFile(List<ILocalAudioObject> audioFiles) {
		return getNavigationController().sameParentFile(audioFiles);		
	}

	protected JTable getNavigationTable() {
		return getNavigationController().getNavigationTablePanel().getNavigationTable();
	}

	public void setNavigationView(String name) {
		getNavigationController().setNavigationView(name);		
	}

	public JComponent getPopupMenuCaller() {
		return getNavigationController().getPopupMenuCaller();
	}

	public ISearch openSearchDialog(ISearchDialog dialog, boolean b) {
		return getNavigationController().openSearchDialog(dialog, b);
	}

	public void updateTableContent(JTree tree) {
		getNavigationController().updateTableContent(tree);
	}

	public void notifyDeviceReload() {
		getNavigationController().notifyDeviceReload();		
	}

	public List<? extends IAudioObject> getAudioObjectsForTreeNode(Class<? extends INavigationView> class1, DefaultMutableTreeNode objectDragged) {
		return getNavigationController().getAudioObjectsForTreeNode(class1, objectDragged);
	}

	/**
	 * Returns selected audio object in navigation table
	 * @return
	 */
	public IAudioObject getSelectedAudioObjectInNavigationTable() {
		return getNavigationController().getAudioObjectInNavigationTable(getNavigationTable().getSelectedRow());
	}

	/**
	 * Returns selected audio objects in navigation table
	 * @return
	 */
	public List<IAudioObject> getSelectedAudioObjectsInNavigationTable() {
		List<IAudioObject> result = new ArrayList<IAudioObject>();
		for (int i = 0; i < getNavigationTable().getSelectedRows().length; i++) {
			result.add(getNavigationController().getAudioObjectInNavigationTable(i));
		}
		return result;
	}

	public IAudioObject getAudioObjectInNavigationTable(int row) {
		return getNavigationController().getAudioObjectInNavigationTable(row);
	}
	
	public void notifyReload() {
		getNavigationController().notifyReload();
	}

	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

	@Override
	public void favoritesChanged() {
        // Update all views
        refreshCurrentView();
	}
	
    /**
     * Show navigation tree.
     * 
     * @param show
     *            the show
     */
    public void showNavigationTree(boolean show) {    	
        getState().setShowNavigationTree(show);

    	// Disable or enable actions
        for (INavigationView navigationView : NavigationHandler.getInstance().getNavigationViews()) {
        	navigationView.getActionToShowView().setEnabled(show);
        }
    	
        getFrame().showNavigationTree(show);
        // Depending if is visible or not filtering is allowed or not
        FilterHandler.getInstance().setFilterEnabled(NavigationHandler.getInstance().getTreeFilter(), show);
        
        applyNavigationTableVisibility(show && getState().isShowNavigationTable());
    }
    
    /**
     * Show navigation table.
     * 
     * @param show
     *            the show
     */
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
        FilterHandler.getInstance().setFilterEnabled(NavigationHandler.getInstance().getTableFilter(), show);
        
        updateTableContent(NavigationHandler.getInstance().getCurrentView().getTree());
    }

	/**
	 * Called to select given artist in navigator
	 * @param artist
	 */
	public void selectArtist(String artist) {
		getCurrentView().selectArtist(getCurrentView().getCurrentViewMode(), artist);		
	}
	
    /**
     * Called to select given audio object in navigator
     * @param audioObject
     */
    public void selectAudioObject(IAudioObject audioObject){
    	getCurrentView().selectAudioObject(getCurrentView().getCurrentViewMode(), audioObject);
    }

}
