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
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

public final class IncompleteTagsChecker {

    private IncompleteTagsChecker() {}

    /**
     * Returns a list with default attributes to highlight folders
     * 
     * @return
     */
    public static List<TagAttribute> getDefaultTagAttributesToHighlightFolders() {
        List<TagAttribute> result = new ArrayList<TagAttribute>();
        result.add(TagAttribute.ARTIST);
        result.add(TagAttribute.ALBUM);
        return result;
    }

    /**
     * Returns a hash map with all tag attributes as key, and state (used or
     * not) as value
     * 
     * @param attributes
     * @return
     */
    public static Map<TagAttribute, Boolean> getAllTagAttributes(List<TagAttribute> attributes) {
        Map<TagAttribute, Boolean> result = new HashMap<TagAttribute, Boolean>();
        result.put(TagAttribute.TITLE, false);
        result.put(TagAttribute.ARTIST, false);
        result.put(TagAttribute.ALBUM, false);
        result.put(TagAttribute.YEAR, false);
        result.put(TagAttribute.COMMENT, false);
        result.put(TagAttribute.GENRE, false);
        result.put(TagAttribute.LYRICS, false);
        result.put(TagAttribute.COMPOSER, false);
        result.put(TagAttribute.ALBUM_ARTIST, false);
        result.put(TagAttribute.TRACK, false);

        for (TagAttribute attr : attributes) {
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
    private static boolean hasTagAttributesFilled(ILocalAudioObject localAudioObject, List<TagAttribute> tagAttributesToCheck) {
        if (localAudioObject.getTag() == null) {
            return false;
        }

        for (TagAttribute ta : tagAttributesToCheck) {
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
	private static boolean analyzeAttribute(ILocalAudioObject localAudioObject, TagAttribute ta) {
		return checkTitle(localAudioObject, ta) && 
		       checkArtist(localAudioObject, ta) &&
		       checkAlbum(localAudioObject, ta) &&
		       checkYear(localAudioObject, ta) &&
		       checkComment(localAudioObject, ta) &&
		       checkGenre(localAudioObject, ta) &&
		       checkLyrics(localAudioObject, ta) &&
		       checkComposer(localAudioObject, ta) &&
		       checkAlbumArtist(localAudioObject, ta) &&
		       checkTrack(localAudioObject, ta);
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkTrack(ILocalAudioObject localAudioObject, TagAttribute ta) {
		if (ta == TagAttribute.TRACK && localAudioObject.getTrackNumber() <= 0) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkAlbumArtist(ILocalAudioObject localAudioObject, TagAttribute ta) {
		if (ta == TagAttribute.ALBUM_ARTIST && localAudioObject.getAlbumArtist().isEmpty()) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkComposer(ILocalAudioObject localAudioObject, TagAttribute ta) {
		if (ta == TagAttribute.COMPOSER && localAudioObject.getComposer().isEmpty()) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkLyrics(ILocalAudioObject localAudioObject, TagAttribute ta) {
		if (ta == TagAttribute.LYRICS && localAudioObject.getLyrics().isEmpty()) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkGenre(ILocalAudioObject localAudioObject, TagAttribute ta) {
		if (ta == TagAttribute.GENRE && UnknownObjectCheck.isUnknownGenre(localAudioObject.getGenre())) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkComment(ILocalAudioObject localAudioObject, TagAttribute ta) {
		if (ta == TagAttribute.COMMENT && (localAudioObject.getComment().isEmpty())) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkYear(ILocalAudioObject localAudioObject, TagAttribute ta) {
		if (ta == TagAttribute.YEAR && localAudioObject.getYear().isEmpty()) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkAlbum(ILocalAudioObject localAudioObject, TagAttribute ta) {
		if (ta == TagAttribute.ALBUM && UnknownObjectCheck.isUnknownAlbum(localAudioObject.getAlbum())) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkArtist(ILocalAudioObject localAudioObject, TagAttribute ta) {
		if (ta == TagAttribute.ARTIST && UnknownObjectCheck.isUnknownArtist(localAudioObject.getArtist())) {
		    return false;
		}
		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 */
	private static boolean checkTitle(ILocalAudioObject localAudioObject, TagAttribute ta) {
		if (ta == TagAttribute.TITLE && localAudioObject.getTitle().isEmpty()) {
		    return false;
		}
		return true;
	}

    /**
     * Returns <code>true</code> if tree object contains audio objects with
     * incomplete tags
     * 
     * @param treeObject
     * @param state
     * @return
     */
    public static boolean hasIncompleteTags(ITreeObject<? extends IAudioObject> treeObject, IState state) {
    	List<TagAttribute> tagAttributes = state.getHighlightIncompleteTagFoldersAttributes();
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
    public static boolean hasIncompleteTags(IAudioObject audioObject, List<TagAttribute> tagAttributes) {
        if (audioObject instanceof ILocalAudioObject) {
            return !hasTagAttributesFilled((ILocalAudioObject) audioObject, tagAttributes);
        }
        return false;
    }
}
