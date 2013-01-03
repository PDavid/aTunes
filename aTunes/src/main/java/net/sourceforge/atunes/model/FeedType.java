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

/**
 * The type of a podcast feed.<br/>
 * 
 * Note: Some XPath expressions are relative to a entry in a feed!
 */
public enum FeedType {

    /**
     * RSS feed, more info: <a
     * href="http://en.wikipedia.org/wiki/RSS">http://en.
     * wikipedia.org/wiki/RSS</a>
     */
    RSS("./author", "./pubDate", "./description", "./duration",
	    "/rss/channel/item", "/rss/channel/title", "./title",
	    "./enclosure/@type", "./enclosure/@url"),

    /**
     * Atom feed, more info: <a
     * href="http://en.wikipedia.org/wiki/Atom_%28standard%29"
     * >http://en.wikipedia.org/wiki/Atom_%28standard%29</a>
     */
    ATOM("./author/name", "./updated", "./summary", "", "/feed/entry",
	    "/feed/title", "./title", "./link[@rel='enclosure']/@type",
	    "./link[@rel='enclosure']/@href");

    private String authorXPath;
    private String dateXPath;
    private String descriptionXPath;
    private String durationXPath;
    private String entryXPath;
    private String nameXPath;
    private String titleXPath;
    private String typeXPath;
    private String urlXPath;

    private FeedType(final String authorXPath, final String dateXPath,
	    final String descriptionXPath, final String durationXPath,
	    final String entryXPath, final String nameXPath,
	    final String titleXPath, final String typeXPath,
	    final String urlXPath) {
	this.authorXPath = authorXPath;
	this.dateXPath = dateXPath;
	this.descriptionXPath = descriptionXPath;
	this.durationXPath = durationXPath;
	this.entryXPath = entryXPath;
	this.nameXPath = nameXPath;
	this.titleXPath = titleXPath;
	this.typeXPath = typeXPath;
	this.urlXPath = urlXPath;
    }

    /**
     * @return author
     */
    public String getAuthorXPath() {
	return authorXPath;
    }

    /**
     * @return date
     */
    public String getDateXPath() {
	return dateXPath;
    }

    /**
     * @return description
     */
    public String getDescriptionXPath() {
	return descriptionXPath;
    }

    /**
     * @return duration
     */
    public String getDurationXPath() {
	return durationXPath;
    }

    /**
     * @return entry
     */
    public String getEntryXPath() {
	return entryXPath;
    }

    /**
     * @return name
     */
    public String getNameXPath() {
	return nameXPath;
    }

    /**
     * @return title
     */
    public String getTitleXPath() {
	return titleXPath;
    }

    /**
     * @return type
     */
    public String getTypeXPath() {
	return typeXPath;
    }

    /**
     * @return url
     */
    public String getUrlXPath() {
	return urlXPath;
    }

}
