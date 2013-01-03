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

package net.sourceforge.atunes.kernel.modules.repository;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

/**
 * Calculates a hash of an audio object using artist, album and title
 * @author alex
 *
 */
final class RepeatedObjectsWithAlbumsHashCalculator implements IHashCalculator {

	private final IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public RepeatedObjectsWithAlbumsHashCalculator(final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	public int getHash(final ILocalAudioObject lao) {
		return (lao.getAlbumArtist(unknownObjectChecker) != null && !lao.getAlbumArtist(unknownObjectChecker).trim().equals("") ?
				lao.getAlbumArtist(unknownObjectChecker) : lao.getArtist(unknownObjectChecker))
				.hashCode() * lao.getAlbum(unknownObjectChecker).hashCode() * lao.getTitle().hashCode();
	}
}