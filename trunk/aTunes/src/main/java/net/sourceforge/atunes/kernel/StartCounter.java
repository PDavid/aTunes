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

package net.sourceforge.atunes.kernel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.swing.Action;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * counts how many times application has been started
 * @author alex
 *
 */
public final class StartCounter {

	private IOSManager osManager;
	
	private String counterFile;
	
	private String counterProperty;
	
	private int counter;
	
	private int counterLevelNeededToFireAction;
	
	private Action actionToFire;
	
	/**
	 * @param counterLevelNeededToFireAction
	 */
	public void setCounterLevelNeededToFireAction(
			int counterLevelNeededToFireAction) {
		this.counterLevelNeededToFireAction = counterLevelNeededToFireAction;
	}
	
	/**
	 * @param actionToFire
	 */
	public void setActionToFire(Action actionToFire) {
		this.actionToFire = actionToFire;
	}
	
	/**
	 * @param counterProperty
	 */
	public void setCounterProperty(String counterProperty) {
		this.counterProperty = counterProperty;
	}
	
	/**
	 * @param counterFile
	 */
	public void setCounterFile(String counterFile) {
		this.counterFile = counterFile;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	/**
	 * Initializes counter and adds 1 
	 */
	public void initialize() {
		Properties properties = getProperties();
		counter = addOneToCounter(properties);
		Logger.info("Start count: ", counter);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(getCounterFilePath());
			properties.store(fos, null);
			fos.flush();
		} catch (IOException e) {
			Logger.error(e);
		} finally {
			ClosingUtils.close(fos);
		}
	}

	/**
	 * @param properties
	 * @return
	 */
	private int addOneToCounter(Properties properties) {
		int counter = 0;
		try {
			String valueString = properties.getProperty(counterProperty);
			if (valueString != null) {
				counter = Integer.valueOf(valueString);
			}
		} catch (NumberFormatException e) {
			Logger.error(e);
		}
		counter++;
		properties.put(counterProperty, String.valueOf(counter));
		return counter;
	}

	/**
	 * loads properties where counter is defined
	 */
	private Properties getProperties() {
		Properties properties = new Properties();
		FileReader fr = null;
		try {
			fr = new FileReader(getCounterFilePath());
			properties.load(fr);
		} catch (FileNotFoundException e) {
			Logger.info("Counter file not found");
		} catch (IOException e) {
			Logger.error(e);
		} finally {
			ClosingUtils.close(fr);
		}
		return properties;
	}

	/**
	 * @return
	 */
	private String getCounterFilePath() {
		return StringUtils.getString(osManager.getUserConfigFolder(), osManager.getFileSeparator(), counterFile);
	}

	/**
	 * Checks counter value
	 */
	public void checkCounter() {
		if (counter == counterLevelNeededToFireAction) {
			actionToFire.actionPerformed(null);
		}
	}
}
