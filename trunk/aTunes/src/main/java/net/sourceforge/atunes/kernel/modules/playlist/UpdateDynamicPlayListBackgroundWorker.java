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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorkerWithIndeterminateProgress;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Updates dynamic playlist in background
 * 
 * @author alex
 * 
 */
public class UpdateDynamicPlayListBackgroundWorker extends
		BackgroundWorkerWithIndeterminateProgress<Void, Void> {

	private DynamicPlayList playList;

	private PlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final PlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param playList
	 * @return
	 */
	public UpdateDynamicPlayListBackgroundWorker setPlayList(
			final DynamicPlayList playList) {
		this.playList = playList;
		return this;
	}

	@Override
	protected String getDialogTitle() {
		return I18nUtils.getString("PLEASE_WAIT");
	}

	@Override
	protected void doneAndDialogClosed(final Void result) {
		this.playListHandler.refreshPlayList();
		this.playListHandler.playListsChanged();
	}

	@Override
	protected Void doInBackground() {
		this.playListHandler.recalculateDynamicPlayList(this.playList);
		return null;
	}

	@Override
	protected void whileWorking(final List<Void> chunks) {
	}
}
