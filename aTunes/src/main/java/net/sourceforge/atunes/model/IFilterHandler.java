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

package net.sourceforge.atunes.model;


/**
 * Responsible of filtering navigator, playlist, etc
 * @author alex
 *
 */
public interface IFilterHandler extends IHandler {

	/**
	 * Removes a filter
	 * 
	 * @param filter
	 */
	public void removeFilter(IFilter filter);

	/**
	 * Applies given filter which is the current filter
	 * 
	 * @param filter
	 */
	public void applyFilter(String filter);

	/**
	 * @param selectedFilter
	 *            the selectedFilter to set
	 */
	public void setSelectedFilter(String selectedFilter);

	/**
	 * Returns filter
	 * 
	 * @return
	 */
	public String getFilter();

	/**
	 * Returns <code>true</code> if given filter is selected (or "all" is
	 * selected) and filter text is not null
	 * 
	 * @param filter
	 * @return
	 */
	public boolean isFilterSelected(IFilter filter);

	/**
	 * Sets filter enabled or disabled
	 * 
	 * @param filter
	 * @param enabled
	 */
	public void setFilterEnabled(IFilter filter, boolean enabled);

}