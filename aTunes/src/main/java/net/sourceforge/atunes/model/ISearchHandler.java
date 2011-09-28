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

package net.sourceforge.atunes.model;

import java.util.List;

import net.sourceforge.atunes.kernel.modules.search.SearchIndexNotAvailableException;
import net.sourceforge.atunes.kernel.modules.search.SearchQuerySyntaxException;

/**
 * Responsible of searching in collections
 * @author alex
 *
 */
public interface ISearchHandler extends IHandler {

    /** Default lucene field. */
    public static final String DEFAULT_INDEX = "any";

	/**
	 * Method to register a searchable object. All searchable objects must call
	 * this method to initialize searches
	 * 
	 * @param so
	 *            the so
	 */
	public void registerSearchableObject(ISearchableObject so);

	/**
	 * Method to unregister a searchable object. After this method is called,
	 * searchable object is not searchable, and must be registered again
	 * 
	 * @param so
	 */
	public void unregisterSearchableObject(ISearchableObject so);

	/**
	 * Starts a search by updating indexes and showing search dialog.
	 */
	public void startSearch();

	/**
	 * Evaluates a search query and returns a result list of audio objects and
	 * scores (0 <= score <= 1).
	 * 
	 * @param queryStr
	 *            The search query
	 * @param searchableObject
	 *            the searchable object
	 * 
	 * @return The search result
	 * 
	 * @throws SearchIndexNotAvailableException
	 *             If no search index was found
	 * @throws SearchQuerySyntaxException
	 *             If the search query has invalid syntax
	 */
	public List<IAudioObject> search(ISearchableObject searchableObject,
			String queryStr) throws SearchIndexNotAvailableException,
			SearchQuerySyntaxException;

	public void refreshSearchResultColumns();

	/**
	 * Shows search results to user
	 * @param searchableObject
	 * @param result
	 */
	public void showSearchResults(ISearchableObject searchableObject, List<IAudioObject> result);
}