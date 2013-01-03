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

package net.sourceforge.atunes.kernel.modules.columns;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.PlaybackState;

/**
 * Column to show play / pause / stop icon
 * @author alex
 *
 */
public class PlayingColumn extends AbstractColumn<PlaybackState> {

	private IPlayListHandler playListHandler;

	private IPlayerHandler playerHandler;

	private static final long serialVersionUID = -5604736587749167043L;

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
	 * Constructor
	 */
	public PlayingColumn() {
		super("PLAYING");
		setResizable(false);
		setWidth(20);
		setVisible(true);
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		return 0;
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		return 0;
	}

	@Override
	public boolean isSortable() {
		return false;
	}

	@Override
	public PlaybackState getValueFor(final IAudioObject audioObject, final int row) {
		if (playListHandler.isCurrentVisibleRowPlaying(row)) {
			return playerHandler.getPlaybackState();
		}
		return null;
	}

	@Override
	public String getHeaderText() {
		return "";
	}
}
