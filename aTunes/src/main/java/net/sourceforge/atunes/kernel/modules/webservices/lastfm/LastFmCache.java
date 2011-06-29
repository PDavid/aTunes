/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.AlbumListInfo;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.SimilarArtistsInfo;
import net.sourceforge.atunes.misc.AbstractCache;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.apache.commons.io.FileUtils;

public class LastFmCache extends AbstractCache {

    private static final String ARTIST_WIKI = "artistWiki";
    private static final String ARTIST_THUMB = "artistThumb";
    private static final String ARTIST_SIMILAR = "artistSimilar";
    private static final String ALBUM_LIST = "albumList";
    private static final String ARTIST_IMAGE = "artistImage";
    private static final String ALBUM_INFO = "albumInfo";
    private static final String ALBUM_COVER = "albumCover";

    private static File submissionCacheDir = new File(StringUtils.getString(OsManager.getUserConfigFolder(Kernel.isDebug()), OsManager.getFileSeparator(),
            Constants.CACHE_DIR, OsManager.getFileSeparator(), Constants.LAST_FM_CACHE_DIR, OsManager.getFileSeparator(), Constants.LAST_FM_SUBMISSION_CACHE_DIR));

    public LastFmCache() {
        super(LastFmCache.class.getResource("/settings/ehcache-lastfm.xml"));
    }

    /**
     * Clears the cache.
     * 
     * @return If an Exception occured during clearing
     */
    public synchronized boolean clearCache() {
        boolean exception = false;
        try {
            getAlbumCoverCache().removeAll();
        } catch (Exception e) {
            Logger.info("Could not delete all files from album cover cache");
            exception = true;
        }
        try {
            getAlbumInfoCache().removeAll();
        } catch (Exception e) {
            Logger.info("Could not delete all files from album info cache");
            exception = true;
        }
        try {
            getArtistImageCache().removeAll();
        } catch (Exception e) {
            Logger.info("Could not delete all files from artist image cache");
            exception = true;
        }
        try {
            getAlbumListCache().removeAll();
        } catch (Exception e) {
            Logger.info("Could not delete all files from album list cache");
            exception = true;
        }
        try {
            getSimilarArtistsCache().removeAll();
        } catch (Exception e) {
            Logger.info("Could not delete all files from similar artist cache");
            exception = true;
        }
        try {
            getArtistThumbsCache().removeAll();
        } catch (Exception e) {
            Logger.info("Could not delete all files from artist thumbs cache");
            exception = true;
        }
        try {
            getArtistWikiCache().removeAll();
        } catch (Exception e) {
            Logger.info("Could not delete all files from artist wiki cache");
            exception = true;
        }

        return exception;
    }

    private synchronized File getSubmissionDataDir() throws IOException {
        if (!submissionCacheDir.exists()) {
            FileUtils.forceMkdir(submissionCacheDir);
        }
        return submissionCacheDir;
    }

    private String getFileNameForSubmissionCache() throws IOException {
        File submissionDataDirFile = getSubmissionDataDir();

        if (submissionDataDirFile == null) {
            return null;
        }

        return StringUtils.getString(submissionDataDirFile.getAbsolutePath(), OsManager.getFileSeparator(), "submissionDataCache.xml");
    }

    /**
     * Retrieves an Album Cover from cache.
     * 
     * @param album
     *            the album
     * 
     * @return the image
     */
    public synchronized Image retrieveAlbumCover(AlbumInfo album) {
        Element element = getAlbumCoverCache().get(album);
        if (element == null) {
            return null;
        } else {
            return ((ImageIcon) element.getValue()).getImage();
        }
    }

    /**
     * Retrieves an Album Cover from cache.
     * 
     * @param album
     *            the album
     * @param artist
     *            the artist
     * 
     * @return the audio scrobbler album
     */
    public synchronized AlbumInfo retrieveAlbumInfo(String artist, String album) {
        Element element = getAlbumInfoCache().get(artist + album);
        if (element == null) {
            return null;
        } else {
            return (AlbumInfo) element.getValue();
        }
    }

    /**
     * Retrieves an Artist Image from cache.
     * 
     * @param artist
     *            the artist
     * 
     * @return the image
     */
    public synchronized Image retrieveArtistImage(String artist) {
        Element element = getArtistImageCache().get(artist);
        if (element == null) {
            return null;
        } else {
            return ((ImageIcon) element.getValue()).getImage();
        }
    }

    /**
     * Retrieves an albumList from cache.
     * 
     * @param artist
     *            the artist
     * 
     * @return the audio scrobbler album list
     */
    public synchronized AlbumListInfo retrieveAbumList(String artist) {
        Element element = getAlbumListCache().get(artist);
        if (element == null) {
            return null;
        } else {
            return (AlbumListInfo) element.getValue();
        }
    }

    /**
     * Retrieves an Artist similar from cache.
     * 
     * @param artist
     *            the artist
     * 
     * @return the audio scrobbler similar artists
     */
    public synchronized SimilarArtistsInfo retrieveArtistSimilar(String artist) {
        Element element = getSimilarArtistsCache().get(artist);
        if (element == null) {
            return null;
        } else {
            return (SimilarArtistsInfo) element.getValue();
        }
    }

    /**
     * Retrieves an Artist Thumb from cache.
     * 
     * @param artist
     *            the artist
     * 
     * @return the image
     */
    public synchronized Image retrieveArtistThumbImage(ArtistInfo artist) {
        Element element = getArtistThumbsCache().get(artist);
        if (element == null) {
            return null;
        } else {
            return ((ImageIcon) element.getValue()).getImage();
        }
    }

    /**
     * Retrieves an Artist wiki from cache.
     * 
     * @param artist
     *            the artist
     * 
     * @return the string
     */
    public synchronized String retrieveArtistWiki(String artist) {
        Element element = getArtistWikiCache().get(artist);
        if (element == null) {
            return null;
        } else {
            return (String) element.getValue();
        }
    }

    /**
     * Stores an Album Cover at cache.
     * 
     * @param album
     *            the album
     * @param cover
     *            the cover
     */
    public synchronized void storeAlbumCover(AlbumInfo album, ImageIcon cover) {
        if (cover == null || album == null) {
            return;
        }

        Element element = new Element(album, cover);
        getAlbumCoverCache().put(element);
        Logger.debug("Stored album Cover for album ", album.getTitle());
    }

    /**
     * Stores an Album Cover at cache.
     * 
     * @param album
     *            the album
     * @param artist
     *            the artist
     * @param albumObject
     *            the album object
     */
    public synchronized void storeAlbumInfo(String artist, String album, AlbumInfo albumObject) {
        if (artist == null || album == null || albumObject == null) {
            return;
        }

        Element element = new Element(artist + album, albumObject);
        getAlbumInfoCache().put(element);
        Logger.debug("Stored album info for album ", artist, " ", album);
    }

    /**
     * Store an Artist Image at cache.
     * 
     * @param artist
     *            the artist
     * @param image
     *            the image
     */
    public synchronized void storeArtistImage(String artist, ImageIcon image) {
        if (image == null || artist == null) {
            return;
        }

        Element element = new Element(artist, image);
        getArtistImageCache().put(element);
        Logger.debug("Stored artist image for ", artist);
    }

    /**
     * Store an album list at cache.
     * 
     * @param artist
     *            the artist
     * @param list
     *            the list
     */
    public synchronized void storeAlbumList(String artist, AlbumListInfo list) {
        if (artist == null || list == null) {
            return;
        }

        Element element = new Element(artist, list);
        getAlbumListCache().put(element);
        Logger.debug("Stored album list for ", artist);
    }

    /**
     * Store an Artist similar at cache.
     * 
     * @param artist
     *            the artist
     * @param similar
     *            the similar
     */
    public synchronized void storeArtistSimilar(String artist, SimilarArtistsInfo similar) {
        if (artist == null || similar == null) {
            return;
        }

        Element element = new Element(artist, similar);
        getSimilarArtistsCache().put(element);
        Logger.debug("Stored artist similar for ", artist);
    }

    /**
     * Stores an Artist Thumb at cache.
     * 
     * @param artist
     *            the artist
     * @param image
     *            the image
     */
    public synchronized void storeArtistThumbImage(ArtistInfo artist, ImageIcon image) {
        if (image == null || artist == null) {
            return;
        }

        Element element = new Element(artist, image);
        getArtistThumbsCache().put(element);
        Logger.debug("Stored artist thumb for ", artist.getName());
    }

    /**
     * Store an Artist wiki at cache.
     * 
     * @param artist
     *            the artist
     * @param wikiText
     *            the wiki text
     */
    public synchronized void storeArtistWiki(String artist, String wikiText) {
        if (artist == null || wikiText == null) {
            return;
        }

        Element element = new Element(artist, wikiText);
        getArtistWikiCache().put(element);
        Logger.debug("Stored artist wiki for ", artist);
    }

    public synchronized void addSubmissionData(SubmissionData submissionData) {
        List<SubmissionData> submissionDataList = getSubmissionData();
        submissionDataList.add(submissionData);
        Collections.sort(submissionDataList, new SubmissionDataComparator());
        try {
            String path = getFileNameForSubmissionCache();
            if (path != null) {
                XMLUtils.writeObjectToFile(submissionDataList, path);
                Logger.debug("Stored submission data: ", submissionData);
            }
        } catch (IOException e) {
            Logger.error(e);
        }

    }

    @SuppressWarnings("unchecked")
    public synchronized List<SubmissionData> getSubmissionData() {
        try {
            String path = getFileNameForSubmissionCache();
            if (path != null && new File(path).exists()) {
                return (List<SubmissionData>) XMLUtils.readObjectFromFile(path);
            }
        } catch (IOException e) {
            Logger.error(e);
        }
        return new ArrayList<SubmissionData>();
    }

    public synchronized void removeSubmissionData() {
        try {
            String path = getFileNameForSubmissionCache();
            if (path != null && new File(path).exists()) {
                XMLUtils.writeObjectToFile(new ArrayList<SubmissionData>(), path);
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    private Cache getArtistWikiCache() {
        return getCache(ARTIST_WIKI);
    }

    private Cache getArtistThumbsCache() {
        return getCache(ARTIST_THUMB);
    }

    private Cache getSimilarArtistsCache() {
        return getCache(ARTIST_SIMILAR);
    }

    private Cache getAlbumListCache() {
        return getCache(ALBUM_LIST);
    }

    private Cache getArtistImageCache() {
        return getCache(ARTIST_IMAGE);
    }

    private Cache getAlbumInfoCache() {
        return getCache(ALBUM_INFO);
    }

    private Cache getAlbumCoverCache() {
        return getCache(ALBUM_COVER);
    }

    public void shutdown() {
        getAlbumCoverCache().dispose();
        getAlbumInfoCache().dispose();
        getAlbumListCache().dispose();
        getArtistImageCache().dispose();
        getSimilarArtistsCache().dispose();
        getArtistThumbsCache().dispose();
        getArtistWikiCache().dispose();
    }

    private static class SubmissionDataComparator implements Comparator<SubmissionData> {
        @Override
        public int compare(SubmissionData o1, SubmissionData o2) {
            return Integer.valueOf(o1.getStartTime()).compareTo(o2.getStartTime());
        }
    }
}
