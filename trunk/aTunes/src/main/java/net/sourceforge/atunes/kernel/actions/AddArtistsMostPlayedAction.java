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
import net.sourceforge.atunes.model.ISmartPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds artists most played to play list
 * 
 * @author fleax
 * 
 */
public class AddArtistsMostPlayedAction extends CustomAbstractAction {

	private static final long serialVersionUID = -5006402582163507581L;

	private int artists;

	private String i18nKey;

	private ISmartPlayListHandler smartPlayListHandler;

	/**
	 * @param artists
	 */
	public void setArtists(final int artists) {
		this.artists = artists;
	}

	/**
	 * @param i18nKey
	 */
	public void setI18nKey(final String i18nKey) {
		this.i18nKey = i18nKey;
	}

	/**
	 * @param smartPlayListHandler
	 */
	public void setSmartPlayListHandler(
			final ISmartPlayListHandler smartPlayListHandler) {
		this.smartPlayListHandler = smartPlayListHandler;
	}

	@Override
	protected void initialize() {
		super.initialize();
		putValue(NAME, I18nUtils.getString(this.i18nKey));
	}

	@Override
	protected void executeAction() {
		this.smartPlayListHandler.addArtistsMostPlayed(this.artists);
	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		return true;
	}

	@Override
	public boolean isEnabledForPlayList(final IPlayList playlist) {
		return !playlist.isDynamic();
	}

}
