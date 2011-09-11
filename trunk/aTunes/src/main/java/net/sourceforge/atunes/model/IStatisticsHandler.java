package net.sourceforge.atunes.model;

import java.util.List;


/**
 * @author alex
 *
 */
public interface IStatisticsHandler extends IHandler {

	/**
     * Gets the number of artist times played.
     * 
     * @param audioFile
     *            the audio file
     * 
     * @return the artist times played
     */
    public int getArtistTimesPlayed(Artist artist);

	/**
	 * Returns statistics for an audio object
	 * @param audioObject
	 * @return
	 */
	public IAudioObjectStatistics getAudioObjectStatistics(IAudioObject audioObject);

	/**
	 * Updates statistics for audio object
	 * @param audioObject
	 */
	public void updateAudioObjectStatistics(IAudioObject audioObject);

	/**
	 * Updates file name of audio object
	 * @param audioFile
	 * @param absolutePath
	 * @param newAbsolutePath
	 */
	public void updateFileName(ILocalAudioObject audioFile, String absolutePath, String newAbsolutePath);

	/**
	 * Replaces artist name
	 * @param artist
	 * @param newArtistName
	 */
	public void replaceArtist(String artist, String newArtistName);

	/**
	 * Replaces album name
	 * @param artist
	 * @param album
	 * @param newAlbum
	 */
	public void replaceAlbum(String artist, String album, String newAlbum);

	/**
	 * Displays statistics to user
	 */
	public void showStatistics();

	/**
	 * Returns given amount of most played albums
	 * @param n
	 * @return
	 */
	public List<Album> getMostPlayedAlbums(int n);
	
	/**
	 * Returns number of plays of most played albums
	 * @param n
	 * @return
	 */
	public List<Integer> getMostPlayedAlbumsCount(int n);

	/**
	 * Returns given amount of most played artists
	 * @param n
	 * @return
	 */
	public List<Artist> getMostPlayedArtists(int n);

	/**
	 * Returns number of plays of most played artists
	 * @param n
	 * @return
	 */
	public List<Integer> getMostPlayedArtistsCount(int n);

	/**
	 * Returns given amount of most played audio objects
	 * @param n
	 * @return
	 */
	public List<IAudioObject> getMostPlayedAudioObjects(int n);

	/**
	 * Returns number of plays of most played audio objects
	 * @param n
	 * @return
	 */
	public List<Integer> getMostPlayedAudioObjectsCount(int n);

	/**
	 * Returns audio objects never played
	 * @return
	 */
	public List<IAudioObject> getUnplayedAudioObjects();

	/**
	 * Returns total number of audio objects
	 * @return
	 */
	public int getTotalAudioObjectsPlayed();

	/**
	 * Number of different audio objects played
	 * @return
	 */
	public int getDifferentAudioObjectsPlayed();


}
