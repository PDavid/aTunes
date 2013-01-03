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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IVideoEntry;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens a youtube video
 * 
 * @author alex
 * 
 */
public final class OpenYoutubeVideoAction extends
		ContextTableAction<IVideoEntry> {

	private static final long serialVersionUID = -7758596564970276630L;

	private IPlayerHandler playerHandler;

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
     * 
     */
	public OpenYoutubeVideoAction() {
		super(I18nUtils.getString("PLAY_VIDEO_AT_YOUTUBE"));
	}

	@Override
	protected void execute(final IVideoEntry entry) {
		// open youtube url
		getDesktop().openURL(entry.getUrl());
		// When playing a video in web browser automatically pause current song
		if (this.playerHandler.isEnginePlaying()) {
			this.playerHandler.resumeOrPauseCurrentAudioObject();
		}
	}

	@Override
	protected IVideoEntry getSelectedObject(final int row) {
		return ((YoutubeResultTableModel) getTable().getModel()).getEntry(row);
	}

	@Override
	protected boolean isEnabledForObject(final Object object) {
		return true;
	}
}