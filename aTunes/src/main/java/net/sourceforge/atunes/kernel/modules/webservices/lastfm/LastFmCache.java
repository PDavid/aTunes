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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.utils.AbstractCache;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLSerializerService;

import org.apache.commons.io.FileUtils;

public class LastFmCache extends AbstractCache {

    private static final String ARTIST_WIKI = "artistWiki";
    private static final String ARTIST_THUMB = "artistThumb";
    private static final String ARTIST_SIMILAR = "artistSimilar";
    private static final String ALBUM_LIST = "albumList";
    private static final String ARTIST_IMAGE = "artistImage";
    private static final String ARTIST_TOP_TRACKS = "artistTopTracks";
    private static final String ALBUM_INFO = "albumInfo";
    private static final String ALBUM_COVER = "albumCover";

    private File submissionCacheDir;
    
    private IOSManager osManager;
    
    private XMLSerializerService xmlSerializerService;

    /**
     * @param osManager
     * @param applicationArguments
     * @param xmlSerializerService
     */
    public LastFmCache(IOSManager osManager, IApplicationArguments applicationArguments, XMLSerializerService xmlSerializerService) {
        super(osManager, applicationArguments, LastFmCache.class.getResource("/settings/ehcache-lastfm.xml"));
        this.xmlSerializerService = xmlSerializerService;
    }

    /**
     * Clears the cache.
     * 
     * @return If an Exception occured during clearing
     */
    public synchronized boolean clearCache() {
        boolean exception = false;
        if (clearCache(getAlbumCoverCache())) {
        	exception = true;
        }
        if (clearCache(getAlbumInfoCache())) {
        	exception = true;
        }
        if (clearCache(getArtistImageCache())) {
        	exception = true;
        }
        if (clearCache(getAlbumListCache())) {
        	exception = true;
        }
        if (clearCache(getSimilarArtistsCache())) {
        	exception = true;
        }
        if (clearCache(getArtistThumbsCache())) {
        	exception = true;
        }
        if (clearCache(getArtistWikiCache())) {
        	exception = true;
        }
        return exception;
    }
    
    /**
     * Clears a cache, returning true if some error happened
     * @param cache
     * @return
     */
    private boolean clearCache(Cache cache) {
        try {
            cache.removeAll();
        } catch (IllegalStateException e) {
            Logger.info("Could not delete all files from cache: ", cache.getName());
            return true;
        } catch (CacheException e) {
        	Logger.info("Could not delete all files from cache: ", cache.getName());
        	return true;
        }
        return false;
    }

    private synchronized File getSubmissionDataDir() throws IOException {
    	if (submissionCacheDir == null) {
    		submissionCacheDir = new File(StringUtils.getString(osManager.getUserConfigFolder(Context.getBean(IApplicationArguments.class).isDebug()), osManager.getFileSeparator(),
    		            Constants.CACHE_DIR, osManager.getFileSeparator(), Constants.LAST_FM_CACHE_DIR, osManager.getFileSeparator(), Constants.LAST_FM_SUBMISSION_CACHE_DIR));
    	}
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

        return StringUtils.getString(submissionDataDirFile.getAbsolutePath(), osManager.getFileSeparator(), "submissionDataCache.xml");
    }

    /**
     * Retrieves an Album Cover from cache.
     * 
     * @param album
     *            the album
     * 
     * @return the image
     */
    public synchronized Image retrieveAlbumCover(IAlbumInfo album) {
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
    public synchronized IAlbumInfo retrieveAlbumInfo(String artist, String album) {
        Element element = getAlbumInfoCache().get(artist + album);
        if (element == null) {
            return null;
        } else {
            return (IAlbumInfo) element.getValue();
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
     * Retrieves a list of top tracks for given artist from cache
     * @param artist
     * @return
     */
    public synchronized IArtistTopTracks retrieveArtistTopTracks(String artist) {
    	Element element = getArtistTopTracksCache().get(artist);
    	if (element == null) {
    		return null;
    	} else {
    		return (IArtistTopTracks) element.getValue();
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
    public synchronized IAlbumListInfo retrieveAbumList(String artist) {
        Element element = getAlbumListCache().get(artist);
        if (element == null) {
            return null;
        } else {
            return (IAlbumListInfo) element.getValue();
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
    public synchronized ISimilarArtistsInfo retrieveArtistSimilar(String artist) {
        Element element = getSimilarArtistsCache().get(artist);
        if (element == null) {
            return null;
        } else {
            return (ISimilarArtistsInfo) element.getValue();
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
    public synchronized Image retrieveArtistThumbImage(IArtistInfo artist) {
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
    public synchronized void storeAlbumCover(IAlbumInfo album, ImageIcon cover) {
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
    public synchronized void storeAlbumInfo(String artist, String album, IAlbumInfo albumObject) {
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
     * Store an Artist top tracks list at cache
     * @param artist
     * @param topTracks
     */
    public synchronized void storeArtistTopTracks(String artist, IArtistTopTracks topTracks) {
    	if (artist == null || topTracks == null) {
    		return;
    	}
    	
    	Element element = new Element(artist, topTracks);
    	getArtistTopTracksCache().put(element);
    	Logger.debug("Stored artist top tracks for ", artist);
    }

    /**
     * Store an album list at cache.
     * 
     * @param artist
     *            the artist
     * @param list
     *            the list
     */
    public synchronized void storeAlbumList(String artist, IAlbumListInfo list) {
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
    public synchronized void storeArtistSimilar(String artist, ISimilarArtistsInfo similar) {
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
    public synchronized void storeArtistThumbImage(IArtistInfo artist, ImageIcon image) {
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
            	xmlSerializerService.writeObjectToFile(submissionDataList, path);
                Logger.debug("Stored submission data: ", submissionData);
            }
        } catch (IOException e) {
            Logger.error(e);
        }

    }

    @SuppressWarnings("unchecked")
    public synchronized List<SubmissionData> getSubmissionData() {
    	List<SubmissionData> data = null;
        try {
            String path = getFileNameForSubmissionCache();
            if (path != null && new File(path).exists()) {
                data = (List<SubmissionData>) xmlSerializerService.readObjectFromFile(path);
            }
        } catch (IOException e) {
            Logger.error(e);
        }
        if (data == null) {
        	data = new ArrayList<SubmissionData>();
        }
        return data;
    }

    public synchronized void removeSubmissionData() {
        try {
            String path = getFileNameForSubmissionCache();
            if (path != null && new File(path).exists()) {
            	xmlSerializerService.writeObjectToFile(new ArrayList<SubmissionData>(), path);
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
    
    private Cache getArtistTopTracksCache() {
    	return getCache(ARTIST_TOP_TRACKS);
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
        getArtistTopTracksCache().dispose();
    }

    private static class SubmissionDataComparator implements Comparator<SubmissionData>, Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = -4769284375865961129L;

		@Override
        public int compare(SubmissionData o1, SubmissionData o2) {
            return Integer.valueOf(o1.getStartTime()).compareTo(o2.getStartTime());
        }
    }
}
