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

import java.util.Collection;

/**
 * Factory of beans (used to avoid direct references to Spring context)
 * 
 * @author alex
 * 
 */
public interface IBeanFactory {

	/**
	 * Get bean of given class
	 * 
	 * @param <T>
	 * @param beanType
	 * @return
	 */
	<T> T getBean(Class<T> beanType);

	/**
	 * Returns all beans of a given type
	 * 
	 * @param <T>
	 * @param beanType
	 * @return
	 */
	<T> Collection<T> getBeans(Class<T> beanType);

	/**
	 * Returns bean with given name and type
	 * 
	 * @param <T>
	 * @param name
	 * @param clazz
	 * @return
	 */
	<T> T getBean(String name, Class<T> clazz);

	/**
	 * Returns bean with given class name and type
	 * 
	 * @param <T>
	 * @param className
	 * @param clazz
	 * @return
	 */
	<T> T getBeanByClassName(String className, Class<T> clazz);

}
