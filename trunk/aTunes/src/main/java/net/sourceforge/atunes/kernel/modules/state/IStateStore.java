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

package net.sourceforge.atunes.kernel.modules.state;

interface IStateStore {

	/**
	 * Retrieves a Preference value from cache.
	 * 
	 * @param preferenceId
	 * @param defaultValue
	 * @return
	 */
	public Object retrievePreference(Preferences preferenceId,
			Object defaultValue);

	/**
	 * Stores a Preference at cache.
	 * 
	 * @param preferenceId
	 * @param value
	 */
	public void storePreference(final Preferences preferenceId,
			final Object value);

	/**
	 * Retrieves a Password Preference from cache
	 * 
	 * @param preferenceId
	 * @return
	 */
	public String retrievePasswordPreference(Preferences preferenceId);

	/**
	 * Stores a Password Preference at cache.
	 * 
	 * @param preferenceId
	 * @param value
	 */
	public void storePasswordPreference(Preferences preferenceId, String value);

	/**
	 * Called when application finishes to save flush all data
	 */
	public void shutdown();

}