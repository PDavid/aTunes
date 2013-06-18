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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities for collections
 * 
 * @author alex
 * 
 */
public final class CollectionUtils {

	private CollectionUtils() {
	}

	/**
	 * Returns if collection is empty or null
	 * 
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * Returns given element of list or null
	 * 
	 * @param <T>
	 * @param list
	 * @param index
	 * @return
	 */
	public static <T> T getElementOrNull(final List<T> list, final int index) {
		if (index >= 0 && list.size() > index) {
			return list.get(index);
		}
		return null;
	}

	/**
	 * @param <T>
	 * @param list
	 * @param elements
	 * @return list with all these elements
	 */
	public static <T> List<T> fillCollectionWithElements(final List<T> list,
			final T... elements) {
		for (T element : elements) {
			list.add(element);
		}
		return list;
	}

	/**
	 * @param <T>
	 * @param elements
	 * @return list with all these elements
	 */
	public static <T> List<T> listWith(final T... elements) {
		return fillCollectionWithElements(new ArrayList<T>(), elements);
	}

}
