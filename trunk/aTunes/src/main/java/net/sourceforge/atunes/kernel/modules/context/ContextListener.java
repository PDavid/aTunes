/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Image;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.context.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.model.AudioObject;

/**
 * The listener interface for receiving context events.
 */
public interface ContextListener {

    /**
     * Gets the albums.
     * 
     * @return the albums
     */
    public List<AlbumInfo> getAlbums();

    /**
     * Notify album retrieved.
     * 
     * @param file
     *            the file
     * @param id
     *            the id
     */
    public void notifyAlbumRetrieved(AudioObject file, long id);

    /**
     * Notify artist image.
     * 
     * @param img
     *            the img
     * @param id
     *            the id
     */
    public void notifyArtistImage(Image img, long id);

    /**
     * Notify cover retrieved.
     * 
     * @param album
     *            the album
     * @param cover
     *            the cover
     * @param id
     *            the id
     */
    public void notifyCoverRetrieved(AlbumInfo album, Image cover, long id);

    /**
     * Notify finish get similar artist.
     * 
     * @param a
     *            the a
     * @param img
     *            the img
     * @param id
     *            the id
     */
    public void notifyFinishGetSimilarArtist(ArtistInfo a, Image img, long id);

    /**
     * Notify start retrieving artist images.
     * 
     * @param id
     *            the id
     */
    public void notifyStartRetrievingArtistImages(long id);

    /**
     * Notify start retrieving covers.
     * 
     * @param id
     *            the id
     */
    public void notifyStartRetrievingCovers(long id);

    /**
     * Notify wiki info retrieved.
     * 
     * @param wikiText
     *            the wiki text
     * @param wikiURL
     *            the wiki url
     * @param id
     *            the id
     */
    public void notifyWikiInfoRetrieved(String wikiText, String wikiURL, long id);

    /**
     * Notify lyrics retrieved.
     * 
     * @param audioObject
     *            the audio object
     * @param lyrics
     *            the lyrics
     * @param id
     *            the id
     */
    public void notifyLyricsRetrieved(AudioObject audioObject, Lyrics lyrics, long id);

    /**
     * Notify Youtube search results
     * 
     * @param result
     *            the list of results from Youtube
     * @param id
     *            the id
     */
    public void notifyYoutubeSearchRetrieved(List<YoutubeResultEntry> result, long id);

    /**
     * Sets the album.
     * 
     * @param album
     *            the album
     * @param id
     *            the id
     */
    public void setAlbum(AlbumInfo album, long id);

    /**
     * Sets the albums.
     * 
     * @param album
     *            the album
     * @param id
     *            the id
     */
    public void setAlbums(List<? extends AlbumInfo> album, long id);

    /**
     * Sets the image.
     * 
     * @param img
     *            the img
     * @param ao
     *            audio object
     * @param id
     *            the id
     */
    public void setImage(Image img, AudioObject ao, long id);

    /**
     * Sets the last album retrieved.
     * 
     * @param album
     *            the album
     * @param id
     *            the id
     */
    public void setLastAlbumRetrieved(String album, long id);

    /**
     * Sets the last artist retrieved.
     * 
     * @param artist
     *            the artist
     * @param id
     *            the id
     */
    public void setLastArtistRetrieved(String artist, long id);
}
