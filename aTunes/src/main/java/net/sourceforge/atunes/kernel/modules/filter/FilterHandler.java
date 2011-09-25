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

package net.sourceforge.atunes.kernel.modules.filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

public final class FilterHandler extends AbstractHandler {

    private final class AllFilter implements IFilter {
    	
		@Override
        public String getName() {
            return "ALL";
        }

		@Override
        public String getDescription() {
            return I18nUtils.getString("ALL");
        }

		@Override
        public void applyFilter(String filterString) {
            for (IFilter filter : getFilters().values()) {
                if (!filter.equals(this)) {
                    filter.applyFilter(filterString);
                }
            }
        }
	}

	/**
     * Singleton instance
     */
    private static FilterHandler instance;

    /**
     * Controller
     */
    private FilterController toolBarFilterController;
    
    /**
     * Available filters
     */
    private Map<String, IFilter> filters;

    /**
     * Filter for all current filters at the same time
     */
    private IFilter allFilter = new AllFilter();

    /**
     * Selected filter (by default all)
     */
    private String selectedFilter = allFilter.getName();

    /**
     * Text used as filter
     */
    private String currentFilterText;

    /**
     * Private constructor
     */
    private FilterHandler() {
    }

    @Override
    protected void initHandler() {
    }

    /**
     * Getter for singleton instance
     * 
     * @return
     */
    public static FilterHandler getInstance() {
        if (instance == null) {
            instance = new FilterHandler();
        }
        return instance;
    }

    /**
     * Adds a new filter
     * 
     * @param filter
     */
    private void addFilter(IFilter filter) {
    	getFilters().put(filter.getName(), filter);
        // Update UI to show new available filter
        getToolBarFilterController().addFilter(filter);
    }

    /**
     * Removes a filter
     * 
     * @param filter
     */
    public void removeFilter(IFilter filter) {
    	getFilters().remove(filter.getName());
        // Update UI to hide filter
        getToolBarFilterController().removeFilter(filter.getName());
    }

    /**
     * Applies given filter which is the current filter
     * 
     * @param filter
     */
    public void applyFilter(String filter) {
        this.currentFilterText = filter;
        Logger.debug("Applying filter: ", filter);
        getFilters().get(selectedFilter).applyFilter(filter);
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
    }
    
    @Override
    public void allHandlersInitialized() {
        addFilter(allFilter);

        addFilter(getBean(INavigationHandler.class).getTreeFilter());
        setFilterEnabled(getBean(INavigationHandler.class).getTreeFilter(), getState().isShowNavigationTree());

        addFilter(getBean(INavigationHandler.class).getTableFilter());
        setFilterEnabled(getBean(INavigationHandler.class).getTableFilter(), getState().isShowNavigationTable());

        addFilter(getBean(IPlayListHandler.class).getPlayListFilter());

        getToolBarFilterController().setSelectedFilter(allFilter.getName());
    }

    @Override
    public void applicationFinish() {
    }

    @Override
    public void applicationStateChanged(IState newState) {
    }

    /**
     * @param selectedFilter
     *            the selectedFilter to set
     */
    public void setSelectedFilter(String selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

    /**
     * Returns filter
     * 
     * @return
     */
    public String getFilter() {
        return this.currentFilterText;
    }

    /**
     * Returns <code>true</code> if given filter is selected (or "all" is
     * selected) and filter text is not null
     * 
     * @param filter
     * @return
     */
    public boolean isFilterSelected(IFilter filter) {
        return (this.selectedFilter.equals(allFilter.getName()) || this.selectedFilter.equals(filter.getName())) && getFilter() != null;
    }

    /**
     * Sets filter enabled or disabled
     * 
     * @param filter
     * @param enabled
     */
    public void setFilterEnabled(IFilter filter, boolean enabled) {
        // If filter is selected and must be disabled change to "all" and set filter null
        if (this.selectedFilter.equals(filter.getName()) && !enabled) {
            this.selectedFilter = allFilter.getName();
            getToolBarFilterController().setSelectedFilter(allFilter.getName());
            applyFilter(null);
        }
        getToolBarFilterController().setFilterEnabled(filter.getName(), enabled);

    }
    
    /**
     * Gets the tool bar filter controller
     * 
     * @return
     */
    private FilterController getToolBarFilterController() {
        if (toolBarFilterController == null) {
            toolBarFilterController = new FilterController(getFrame().getPlayerControls().getFilterPanel(), getState());
        }
        return toolBarFilterController;
    }
    
    /**
     * Return filters
     * @return
     */
    private Map<String, IFilter> getFilters() {
    	if (filters == null) {
    		filters = new HashMap<String, IFilter>();
    	}
    	return filters;
    }
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

}
