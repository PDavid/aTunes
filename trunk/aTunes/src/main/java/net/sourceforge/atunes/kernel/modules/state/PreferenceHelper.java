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

import net.sourceforge.atunes.utils.Logger;

/**
 * Utilities for reading and writing preferences
 * 
 * @author alex
 * 
 */
public class PreferenceHelper {

	/**
	 * Component responsible of store state
	 */
	private IStateStore stateStore;

	/**
	 * Sets state store
	 * 
	 * @param store
	 */
	public void setStateStore(final IStateStore store) {
		this.stateStore = store;
	}

	@SuppressWarnings("unchecked")
	<T> T getPreference(final Preferences pref, final Class<T> type,
			final T defaultValue) {
		Object o = this.stateStore.retrievePreference(pref, defaultValue);
		if (o != null) {
			if (type.isAssignableFrom(o.getClass())) {
				return (T) o;
			} else {
				Logger.error("Error reading preference ", pref.toString());
				return defaultValue;
			}
		}
		return null;
	}

	void setPreference(final Preferences pref, final Object value) {
		this.stateStore.storePreference(pref, value);
	}

	String getPasswordPreference(final Preferences pref) {
		return this.stateStore.retrievePasswordPreference(pref);
	}

	void setPasswordPreference(final Preferences pref, final String value) {
		this.stateStore.storePasswordPreference(pref, value);
	}

}
