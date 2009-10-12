package net.sourceforge.atunes.kernel.modules.repository;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

public enum SortType {

	/** by track sort order */
	BY_TRACK_NUMBER(new Comparator<AudioObject>() {
		@Override
		public int compare(AudioObject a1, AudioObject a2) {
			return Integer.valueOf(a1.getTrackNumber()).compareTo(a2.getTrackNumber());
		}
	}),

	/** by artist sort order */
	BY_ARTIST_AND_ALBUM(new Comparator<AudioObject>() {
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
	}),

	/** by title sort order */
	BY_TITLE(new Comparator<AudioObject>() {
		@Override
		public int compare(AudioObject a0, AudioObject a1) {
			return a0.getTitleOrFileName().compareTo(a1.getTitleOrFileName());
		}
	}),

	/** by file name sort order */
	BY_FILE(new Comparator<AudioObject>() {
		@Override
		public int compare(AudioObject o1, AudioObject o2) {
			if (o1 instanceof AudioFile && o2 instanceof AudioFile) {
				return ((AudioFile) o1).getFile().getName().compareTo(((AudioFile) o2).getFile().getName());
			}
			return 0;
		}
	}),

    /** by modification date sort order */
    BY_MODIFICATION_TIME(new Comparator<AudioObject>() {
    	@Override
    	public int compare(AudioObject o1, AudioObject o2) {
    		if (o1 instanceof AudioFile && o2 instanceof AudioFile) {
    			return Long.valueOf(((AudioFile) o1).getFile().lastModified()).compareTo(Long.valueOf(((AudioFile) o2).getFile().lastModified()));
    		}
    		return 0;
    	}
    });
    
	/**
	 * Comparator used for the SortType instance
	 */
    private Comparator<AudioObject> sorter;
    
    /**
     * Constructor for every sort type, defining comparator used in sort operations
     * @param sorter
     */
    private SortType(Comparator<AudioObject> sorter) {
    	this.sorter = sorter;
    }

    /**
     * Sort.
     * 
     * @param audioObjects
     *            the audio objects
     * 
     * @return the list
     */
    public static List<AudioObject> sort(List<? extends AudioObject> audioObjects) {
        return sort(audioObjects, ApplicationState.getInstance().getSortType());
    }

    /**
     * Sort.
     * 
     * @param audioObjects
     *            the audio objects
     * @param type
     *            the type
     * 
     * @return the list
     */
    public static List<AudioObject> sort(List<? extends AudioObject> audioObjects, SortType type) {
        AudioObject[] array = audioObjects.toArray(new AudioObject[audioObjects.size()]);
        Arrays.sort(array, type.sorter);
        return Arrays.asList(array);
    }

}
