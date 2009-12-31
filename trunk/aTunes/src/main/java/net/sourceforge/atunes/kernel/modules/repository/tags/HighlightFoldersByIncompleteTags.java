/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.repository.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.data.Folder;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.TagAttribute;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

public class HighlightFoldersByIncompleteTags {

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
     * @return
     */
    public static Map<TagAttribute, Boolean> getAllTagAttributes() {
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

        for (TagAttribute attr : ApplicationState.getInstance().getHighlightIncompleteTagFoldersAttributes()) {
            result.put(attr, true);
        }

        return result;
    }

    /**
     * Returns true if audio file has filled all enabled attributes
     * 
     * @return
     */
    private static boolean hasTagAttributesFilled(AudioFile audioFile) {
        if (audioFile.getTag() == null) {
            return false;
        }

        for (TagAttribute ta : ApplicationState.getInstance().getHighlightIncompleteTagFoldersAttributes()) {
            if (ta == TagAttribute.TITLE && audioFile.getTitle().isEmpty()) {
                return false;
            }

            if (ta == TagAttribute.ARTIST && Artist.isUnknownArtist(audioFile.getArtist())) {
                return false;
            }

            if (ta == TagAttribute.ALBUM && Album.isUnknownAlbum(audioFile.getAlbum())) {
                return false;
            }

            if (ta == TagAttribute.YEAR && audioFile.getYear().isEmpty()) {
                return false;
            }

            if (ta == TagAttribute.COMMENT && (audioFile.getComment().isEmpty())) {
                return false;
            }

            if (ta == TagAttribute.GENRE && Genre.isUnknownGenre(audioFile.getGenre())) {
                return false;
            }

            if (ta == TagAttribute.LYRICS && audioFile.getLyrics().isEmpty()) {
                return false;
            }

            if (ta == TagAttribute.COMPOSER && audioFile.getComposer().isEmpty()) {
                return false;
            }

            if (ta == TagAttribute.ALBUM_ARTIST && audioFile.getAlbumArtist().isEmpty()) {
                return false;
            }

            if (ta == TagAttribute.TRACK && audioFile.getTrackNumber() <= 0) {
                return false;
            }

        }

        return true;
    }

    /**
     * Returns <code>true</code> if folder contains audio files with incomplete
     * tags
     * 
     * @return
     */
    public static boolean hasIncompleteTags(Folder folder) {
        for (AudioFile f : folder.getAudioFiles()) {
            if (!hasTagAttributesFilled(f)) {
                return true;
            }
        }
        return false;
    }

}
