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

package net.sourceforge.atunes.kernel.actions;

import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListObjectFilter;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Copies current selected play list to device
 * 
 * @author fleax
 * 
 */
public class CopyPlayListToDeviceAction extends CustomAbstractAction {

	private static final long serialVersionUID = 5899793232403738425L;

	private IDeviceHandler deviceHandler;

	private IPlayListHandler playListHandler;

	private IPlayListObjectFilter<ILocalAudioObject> playListObjectFilter;

	CopyPlayListToDeviceAction() {
		super(I18nUtils.getString("COPY_PLAYLIST_TO_DEVICE"));
		setEnabled(false);
	}

	/**
	 * @param deviceHandler
	 */
	public void setDeviceHandler(final IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param playListObjectFilter
	 */
	public void setPlayListObjectFilter(
			final IPlayListObjectFilter<ILocalAudioObject> playListObjectFilter) {
		this.playListObjectFilter = playListObjectFilter;
	}

	@Override
	protected void executeAction() {
		// Copy only LocalAudioObject objects
		deviceHandler.copyFilesToDevice(playListObjectFilter
				.getObjects(playListHandler.getVisiblePlayList()));
	}
}
