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

import java.util.Collection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Context {

	private static ApplicationContext context;
	
	private Context() {
	}
	
	/**
	 * Initializes Spring with bean definition files
	 */
	public static void initialize() {
		context = new ClassPathXmlApplicationContext(
				"/settings/spring/actions.xml", 
				"/settings/spring/columnsets.xml", 
				"/settings/spring/context.xml", 
				"/settings/spring/core.xml", 
				"/settings/spring/dialogs.xml", 
				"/settings/spring/genres.xml", 
				"/settings/spring/gui.xml", 
				"/settings/spring/gui.icons.xml",
				"/settings/spring/gui.player.xml", 
				"/settings/spring/handlers.xml", 
				"/settings/spring/lookandfeel.xml", 
				"/settings/spring/navigationviews.xml", 
				"/settings/spring/os.xml", 
				"/settings/spring/player.xml", 
				"/settings/spring/repository.xml",
				"/settings/spring/tasks.xml", 
				"/settings/spring/treecelldecorators.xml", 
				"/settings/spring/treegenerators.xml", 
				"/settings/spring/utils.xml", 
				"/settings/spring/webservices.xml" 
				);
	}
	
	public static <T> T getBean(Class<T> beanType) {
		return context.getBean(beanType);
	}
	
	public static Object getBean(String name) {
		return context.getBean(name);
	}

	public static <T> T getBean(String name, Class<T> clazz) {
		return context.getBean(name, clazz);
	}
	public static <T> Collection<T> getBeans(Class<T> beanType) {
		return context.getBeansOfType(beanType).values();
	}
	
	
}
