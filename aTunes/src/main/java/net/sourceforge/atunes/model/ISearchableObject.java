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

import java.io.IOException;
import java.util.List;

import org.apache.lucene.store.FSDirectory;

/**
 * This interface represents objects that can be searched with Lucene.
 */
public interface ISearchableObject {

	/**
	 * Returns string representation of this searchable object.
	 * 
	 * @return the searchable object name
	 */
	public String getSearchableObjectName();

	/**
	 * Returns the index directory.
	 * 
	 * @return the index directory
	 * @throws IOException
	 */
	public FSDirectory getIndexDirectory() throws IOException;

	/**
	 * Returns result from hits.
	 * 
	 * @param rawSearchResults
	 *            the raw search results
	 * 
	 * @return the search result
	 * 
	 */
	public List<IAudioObject> getSearchResult(
			List<ISearchResult> rawSearchResults);

	/**
	 * Returns elements to index.
	 * 
	 * @return the elements to index
	 */
	public List<IAudioObject> getElementsToIndex();
}
