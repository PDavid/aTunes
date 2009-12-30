/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.Map;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.I18nUtils;

public class FilterHandler extends Handler {
	

	/**
	 * Singleton instance
	 */
	private static FilterHandler instance;
	
	/**
	 * Available filters
	 */
	protected Map<String, Filter> filters;
	
	/**
	 * Filter for all current filters at the same time
	 */
	private Filter allFilter = new Filter() {
		
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
			for (Filter filter : filters.values()) {
				if (!filter.equals(this)) {
					filter.applyFilter(filterString);
				}
			}
			
		}
	};
	
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
		this.filters = new HashMap<String, Filter>();
	}
	
	@Override
	protected void initHandler() {
		addFilter(allFilter);
		
		addFilter(NavigationHandler.getInstance().getTreeFilter());
		setFilterEnabled(NavigationHandler.getInstance().getTreeFilter(), ApplicationState.getInstance().isShowNavigationTree());
		
		addFilter(NavigationHandler.getInstance().getTableFilter());
		setFilterEnabled(NavigationHandler.getInstance().getTableFilter(), ApplicationState.getInstance().isShowNavigationTable());
		
		addFilter(PlayListHandler.getInstance().getPlayListFilter());
		
		ControllerProxy.getInstance().getToolBarFilterController().setSelectedFilter(allFilter.getName());
		
		
		
	}
	
	/**
	 * Getter for singleton instance
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
	 * @param filter
	 */
	private void addFilter(Filter filter) {
		this.filters.put(filter.getName(), filter);
		// Update UI to show new available filter
		ControllerProxy.getInstance().getToolBarFilterController().addFilter(filter);
	}
	
	/**
	 * Removes a filter
	 * @param filter
	 */
	public void removeFilter(Filter filter) {
		this.filters.remove(filter.getName());
		// Update UI to hide filter
		ControllerProxy.getInstance().getToolBarFilterController().removeFilter(filter.getName());		
	}
	
	/**
	 * Applies given filter which is the current filter
	 * @param filter
	 */
	public void applyFilter(String filter) {
		this.currentFilterText = filter;
		getLogger().debug(LogCategories.HANDLER, "Applying filter: ", filter);
		filters.get(selectedFilter).applyFilter(filter);
	}
	
	@Override
	public void applicationStarted() {
		// TODO Auto-generated method stub
	}

	@Override
	public void applicationFinish() {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void applicationStateChanged(ApplicationState newState) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * @param selectedFilter the selectedFilter to set
	 */
	public void setSelectedFilter(String selectedFilter) {
		this.selectedFilter = selectedFilter;
	}
	
	/**
	 * Returns filter
	 * @return
	 */
	public String getFilter() {
		return this.currentFilterText;
	}
	
	/**
	 * Returns <code>true</code> if given filter is selected (or "all" is selected) and filter text is not null
	 * @param filter
	 * @return
	 */
	public boolean isFilterSelected(Filter filter) {
		return (this.selectedFilter.equals(allFilter.getName()) || this.selectedFilter.equals(filter.getName())) 
			&& getFilter() != null;
	}
	
	/**
	 * Sets filter enabled or disabled
	 * @param filter
	 * @param enabled
	 */
	public void setFilterEnabled(Filter filter, boolean enabled) {
		// If filter is selected and must be disabled change to "all" and set filter null
		if (this.selectedFilter.equals(filter.getName()) && !enabled) {
			this.selectedFilter = allFilter.getName();
			ControllerProxy.getInstance().getToolBarFilterController().setSelectedFilter(allFilter.getName());
			applyFilter(null);
		}
		ControllerProxy.getInstance().getToolBarFilterController().setFilterEnabled(filter.getName(), enabled);
		
	}

}
