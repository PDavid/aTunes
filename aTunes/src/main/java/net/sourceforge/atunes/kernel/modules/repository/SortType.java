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
package net.sourceforge.atunes.kernel.modules.repository;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

public enum SortType {

    /** By track */
    BY_TRACK_NUMBER,

    /** By artist */
    BY_ARTIST_AND_ALBUM,

    /** By title */
    BY_TITLE,

    /** By file name */
    BY_FILE,

    /** By modification date */
    BY_MODIFICATION_TIME;

    /**
     * Sort audio objects.
     * 
     * @param audioObjects
     *            the audio objects
     * 
     * @return the sorted list
     */
    public static List<AudioObject> sort(List<? extends AudioObject> audioObjects) {
        return sort(audioObjects, ApplicationState.getInstance().getSortType());
    }

    /**
     * Sort audio objects.
     * 
     * @param audioObjects
     *            the audio objects
     * @param type
     *            the sort type
     * 
     * @return the sorted list
     */
    public static List<AudioObject> sort(List<? extends AudioObject> audioObjects, SortType type) {
        AudioObject[] array = audioObjects.toArray(new AudioObject[audioObjects.size()]);
        Arrays.sort(array, getComparator(type));
        return Arrays.asList(array);
    }

    /**
     * Returns appropiate comparator for sort type
     * 
     * @param type
     *            the sort type
     * @return the appropiate comparator
     */
    private static Comparator<AudioObject> getComparator(SortType type) {
        switch (type) {
        case BY_TRACK_NUMBER:
            return new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject a1, AudioObject a2) {
                    return Integer.valueOf(a1.getTrackNumber()).compareTo(a2.getTrackNumber());
                }
            };
        case BY_ARTIST_AND_ALBUM:
            return new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject a1, AudioObject a2) {

                    // Sort by album artist
                    int c1 = a1.getAlbumArtist().compareTo(a2.getAlbumArtist());
                    if (c1 != 0) {
                        return c1;
                    }

                    /*
                     * If album artist is "" in both audio objects (we just need
                     * to check only one audio object since if execution reaches
                     * this code both album artist fields are equal) then sort
                     * by artist, album and track If album artist is not "",
                     * then only sort by album and track
                     */
                    if (a1.getAlbumArtist().isEmpty()) {
                        int c2 = a1.getArtist().compareTo(a2.getArtist());
                        if (c2 != 0) {
                            return c2;
                        }
                    }

                    // Sort by album
                    int c3 = a1.getAlbum().compareTo(a2.getAlbum());
                    if (c3 != 0) {
                        return c3;
                    }

                    // Sort by disc number
                    int c4 = Integer.valueOf(a1.getDiscNumber()).compareTo(a2.getDiscNumber());
                    if (c4 != 0) {
                        return c4;
                    }

                    return Integer.valueOf(a1.getTrackNumber()).compareTo(a2.getTrackNumber());
                }
            };
        case BY_TITLE:
            return new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject a0, AudioObject a1) {
                    return a0.getTitleOrFileName().compareTo(a1.getTitleOrFileName());
                }
            };
        case BY_MODIFICATION_TIME:
            return new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject o1, AudioObject o2) {
                    if (o1 instanceof AudioFile && o2 instanceof AudioFile) {
                        return Long.valueOf(((AudioFile) o1).getFile().lastModified()).compareTo(Long.valueOf(((AudioFile) o2).getFile().lastModified()));
                    }
                    return 0;
                }
            };
        case BY_FILE:
            return new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject o1, AudioObject o2) {
                    if (o1 instanceof AudioFile && o2 instanceof AudioFile) {
                        return ((AudioFile) o1).getFile().getName().compareTo(((AudioFile) o2).getFile().getName());
                    }
                    return 0;
                }
            };
        default:
            return null;
        }
    }

}
