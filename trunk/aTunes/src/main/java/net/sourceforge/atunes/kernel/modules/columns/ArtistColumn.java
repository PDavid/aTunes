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

import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.TextAndIcon;
import net.sourceforge.atunes.model.ColumnSort;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

/**
 * Column to show artist
 * 
 * @author alex
 * 
 */
public class ArtistColumn extends AbstractColumn<TextAndIcon> {

	private static final long serialVersionUID = 8144686293055648148L;

	private IIconFactory artistFavoriteIcon;

	private transient IBeanFactory beanFactory;

	private transient IFavoritesHandler favoritesHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * Default constructor
	 */
	public ArtistColumn() {
		super("ARTIST");
		setVisible(true);
		setUsedForFilter(true);
		setColumnSort(ColumnSort.ASCENDING); // Column sets are ordered by
												// default by this column
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		int artist = compare(ao1.getArtist(this.unknownObjectChecker),
				ao2.getArtist(this.unknownObjectChecker));
		if (artist != 0) {
			return artist;
		} else {
			int album = compare(ao1.getAlbum(this.unknownObjectChecker),
					ao2.getAlbum(this.unknownObjectChecker));
			if (album != 0) {
				return album;
			} else {
				int disc = ao2.getDiscNumber() - ao1.getDiscNumber();
				if (disc != 0) {
					return disc;
				} else {
					return -ao2.getTrackNumber() + ao1.getTrackNumber();
				}
			}
		}
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		int artist = compare(ao2.getArtist(this.unknownObjectChecker),
				ao1.getArtist(this.unknownObjectChecker));
		if (artist != 0) {
			return artist;
		} else {
			int album = compare(ao1.getAlbum(this.unknownObjectChecker),
					ao2.getAlbum(this.unknownObjectChecker));
			if (album != 0) {
				return album;
			} else {
				int disc = ao2.getDiscNumber() - ao1.getDiscNumber();
				if (disc != 0) {
					return disc;
				} else {
					return -ao2.getTrackNumber() + ao1.getTrackNumber();
				}
			}
		}
	}

	@Override
	public TextAndIcon getValueFor(final IAudioObject audioObject, final int row) {
		if (getFavoritesHandler().isArtistFavorite(
				audioObject.getArtist(this.unknownObjectChecker))) {
			return new TextAndIcon(
					audioObject.getArtist(this.unknownObjectChecker),
					this.artistFavoriteIcon.getColorMutableIcon(),
					SwingConstants.LEFT);
		} else {
			return new TextAndIcon(
					audioObject.getArtist(this.unknownObjectChecker), null,
					SwingConstants.LEFT);
		}
	}

	@Override
	public String getValueForFilter(final IAudioObject audioObject,
			final int row) {
		return audioObject.getArtist(this.unknownObjectChecker);
	}

	/**
	 * @return favorites handler
	 */
	private IFavoritesHandler getFavoritesHandler() {
		if (this.favoritesHandler == null) {
			this.favoritesHandler = this.beanFactory
					.getBean(IFavoritesHandler.class);
		}
		return this.favoritesHandler;
	}

	/**
	 * @param artistFavoriteIcon
	 */
	public void setArtistFavoriteIcon(final IIconFactory artistFavoriteIcon) {
		this.artistFavoriteIcon = artistFavoriteIcon;
	}
}
