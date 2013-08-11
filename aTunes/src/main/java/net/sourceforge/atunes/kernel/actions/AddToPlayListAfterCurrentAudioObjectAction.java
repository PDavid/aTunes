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
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds audio objects to play list after the current audio object which is being
 * played
 * 
 * @author alex
 * 
 */
public class AddToPlayListAfterCurrentAudioObjectAction extends
		AbstractActionOverSelectedObjects<IAudioObject> {

	private static final long serialVersionUID = 1625697867534974341L;

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
	public AddToPlayListAfterCurrentAudioObjectAction() {
		super(I18nUtils
				.getString("ADD_TO_ACTIVE_PLAYLIST_AFTER_CURRENT_AUDIO_OBJECT"));
	}

	@Override
	protected void executeAction(final List<IAudioObject> objects) {
		this.playListHandler.addToActivePlayList(this.playListHandler
				.getActivePlayList().getCurrentAudioObjectIndex() + 1, objects);
	}

	@Override
	public boolean isEnabledForNavigationTreeSelection(
			final boolean rootSelected, final List<ITreeNode> selection) {
		return !selection.isEmpty();
	}

	@Override
	public boolean isEnabledForNavigationTableSelection(
			final List<IAudioObject> selection) {
		return !selection.isEmpty();
	}

	@Override
	public boolean isEnabledForPlayList(final IPlayList playlist) {
		return !playlist.isDynamic();
	}

}
