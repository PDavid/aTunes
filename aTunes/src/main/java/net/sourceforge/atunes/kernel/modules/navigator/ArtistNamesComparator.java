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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ArtistNamesComparator implements Comparator<String>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 681733305318937293L;
	private final Collator collator;

	/**
	 * @param collator
	 */
	ArtistNamesComparator(Collator collator) {
		this.collator = collator;
	}

	private static final Pattern PATTERN = Pattern.compile("(.*)\\s+(.*?)");

	@Override
	public int compare(String s1, String s2) {
	    String[] ss1 = s1.split("[,\\&]");
	    String[] ss2 = s2.split("[,\\&]");
	    String d1 = getStringForSorting(s1, ss1);
	    String d2 = getStringForSorting(s2, ss2);
	    return this.collator.compare(d1.toLowerCase(), d2.toLowerCase());
	}

	private String getStringForSorting(String s, String[] ss) {
	    StringBuilder sb = new StringBuilder();
	    for (String k : ss) {
	        Matcher matcher = PATTERN.matcher(k.trim());
	        String m = s;
	        String n = "";
	        if (matcher.matches()) {
	            m = matcher.group(2);
	            n = matcher.group(1);
	        }
	        sb.append(m);
	        sb.append(n);
	    }
	    return sb.toString();
	}
}