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

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Edits a dynamic play list
 * 
 * @author fleax
 * 
 */
public class EditDynamicPlayListAction extends CustomAbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3082284931181535129L;

	private IPlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Default constructor
	 */
	public EditDynamicPlayListAction() {
		super(I18nUtils.getString("EDIT_DYNAMIC_PLAYLIST"));
	}

	@Override
	protected void executeAction() {
		this.playListHandler.editDynamicQuery(this.playListHandler
				.getVisiblePlayList());
	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		return true;
	}

	@Override
	public boolean isEnabledForPlayList(final IPlayList playlist) {
		return playlist.isDynamic();
	}

}
