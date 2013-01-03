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
 * Enum for properties available for several types of audio object
 */
public enum AudioObjectProperty {
    /**
     * No properties
     */
    NO_PROPERTIES,
    /**
     * Favorite
     */
    FAVORITE,
    /**
     * Not listened
     */
    NOT_LISTENED_ENTRY,
    /**
     * Downloaded
     */
    DOWNLOADED_ENTRY,
    /**
     * Old entry
     */
    OLD_ENTRY;
}