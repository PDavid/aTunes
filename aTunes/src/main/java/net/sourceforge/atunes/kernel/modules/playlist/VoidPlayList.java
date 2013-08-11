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

import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListMode;
import net.sourceforge.atunes.model.IStatePlayer;

/**
 * playlist returned when no playlist data loaded yet
 * 
 * @author alex
 * 
 */
class VoidPlayList implements IPlayList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3581989108482549526L;

	@Override
	public boolean isDynamic() {
		return false;
	}

	@Override
	public void moveRowTo(final int sourceRow, final int targetRow) {
	}

	@Override
	public int indexOf(final IAudioObject audioObject) {
		return 0;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public IAudioObject get(final int index) {
		return null;
	}

	@Override
	public boolean contains(final IAudioObject audioObject) {
		return false;
	}

	@Override
	public IAudioObject getNextAudioObject(final int index) {
		return null;
	}

	@Override
	public IAudioObject getPreviousAudioObject(final int index) {
		return null;
	}

	@Override
	public String getLength() {
		return null;
	}

	@Override
	public void reset() {
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(final String newName) {
	}

	@Override
	public IAudioObject getCurrentAudioObject() {
		return null;
	}

	@Override
	public void sortByColumn(final IColumn<?> column) {
	}

	@Override
	public void add(final int newLocation,
			final List<? extends IAudioObject> audioObjects) {
	}

	@Override
	public int getRandomPosition() {
		return 0;
	}

	@Override
	public void setCurrentAudioObjectIndex(final int selectedAudioObject) {
	}

	@Override
	public int getCurrentAudioObjectIndex() {
		return 0;
	}

	@Override
	public void clear() {
	}

	@Override
	public void remove(final int i) {
	}

	@Override
	public void add(final int newLocation, final IAudioObject aux) {
	}

	@Override
	public void shuffle() {
	}

	@Override
	public IPlayList copyPlayList() {
		return null;
	}

	@Override
	public IAudioObject moveToNextAudioObject() {
		return null;
	}

	@Override
	public IAudioObject moveToPreviousAudioObject() {
		return null;
	}

	@Override
	public void addToPlaybackHistory(final IAudioObject object) {
	}

	@Override
	public void remove(final Collection<? extends IAudioObject> audioObjects) {
	}

	@Override
	public List<IAudioObject> getAudioObjectsList() {
		return null;
	}

	@Override
	public void setPlayListEventListeners(final PlayListEventListeners listeners) {
	}

	@Override
	public void setMode(final IPlayListMode playListMode) {
	}

	@Override
	public void setStatePlayer(final IStatePlayer statePlayer) {
	}

	@Override
	public void saveInternalData() {
	}
}
