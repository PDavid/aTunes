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

import org.apache.commons.lang.StringUtils;

/**
 * Pattern for first artist char
 * 
 * @author alex
 * 
 */
public final class ArtistFirstCharPattern extends AbstractPattern {

	@Override
	public String getAudioFileStringValue(final ILocalAudioObject audioFile) {
		return audioFile.getArtist(getUnknownObjectChecker()).substring(0, 1);
	}

	@Override
	public String getCDMetadataStringValue(final CDMetadata metadata,
			final int trackNumber) {
		String artist = metadata.getArtist(trackNumber);
		return StringUtils.length(artist) >= 1 ? artist.substring(0, 1) : null;
	}
}