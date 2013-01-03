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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;

/**
 * Compares strings ignoring leading "the "
 * 
 * @author alex
 * 
 */
final class SmartComparator implements Comparator<String>, Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 7941088987515467343L;

    private final Collator collator;

    /**
     * @param collator
     */
    public SmartComparator(final Collator collator) {
	this.collator = collator;
    }

    private String removeThe(final String str) {
	if (str.toLowerCase().startsWith("the ") && str.length() > 4) {
	    return str.substring(4);
	}
	return str;
    }

    @Override
    public int compare(final String s1, final String s2) {
	return this.collator.compare(removeThe(s1).toLowerCase(), removeThe(s2)
		.toLowerCase());
    }
}