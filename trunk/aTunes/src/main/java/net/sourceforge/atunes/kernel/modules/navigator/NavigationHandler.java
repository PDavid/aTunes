/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginListener;
import org.commonjukebox.plugins.PluginSystemException;

public final class NavigationHandler extends Handler implements PluginListener {

    /**
     * Singleton instance
     */
    private static NavigationHandler instance;

    private List<NavigationView> navigationViews;

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

    @Override
    protected void initHandler() {
    }

    @Override
    public void applicationStarted() {
    }

    public List<NavigationView> getNavigationViews() {
        if (navigationViews == null) {
            navigationViews = new ArrayList<NavigationView>();
            // TODO: Dynamic load of navigation views, possibly from a config file
            navigationViews.add(new RepositoryNavigationView());
            navigationViews.add(new FavoritesNavigationView());
            navigationViews.add(new DeviceNavigationView());
            navigationViews.add(new RadioNavigationView());
            navigationViews.add(new PodcastNavigationView());
        }
        return navigationViews;
    }

    /**
     * Builds a map containing classes of navigation view as keys and references
     * to instances as values
     * 
     * @return
     */
    private Map<Class<? extends NavigationView>, NavigationView> getNavigationViewsMap() {
        Map<Class<? extends NavigationView>, NavigationView> navigationViewsMap = new HashMap<Class<? extends NavigationView>, NavigationView>();
        for (NavigationView view : getNavigationViews()) {
            navigationViewsMap.put(view.getClass(), view);
        }
        return navigationViewsMap;
    }

    public NavigationView getCurrentView() {
        return getView(getViewByName(ApplicationState.getInstance().getNavigationView()));
    }

    public ViewMode getCurrentViewMode() {
        return getCurrentView().getCurrentViewMode();
    }

    public NavigationView getView(Class<? extends NavigationView> navigationViewClass) {
        return getNavigationViewsMap().get(navigationViewClass);
    }

    /**
     * Refreshes current view to update data shown
     */
    public void refreshCurrentView() {
        getCurrentView().refreshView(ApplicationState.getInstance().getViewMode(),
                ControllerProxy.getInstance().getNavigationController().getNavigationTreePanel().getTreeFilterPanel().getFilter());
    }

    /**
     * Refreshes given view. To avoid unnecessary actions, given view is only
     * refreshed if it's the current view
     * 
     * @param navigationViewClass
     */
    public void refreshView(Class<? extends NavigationView> navigationViewClass) {
        if (getView(navigationViewClass).equals(getCurrentView())) {
            getView(navigationViewClass).refreshView(ApplicationState.getInstance().getViewMode(),
                    ControllerProxy.getInstance().getNavigationController().getNavigationTreePanel().getTreeFilterPanel().getFilter());
        }
    }

    public Class<? extends NavigationView> getViewByName(String className) {
        if (className == null) {
            return null;
        }
        for (Class<? extends NavigationView> viewFromMap : getNavigationViewsMap().keySet()) {
            if (viewFromMap.getName().equals(className)) {
                return viewFromMap;
            }
        }

        // If class is not found (maybe the view was a plugin and has been removed, return default view)
        return RepositoryNavigationView.class;
    }

    public int indexOf(Class<? extends NavigationView> view) {
        return getNavigationViews().indexOf(getNavigationViewsMap().get(view));
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            getNavigationViews().add((NavigationView) plugin.getInstance());
            // Set tabs and text for navigator
            ControllerProxy.getInstance().getNavigationController().getNavigationTreePanel().updateTabs();
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.PLUGINS, e);
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
    public void applicationStateChanged(ApplicationState newState) {
        // TODO: Remove refreshing explicitly radio view
        refreshView(RadioNavigationView.class);
        refreshCurrentView();
    }
}
