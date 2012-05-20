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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.TextTagAttribute;

class TextTagAttributesAndCheckers {
	
	private static final Map<TextTagAttribute, ITagChecker> CHECKERS;
	
	static {
		CHECKERS = new HashMap<TextTagAttribute, ITagChecker>();
		CHECKERS.put(TextTagAttribute.ALBUM, new AlbumTagChecker());
		CHECKERS.put(TextTagAttribute.ALBUM_ARTIST, new AlbumArtistTagChecker());
		CHECKERS.put(TextTagAttribute.ARTIST, new ArtistTagChecker());
		CHECKERS.put(TextTagAttribute.COMMENT, new CommentTagChecker());
		CHECKERS.put(TextTagAttribute.COMPOSER, new ComposerTagChecker());
		CHECKERS.put(TextTagAttribute.DISC_NUMBER, new DiscNumberTagChecker());
		CHECKERS.put(TextTagAttribute.GENRE, new GenreTagChecker());
		CHECKERS.put(TextTagAttribute.LYRICS, new LyricsTagChecker());
		CHECKERS.put(TextTagAttribute.TITLE, new TitleTagChecker());
		CHECKERS.put(TextTagAttribute.TRACK, new TrackTagChecker());
		CHECKERS.put(TextTagAttribute.YEAR, new YearTagChecker());
	}
	
	private TextTagAttributesAndCheckers() {}
	
	/**
	 * @param attribute
	 * @return tag chcker for given attribute
	 */
	static ITagChecker getTagCheckerForAttribute(TextTagAttribute attribute) {
		return CHECKERS.get(attribute);
	}

}
