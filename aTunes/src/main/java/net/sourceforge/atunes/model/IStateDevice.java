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

package net.sourceforge.atunes.model;

/**
 * @author alex
 * 
 */
public interface IStateDevice extends IState {

	/**
	 * Default device location
	 * 
	 * @return
	 */
	public String getDefaultDeviceLocation();

	/**
	 * Default device location
	 * 
	 * @param defaultDeviceLocation
	 */
	public void setDefaultDeviceLocation(String defaultDeviceLocation);

	/**
	 * Device file name pattern
	 * 
	 * @return
	 */
	public String getDeviceFileNamePattern();

	/**
	 * Device file name pattern
	 * 
	 * @param deviceFileNamePattern
	 */
	public void setDeviceFileNamePattern(String deviceFileNamePattern);

	/**
	 * Device folder path pattern
	 * 
	 * @return
	 */
	public String getDeviceFolderPathPattern();

	/**
	 * Device folder path pattern
	 * 
	 * @param deviceFolderPathPattern
	 */
	public void setDeviceFolderPathPattern(String deviceFolderPathPattern);

	/**
	 * Allow repeated songs in device
	 * 
	 * @return
	 */
	public boolean isAllowRepeatedSongsInDevice();

	/**
	 * Allow repeated songs in device
	 * 
	 * @param allowRepeatedSongsInDevice
	 */
	public void setAllowRepeatedSongsInDevice(boolean allowRepeatedSongsInDevice);

}
