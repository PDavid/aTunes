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

package net.sourceforge.atunes.kernel.modules.pattern;

import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * Pattern for artist
 * @author alex
 *
 */
public final class ArtistPattern extends AbstractPattern {

	@Override
	public String getAudioFileStringValue(final ILocalAudioObject audioFile) {
		return audioFile.getArtist(getUnknownObjectChecker());
	}

	@Override
	public String getCDMetadataStringValue(final CDMetadata metadata, final int trackNumber) {
		String artist = metadata.getArtist(trackNumber);
		// If track has not artist, then use album artist
		if (artist == null) {
			artist = metadata.getAlbumArtist();
		}
		// If still null then use unknown artist
		return artist != null ? artist : getUnknownObjectChecker().getUnknownArtist();
	}
}