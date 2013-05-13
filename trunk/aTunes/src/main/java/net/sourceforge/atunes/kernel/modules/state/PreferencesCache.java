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

import net.sf.ehcache.Element;
import net.sourceforge.atunes.utils.AbstractCache;
import net.sourceforge.atunes.utils.Logger;

class PreferencesCache extends AbstractCache implements IStateStore {

	@Override
	public Object retrievePreference(final Preferences preferenceId,
			final Object defaultValue) {
		Element element = get(preferenceId.toString());
		if (element == null) {
			return defaultValue;
		} else {
			Preference preference = (Preference) element.getValue();
			return preference != null ? preference.getValue() : null;
		}
	}

	@Override
	public synchronized void storePreference(final Preferences preferenceId,
			final Object value) {
		if (preferenceId == null) {
			return;
		}

		// Store same preferences even if value is equal, otherwise could cause
		// problems with collections (where equals is always true)
		Preference pref = null;
		if (value != null) {
			pref = new Preference();
			pref.setValue(value);
		}
		Element element = new Element(preferenceId.toString(), pref);
		put(element);
		flush();
		Logger.debug("Stored Preference: ", preferenceId, " Value: ",
				value != null ? value.toString() : null);
	}

	@Override
	public String retrievePasswordPreference(final Preferences preferenceId) {
		Element element = get(preferenceId.toString());
		if (element == null) {
			return null;
		} else {
			PasswordPreference preferences = (PasswordPreference) element
					.getValue();
			return preferences.getPassword();
		}
	}

	@Override
	public synchronized void storePasswordPreference(
			final Preferences preferenceId, final String value) {
		if (preferenceId == null) {
			return;
		}

		Element element = new Element(preferenceId.toString(),
				value != null ? new PasswordPreference(value) : null);
		put(element);
		flush();
		Logger.debug("Stored Password Preference: ", preferenceId);
	}

	@Override
	public void shutdown() {
		dispose();
	}
}
