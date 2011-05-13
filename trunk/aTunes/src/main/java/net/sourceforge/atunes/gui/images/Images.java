/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.gui.images;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.OsManager;

/**
 * This class contains the images that are used by aTunes.
 * 
 * @author fleax
 */
public final class Images {

    // Every time a image is added, an attribute must be added
    public static final String ALBUM_FAVORITE = "albumFavorite.png";
    public static final String APP_TITLE = "title.png";
    public static final String APP_LOGO_16 = "logo16x16.png";
    public static final String APP_LOGO_24 = "logo24x24.png";
    public static final String APP_LOGO_32 = "logo32x32.png";
    public static final String APP_LOGO_90 = "logo90x90.png";
    public static final String APP_LOGO_150 = "logo150x150.png";
    public static final String APP_LOGO_300 = "logo300x300.png";
    public static final String CD_AUDIO = "cdAudio.png";
    public static final String CHECK_FOR_UPDATES = "checkForUpdates.png";
    public static final String CHECK_FOR_UPDATES_BW = "checkForUpdatesBW.png";
    public static final String CONTEXT = "context.png";
    public static final String COPY = "copy.png";
    public static final String DATE = "date.png";
    public static final String DOWNLOAD_PODCAST = "rss_download.png";
    public static final String EXPORT = "export.png";
    public static final String FOLDER = "repository.png";
    public static final String GENRE = "genre.png";
    public static final String INFO = "info.png";
    public static final String KARAOKE = "karaoke.png";
    public static final String LANGUAGE = "language.png";
    public static final String LASTFM = "lastFm.png";
    public static final String NAVIGATE = "navigate.png";
    public static final String NETWORK_LITTLE = "network_little.png";
    public static final String NEW_PODCAST_ENTRY = "new_podcast_entry.png";
    public static final String NEXT_TRAY = OsManager.osType.isWindowsVista() ? "nextTrayVista.png" : "nextTray.png";
    public static final String NEXT_TRAY_MENU = "nextTrayMenu.png";
    public static final String NORMALIZATION = "normalization.png";
    public static final String OSD = "osd.png";
    public static final String PAUSE_TRAY = OsManager.osType.isWindowsVista() ? "pauseTrayVista.png" : "pauseTray.png";
    public static final String PAUSE_TRAY_MENU = "pauseTrayMenu.png";
    public static final String PLAY_TINY = "playTiny.png";
    public static final String PLAY_TRAY = OsManager.osType.isWindowsVista() ? "playTrayVista.png" : "playTray.png";
    public static final String PLAY_TRAY_MENU = "playTrayMenu.png";
    public static final String PLAYLIST = "playlist.png";
    public static final String PLUGIN = "plugin.png";
    public static final String POWERED_BY_LAST_FM = "poweredByLastFm.png";
    public static final String PREFS = "prefs.png";
    public static final String PREVIOUS_TRAY = OsManager.osType.isWindowsVista() ? "previousTrayVista.png" : "previousTray.png";
    public static final String PREVIOUS_TRAY_MENU = "previousTrayMenu.png";
    public static final String RADIO = "radio.png";
    public static final String RADIO_BIG = "radio_big.png";
    public static final String RADIO_LITTLE = "radio_little.png";
    public static final String RSS_LITTLE = "rss_little.png";
    public static final String RSS = "rss.png";
    public static final String RSS_BIG = "rss_big.png";
    public static final String STOP_TRAY = OsManager.osType.isWindowsVista() ? "stopTrayVista.png" : "stopTray.png";
    public static final String STOP_TRAY_MENU = "stopTrayMenu.png";
    public static final String VOLUME_MUTE_TRAY_MENU = "volume-mute-tray-menu.png";
    public static final String ONE_STAR = "1_star.png";
    public static final String TWO_STAR = "2_star.png";
    public static final String THREE_STAR = "3_star.png";
    public static final String FOUR_STAR = "4_star.png";
    public static final String FIVE_STAR = "5_star.png";
    public static final String WARNING = "warning.png";

    /**
     * cache of images
     */
    private static Map<String, ImageIcon> images = new HashMap<String, ImageIcon>();

    private Images() {

    }

    /**
     * Returns an image.
     * 
     * @param imgName
     *            the img name
     * 
     * @return An ImageIcon
     */
    public static ImageIcon getImage(String imgName) {
        if (!images.containsKey(imgName)) {
            URL imgURL = Images.class.getResource("/images/" + imgName);
            if (imgURL != null) {
                images.put(imgName, new ImageIcon(imgURL));
            }
        }
        return images.get(imgName);
    }
}
