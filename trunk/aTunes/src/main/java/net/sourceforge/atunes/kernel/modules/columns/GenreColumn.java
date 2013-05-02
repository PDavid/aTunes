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
import net.sourceforge.atunes.model.IUnknownObjectChecker;

/**
 * Column to show genre
 * @author alex
 *
 */
public class GenreColumn extends AbstractColumn<String> {

	private static final long serialVersionUID = 1420893111015572964L;

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
	public GenreColumn() {
		super("GENRE");
		setVisible(true);
		setUsedForFilter(true);
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		if (getGenre(ao1).equals(getGenre(ao2))) {
			if (getArtist(ao1).equals(getArtist(ao2))) {
				if (getAlbum(ao1).equals(getAlbum(ao2))) {
					return Integer.valueOf(ao1.getTrackNumber()).compareTo(ao2.getTrackNumber());
				}
				return compare(getAlbum(ao1), getAlbum(ao2));
			}
			return compare(getArtist(ao1), getArtist(ao2));
		}
		return compare(getGenre(ao1), getGenre(ao2));
	}

	/**
	 * @param ao1
	 * @return
	 */
	private String getAlbum(final IAudioObject ao1) {
		return ao1.getAlbum(unknownObjectChecker);
	}

	/**
	 * @param ao1
	 * @return
	 */
	private String getArtist(final IAudioObject ao1) {
		return ao1.getArtist(unknownObjectChecker);
	}

	/**
	 * @param ao1
	 * @return
	 */
	private String getGenre(final IAudioObject ao1) {
		return ao1.getGenre(unknownObjectChecker);
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		if (getGenre(ao1).equals(getGenre(ao2))) {
			if (getArtist(ao1).equals(getArtist(ao2))) {
				if (getAlbum(ao1).equals(getAlbum(ao2))) {
					return Integer.valueOf(ao1.getTrackNumber()).compareTo(ao2.getTrackNumber());
				}
				return compare(getAlbum(ao1), getAlbum(ao2));
			}
			return compare(getArtist(ao1), getArtist(ao2));
		}
		return compare(getGenre(ao2), getGenre(ao1));
	}

	@Override
	public String getValueFor(final IAudioObject audioObject, final int row) {
		// Return genre
		return getGenre(audioObject);
	}

	@Override
	public String getValueForFilter(final IAudioObject audioObject, final int row) {
		return getGenre(audioObject);
	}
}
