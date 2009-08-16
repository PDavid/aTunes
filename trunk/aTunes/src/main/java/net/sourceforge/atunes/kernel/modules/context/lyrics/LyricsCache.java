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

package net.sourceforge.atunes.kernel.modules.context.lyrics;

import java.io.File;
import java.io.IOException;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.context.Lyrics;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.apache.commons.io.FileUtils;

public class LyricsCache {

    private static Logger logger = new Logger();

    private static File lyricsCacheDir = new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR, Constants.CACHE_DIR,
            SystemProperties.FILE_SEPARATOR, Constants.LYRICS_CACHE_DIR));

    /**
     * Clears the cache.
     * 
     * @return If an IOException occured during clearing
     */
    public synchronized boolean clearCache() {
        try {
            FileUtils.cleanDirectory(lyricsCacheDir);
        } catch (IOException e) {
            logger.info(LogCategories.FILE_DELETE, "Could not delete all files from lyricsr cache");
            return true;
        }
        return false;
    }

    /**
     * Lyrics Filename.
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * 
     * @return the file name for lyric
     */

    private String getFileNameForLyric(String artist, String title) {
        return StringUtils.getString(artist.hashCode(), title.hashCode(), ".xml");
    }

    /**
     * Absolute Path to Lyric Filename.
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * 
     * @return the file name for lyric at cache
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String getFileNameForLyricAtCache(String artist, String title) throws IOException {
        File lyricCacheDir = getLyricsCacheDir();

        if (lyricCacheDir == null) {
            return null;
        }

        return StringUtils.getString(lyricCacheDir.getAbsolutePath(), SystemProperties.FILE_SEPARATOR, getFileNameForLyric(artist, title));
    }

    /**
     * Private getter for lyricsCacheDir. If dir does not exist, it's created
     * 
     * @return the lyrics cache dir
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static File getLyricsCacheDir() throws IOException {
        if (!lyricsCacheDir.exists()) {
            FileUtils.forceMkdir(lyricsCacheDir);
        }
        return lyricsCacheDir;
    }

    /**
     * Retrieves lyrics from cache.
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * 
     * @return the string
     */
    public synchronized Lyrics retrieveLyric(String artist, String title) {
        try {
            String path = getFileNameForLyricAtCache(artist, title);
            if (path != null && new File(path).exists()) {
                return (Lyrics) XMLUtils.readBeanFromFile(path);
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
        return null;
    }

    /**
     * Stores lyrics in cache.
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * @param lyric
     *            the lyric
     */
    public synchronized void storeLyric(String artist, String title, Lyrics lyric) {
        if (artist == null || title == null || lyric == null) {
            return;
        }

        try {
            String fileAbsPath = getFileNameForLyricAtCache(artist, title);
            if (fileAbsPath != null) {
                XMLUtils.writeBeanToFile(lyric, fileAbsPath);
                logger.debug(LogCategories.CACHE, StringUtils.getString("Stored lyric for ", title));
            }
        } catch (IOException e) {
            logger.error(LogCategories.CACHE, e);
        }
    }
}
