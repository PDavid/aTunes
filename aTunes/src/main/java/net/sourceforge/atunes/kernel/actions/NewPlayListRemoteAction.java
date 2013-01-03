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

import net.sourceforge.atunes.model.IPlayListHandler;

/**
 * This action, when called from telnet, creates a new playlist. The new
 * playlist is created in lieu of clearing, because clearing items requires
 * confirmation via GUI.
 *
 */
public class NewPlayListRemoteAction extends RemoteAction {

	private static final long serialVersionUID = -5575374156962786237L;

	private IPlayListHandler playListHandler;

	/**
	 * @param handler
	 */
	public void setPlayListHandler(final IPlayListHandler handler) {
		this.playListHandler = handler;
	}

	@Override
	public String runCommand(final List<String> parameters) {
		playListHandler.newPlayList(null);
		playListHandler.switchToPlaylist(playListHandler.getPlayListCount() - 1);
		return OK;
	}

	@Override
	protected String getHelpText() {
		return "Creates a new play list";
	}

	@Override
	protected String getOptionalParameters() {
		return null;
	}
}
