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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * The listener interface for receiving playListTab events.
 */
final class PlayListTabListener implements ItemListener {

	private final PlayListSelectorWrapper playListSelectorWrapper;

	/**
	 * Instantiates a new play list tab listener.
	 * 
	 * @param playListSelectorWrapper
	 */
	public PlayListTabListener(
			final PlayListSelectorWrapper playListSelectorWrapper) {
		this.playListSelectorWrapper = playListSelectorWrapper;
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		playListSelectorWrapper.switchToPlaylist(playListSelectorWrapper
				.getSelectedPlayListIndex());
	}
}
