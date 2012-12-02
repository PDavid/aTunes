/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.search;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.ISearchResult;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;

/**
 * This class represents a raw search result by Lucene.
 */
public class RawSearchResult implements ISearchResult {

    private final Document document;
    private final float score;

    /**
     * @param document
     * @param score
     */
    public RawSearchResult(final Document document, final float score) {
	this.document = document;
	this.score = score;
    }

    /**
     * @return the document
     */
    @Override
    public Map<String, String> getObject() {
	Map<String, String> objects = new HashMap<String, String>();
	for (Fieldable field : document.getFields()) {
	    objects.put(field.name(), document.get(field.name()));
	}
	return objects;
    }

    /**
     * @return the score
     */
    @Override
    public float getScore() {
	return score;
    }

}
