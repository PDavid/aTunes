/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a Rank: a list of objects, every one with an associate
 * counter. Objects are ordered by this counter. This class is used for
 * statistics
 */
public class RankList<T> implements Serializable {

    private static final long serialVersionUID = -4404155415144124761L;

    /** Order of elements. */
    private List<T> order;

    /** Count of every object. */
    private Map<T, Integer> count;

    /**
     * Constructor.
     */
    public RankList() {
        order = new ArrayList<T>();
        count = new HashMap<T, Integer>();
    }

    /**
     * Adds an object to rank. If rank contains object, adds 1 to counter and
     * updates rank order If not, adds object with count 1
     * 
     * @param obj
     *            the obj
     */
    public void addItem(T obj) {
    	if (obj == null) {
    		return;
    	}
        if (order.contains(obj)) {
            Integer previousCount = count.get(obj);
            count.put(obj, previousCount + 1);
            moveUpOnList(obj);
        } else {
            order.add(obj);
            count.put(obj, 1);
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
    public Integer getCount(T obj) {
        return count.get(obj);
    }

    /**
     * Returns the first n elements count of this rank.
     * 
     * @param n
     *            the n
     * 
     * @return the n first element counts
     */
    public List<Integer> getNFirstElementCounts(int n) {
        int aux = n;
        if (aux <= -1 || aux > order.size()) {
            aux = order.size();
        }
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < aux; i++) {
            result.add(count.get(order.get(i)));
        }
        return result;
    }

    /**
     * Returns the first n elements of this rank.
     * 
     * @param n
     *            the n
     * 
     * @return the n first elements
     */
    public List<T> getNFirstElements(int n) {
        if (n <= -1 || n > order.size()) {
            return new ArrayList<T>(order);
        }
        return new ArrayList<T>(order.subList(0, n));
    }

    /**
     * Gets the order.
     * 
     * @return the order
     */
    public List<T> getOrder() {
        return new ArrayList<T>(order);
    }

    /**
     * Updates order object.
     * 
     * @param obj
     *            the obj
     */
    private void moveUpOnList(T obj) {
        int index = order.indexOf(obj);
        if (index > 0) {
            int previousItemCount = count.get(order.get(index - 1));
            int currentItemCount = count.get(order.get(index));
            if (previousItemCount < currentItemCount) {
                T previous = order.get(index - 1);
                T current = order.get(index);

                order.remove(previous);
                order.remove(current);

                order.add(index - 1, current);
                order.add(index, previous);

                moveUpOnList(obj);
            }
        }
    }

    /**
     * Replaces an object, keeping order and count.
     * 
     * @param oldItem
     *            the old item
     * @param newItem
     *            the new item
     */
    public void replaceItem(T oldItem, T newItem) {
        int order1 = this.order.indexOf(oldItem);
        Integer count1 = this.count.get(oldItem);
        if (order1 != -1 && count1 != null) {
            this.order.remove(order1);
            this.order.add(order1, newItem);

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
        return order.size();
    }

    public void clear() {
        order.clear();
        count.clear();
    }
}
