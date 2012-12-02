/*
 * aTunes 3.0.0
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

import net.sourceforge.atunes.model.IStateDevice;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateDevice implements IStateDevice {

	/**
     * Component responsible of store state
     */
    private IStateStore stateStore;
    
    /**
     * Sets state store
     * @param store
     */
    public void setStateStore(IStateStore store) {
		this.stateStore = store;
	}

    @Override
	public String getDefaultDeviceLocation() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEFAULT_DEVICE_LOCATION, null);
    }

    @Override
	public void setDefaultDeviceLocation(String defaultDeviceLocation) {
    	this.stateStore.storePreference(Preferences.DEFAULT_DEVICE_LOCATION, defaultDeviceLocation);
    }
    
    @Override
	public String getDeviceFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEVICE_FILENAME_PATTERN, null);
    }

    @Override
	public void setDeviceFileNamePattern(String deviceFileNamePattern) {
    	this.stateStore.storePreference(Preferences.DEVICE_FILENAME_PATTERN, deviceFileNamePattern);
    }
    
    @Override
	public String getDeviceFolderPathPattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEVICE_FOLDER_PATH_PATTERN, null);
    }

    @Override
	public void setDeviceFolderPathPattern(String deviceFolderPathPattern) {
    	this.stateStore.storePreference(Preferences.DEVICE_FOLDER_PATH_PATTERN, deviceFolderPathPattern);
    }
    
    @Override
	public boolean isAllowRepeatedSongsInDevice() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE, true);
    }

    @Override
	public void setAllowRepeatedSongsInDevice(boolean allowRepeatedSongsInDevice) {
    	this.stateStore.storePreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE, allowRepeatedSongsInDevice);
    }
}
