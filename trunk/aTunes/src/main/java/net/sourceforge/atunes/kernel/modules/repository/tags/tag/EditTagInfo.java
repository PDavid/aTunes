/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.repository.tags.tag;

import java.util.HashMap;
import java.util.Map;

public class EditTagInfo {

    private Map<String, Object> properties = new HashMap<String, Object>();

    public EditTagInfo() {
    }

    /**
     * Gets the value for the specified tag key
     * 
     * @param key
     *            the tag key
     * @return the value fo the specified tag key
     */
    public Object get(String key) {
        return properties.get(key);
    }

    /**
     * Puts the value for the specified tag
     * 
     * @param key
     *            the tag
     * @param value
     *            that value
     */
    public void put(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * Returns if a tag is edited
     * 
     * @param tag
     *            tag
     * @return if a tag is edited
     */
    public boolean isTagEdited(String tag) {
        return properties.containsKey(tag);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return properties.toString();
    }

}
