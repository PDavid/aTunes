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

package net.sourceforge.atunes.kernel;

import java.io.IOException;
import java.util.Properties;

import javax.swing.Action;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IApplicationUpdatedListener;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.PropertiesUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * counts how many times application has been started
 * 
 * @author alex
 * 
 */
public final class StartCounter implements IApplicationUpdatedListener {

	private IOSManager osManager;

	private String counterFile;

	private String counterProperty;

	private int counterLevelNeededToFireAction;

	private String dontFireActionProperty;

	private String userDonatedProperty;

	private Action actionToFire;

	/**
	 * @param userDonatedProperty
	 */
	public void setUserDonatedProperty(final String userDonatedProperty) {
		this.userDonatedProperty = userDonatedProperty;
	}

	/**
	 * @param dontFireActionProperty
	 */
	public void setDontFireActionProperty(final String dontFireActionProperty) {
		this.dontFireActionProperty = dontFireActionProperty;
	}

	/**
	 * @param counterLevelNeededToFireAction
	 */
	public void setCounterLevelNeededToFireAction(
			final int counterLevelNeededToFireAction) {
		this.counterLevelNeededToFireAction = counterLevelNeededToFireAction;
	}

	/**
	 * @param actionToFire
	 */
	public void setActionToFire(final Action actionToFire) {
		this.actionToFire = actionToFire;
	}

	/**
	 * @param counterProperty
	 */
	public void setCounterProperty(final String counterProperty) {
		this.counterProperty = counterProperty;
	}

	/**
	 * @param counterFile
	 */
	public void setCounterFile(final String counterFile) {
		this.counterFile = counterFile;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * Initializes counter and adds 1
	 */
	public void addOne() {
		Properties properties = getProperties();
		Logger.info("Start count: ", addOneToCounter(properties));
		try {
			PropertiesUtils.writeProperties(getCounterFilePath(), properties);
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	private void reset() {
		Properties properties = getProperties();
		if (!isUserDonated()) {
			// Reset only if user has not donated yet
			properties.put(this.counterProperty, String.valueOf(1));
			properties
					.put(this.dontFireActionProperty, Boolean.toString(false));

			try {
				PropertiesUtils.writeProperties(getCounterFilePath(),
						properties);
			} catch (IOException e) {
				Logger.error(e);
			}
		}
	}

	/**
	 * @return value of counter
	 */
	public int getCounter() {
		return readCounter(getProperties());
	}

	/**
	 * @param properties
	 * @return
	 */
	private int addOneToCounter(final Properties properties) {
		int counter = readCounter(properties);
		counter++;
		properties.put(this.counterProperty, String.valueOf(counter));
		return counter;
	}

	/**
	 * @param properties
	 * @return
	 */
	private int readCounter(final Properties properties) {
		int counter = 0;
		try {
			String valueString = properties.getProperty(this.counterProperty);
			if (valueString != null) {
				counter = Integer.valueOf(valueString);
			}
		} catch (NumberFormatException e) {
			Logger.error(e);
		}
		return counter;
	}

	/**
	 * @param properties
	 * @return
	 */
	private void dontFireActionAgain(final Properties properties) {
		properties.put(this.dontFireActionProperty, Boolean.toString(true));
	}

	/**
	 * Called when user makes a donation so action will not be fired again
	 */
	public void userDonated() {
		Properties properties = getProperties();
		properties.put(this.userDonatedProperty, Boolean.toString(true));
		try {
			PropertiesUtils.writeProperties(getCounterFilePath(), properties);
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	/**
	 * loads properties where counter is defined
	 */
	private Properties getProperties() {
		return PropertiesUtils.readProperties(getCounterFilePath());
	}

	/**
	 * @return
	 */
	private String getCounterFilePath() {
		return StringUtils.getString(this.osManager.getUserConfigFolder(),
				this.osManager.getFileSeparator(), this.counterFile);
	}

	/**
	 * Checks counter value
	 */
	public void checkCounter() {
		if (!isDontFireActionAgain() && !isUserDonated()) {
			if (getCounter() >= this.counterLevelNeededToFireAction) {
				GuiUtils.callInEventDispatchThread(new Runnable() {
					@Override
					public void run() {
						StartCounter.this.actionToFire.actionPerformed(null);
					}
				});
			}
		}
	}

	/**
	 * @return if it's the first time the action must be fired
	 */
	public boolean isFirstTimeActionFired() {
		return getCounter() == this.counterLevelNeededToFireAction;
	}

	/**
	 * @return
	 */
	private boolean isDontFireActionAgain() {
		Object property = getProperties().get(this.dontFireActionProperty);
		return property != null ? Boolean.valueOf((String) property) : false;
	}

	/**
	 * @return
	 */
	private boolean isUserDonated() {
		Object property = getProperties().get(this.userDonatedProperty);
		return property != null ? Boolean.valueOf((String) property) : false;
	}

	/**
	 * Blocks action to be fired again
	 */
	public void dontFireActionAgain() {
		Properties properties = getProperties();
		dontFireActionAgain(properties);
		try {
			PropertiesUtils.writeProperties(getCounterFilePath(), properties);
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	@Override
	public void versionChanged(final String oldVersion, final String newVersion) {
		Logger.info(String.format("Version changed from '%s' to '%s'",
				oldVersion, newVersion));
		reset();
	}
}
