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

package net.sourceforge.atunes.utils;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;

/**
 * Comparator for strings using collator
 * 
 * @author alex
 * 
 */
public final class DefaultComparator implements Comparator<String>,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7737153282909609592L;
	private final Collator collator;

	/**
	 * @param collator
	 */
	public DefaultComparator(final Collator collator) {
		this.collator = collator;
	}

	@Override
	public int compare(final String s1, final String s2) {
		return this.collator.compare(s1.toLowerCase(), s2.toLowerCase());
	}
}