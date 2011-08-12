package net.sourceforge.atunes.kernel.modules.context;

import java.io.Serializable;
import java.util.List;

public interface ArtistTopTracks extends Serializable {

	/**
	 * Gets the artist.
	 * 
	 * @return the artist
	 */
	public String getArtist();

    /**
     * Gets the tracks.
     * 
     * @return the tracks
     */
    public List<TrackInfo> getTracks();


}
