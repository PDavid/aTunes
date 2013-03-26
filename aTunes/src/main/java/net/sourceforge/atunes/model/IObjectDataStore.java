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

/**
 * Responsible of storing data objects
 * @author alex
 *
 * @param <T>
 */
public interface IObjectDataStore<T> {

	/**
	 * Writes object to data store
	 * @param id
	 * @param object
	 */
	void write(String id, T object);
	
	/**
	 * Reads object from data store
	 * @param id
	 * @return object read
	 */
	T read(String id);

	/**
	 * Writes object to data store
	 * @param object
	 */
	void write(T object);
	
	/**
	 * Reads object from data store
	 * @return object read
	 */
	T read();
}
