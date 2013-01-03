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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A list of objects with a pointer
 * @author alex
 *
 * @param <T>
 */
public class PointedList<T> implements Serializable {

    private static final long serialVersionUID = 5516540213103483594L;

    /**
     * List with objects
     */
    List<T> list = null;

    /**
     * Pointer to object or null. Initially first object is pointed
     */
    Integer pointer = 0;

    /**
     * Default constructor
     */
    public PointedList() {
        this.list = new ArrayList<T>();
    }

    /**
     * Construct a new pointed list from another pointed list
     * 
     * @param pointedList
     */
    public PointedList(PointedList<? extends T> pointedList) {
        this.list = new ArrayList<T>(pointedList.getList());
        this.pointer = pointedList.pointer == null ? null : Integer.valueOf(pointedList.pointer);
    }

    /**
     * Returns current pointed object
     * 
     * @return the current object
     */
    public T getCurrentObject() {
        if (this.pointer == null || isEmpty() || size() <= this.pointer) {
            return null;
        }
        return get(pointer);
    }

    /**
     * Returns next object
     * @param index 
     * @return the next object
     */
    public T getNextObject(int index) {
        return getNextObject(false, index);
    }

    /**
     * Returns previous object
     * @param index
     * @return the previous object
     */
    public T getPreviousObject(int index) {
        return getPreviousObject(false, index);
    }

    /**
     * Moves pointer to next object
     * 
     * @return the next object
     */
    public T moveToNextObject() {
        return getNextObject(true, 1);
    }

    /**
     * Moved pointer to previous object
     * 
     * @return the previous object
     */
    public T moveToPreviousObject() {
        return getPreviousObject(true, 1);
    }

    /**
     * Sorts this list with given comparator
     * 
     * @param c
     */
    public void sort(Comparator<T> c) {
        if (isEmpty()) {
            return;
        }
        T currentObject = getCurrentObject();
        Collections.sort(getList(), c);
        if (currentObject != null) {
        	setPointer(indexOf(currentObject));
        }
    }

    /**
     * Shuffles this list keeping pointed object
     */
    public void shuffle() {
        if (isEmpty()) {
            return;
        }
        T currentObject = getCurrentObject();
        Collections.shuffle(getList());
        if (currentObject != null) {
        	setPointer(indexOf(currentObject));
        }
    }

    /**
     * @param element
     */
    public void add(T element) {
        add(getList().size(), element);
    }

    /**
     * @param index
     * @param element
     */
    public void add(int index, T element) {
        getList().add(index, element);
        if (size() > 1 && this.pointer != null && index <= this.pointer) {
            this.pointer = this.pointer + 1;
        }
    }

    /**
     * @param index
     * @param c
     * @return
     */
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean result = getList().addAll(index, c);
        if (size() > 1 && this.pointer != null && index <= this.pointer) {
            this.pointer = this.pointer + c.size();
        }
        return result;
    }

    /**
     * @param index
     * @return
     */
    public T remove(int index) {
        if (index < 0 || getList().size() <= index) {
            return null;
        }

        if (this.pointer != null && index < this.pointer) {
            // If position to remove is under pointer, move pointer one position 
            this.pointer = this.pointer - 1;
        } else if (this.pointer != null && index == this.pointer && this.pointer == size() - 1) {
            // If pointed and removed object is the last one, update pointer to previous
        	this.pointer = size() - 2;
        	// Only one item, put pointer to null
        	if (this.pointer == -1) {
        		this.pointer = null;
        	}
        }
        return getList().remove(index);
    }

    /**
     * Empty list
     */
    public void clear() {
        this.pointer = 0;
        getList().clear();
    }

    /**
     * Replaces an element in a position
     * @param index
     * @param newObject
     */
    public void replace(int index, T newObject) {
        if (index < 0 || getList().size() <= index || newObject == null) {
            return;
        }

        getList().remove(index);
        getList().add(index, newObject);
    }

    /**
     * Sets pointer
     * 
     * @param pointer
     */
    public void setPointer(Integer pointer) {
        this.pointer = pointer;
    }

    /**
     * Returns next object and updates pointer if necessary
     * 
     * @return the next object
     */
    private T getNextObject(boolean movePointer, int index) {
        if (isEmpty()) {
            return null;
        }

        Integer nextObjectIndex = null;
        if (this.pointer == null) {
            nextObjectIndex = 0;
        } else {
            nextObjectIndex = (this.pointer + index) % size();
            // End of list reached and cyclic disabled, return null
            if (nextObjectIndex < index && !isCyclic()) {
                nextObjectIndex = null;
                return null;
            }
        }

        if (movePointer) {
            this.pointer = nextObjectIndex;
        }

        // Return object
        return get(nextObjectIndex);
    }

    /**
     * Returns previous object and updates pointer if necessary
     * 
     * @return the previous object
     */
    private T getPreviousObject(boolean movePointer, int index) {
        if (isEmpty()) {
            return null;
        }

        Integer previousObjectIndex = null;
        if (this.pointer == null) {
            previousObjectIndex = size() - index;
        } else {
            previousObjectIndex = this.pointer - index;
            // Beginning of list reached and cyclic disabled, return null, otherwise move to end of list
            if (previousObjectIndex <= -index) {
                if (!isCyclic()) {
                    previousObjectIndex = null;
                    return null;
                }
                previousObjectIndex = size() - index;
            }
        }

        if (movePointer) {
            this.pointer = previousObjectIndex;
        }

        // Return object
        return get(previousObjectIndex);
    }

    /**
     * Returns <code>true</code> if list is cyclic
     * 
     * @return
     */
    public boolean isCyclic() {
        // Default implementation is cyclic
        return true;
    }

    /**
     * @return the pointer
     */
    public Integer getPointer() {
        return this.pointer;
    }

    /**
     * Returns size of this list
     * 
     * @return
     */
    public int size() {
        return getList().size();
    }

    /**
     * Returns if this list is empty
     * 
     * @return
     */
    public boolean isEmpty() {
        return getList().isEmpty();
    }

    /**
     * Returns object at given position
     * 
     * @param index
     * @return
     */
    public T get(int index) {
        if (index >= 0 && getList().size() > index) {
            return getList().get(index);
        }
        return null;
    }

    /**
     * Returns index of given object
     * 
     * @param object
     * @return
     */
    public int indexOf(T object) {
        return getList().indexOf(object);
    }

    /**
     * @return the list
     */
    public List<T> getList() {
        if (this.list == null) {
            this.list = new ArrayList<T>();
        }
        return this.list;
    }

    /**
     * @param object
     * @return true if object in list
     */
    public boolean contains(T object) {
        return getList().contains(object);
    }

    /**
     * Set content without changing pointer
     * 
     * @param content
     */
    public void setContent(List<T> content) {
        getList().addAll(content);
    }
}
