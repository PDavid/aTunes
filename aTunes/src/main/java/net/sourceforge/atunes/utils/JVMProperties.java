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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Properties of current Java Virtual Machine
 */
public final class JVMProperties {

	/**
	 * Checks if java 6 update 10 or later is used.
	 * 
	 * @return true, if java 6 update 10 or later is used
	 */
	public boolean isJava6Update10OrLater() {
		return isJava6Update10OrLater(System.getProperty("java.version"));
	}

	/**
	 * Checks if java 6 update 10 or later is used.
	 * 
	 * @param version
	 * @return
	 */
	boolean isJava6Update10OrLater(final String version) {
		return isVersionEqualOrLaterTo(version, 1, 6, 0, 10);
	}

	/**
	 * Checks if java 7 or later is used.
	 * 
	 * @return true, if java 7 or later is used
	 */
	public boolean isJava7OrLater() {
		return isVersionEqualOrLaterTo(System.getProperty("java.version"), 1,
				7, 0, 0);
	}

	/**
	 * Checks if java version is equal or later to a given version
	 * 
	 * @param javaVersion
	 * @param v1Compare
	 * @param v2Compare
	 * @param v3Compare
	 * @param updateCompare
	 * @return
	 */
	private boolean isVersionEqualOrLaterTo(final String javaVersion,
			final int v1Compare, final int v2Compare, final int v3Compare,
			final int updateCompare) {
		if (javaVersion != null) {
			Pattern pattern = Pattern
					.compile("(\\d+)\\.(\\d+)\\.(\\d+)_(\\d+).*");
			Matcher matcher = pattern.matcher(javaVersion);
			if (matcher.find()) {
				try {
					int v1 = Integer.parseInt(matcher.group(1));
					int v2 = Integer.parseInt(matcher.group(2));
					int v3 = Integer.parseInt(matcher.group(3));
					int update = Integer.parseInt(matcher.group(4));
					return isVersionEqualOrLaterTo(v1, v2, v3, update,
							v1Compare, v2Compare, v3Compare, updateCompare);
				} catch (NumberFormatException e) {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Returns if version is equal or later to a version
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param update
	 * @param v1Compare
	 * @param v2Compare
	 * @param v3Compare
	 * @param updateCompare
	 * @return
	 */
	private boolean isVersionEqualOrLaterTo(final int v1, final int v2,
			final int v3, final int update, final int v1Compare,
			final int v2Compare, final int v3Compare, final int updateCompare) {
		if (v1 > v1Compare) {
			return true;
		} else if (v1 == v1Compare) {
			if (v2 > v2Compare) {
				return true;
			} else if (v2 == v2Compare) {
				if (v3 > v3Compare) {
					return true;
				} else if (v3 == v3Compare && update >= updateCompare) {
					return true;
				}
			}
		}
		return false;
	}

}
