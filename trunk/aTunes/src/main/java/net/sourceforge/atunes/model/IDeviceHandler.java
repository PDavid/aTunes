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

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Manages device
 * 
 * @author alex
 * 
 */
public interface IDeviceHandler extends IHandler, IRepositoryLoaderListener,
		IAudioFilesRemovedListener {

	/**
	 * Fills the device with songs until the specified memory is left.
	 * 
	 * @param leaveFreeLong
	 *            Memory to leave free
	 */
	public void fillWithRandomSongs(long leaveFreeLong);

	/**
	 * Connect device.
	 */
	public void connectDevice();

	/**
	 * Copy files to device
	 * 
	 * @param collection
	 */
	public void copyFilesToDevice(List<ILocalAudioObject> collection);

	/**
	 * Copy files to mp3 device.
	 * 
	 * @param collection
	 *            Files to be written to a mp3 device
	 * @param listener
	 *            A listener to be notified
	 */
	public void copyFilesToDevice(List<ILocalAudioObject> collection,
			IProcessListener<List<ILocalAudioObject>> listener);

	/**
	 * Gets the device songs.
	 * 
	 * @return the device songs
	 */
	public Collection<ILocalAudioObject> getAudioFilesList();

	/**
	 * Checks if is device connected.
	 * 
	 * @return true, if is device connected
	 */
	public boolean isDeviceConnected();

	/**
	 * Checks if given file is in the device path.
	 * 
	 * @param path
	 *            Absolute path of the file
	 * 
	 * @return true if file is in device, false otherwise
	 */
	public boolean isDevicePath(String path);

	/**
	 * Refresh device.
	 */
	public void refreshDevice();

	/**
	 * Gets the file if is in device
	 * 
	 * @param fileName
	 *            the file name
	 * 
	 * @return the file if loaded
	 */
	public ILocalAudioObject getFileIfLoaded(String fileName);

	/**
	 * @return the filesCopiedToDevice
	 */
	public int getFilesCopiedToDevice();

	/**
	 * Returns elements present in list and not present in device
	 * 
	 * @param list
	 * @return
	 */
	public List<ILocalAudioObject> getElementsNotPresentInDevice(
			List<ILocalAudioObject> list);

	/**
	 * Returns elements present in device and not present in list
	 * 
	 * @param list
	 * @return
	 */
	public List<ILocalAudioObject> getElementsNotPresentInList(
			List<ILocalAudioObject> list);

	/**
	 * Returns device location
	 * 
	 * @return
	 */
	public String getDeviceLocation();

	/**
	 * Returns artist with given name
	 * 
	 * @param name
	 * @return
	 */
	public IArtist getArtist(String name);

	/**
	 * Returns data to show in a tree
	 * 
	 * @param viewMode
	 * @return
	 */
	public Map<String, ?> getDataForView(ViewMode viewMode);

}