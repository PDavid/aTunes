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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.TextTagAttribute;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

public final class IncompleteTagsChecker {

    private IncompleteTagsChecker() {}

    /**
     * Returns a list with default attributes to highlight folders
     * 
     * @return
     */
    public static List<TextTagAttribute> getDefaultTagAttributesToHighlightFolders() {
        List<TextTagAttribute> result = new ArrayList<TextTagAttribute>();
        result.add(TextTagAttribute.ARTIST);
        result.add(TextTagAttribute.ALBUM);
        return result;
    }

    /**
     * Returns a hash map with all tag attributes as key, and state (used or
     * not) as value
     * 
     * @param attributes
     * @return
     */
    public static Map<TextTagAttribute, Boolean> getAllTagAttributes(List<TextTagAttribute> attributes) {
        Map<TextTagAttribute, Boolean> result = new HashMap<TextTagAttribute, Boolean>();

        for (TextTagAttribute att : TextTagAttribute.values()) {
        	result.put(att, false);
        }

        for (TextTagAttribute attr : attributes) {
            result.put(attr, true);
        }

        return result;
    }

    /**
     * Returns true if audio file has filled all enabled attributes
     * @param localAudioObject
     * @param tagAttributesToCheck
     * @return
     */
    private static boolean hasTagAttributesFilled(ILocalAudioObject localAudioObject, List<TextTagAttribute> tagAttributesToCheck) {
        if (localAudioObject.getTag() == null) {
            return false;
        }

        for (TextTagAttribute ta : tagAttributesToCheck) {
            boolean filled = analyzeAttribute(localAudioObject, ta);
            if (!filled) {
            	return false;
            }
        }

        return true;
    }

	/**
	 * @param localAudioObject
	 * @param ta
	 * @return if attribute is filled
	 */
	private static boolean analyzeAttribute(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		return checkTitle(localAudioObject, ta) && 
		       checkArtist(localAudioObject, ta) &&
		       checkAlbum(localAudioObject, ta) &&
		       checkYear(localAudioObject, ta) &&
		       checkComment(localAudioObject, ta) &&
		       checkGenre(localAudioObject, ta) &&
		       checkLyrics(localAudioObject, ta) &&
		       checkComposer(localAudioObject, ta) &&
		       checkAlbumArtist(localAudioObject, ta) &&
		       checkTrack(localAudioObject, ta) &&
		       checkDiscNumber(localAudioObject, ta);
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkTrack(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.TRACK && localAudioObject.getTrackNumber() <= 0) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkDiscNumber(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.DISC_NUMBER && localAudioObject.getDiscNumber() <= 0) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkAlbumArtist(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.ALBUM_ARTIST && localAudioObject.getAlbumArtist().isEmpty()) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkComposer(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.COMPOSER && localAudioObject.getComposer().isEmpty()) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkLyrics(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.LYRICS && localAudioObject.getLyrics().isEmpty()) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkGenre(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.GENRE && UnknownObjectCheck.isUnknownGenre(localAudioObject.getGenre())) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkComment(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.COMMENT && (localAudioObject.getComment().isEmpty())) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkYear(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.YEAR && localAudioObject.getYear().isEmpty()) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkAlbum(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.ALBUM && UnknownObjectCheck.isUnknownAlbum(localAudioObject.getAlbum())) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkArtist(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.ARTIST && UnknownObjectCheck.isUnknownArtist(localAudioObject.getArtist())) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkTitle(ILocalAudioObject localAudioObject, TextTagAttribute ta) {
		if (ta == TextTagAttribute.TITLE && localAudioObject.getTitle().isEmpty()) {
		    return false;
		}
		return true;
	}

    /**
     * Returns <code>true</code> if tree object contains audio objects with
     * incomplete tags
     * 
     * @param treeObject
     * @param tagAttributes
     * @return
     */
    static boolean hasIncompleteTags(ITreeObject<? extends IAudioObject> treeObject, List<TextTagAttribute> tagAttributes) {
        for (IAudioObject f : treeObject.getAudioObjects()) {
            if (hasIncompleteTags(f, tagAttributes)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if object has incomplete tag tags
     * @param audioObject
     * @param tagAttributes
     * @return
     */
    static boolean hasIncompleteTags(IAudioObject audioObject, List<TextTagAttribute> tagAttributes) {
        if (audioObject instanceof ILocalAudioObject) {
            return !hasTagAttributesFilled((ILocalAudioObject) audioObject, tagAttributes);
        }
        return false;
    }
}
