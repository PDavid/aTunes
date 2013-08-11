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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Action to move the current selection in the active playlist to a position
 * immediately after the current song.
 * 
 * @author deathgorepain
 */
public class MoveAfterCurrentAudioObjectAction extends CustomAbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 89994064517052361L;

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
	public MoveAfterCurrentAudioObjectAction() {
		super(I18nUtils.getString("MOVE_AFTER_CURRENT_SONG"));
		putValue(SHORT_DESCRIPTION,
				I18nUtils.getString("MOVE_AFTER_CURRENT_SONG_TOOLTIP"));
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
		setEnabled(false);
	}

	@Override
	protected void executeAction() {
		IPlayList currentPlayList = this.playListHandler.getVisiblePlayList();
		List<IAudioObject> selectedAudioObjects = this.playListHandler
				.getSelectedAudioObjects();

		// Recurse backwards to move the elements to the correct position
		Collections.reverse(selectedAudioObjects);

		int beginNewPosition = this.playListHandler
				.getCurrentAudioObjectIndexInVisiblePlayList();
		int endNewPosition = this.playListHandler
				.getCurrentAudioObjectIndexInVisiblePlayList();
		for (int i = 0; i < selectedAudioObjects.size(); i++) {
			IAudioObject o = selectedAudioObjects.get(i);
			int currentIndex = this.playListHandler
					.getCurrentAudioObjectIndexInVisiblePlayList();
			int sourceIndex = currentPlayList.indexOf(o);

			// Workaround otherwise file is put before current playing
			if (sourceIndex > currentIndex) {
				currentIndex++;
			}
			currentPlayList.moveRowTo(sourceIndex, currentIndex);

			// Get min and max indexes to set selected
			beginNewPosition = Math.min(currentIndex, beginNewPosition);
			endNewPosition = Math.max(currentIndex, endNewPosition);
		}

		this.playListHandler.refreshPlayList();

		// Keep selected elements
		this.playListHandler.setSelectionInterval(
				this.playListHandler
						.getCurrentAudioObjectIndexInVisiblePlayList() + 1,
				this.playListHandler
						.getCurrentAudioObjectIndexInVisiblePlayList()
						+ selectedAudioObjects.size());

	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		// Don't allow moving songs from other playlists into active playlist
		if (!this.playListHandler.isActivePlayListVisible()
				|| this.playListHandler.isFiltered()) {
			return false;
		}

		return !selection.isEmpty();
	}

	@Override
	public boolean isEnabledForPlayList(final IPlayList playlist) {
		return !playlist.isDynamic();
	}

}
