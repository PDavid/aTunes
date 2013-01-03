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

import java.util.Map;

import net.sourceforge.atunes.model.AudioObjectProperty;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;

/**
 * Column to show favorite
 * 
 * @author alex
 * 
 */
public class FavoriteColumn extends AbstractColumn<AudioObjectProperty> {

	private static final long serialVersionUID = -4652512586792166062L;

	private transient IBeanFactory beanFactory;

	private transient Map<String, ILocalAudioObject> favoriteObjects;

	/**
	 * Default constructor
	 */
	public FavoriteColumn() {
		super("FAVORITES");
		setResizable(false);
		setWidth(20);
		setVisible(true);
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
		if (audioObject instanceof IRadio) {
			return null;
		}
		if (audioObject instanceof IPodcastFeedEntry) {
			return null;
		}
		return getFavoriteObjects().containsValue(audioObject) ? AudioObjectProperty.FAVORITE
				: null;
	}

	@Override
	public String getHeaderText() {
		return null;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	private Map<String, ILocalAudioObject> getFavoriteObjects() {
		if (this.favoriteObjects == null) {
			// Access directly to favorites
			this.favoriteObjects = this.beanFactory.getBean(
					IFavoritesHandler.class).getFavoriteSongsInfo();
		}
		return this.favoriteObjects;
	}
}
