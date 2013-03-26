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
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

class ContextInitializer {

	private static final String SPRING_BEANS_XML = "/settings/spring/spring_beans.xml";

	private static final String SWING_BEANS_XML = "/settings/spring/swing_beans.xml";

	private static final String JAVAFX_BEANS_XML = "/settings/spring/javafx_beans.xml";

	private ContextInitializer() {
	}

	/**
	 * Initializes Spring with bean definition files
	 * 
	 * @param useJavaFX
	 * @return
	 */
	static ApplicationContext initialize(boolean useJavaFX) {
		ApplicationContext context = null;
		List<String> configuration = new ArrayList<String>();

		setupConfiguration(SPRING_BEANS_XML, configuration);

		if (useJavaFX) {
			setupConfiguration(JAVAFX_BEANS_XML, configuration);
		} else {
			setupConfiguration(SWING_BEANS_XML, configuration);
		}

		context = new ClassPathXmlApplicationContext(
				configuration.toArray(new String[configuration.size()]));
		return context;
	}

	private static void setupConfiguration(String beansFile,
			List<String> configuration) {
		File file = ResourceLocator.getFile(beansFile);
		if (file != null) {
			configuration.add("file:"
					+ net.sourceforge.atunes.utils.FileUtils.getPath(file));
		} else {
			// Use classpath
			configuration.add(beansFile);
		}
	}
}
