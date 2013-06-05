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

package net.sourceforge.atunes.kernel.modules.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.BackgroundWorkerWithIndeterminateProgress;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListObjectFilter;
import net.sourceforge.atunes.model.IStateDevice;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Calculates synchronization between objects in device and play list
 * 
 * @author alex
 * 
 */
public class CalculateSynchronizationBetweenDeviceAndPlayListBackgroundWorker
		extends
		BackgroundWorkerWithIndeterminateProgress<Map<String, List<ILocalAudioObject>>, Void> {

	private IStateDevice stateDevice;

	private IDeviceHandler deviceHandler;

	private IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter;

	private IPlayList playList;

	/**
	 * @param deviceHandler
	 */
	public void setDeviceHandler(final IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}

	/**
	 * @param playListLocalAudioObjectFilter
	 */
	public void setPlayListLocalAudioObjectFilter(
			final IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter) {
		this.playListLocalAudioObjectFilter = playListLocalAudioObjectFilter;
	}

	/**
	 * @param playList
	 */
	public void setPlayList(final IPlayList playList) {
		this.playList = playList;
	}

	/**
	 * @param stateDevice
	 */
	public void setStateDevice(final IStateDevice stateDevice) {
		this.stateDevice = stateDevice;
	}

	@Override
	protected String getDialogTitle() {
		return I18nUtils.getString("PLEASE_WAIT");
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected Map<String, List<ILocalAudioObject>> doInBackground() {
		// Get play list elements
		List<ILocalAudioObject> playListObjects;
		if (this.stateDevice.isAllowRepeatedSongsInDevice()) {
			// Repeated songs allowed, filter only if have same artist and
			// album
			playListObjects = getBeanFactory().getBean(
					ILocalAudioObjectFilter.class)
					.filterRepeatedObjectsWithAlbums(
							this.playListLocalAudioObjectFilter
									.getObjects(this.playList));
		} else {
			// Repeated songs not allows, filter even if have different
			// album
			playListObjects = getBeanFactory().getBean(
					ILocalAudioObjectFilter.class).filterRepeatedObjects(
					this.playListLocalAudioObjectFilter
							.getObjects(this.playList));
		}

		// Get elements present in play list and not in device -> objects to
		// be copied to device
		List<ILocalAudioObject> objectsToCopyToDevice = this.deviceHandler
				.getElementsNotPresentInDevice(playListObjects);

		// Get elements present in device and not in play list -> objects to
		// be removed from device
		List<ILocalAudioObject> objectsToRemoveFromDevice = this.deviceHandler
				.getElementsNotPresentInList(playListObjects);

		Map<String, List<ILocalAudioObject>> result = new HashMap<String, List<ILocalAudioObject>>();
		result.put("ADD", objectsToCopyToDevice);
		result.put("REMOVE", objectsToRemoveFromDevice);
		return result;
	}

	@Override
	protected void doneAndDialogClosed(
			Map<String, List<ILocalAudioObject>> result) {
	}
}
