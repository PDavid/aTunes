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

import java.util.Map;

import net.sourceforge.atunes.model.IStateDevice;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateDevice implements IStateDevice {

	private PreferenceHelper preferenceHelper;

	/**
	 * @param preferenceHelper
	 */
	public void setPreferenceHelper(final PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	@Override
	public String getDefaultDeviceLocation() {
		return this.preferenceHelper.getPreference(
				Preferences.DEFAULT_DEVICE_LOCATION, String.class, null);
	}

	@Override
	public void setDefaultDeviceLocation(final String defaultDeviceLocation) {
		this.preferenceHelper.setPreference(
				Preferences.DEFAULT_DEVICE_LOCATION, defaultDeviceLocation);
	}

	@Override
	public String getDeviceFileNamePattern() {
		return this.preferenceHelper.getPreference(
				Preferences.DEVICE_FILENAME_PATTERN, String.class, null);
	}

	@Override
	public void setDeviceFileNamePattern(final String deviceFileNamePattern) {
		this.preferenceHelper.setPreference(
				Preferences.DEVICE_FILENAME_PATTERN, deviceFileNamePattern);
	}

	@Override
	public String getDeviceFolderPathPattern() {
		return this.preferenceHelper.getPreference(
				Preferences.DEVICE_FOLDER_PATH_PATTERN, String.class, null);
	}

	@Override
	public void setDeviceFolderPathPattern(final String deviceFolderPathPattern) {
		this.preferenceHelper
				.setPreference(Preferences.DEVICE_FOLDER_PATH_PATTERN,
						deviceFolderPathPattern);
	}

	@Override
	public boolean isAllowRepeatedSongsInDevice() {
		return this.preferenceHelper
				.getPreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE,
						Boolean.class, true);
	}

	@Override
	public void setAllowRepeatedSongsInDevice(
			final boolean allowRepeatedSongsInDevice) {
		this.preferenceHelper.setPreference(
				Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE,
				allowRepeatedSongsInDevice);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}
}
