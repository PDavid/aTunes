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

package net.sourceforge.atunes.model;

import java.util.Collection;

/**
 * Responsible of searching in collections
 * 
 * @author alex
 * 
 */
public interface ISearchHandler extends IHandler {

	/**
	 * Starts a search by updating indexes and showing search dialog.
	 */
	public void startSearch();

	/**
	 * Starts search dialog to create a query (search will not be executed)
	 * 
	 * @param title
	 * @param text
	 * @return created query
	 */
	public ISearchNode createQuery(String title, String text);

	/**
	 * Starts search dialog to edit a query (search will not be executed)
	 * 
	 * @param query
	 * @param title
	 * @param text
	 * @return query changed or null if user didn't change it
	 */
	public ISearchNode editQuery(ISearchNode query, String title, String text);

	/**
	 * Executes a query to find audio objects
	 * 
	 * @param query
	 * @return results
	 */
	public Collection<IAudioObject> search(ISearchNode query);

	/**
	 * Shows search results to user
	 * 
	 * @param query
	 * @param result
	 */
	public void showSearchResults(ISearchNode query,
			Collection<IAudioObject> result);
}