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

package net.sourceforge.atunes.kernel.modules.filter;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.utils.Logger;

public final class FilterHandler extends AbstractHandler implements IFilterHandler {

    /**
     * Text used as filter
     */
    private Map<IFilter, String> currentFilterText;
    
    private FilterController playListFilterPanelController;
    
    private FilterController navigatorFilterPanelController;
    
    private IFilter navigationTreeFilter;
    
    private IFilter playListFilter;
    
    /**
     * @param playListFilter
     */
    public void setPlayListFilter(IFilter playListFilter) {
		this.playListFilter = playListFilter;
	}
    
    /**
     * @param navigationTreeFilter
     */
    public void setNavigationTreeFilter(IFilter navigationTreeFilter) {
		this.navigationTreeFilter = navigationTreeFilter;
	}
    
    /**
     * @param navigatorFilterPanelController
     */
    public void setNavigatorFilterPanelController(FilterController navigatorFilterPanelController) {
		this.navigatorFilterPanelController = navigatorFilterPanelController;
	}
    
    /**
     * @param playListFilterPanelController
     */
    public void setPlayListFilterPanelController(FilterController playListFilterPanelController) {
		this.playListFilterPanelController = playListFilterPanelController;
	}
    
    /**
     * Default constructor
     */
    public FilterHandler() {
    	this.currentFilterText = new HashMap<IFilter, String>();
	}
    
    @Override
	public void applyFilter(IFilter filter, String filterText) {
        this.currentFilterText.put(filter, filterText);
        Logger.debug("Applying filter: ", filter.getName(), " with text: ", filterText);
        filter.applyFilter(filterText);
    }

    @Override
    public void allHandlersInitialized() {
    	// Set filter for each filter panel
    	playListFilterPanelController.setFilter(playListFilter);
    	navigatorFilterPanelController.setFilter(navigationTreeFilter);
    }

    @Override
	public String getFilterText(IFilter filter) {
        return this.currentFilterText.get(filter);
    }
}
