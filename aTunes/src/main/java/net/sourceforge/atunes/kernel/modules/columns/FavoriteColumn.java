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

import net.sourceforge.atunes.model.AudioObjectProperty;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * Column to show favorite
 * 
 * @author alex
 * 
 */
public class FavoriteColumn extends AbstractColumn<AudioObjectProperty> {

	private static final long serialVersionUID = -4652512586792166062L;

	private transient IFavoritesHandler favoritesHandler;

	/**
	 * Default constructor
	 */
	public FavoriteColumn() {
		super("FAVORITES");
		setResizable(false);
		setWidth(20);
		setVisible(true);
	}

	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(final IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return 0;
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return 0;
	}

	@Override
	public boolean isSortable() {
		return false;
	}

	@Override
	public AudioObjectProperty getValueFor(final IAudioObject audioObject,
			final int row) {
		// Return image
		if (audioObject instanceof ILocalAudioObject
				&& this.favoritesHandler
						.isSongFavorite((ILocalAudioObject) audioObject)) {
			return AudioObjectProperty.FAVORITE;
		}
		return null;
	}

	@Override
	public String getHeaderText() {
		return null;
	}
}
