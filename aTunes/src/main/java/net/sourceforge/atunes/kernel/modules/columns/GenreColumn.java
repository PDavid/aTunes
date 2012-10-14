/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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
		if (ao1.getGenre(unknownObjectChecker).equals(ao2.getGenre(unknownObjectChecker))) {
			if (ao1.getArtist(unknownObjectChecker).equals(ao2.getArtist(unknownObjectChecker))) {
				if (ao1.getAlbum(unknownObjectChecker).equals(ao2.getAlbum(unknownObjectChecker))) {
					return Integer.valueOf(ao1.getTrackNumber()).compareTo(ao2.getTrackNumber());
				}
				return ao1.getAlbum(unknownObjectChecker).compareTo(ao2.getAlbum(unknownObjectChecker));
			}
			return ao1.getArtist(unknownObjectChecker).compareTo(ao2.getArtist(unknownObjectChecker));
		}
		return ao1.getGenre(unknownObjectChecker).compareTo(ao2.getGenre(unknownObjectChecker));
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		if (ao1.getGenre(unknownObjectChecker).equals(ao2.getGenre(unknownObjectChecker))) {
			if (ao1.getArtist(unknownObjectChecker).equals(ao2.getArtist(unknownObjectChecker))) {
				if (ao1.getAlbum(unknownObjectChecker).equals(ao2.getAlbum(unknownObjectChecker))) {
					return Integer.valueOf(ao1.getTrackNumber()).compareTo(ao2.getTrackNumber());
				}
				return ao1.getAlbum(unknownObjectChecker).compareTo(ao2.getAlbum(unknownObjectChecker));
			}
			return ao1.getArtist(unknownObjectChecker).compareTo(ao2.getArtist(unknownObjectChecker));
		}
		return ao2.getGenre(unknownObjectChecker).compareTo(ao1.getGenre(unknownObjectChecker));
	}

	@Override
	public String getValueFor(final IAudioObject audioObject, final int row) {
		// Return genre
		return audioObject.getGenre(unknownObjectChecker);
	}

	@Override
	public String getValueForFilter(final IAudioObject audioObject, final int row) {
		return audioObject.getGenre(unknownObjectChecker);
	}
}
