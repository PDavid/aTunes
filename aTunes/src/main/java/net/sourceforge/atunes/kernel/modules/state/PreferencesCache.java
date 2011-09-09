/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.state;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sourceforge.atunes.misc.AbstractCache;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IStateStore;

class PreferencesCache extends AbstractCache implements IStateStore {

    private Cache cache;
    
    /**
     * Creates a new preferences cache
     * @param configFile
     */
    protected PreferencesCache(String configFile) {
        super(PreferencesCache.class.getResource(configFile));
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateStore#clearCache()
	 */
    @Override
	public synchronized boolean clearCache() {
        boolean exception = false;
        try {
            getCache().removeAll();
        } catch (Exception e) {
            Logger.info("Could not delete all files from preferences cache");
            exception = true;
        }
        return exception;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateStore#retrievePreference(net.sourceforge.atunes.kernel.modules.state.Preferences, java.lang.Object)
	 */
    @Override
	public Object retrievePreference(Preferences preferenceId, Object defaultValue) {
        
    	if (getCache()== null) {
        	return defaultValue;
        }
    	Element element = getCache().get(preferenceId.toString());
        if (element == null) {
            return defaultValue;
        } else {
        	Preference preference = (Preference) element.getValue();
            return preference != null ? preference.getValue() : null;
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateStore#storePreference(net.sourceforge.atunes.kernel.modules.state.Preferences, java.lang.Object)
	 */
    @Override
	public synchronized void storePreference(final Preferences preferenceId, final Object value) {
        if (preferenceId == null) {
            return;
        }

        // Store same preferences even if value is equal, otherwise could cause problems with collections (where equals is always true)
        Element element = new Element(preferenceId.toString(), value != null ? new Preference(value) : null);
        getCache().put(element);
        getCache().flush();
        Logger.debug("Stored Preference: ", preferenceId, " Value: ", value != null ? value.toString() : null);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateStore#retrievePasswordPreference(net.sourceforge.atunes.kernel.modules.state.Preferences)
	 */
    @Override
	public String retrievePasswordPreference(Preferences preferenceId) {
    	Element element = getCache().get(preferenceId.toString());
    	if (element == null) {
    		return null;
    	} else {
    		PasswordPreference preferences = (PasswordPreference) element.getValue();
    		return preferences.getPassword();
    	}
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateStore#storePasswordPreference(net.sourceforge.atunes.kernel.modules.state.Preferences, java.lang.String)
	 */
    @Override
	public synchronized void storePasswordPreference(Preferences preferenceId, String value) {
        if (preferenceId == null) {
            return;
        }

        Element element = new Element(preferenceId.toString(), value != null ? new PasswordPreference(value) : null);
        getCache().put(element);
        getCache().flush();
        Logger.debug("Stored Password Preference: ", preferenceId, " Value: ", value != null ? value.toString() : null);
    }

    private Cache getCache() {
    	if (cache == null) {
    		cache = getCache("preferences"); 
    	}
        return cache;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateStore#shutdown()
	 */
    @Override
	public void shutdown() {
        getCache().dispose();
    }
}
