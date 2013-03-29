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

package net.sourceforge.atunes.kernel.modules.filter;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.utils.Logger;

/**
 * Manages play list and navigator filters
 * 
 * @author alex
 * 
 */
public final class FilterHandler extends AbstractHandler implements
		IFilterHandler {

	/**
	 * Text used as filter
	 */
	private final Map<IFilter, String> currentFilterText;

	private FilterController playListFilterPanelController;

	private FilterController navigatorFilterPanelController;

	private FilterController navigatorTableFilterPanelController;

	private IFilter navigationTreeFilter;

	private IFilter navigationTableFilter;

	private IFilter playListFilter;

	/**
	 * @param navigatorTableFilterPanelController
	 */
	public void setNavigatorTableFilterPanelController(
			FilterController navigatorTableFilterPanelController) {
		this.navigatorTableFilterPanelController = navigatorTableFilterPanelController;
	}

	/**
	 * @param navigationTableFilter
	 */
	public void setNavigationTableFilter(IFilter navigationTableFilter) {
		this.navigationTableFilter = navigationTableFilter;
	}

	/**
	 * @param playListFilter
	 */
	public void setPlayListFilter(final IFilter playListFilter) {
		this.playListFilter = playListFilter;
	}

	/**
	 * @param navigationTreeFilter
	 */
	public void setNavigationTreeFilter(final IFilter navigationTreeFilter) {
		this.navigationTreeFilter = navigationTreeFilter;
	}

	/**
	 * @param navigatorFilterPanelController
	 */
	public void setNavigatorFilterPanelController(
			final FilterController navigatorFilterPanelController) {
		this.navigatorFilterPanelController = navigatorFilterPanelController;
	}

	/**
	 * @param playListFilterPanelController
	 */
	public void setPlayListFilterPanelController(
			final FilterController playListFilterPanelController) {
		this.playListFilterPanelController = playListFilterPanelController;
	}

	/**
	 * Default constructor
	 */
	public FilterHandler() {
		this.currentFilterText = new HashMap<IFilter, String>();
	}

	@Override
	public void applyFilter(final IFilter filter, final String filterText) {
		this.currentFilterText.put(filter, filterText);
		Logger.debug("Applying filter: ", filter.getName(), " with text: ",
				filterText);
		filter.applyFilter(filterText);
	}

	@Override
	public void allHandlersInitialized() {
		// Set filter for each filter panel
		playListFilterPanelController.setFilter(playListFilter);
		navigatorFilterPanelController.setFilter(navigationTreeFilter);
		navigatorTableFilterPanelController.setFilter(navigationTableFilter);
	}

	@Override
	public String getFilterText(final IFilter filter) {
		return this.currentFilterText.get(filter);
	}
}
