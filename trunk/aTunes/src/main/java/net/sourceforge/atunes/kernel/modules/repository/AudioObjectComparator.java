package net.sourceforge.atunes.kernel.modules.repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.atunes.model.AudioObject;

/**
 * Default comparator used to sort audio objects
 * @author fleax
 *
 */
public class AudioObjectComparator implements Comparator<AudioObject>{

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
    
    /**
     * Sorts given list with this comparator
     * @param audioObjects
     */
    public static void sort(List<AudioObject> audioObjects) {
    	Collections.sort(audioObjects, new AudioObjectComparator());
    }

}
