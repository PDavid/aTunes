/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.kernel.modules.search;

import java.util.List;

import net.sourceforge.atunes.model.AudioObject;

import org.apache.lucene.document.Document;

/**
 * This interface represents objects that can be searched with Lucene.
 */
public interface SearchableObject {

    /**
     * Returns string representation of this searchable object.
     * 
     * @return the searchable object name
     */
    public String getSearchableObjectName();

    /**
     * Returns a list of attributes that can be used in queries.
     * 
     * @return the searchable attributes
     */
    public List<String> getSearchableAttributes();

    /**
     * Returns path to index.
     * 
     * @return the path to index
     */
    public String getPathToIndex();

    /**
     * Returns result from hits.
     * 
     * @param rawSearchResults
     *            the raw search results
     * 
     * @return the search result
     * 
     */
    public List<SearchResult> getSearchResult(List<RawSearchResult> rawSearchResults);

    /**
     * Returns elements to index.
     * 
     * @return the elements to index
     */
    public List<AudioObject> getElementsToIndex();

    /**
     * Builds a Lucene Document for a given AudioObject.
     * 
     * @param audioObject
     *            the audio object
     * 
     * @return the document for element
     */
    public Document getDocumentForElement(AudioObject audioObject);

    /**
     * Returns a table model to show search results.
     * 
     * @param results
     *            the results
     * 
     * @return the search results table model
     */
    public SearchResultTableModel getSearchResultsTableModel(List<SearchResult> results);
}
