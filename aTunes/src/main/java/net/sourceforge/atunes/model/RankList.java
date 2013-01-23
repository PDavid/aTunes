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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents a Rank: a list of objects, every one with an associate
 * counter. Objects are ordered by this counter. This class is used for
 * statistics
 * 
 * @param <T>
 */
public class RankList<T> implements Serializable {

	private static final long serialVersionUID = -4404155415144124761L;

	/** Count of every object. */
	final Map<T, Integer> count;

	/**
	 * OLD list with order of objects DEPRECATED: used only to keep
	 * compatibility with statistics of previous versions
	 */
	List<T> order;

	/**
	 * Constructor.
	 */
	public RankList() {
		this.count = new HashMap<T, Integer>();
	}

	/**
	 * Adds an object to rank. If rank contains object, adds 1 to counter and
	 * updates rank order If not, adds object with count 1
	 * 
	 * @param obj
	 *            the obj
	 */
	public void addItem(final T obj) {
		if (obj == null) {
			return;
		}
		if (this.count.containsKey(obj)) {
			Integer previousCount = this.count.get(obj);
			this.count.put(obj, previousCount + 1);
		} else {
			this.count.put(obj, 1);
		}
	}

	/**
	 * Returns count for a given object.
	 * 
	 * @param obj
	 *            the obj
	 * 
	 * @return the count
	 */
	public Integer getCount(final T obj) {
		return this.count.get(obj);
	}

	/**
	 * Returns the first n elements count of this rank.
	 * 
	 * @param n
	 *            the n
	 * 
	 * @return the n first element counts
	 */
	public List<Integer> getNFirstElementCounts(final int n) {
		List<Entry<T, Integer>> list = getElementsSorted();

		int aux = n;
		if (aux <= -1 || aux > list.size()) {
			aux = list.size();
		}
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < aux; i++) {
			result.add(list.get(i).getValue());
		}
		return result;
	}

	/**
	 * @return elements sorted
	 */
	private List<Entry<T, Integer>> getElementsSorted() {
		List<Entry<T, Integer>> list = new ArrayList<Map.Entry<T, Integer>>(
				this.count.entrySet());
		Collections.sort(list, new Comparator<Entry<T, Integer>>() {
			@Override
			public int compare(final Entry<T, Integer> o1,
					final Entry<T, Integer> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		return list;
	}

	/**
	 * Returns the first n elements of this rank or all if n is negative
	 * 
	 * @param n
	 *            the n
	 * 
	 * @return the n first elements
	 */
	public List<T> getNFirstElements(final int n) {
		List<Entry<T, Integer>> list = getElementsSorted();

		List<T> elements = new ArrayList<T>();
		int aux = Math.min(n, list.size());
		if (aux < 0) {
			aux = list.size();
		}
		for (int i = 0; i < aux; i++) {
			elements.add(list.get(i).getKey());
		}
		return elements;
	}

	/**
	 * Gets the order.
	 * 
	 * @return the order
	 */
	public List<T> getOrder() {
		return getNFirstElements(this.count.size());
	}

	/**
	 * Replaces an object, keeping order and count.
	 * 
	 * @param oldItem
	 *            the old item
	 * @param newItem
	 *            the new item
	 */
	public void replaceItem(final T oldItem, final T newItem) {
		Integer count1 = this.count.get(oldItem);
		if (count1 != null) {
			this.count.remove(oldItem);
			this.count.put(newItem, count1);
		}
	}

	/**
	 * Return the size of rank.
	 * 
	 * @return the int
	 */
	public int size() {
		return this.count.size();
	}

	/**
	 * Removes all data
	 */
	public void clear() {
		this.count.clear();
	}
}
