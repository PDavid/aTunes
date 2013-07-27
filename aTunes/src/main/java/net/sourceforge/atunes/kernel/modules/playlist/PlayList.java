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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IStatePlayer;

/**
 * This class represents a play list.
 * 
 * @author alex
 * 
 */
public class PlayList extends AbstractPlayList {

	private static final long serialVersionUID = 2756513776762920794L;

	/**
	 * No args constructor for serialization
	 */
	PlayList() {
		super();
	}

	/**
	 * @param statePlayer
	 */
	protected PlayList(final IStatePlayer statePlayer) {
		super(statePlayer);
	}

	/**
	 * Builds a new play list with the given list of audio objects
	 * 
	 * @param audioObjectsList
	 * @param statePlayer
	 */
	protected PlayList(final List<? extends IAudioObject> audioObjectsList,
			final IStatePlayer statePlayer) {
		super(audioObjectsList, statePlayer);
	}

	@Override
	public IPlayList copyPlayList() {
		PlayList copy = new PlayList();
		copy.setName(getName());
		copy.setAudioObjects(new PlayListPointedList(getPointedList(),
				getStatePlayer()));
		copy.setStatePlayer(getStatePlayer());
		copy.setMode(PlayListMode.getPlayListMode(this, getStatePlayer()));
		copy.setListeners(getListeners());
		return copy;
	}
}
