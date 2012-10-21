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

package net.sourceforge.atunes;

import java.io.File;

import net.sourceforge.atunes.utils.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Initializes and gives access to Spring context
 * @author alex
 *
 */
public final class Context {

	private static final String SPRING_BEANS_XML = "/settings/spring/spring_beans.xml";

	private static ApplicationContext context;

	private Context() {
	}

	/**
	 * Initializes Spring with bean definition files
	 * @return
	 */
	public static ApplicationContext initialize() {
		File springBeans = ResourceLocator.getFile(SPRING_BEANS_XML);
		if (springBeans != null) {
			context = new ClassPathXmlApplicationContext(StringUtils.getString("file:" + net.sourceforge.atunes.utils.FileUtils.getPath(springBeans)));
		} else {
			// Use classpath
			context = new ClassPathXmlApplicationContext(SPRING_BEANS_XML);
		}
		return context;
	}

	/**
	 * Returns bean of type
	 * @param <T>
	 * @param beanType
	 * @return
	 */
	@Deprecated
	public static <T> T getBean(final Class<T> beanType) {
		return context.getBean(beanType);
	}

	/**
	 * Returns bean with given name and type
	 * @param <T>
	 * @param name
	 * @param clazz
	 * @return
	 */
	@Deprecated
	public static <T> T getBean(final String name, final Class<T> clazz) {
		return context.getBean(name, clazz);
	}
}
