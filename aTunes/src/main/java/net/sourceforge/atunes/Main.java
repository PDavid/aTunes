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

package net.sourceforge.atunes;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Main class to launch aTunes.
 */
public final class Main {

	private static final String SPRING_BEANS_XML = "/settings/spring/spring_beans.xml";

	private Main() {
	}

	/**
	 * Main method for calling aTunes.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		List<String> arguments = StringUtils.fromStringArrayToList(args);

		// Add minimal log properties for Spring
		Logger.loadInitProperties(arguments
				.contains(ApplicationArguments.DEBUG));

		// Enable a basic uncaught exception handler to register problems with
		// Spring
		Thread.setDefaultUncaughtExceptionHandler(new BasicUncaughtExceptionHandler());

		// Initialize Spring
		ApplicationContext context = initialize();

		// Enable uncaught exception catching
		Thread.setDefaultUncaughtExceptionHandler(context
				.getBean(UncaughtExceptionHandler.class));

		// Save arguments, if application is restarted they will be necessary
		context.getBean(IApplicationArguments.class).saveArguments(arguments);

		// Now start application
		context.getBean(ApplicationStarter.class).start(arguments);
	}

	/**
	 * Initializes Spring with bean definition files
	 * 
	 * @return
	 */
	private static ApplicationContext initialize() {
		ApplicationContext context = null;
		File springBeans = ResourceLocator.getFile(SPRING_BEANS_XML);
		if (springBeans != null) {
			context = new ClassPathXmlApplicationContext(
					StringUtils.getString("file:"
							+ net.sourceforge.atunes.utils.FileUtils
									.getPath(springBeans)));
		} else {
			// Use classpath
			context = new ClassPathXmlApplicationContext(SPRING_BEANS_XML);
		}
		return context;
	}
}
