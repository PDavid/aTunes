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

import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * This action enables a user to add a song to the and of the playlist via telnet.
 * 
 */
public class AddToPlayListRemoteAction extends RemoteAction {

	private static final String NOW = "--NOW";

	private static final long serialVersionUID = -634125878559446978L;

	private IPlayListHandler playListHandler;

	private IRepositoryHandler repositoryHandler;

	private PlayNowAction playNowAction;

	/**
	 * @param playNowAction
	 */
	public void setPlayNowAction(final PlayNowAction playNowAction) {
		this.playNowAction = playNowAction;
	}

	/**
	 * @param handler
	 */
	public void setRepositoryHandler(final IRepositoryHandler handler) {
		this.repositoryHandler = handler;
	}

	/**
	 * @param handler
	 */
	public void setPlayListHandler(final IPlayListHandler handler) {
		this.playListHandler = handler;
	}

	@Override
	public String runCommand(final List<String> parameters) {
		if (CollectionUtils.isEmpty(parameters)) {
			return "Please input a valid song filename";
		}

		boolean playNow = getPlayNow(parameters);

		//as the parameters are normally divided into parts based on spaces, put all together
		String name = org.apache.commons.lang.StringUtils.join(parameters, ' ');

		ILocalAudioObject file = repositoryHandler.getFile(name);
		if (file != null) {
			processFile(playNow, file);
			return OK;
		} else {
			return "Bad song name, for a full list of songs in the repository, please type \"command:list\"";
		}
	}

	/**
	 * @return
	 */
	private boolean getPlayNow(final List<String> parameters) {
		boolean playNow = false;
		if(parameters.contains(NOW)) {
			playNow = true;
			parameters.remove(NOW);
		}
		return playNow;
	}

	/**
	 * @param playNow
	 * @param file
	 */
	private void processFile(final boolean playNow, final ILocalAudioObject file) {
		if(playNow) {
			playNowAction.playNow(file);
		} else {
			playListHandler.addToVisiblePlayList(Collections.singletonList(file));
		}
	}

	@Override
	protected String getOptionalParameters() {
		return "[--NOW] SONGNAME";
	}

	@Override
	protected String getHelpText() {
		return "Appends SONGNAME to the end of the playlist. If --NOW follows the add keyword, it plays the song immediately";
	}
}
