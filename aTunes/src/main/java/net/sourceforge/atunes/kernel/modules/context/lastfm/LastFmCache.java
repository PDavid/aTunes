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

package net.sourceforge.atunes.kernel.modules.context.lastfm;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.AlbumListInfo;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.SimilarArtistsInfo;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.apache.commons.io.FileUtils;

/**
 * The Class LastFmCache.
 */
public class LastFmCache {

    /** Logger. */
    private static Logger logger = new Logger();

    /** Album Cover Cache dir. */
    private static File albumCoverCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR,
            Constants.CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_ALBUM_COVER_CACHE_DIR));

    /** Album Cover Cache dir. */
    private static File albumInfoCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR, Constants.CACHE_DIR,
            SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_ALBUM_INFO_CACHE_DIR));

    /** Artist thumbs cache dir. */
    private static File artistThumbCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR,
            Constants.CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_ARTIST_THUMB_CACHE_DIR));

    /** Artist image cache dir. */
    private static File artistImageCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR,
            Constants.CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_ARTIST_IMAGE_CACHE_DIR));

    /** Artist image cache dir. */
    private static File artistSimilarCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR,
            Constants.CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_ARTIST_SIMILAR_CACHE_DIR));

    /** Album list cache dir. */
    private static File albumListCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR, Constants.CACHE_DIR,
            SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_ALBUM_LIST_CACHE_DIR));

    /** Artist info cache dir. */
    private static File artistWikiCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR,
            Constants.CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_ARTIST_WIKI_CACHE_DIR));

    private static File submissionCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR,
            Constants.CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_CACHE_DIR, SystemProperties.FILE_SEPARATOR, Constants.LAST_FM_SUBMISSION_CACHE_DIR));

    /**
     * Clears the cache.
     * 
     * @return If an IOException occured during clearing
     */
    public synchronized boolean clearCache() {
        boolean exception = false;
        try {
            FileUtils.cleanDirectory(albumCoverCacheDir);
        } catch (IOException e) {
            logger.info(LogCategories.FILE_DELETE, "Could not delete all files from album cover cache");
            exception = true;
        }
        try {
            FileUtils.cleanDirectory(albumInfoCacheDir);
        } catch (IOException e) {
            logger.info(LogCategories.FILE_DELETE, "Could not delete all files from album info cache");
            exception = true;
        }
        try {
            FileUtils.cleanDirectory(artistImageCacheDir);
        } catch (IOException e) {
            logger.info(LogCategories.FILE_DELETE, "Could not delete all files from artist image cache");
            exception = true;
        }
        try {
            FileUtils.cleanDirectory(albumListCacheDir);
        } catch (IOException e) {
            logger.info(LogCategories.FILE_DELETE, "Could not delete all files from album list cache");
            exception = true;
        }
        try {
            FileUtils.cleanDirectory(artistSimilarCacheDir);
        } catch (IOException e) {
            logger.info(LogCategories.FILE_DELETE, "Could not delete all files from similar artist cache");
            exception = true;
        }
        try {
            FileUtils.cleanDirectory(artistThumbCacheDir);
        } catch (IOException e) {
            logger.info(LogCategories.FILE_DELETE, "Could not delete all files from artist thumbs cache");
            exception = true;
        }
        try {
            FileUtils.cleanDirectory(artistWikiCacheDir);
        } catch (IOException e) {
            logger.info(LogCategories.FILE_DELETE, "Could not delete all files from artist wiki cache");
            exception = true;
        }

        return exception;
    }

    /**
     * Private getter for albumCoverCacheDir. If dir does not exist, it's
     * created
     * 
     * @return the album cover cache dir
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private synchronized File getAlbumCoverCacheDir() throws IOException {
        if (!albumCoverCacheDir.exists()) {
            FileUtils.forceMkdir(albumCoverCacheDir);
        }
        return albumCoverCacheDir;
    }

    /**
     * Private getter for albumInfoCacheDir. If dir does not exist, it's created
     * 
     * @return the album info cache dir
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private synchronized File getAlbumInfoCacheDir() throws IOException {
        if (!albumInfoCacheDir.exists()) {
            FileUtils.forceMkdir(albumInfoCacheDir);
        }
        return albumInfoCacheDir;
    }

    /**
     * Private getter for artistImageCacheDir. If dir does not exist, it's
     * created
     * 
     * @return the artist image cache dir
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private synchronized File getArtistImageCacheDir() throws IOException {
        if (!artistImageCacheDir.exists()) {
            FileUtils.forceMkdir(artistImageCacheDir);
        }
        return artistImageCacheDir;
    }

    /**
     * Private getter for artistInfoCacheDir. If dir does not exist, it's
     * created
     * 
     * @return the artist info cache dir
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private synchronized File getAlbumListCacheDir() throws IOException {
        if (!albumListCacheDir.exists()) {
            FileUtils.forceMkdir(albumListCacheDir);
        }
        return albumListCacheDir;
    }

    /**
     * Private getter for artistSimilarCacheDir. If dir does not exist, it's
     * created
     * 
     * @return the artist similar cache dir
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private synchronized File getArtistSimilarCacheDir() throws IOException {
        if (!artistSimilarCacheDir.exists()) {
            FileUtils.forceMkdir(artistSimilarCacheDir);
        }
        return artistSimilarCacheDir;
    }

    /**
     * Private getter for artistThumbCacheDir. If dir does not exist, it's
     * created
     * 
     * @return the artist thumbs cache dir
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private synchronized File getArtistThumbsCacheDir() throws IOException {
        if (!artistThumbCacheDir.exists()) {
            FileUtils.forceMkdir(artistThumbCacheDir);
        }
        return artistThumbCacheDir;
    }

    /**
     * Private getter for artistWikiCacheDir. If dir does not exist, it's
     * created
     * 
     * @return the artist wiki cache dir
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private synchronized File getArtistWikiCacheDir() throws IOException {
        if (!artistWikiCacheDir.exists()) {
            FileUtils.forceMkdir(artistWikiCacheDir);
        }
        return artistWikiCacheDir;
    }

    private synchronized File getSubmissionDataDir() throws IOException {
        if (!submissionCacheDir.exists()) {
            FileUtils.forceMkdir(submissionCacheDir);
        }
        return submissionCacheDir;
    }

    /**
     * Album Cover Filename.
     * 
     * @param album
     *            the album
     * 
     * @return the file name for album cover
     */

    private String getFileNameForAlbumCover(AlbumInfo album) {
        return StringUtils.getString(album.getBigCoverURL().hashCode(), ".", ImageUtils.FILES_EXTENSION);
    }

    /**
     * Absolute Path to Album Cover Filename.
     * 
     * @param album
     *            the album
     * 
     * @return the file name for album cover at cache
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String getFileNameForAlbumCoverAtCache(AlbumInfo album) throws IOException {
        File albumCoverCacheDirFile = getAlbumCoverCacheDir();

        if (albumCoverCacheDirFile == null) {
            return null;
        }

        return StringUtils.getString(albumCoverCacheDirFile.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, getFileNameForAlbumCover(album));
    }

    /**
     * Album Cover Filename.
     * 
     * @param album
     *            the album
     * @param artist
     *            the artist
     * 
     * @return the file name for album info
     */

    private String getFileNameForAlbumInfo(String artist, String album) {
        return StringUtils.getString(artist.hashCode(), album.hashCode(), ".xml");
    }

    /**
     * Absolute Path to Album Info Filename.
     * 
     * @param album
     *            the album
     * @param artist
     *            the artist
     * 
     * @return the file name for album info at cache
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String getFileNameForAlbumInfoAtCache(String artist, String album) throws IOException {
        File albumInfoCacheDirFile = getAlbumInfoCacheDir();

        if (albumInfoCacheDirFile == null) {
            return null;
        }

        return StringUtils.getString(albumInfoCacheDirFile.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, getFileNameForAlbumInfo(artist, album));
    }

    /**
     * Artist Image Filename.
     * 
     * @param artist
     *            the artist
     * 
     * @return the file name for artist image
     */

    private String getFileNameForArtistImage(SimilarArtistsInfo artist) {
        return StringUtils.getString(artist.getArtistName().hashCode(), ".", ImageUtils.FILES_EXTENSION);
    }

    /**
     * Absolute Path to Artist Image Filename.
     * 
     * @param artist
     *            the artist
     * 
     * @return the file name for artist image at cache
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String getFileNameForArtistImageAtCache(SimilarArtistsInfo artist) throws IOException {
        File artistImageCacheDirFile = getArtistImageCacheDir();

        if (artistImageCacheDirFile == null) {
            return null;
        }

        return StringUtils.getString(artistImageCacheDirFile.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, getFileNameForArtistImage(artist));
    }

    /**
     * Artist Info Filename.
     * 
     * @param artist
     *            the artist
     * 
     * @return the file name for artist info
     */

    private String getFileNameForArtistInfo(String artist) {
        return StringUtils.getString(artist.hashCode(), ".xml");
    }

    /**
     * Absolute Path to Artist info Filename.
     * 
     * @param artist
     *            the artist
     * 
     * @return the file name for artist info at cache
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String getFileNameForAlbumListAtCache(String artist) throws IOException {
        File albumListCacheDirFile = getAlbumListCacheDir();

        if (albumListCacheDirFile == null) {
            return null;
        }

        return StringUtils.getString(albumListCacheDirFile.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, getFileNameForArtistInfo(artist));
    }

    /**
     * Artist Similar Filename.
     * 
     * @param artist
     *            the artist
     * 
     * @return the file name for artist similar
     */

    private String getFileNameForArtistSimilar(String artist) {
        return StringUtils.getString(artist.hashCode(), ".xml");
    }

    /**
     * Absolute Path to Artist similar Filename.
     * 
     * @param artist
     *            the artist
     * 
     * @return the file name for artist similar at cache
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String getFileNameForArtistSimilarAtCache(String artist) throws IOException {
        File artistSimilarCacheDirFile = getArtistSimilarCacheDir();

        if (artistSimilarCacheDirFile == null) {
            return null;
        }

        return StringUtils.getString(artistSimilarCacheDirFile.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, getFileNameForArtistSimilar(artist));
    }

    /**
     * Artist Thumb Filename.
     * 
     * @param artist
     *            the artist
     * 
     * @return the file name for artist thumb
     */

    private String getFileNameForArtistThumb(ArtistInfo artist) {
        return StringUtils.getString(artist.getName().hashCode(), ".", ImageUtils.FILES_EXTENSION);
    }

    /**
     * Absolute Path to Artist Thumb Filename.
     * 
     * @param artist
     *            the artist
     * 
     * @return the file name for artist thumb at cache
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String getFileNameForArtistThumbAtCache(ArtistInfo artist) throws IOException {
        File artistThumbCacheDirFile = getArtistThumbsCacheDir();

        if (artistThumbCacheDirFile == null) {
            return null;
        }

        return StringUtils.getString(artistThumbCacheDirFile.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, getFileNameForArtistThumb(artist));
    }

    /**
     * Artist Info Filename.
     * 
     * @param artist
     *            the artist
     * 
     * @return the file name for artist wiki
     */

    private String getFileNameForArtistWiki(String artist) {
        return StringUtils.getString(artist.hashCode(), ".xml");
    }

    /**
     * Absolute Path to Artist similar Filename.
     * 
     * @param artist
     *            the artist
     * 
     * @return the file name for artist wiki at cache
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String getFileNameForArtistWikiAtCache(String artist) throws IOException {
        File artistWikiCacheDirFile = getArtistWikiCacheDir();

        if (artistWikiCacheDirFile == null) {
            return null;
        }

        return StringUtils.getString(artistWikiCacheDirFile.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, getFileNameForArtistWiki(artist));
    }

    private String getFileNameForSubmissionCache() throws IOException {
        File submissionDataDirFile = getSubmissionDataDir();

        if (submissionDataDirFile == null) {
            return null;
        }

        return StringUtils.getString(submissionDataDirFile.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, "submissionDataCache.xml");
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
        try {
            String path = getFileNameForAlbumCoverAtCache(album);
            if (path != null && new File(path).exists()) {
                return new ImageIcon(path).getImage();
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
        return null;
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
        try {
            String path = getFileNameForAlbumInfoAtCache(artist, album);
            if (path != null && new File(path).exists()) {
                return (AlbumInfo) XMLUtils.readBeanFromFile(path);
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
        return null;
    }

    /**
     * Retrieves an Artist Image from cache.
     * 
     * @param artist
     *            the artist
     * 
     * @return the image
     */
    public synchronized Image retrieveArtistImage(SimilarArtistsInfo artist) {
        try {
            String path = getFileNameForArtistImageAtCache(artist);
            if (path != null && new File(path).exists()) {
                return new ImageIcon(path).getImage();
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
        return null;
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
        try {
            String path = getFileNameForAlbumListAtCache(artist);
            if (path != null && new File(path).exists()) {
                return (AlbumListInfo) XMLUtils.readBeanFromFile(path);
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
        return null;
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
        try {
            String path = getFileNameForArtistSimilarAtCache(artist);
            if (path != null && new File(path).exists()) {
                return (SimilarArtistsInfo) XMLUtils.readBeanFromFile(path);
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
        return null;
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
        try {
            String path = getFileNameForArtistThumbAtCache(artist);
            if (path != null && new File(path).exists()) {
            	ImageIcon icon = new ImageIcon(path);
                return icon.getImage();
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
        return null;
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
        try {
            String path = getFileNameForArtistWikiAtCache(artist);
            if (path != null && new File(path).exists()) {
                return (String) XMLUtils.readBeanFromFile(path);
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
        return null;
    }

    /**
     * Stores an Album Cover at cache.
     * 
     * @param album
     *            the album
     * @param cover
     *            the cover
     */
    public synchronized void storeAlbumCover(AlbumInfo album, Image cover) {
        if (cover == null || album == null) {
            return;
        }

        try {
            String fileAbsPath = getFileNameForAlbumCoverAtCache(album);
            if (fileAbsPath != null) {
                ImageUtils.writeImageToFile(cover, fileAbsPath);
                logger.debug(LogCategories.CACHE, StringUtils.getString("Stored album Cover for album ", album.getTitle()));
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
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

        try {
            String fileAbsPath = getFileNameForAlbumInfoAtCache(artist, album);
            if (fileAbsPath != null) {
                XMLUtils.writeBeanToFile(albumObject, fileAbsPath);
                logger.debug(LogCategories.CACHE, StringUtils.getString("Stored album info for album ", artist, " ", album));
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
    }

    /**
     * Store an Artist Image at cache.
     * 
     * @param artist
     *            the artist
     * @param image
     *            the image
     */
    public synchronized void storeArtistImage(SimilarArtistsInfo artist, Image image) {
        if (image == null || artist == null) {
            return;
        }

        try {
            String fileAbsPath = getFileNameForArtistImageAtCache(artist);
            if (fileAbsPath != null) {
                ImageUtils.writeImageToFile(image, fileAbsPath);
                logger.debug(LogCategories.CACHE, StringUtils.getString("Stored artist image for ", artist.getArtistName()));
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
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

        try {
            String fileAbsPath = getFileNameForAlbumListAtCache(artist);
            if (fileAbsPath != null) {
                XMLUtils.writeBeanToFile(list, fileAbsPath);
                logger.debug(LogCategories.CACHE, StringUtils.getString("Stored album list for ", artist));
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
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

        try {
            String fileAbsPath = getFileNameForArtistSimilarAtCache(artist);
            if (fileAbsPath != null) {
                XMLUtils.writeBeanToFile(similar, fileAbsPath);
                logger.debug(LogCategories.CACHE, StringUtils.getString("Stored artist similar for ", artist));
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
    }

    /**
     * Stores an Artist Thumb at cache.
     * 
     * @param artist
     *            the artist
     * @param image
     *            the image
     */
    public synchronized void storeArtistThumbImage(ArtistInfo artist, Image image) {
        if (image == null || artist == null) {
            return;
        }

        try {
            String fileAbsPath = getFileNameForArtistThumbAtCache(artist);
            if (fileAbsPath != null) {
                ImageUtils.writeImageToFile(image, fileAbsPath);
                logger.debug(LogCategories.CACHE, StringUtils.getString("Stored artist thumb for ", artist.getName()));
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
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

        try {
            String fileAbsPath = getFileNameForArtistWikiAtCache(artist);
            if (fileAbsPath != null) {
                XMLUtils.writeBeanToFile(wikiText, fileAbsPath);
                logger.debug(LogCategories.CACHE, StringUtils.getString("Stored artist wiki for ", artist));
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
    }

    public synchronized void addSubmissionData(SubmissionData submissionData) {
        List<SubmissionData> submissionDataList = getSubmissionData();
        submissionDataList.add(submissionData);
        Collections.sort(submissionDataList, new Comparator<SubmissionData>() {
            @Override
            public int compare(SubmissionData o1, SubmissionData o2) {
                return Integer.valueOf(o1.getStartTime()).compareTo(o2.getStartTime());
            }
        });
        try {
            String path = getFileNameForSubmissionCache();
            if (path != null) {
                XMLUtils.writeObjectToFile(submissionDataList, path);
                logger.debug(LogCategories.CACHE, StringUtils.getString("Stored submission data: " + submissionData));
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
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
            logger.error(LogCategories.CACHE, e);
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
            logger.error(LogCategories.CACHE, e);
        }
    }
}
