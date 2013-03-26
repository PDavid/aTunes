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

import java.util.List;


/**
 * A set of columns used in a table
 * @author alex
 *
 */
public interface IColumnSet {

	/**
	 * Store current column settings.
	 */
	public void saveColumnSet();

	/**
	 * Returns the amount of columns visible in this set
	 * 
	 * @return
	 */
	public int getVisibleColumnCount();

	/**
	 * Returns columns in order.
	 * 
	 * @return the columns ordered
	 */
	public List<IColumn<?>> getColumnsOrdered();

	/**
	 * Sets columns
	 */
	public void setCurrentColumns();

	/**
	 * Returns Column ID given a visible column number
	 * 
	 * @param colIndex
	 *            the col index
	 * 
	 * @return the column id
	 */
	public Class<? extends IColumn<?>> getColumnId(int colIndex);

	/**
	 * Returns columns for selection
	 * 
	 * @return the columns for selection
	 */
	public List<IColumn<?>> getColumnsForSelection();

	/**
	 * Returns a column object given its class name
	 * 
	 * @param columnClass
	 * @return
	 */
	public IColumn<?> getColumn(Class<? extends IColumn<?>> columnClass);

	/**
	 * Filters audio objects with given filter and current visible columns of
	 * this column set
	 * 
	 * @param audioObjects
	 * @param filter
	 * @return
	 */
	public List<IAudioObject> filterAudioObjects(
			List<IAudioObject> audioObjects, String filter);

	/**
	 * Called to remove an available column
	 * 
	 * @param columnClass
	 */
	public void removeColumn(Class<?> columnClass);

	/**
	 * Returns the column sorted (if any)
	 * 
	 * @return
	 */
	public IColumn<?> getSortedColumn();

}