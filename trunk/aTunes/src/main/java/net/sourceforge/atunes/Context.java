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
import java.io.FilenameFilter;
import java.net.URISyntaxException;
import java.util.Collection;

import net.sourceforge.atunes.utils.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Context {

	private static ApplicationContext context;
	
	private Context() {
	}
	
	/**
	 * Initializes Spring with path where bean definition files are
	 * @param path
	 */
	public static void initialize(String path) {
		File folder = null;
		String[] paths = null;
		try {
			folder = new File(Context.class.getResource(path).toURI());
			File[] files = folder.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".xml");
				}
			});
			paths = new String[files.length];
			for (int i = 0; i < paths.length; i++) {
				paths[i] = StringUtils.getString(path, files[i].getName());
			} 
			
			context = new ClassPathXmlApplicationContext(paths);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static <T> T getBean(Class<T> beanType) {
		return context.getBean(beanType);
	}
	
	public static Object getBean(String name) {
		return context.getBean(name);
	}
	
	public static <T> Collection<T> getBeans(Class<T> beanType) {
		return context.getBeansOfType(beanType).values();
	}
	
	
}
