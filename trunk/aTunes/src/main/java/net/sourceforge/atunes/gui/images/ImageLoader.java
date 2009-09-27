/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.gui.images;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.misc.SystemProperties;

/**
 * This class contains the images that are used by aTunes.
 * 
 * @author fleax
 */
public class ImageLoader {

    // Every time a image is added, an attribute must be added
    public static final String ADD = "add.png";
    public static final String RADIO_ADD = "radio-add.png";
    public static final String ALBUM = "album.png";
    public static final String ALBUM_FAVORITE = "albumFavorite.png";
    public static final String APP_TITLE = "title.png";
    public static final String APP_ICON = "appIcon.png";
    public static final String APP_ICON_TRAY = "appIconTray.png";
    public static final String APP_ICON_MEDIUM = "appIconMedium.png";
    public static final String APP_ICON_TINY = "appIconTiny.png";
    public static final String APP_ICON_BIG = "appIconBig.png";
    public static final String ARTIST = "artist.png";
    public static final String ARTIST_FAVORITE = "artistFavorite.png";
    public static final String ARTIST_SIMILAR = "artistSimilar.png";
    public static final String AUDIO_FILE = "audioFile.png";
    public static final String AUDIO_FILE_LITTLE = "audioFile_little.png";
    public static final String AUDIO_FILE_BIG = "audioFile_big.png";
    public static final String BALANCE_LEFT = "balance-left.png";
    public static final String BALANCE_MED_LEFT = "balance-med-left.png";
    public static final String BALANCE_MED = "balance-med.png";
    public static final String BALANCE_MED_RIGHT = "balance-med-right.png";
    public static final String BALANCE_RIGHT = "balance-right.png";
    public static final String CANCEL = "cancel.png";
    public static final String CD_AUDIO = "cdAudio.png";
    public static final String CD_AUDIO_TINY = "cdAudioTiny.png";
    public static final String CD_COVER = "cd-cover.png";
    public static final String CHECK_FOR_UPDATES = "checkForUpdates.png";
    public static final String CHECK_FOR_UPDATES_BW = "checkForUpdatesBW.png";
    public static final String CLEAR = "clear.png";
    public static final String CLOSE_BUTTON = "closeButton.png";
    public static final String CLOSE_TAB = "closeTab.png";
    public static final String COLLAPSE = "collapse.png";
    public static final String CONTEXT = "context.png";
    public static final String DATE = "date.png";
    public static final String DELETE_FILE = "delete.png";
    public static final String DELETE_TAG = "erase.png";
    public static final String DEVICE = "device.png";
    public static final String DEVICE_CONNECT = "deviceConnect.png";
    public static final String DEVICE_DECONNECT = "deviceDeconnect.png";
    public static final String DEVICE_REFRESH = "deviceRefresh.png";
    public static final String DOWNLOAD_PODCAST = "rss_download.png";
    public static final String EDIT_ALBUM = "editAlbum.png";
    public static final String EMPTY = "empty.png";
    public static final String EMPTY_CONTEXT = "emptyContext.png";
    public static final String EXIT = "exit.png";
    public static final String EXPAND = "expand.png";
    public static final String EXPORT = "export.png";
    public static final String EXPORT_FILE = "export-files.png";
    public static final String EXPORT_PICTURE = "exportPicture.png";
    public static final String FAVORITE = "favorite.png";
    public static final String FILE = "file.png";
    public static final String FILE_NAME = "file_name.png";
    public static final String FOLDER = "repository.png";
    public static final String FOLDER_ORANGE = "folder.png";
    public static final String FULLSCREEN = "window-fullscreen.png";
    public static final String GENRE = "genre.png";
    public static final String GO_BOTTOM = "go-bottom.png";
    public static final String GO_DOWN = "go-down.png";
    public static final String GO_TOP = "go-top.png";
    public static final String GO_UP = "go-up.png";
    public static final String INFO = "info.png";
    public static final String KARAOKE = "karaoke.png";
    public static final String LANGUAGE = "language.png";
    public static final String LOG = "loag.png";
    public static final String LASTFM = "lastFm.png";
    public static final String MARK_RSS_AS_READ = "markAsRead.png";
    public static final String MINIMIZE_BUTTON = "minimizeButton.png";
    public static final String NAVIGATE = "navigate.png";
    public static final String NAVIGATION_TABLE = "navigationTable.png";
    public static final String NETWORK = "network.png";
    public static final String NETWORK_LITTLE = "network_little.png";
    public static final String NEW_PLAYLIST = "new_playlist.png";
    public static final String NEW_PODCAST_ENTRY = "new_podcast_entry.png";
    public static final String NEXT = "next.png";
    public static final String NEXT_TRAY = SystemProperties.OS.isWindowsVista() ? "nextTrayVista.png" : "nextTray.png";
    public static final String NEXT_TRAY_MENU = "nextTrayMenu.png";
    public static final String NO_COVER = "noCover.gif";
    public static final String NO_COVER_AUDIOFILE_PROPERTIES = "noCover_AudioFileProperties.png";
    public static final String NORMALIZATION = "normalization.png";
    public static final String NUMBER = "tag_number.png";
    public static final String OSD = "osd.png";
    public static final String PAUSE = "pause.png";
    public static final String PAUSE_TINY = "pauseTiny.png";
    public static final String PAUSE_TRAY = SystemProperties.OS.isWindowsVista() ? "pauseTrayVista.png" : "pauseTray.png";
    public static final String PAUSE_TRAY_MENU = "pauseTrayMenu.png";
    public static final String PLAY = "play.png";
    public static final String PLAY_LIST_CONTROLS = "playListControls.png";
    public static final String PLAY_TINY = "playTiny.png";
    public static final String PLAY_MENU = "playMenu.png";
    public static final String PLAY_TRAY = SystemProperties.OS.isWindowsVista() ? "playTrayVista.png" : "playTray.png";
    public static final String PLAY_TRAY_MENU = "playTrayMenu.png";
    public static final String PLAYLIST = "playlist.png";
    public static final String PLUGIN = "plugin.png";
    public static final String POWERED_BY_LAST_FM = "poweredByLastFm.png";
    public static final String PREFS = "prefs.png";
    public static final String PREVIOUS = "previous.png";
    public static final String PREVIOUS_TRAY = SystemProperties.OS.isWindowsVista() ? "previousTrayVista.png" : "previousTray.png";
    public static final String PREVIOUS_TRAY_MENU = "previousTrayMenu.png";
    public static final String RADIO = "radio.png";
    public static final String RADIO_BIG = "radio_big.png";
    public static final String RADIO_FAVORITE = "radio_favorite.png";
    public static final String RADIO_LITTLE = "radio_little.png";
    public static final String REFRESH = "refresh.png";
    public static final String REMOVE = "remove.png";
    public static final String REPEAT = "repeat.png";
    public static final String REPORT_BUG_OR_REQUEST_FEATURE = "reportBugOrRequestFeature.png";
    public static final String RSS_ADD = "rss-add.png";
    public static final String RSS_LITTLE = "rss_little.png";
    public static final String RSS = "rss.png";
    public static final String RSS_BIG = "rss_big.png";
    public static final String TAG = "tag.png";
    public static final String SAVE = "save.png";
    public static final String SCROLL_PLAYLIST = "scrollPlayList.png";
    public static final String SEARCH = "search.png";
    public static final String SEARCH_ARTIST = "searchArtist.png";
    public static final String SEARCH_AT = "searchWith.png";
    public static final String SHOW_LOG_FILE = "showLogFile.png";
    public static final String SHUFFLE = "shuffle.png";
    public static final String SHUFFLE_PLAYLIST = "shufflePlaylist.png";
    public static final String STATS = "stats.png";
    public static final String STATUS_BAR = "statusBar.png";
    public static final String STOP = "stop.png";
    public static final String STOP_TINY = "stopTiny.png";
    public static final String STOP_TRAY = SystemProperties.OS.isWindowsVista() ? "stopTrayVista.png" : "stopTray.png";
    public static final String STOP_TRAY_MENU = "stopTrayMenu.png";
    public static final String TITLE = "tag_title.png";
    public static final String TOOL_BAR = "toolBar.png";
    public static final String UNDO = "undo.png";
    public static final String VOLUME = "volume-tiny.png";
    public static final String VOlUME_MAX = "volume-max.png";
    public static final String VOLUME_MED = "volume-med.png";
    public static final String VOLUME_MIN = "volume-min.png";
    public static final String VOLUME_MUTE = "volume-mute.png";
    public static final String VOLUME_MUTE_TRAY_MENU = "volume-mute-tray-menu.png";
    public static final String VOLUME_ZERO = "volume-zero.png";
    public static final String ONE_STAR = "1_star.png";
    public static final String TWO_STAR = "2_star.png";
    public static final String THREE_STAR = "3_star.png";
    public static final String FOUR_STAR = "4_star.png";
    public static final String FIVE_STAR = "5_star.png";
    public static final String WARNING = "warning.png";
    public static final String YOUTUBE = "Youtube_16x16.png";

    /**
     * cache of images
     */
    private static Map<String, ImageIcon> images = new HashMap<String, ImageIcon>();

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
            URL imgURL = ImageLoader.class.getResource("/images/" + imgName);
            if (imgURL != null) {
                images.put(imgName, new ImageIcon(imgURL));
            }
        }
        return images.get(imgName);
    }
}
