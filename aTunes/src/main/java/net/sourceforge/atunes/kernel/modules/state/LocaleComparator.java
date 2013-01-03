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

package net.sourceforge.atunes.kernel.modules.state;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

final class LocaleComparator implements Comparator<Locale>, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8454923131242388725L;
	
	private final Locale currentLocale;

    LocaleComparator(Locale currentLocale) {
        this.currentLocale = currentLocale;
    }

    @Override
    public int compare(Locale l1, Locale l2) {
        return Collator.getInstance().compare(l1.getDisplayName(currentLocale), l2.getDisplayName(currentLocale));
    }
}