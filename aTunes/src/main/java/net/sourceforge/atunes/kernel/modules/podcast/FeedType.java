/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.modules.podcast;

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
    RSS("./author", "./pubDate", "./description", "./duration", "/rss/channel/item", "/rss/channel/title", "./title", "./enclosure/@type", "./enclosure/@url"),

    /**
     * Atom feed, more info: <a
     * href="http://en.wikipedia.org/wiki/Atom_%28standard%29"
     * >http://en.wikipedia.org/wiki/Atom_%28standard%29</a>
     */
    ATOM("./author/name", "./updated", "./summary", "", "/feed/entry", "/feed/title", "./title", "./link[@rel='enclosure']/@type", "./link[@rel='enclosure']/@href");

    private String authorXPath;
    private String dateXPath;
    private String descriptionXPath;
    private String durationXPath;
    private String entryXPath;
    private String nameXPath;
    private String titleXPath;
    private String typeXPath;
    private String urlXPath;

    private FeedType(String authorXPath, String dateXPath, String descriptionXPath, String durationXPath, String entryXPath, String nameXPath, String titleXPath, String typeXPath,
            String urlXPath) {
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

    public String getAuthorXPath() {
        return authorXPath;
    }

    public String getDateXPath() {
        return dateXPath;
    }

    public String getDescriptionXPath() {
        return descriptionXPath;
    }

    public String getDurationXPath() {
        return durationXPath;
    }

    public String getEntryXPath() {
        return entryXPath;
    }

    public String getNameXPath() {
        return nameXPath;
    }

    public String getTitleXPath() {
        return titleXPath;
    }

    public String getTypeXPath() {
        return typeXPath;
    }

    public String getUrlXPath() {
        return urlXPath;
    }

}
