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
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Causes player to start or pause play back
 * 
 * @author alex
 * 
 */
public class PlayAction extends CustomAbstractAction {

	private static final long serialVersionUID = -1122746023245126869L;

	private IPlayerHandler playerHandler;

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
	 * Default constructor
	 */
	public PlayAction() {
		super(I18nUtils.getString("PLAY"));
	}

	@Override
	protected void executeAction() {
		if (this.playerHandler.isEnginePlaying()
				|| this.playerHandler.isEnginePaused()) {
			this.playerHandler.resumeOrPauseCurrentAudioObject();
		} else {
			this.playerHandler.startPlayingAudioObjectInActivePlayList();
		}
	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		// Play action is always enabled even if play list or selection are
		// empty, because this action is used in play button
		return true;
	}
}
