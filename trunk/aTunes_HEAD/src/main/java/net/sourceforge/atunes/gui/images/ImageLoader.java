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

package net.sourceforge.atunes.gui.images;

import java.net.URL;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.misc.SystemProperties;

/**
 * This class contains the images that are used by aTunes.
 * 
 * @author fleax
 */
public class ImageLoader {

    // Every time a image is added, an attribute must be added
    public static final ImageIcon ADD = getImage("add.png");
    public static final ImageIcon RADIO_ADD = getImage("radio-add.png");
    public static final ImageIcon ALBUM = getImage("album.png");
    public static final ImageIcon ALBUM_FAVORITE = getImage("albumFavorite.png");
    public static final ImageIcon APP_TITLE = getImage("title.png");
    public static final ImageIcon APP_ICON = getImage("appIcon.png");
    public static final ImageIcon APP_ICON_TRAY = getImage("appIconTray.png");
    public static final ImageIcon APP_ICON_BIG = getImage("appIconBig.png");
    public static final ImageIcon APP_ICON_TINY = getImage("appIconTiny.png");
    public static final ImageIcon APP_ICON_TITLE = getImage("appIconTitle.png");
    public static final ImageIcon ARTIST = getImage("artist.png");
    public static final ImageIcon ARTIST_FAVORITE = getImage("artistFavorite.png");
    public static final ImageIcon ARTIST_SIMILAR = getImage("artistSimilar.png");
    public static final ImageIcon AUDIO_FILE = getImage("audioFile.png");
    public static final ImageIcon AUDIO_FILE_LITTLE = getImage("audioFile_little.png");
    public static final ImageIcon AUDIO_FILE_BIG = getImage("audioFile_big.png");
    public static final ImageIcon BALANCE_LEFT = getImage("balance-left.png");
    public static final ImageIcon BALANCE_MED_LEFT = getImage("balance-med-left.png");
    public static final ImageIcon BALANCE_MED = getImage("balance-med.png");
    public static final ImageIcon BALANCE_MED_RIGHT = getImage("balance-med-right.png");
    public static final ImageIcon BALANCE_RIGHT = getImage("balance-right.png");
    public static final ImageIcon CANCEL = getImage("cancel.png");
    public static final ImageIcon CD_AUDIO = getImage("cdAudio.png");
    public static final ImageIcon CD_AUDIO_TINY = getImage("cdAudioTiny.png");
    public static final ImageIcon CD_COVER = getImage("cd-cover.png");
    public static final ImageIcon CHECK_FOR_UPDATES = getImage("checkForUpdates.png");
    public static final ImageIcon CHECK_FOR_UPDATES_BW = getImage("checkForUpdatesBW.png");
    public static final ImageIcon CLEAR = getImage("clear.png");
    public static final ImageIcon CLOSE_BUTTON = getImage("closeButton.png");
    public static final ImageIcon CLOSE_TAB = getImage("closeTab.png");
    public static final ImageIcon COLLAPSE = getImage("collapse.png");
    public static final ImageIcon DATE = getImage("date.png");
    public static final ImageIcon DELETE_FILE = getImage("delete.png");
    public static final ImageIcon DELETE_TAG = getImage("erase.png");
    public static final ImageIcon DEVICE = getImage("device.png");
    public static final ImageIcon DEVICE_CONNECT = getImage("deviceConnect.png");
    public static final ImageIcon DEVICE_DECONNECT = getImage("deviceDeconnect.png");
    public static final ImageIcon DEVICE_REFRESH = getImage("deviceRefresh.png");
    public static final ImageIcon DOWNLOAD_PODCAST = getImage("rss_download.png");
    public static final ImageIcon EDIT_ALBUM = getImage("editAlbum.png");
    public static final ImageIcon EMPTY = getImage("empty.png");
    public static final ImageIcon EMPTY_CONTEXT = getImage("emptyContext.png");
    public static final ImageIcon EXIT = getImage("exit.png");
    public static final ImageIcon EXPAND = getImage("expand.png");
    public static final ImageIcon EXPORT = getImage("export.png");
    public static final ImageIcon EXPORT_FILE = getImage("export-files.png");
    public static final ImageIcon EXPORT_PICTURE = getImage("exportPicture.png");
    public static final ImageIcon FAVORITE = getImage("favorite.png");
    public static final ImageIcon FILE = getImage("file.png");
    public static final ImageIcon FILE_NAME = getImage("file_name.png");
    public static final ImageIcon FOLDER = getImage("repository.png");
    public static final ImageIcon FOLDER_ORANGE = getImage("folder.png");
    public static final ImageIcon FULLSCREEN = getImage("window-fullscreen.png");
    public static final ImageIcon GENRE = getImage("genre.png");
    public static final ImageIcon GO_BOTTOM = getImage("go-bottom.png");
    public static final ImageIcon GO_DOWN = getImage("go-down.png");
    public static final ImageIcon GO_TOP = getImage("go-top.png");
    public static final ImageIcon GO_UP = getImage("go-up.png");
    public static final ImageIcon INFO = getImage("info.png");
    public static final ImageIcon KARAOKE = getImage("karaoke.png");
    public static final ImageIcon LANGUAGE = getImage("language.png");
    public static final ImageIcon LOG = getImage("loag.png");
    public static final ImageIcon MARK_RSS_AS_READ = getImage("markAsRead.png");
    public static final ImageIcon MINIMIZE_BUTTON = getImage("minimizeButton.png");
    public static final ImageIcon NAVIGATE = getImage("navigate.png");
    public static final ImageIcon NAVIGATION_TABLE = getImage("navigationTable.png");
    public static final ImageIcon NETWORK = getImage("network.png");
    public static final ImageIcon NETWORK_LITTLE = getImage("network_little.png");
    public static final ImageIcon NEW_PLAYLIST = getImage("new_playlist.png");
    public static final ImageIcon NEW_PODCAST_ENTRY = getImage("new_podcast_entry.png");
    public static final ImageIcon NEXT = getImage("next.png");
    public static final ImageIcon NEXT_TRAY = SystemProperties.OS.isWindowsVista() ? getImage("nextTrayVista.png") : getImage("nextTray.png");
    public static final ImageIcon NEXT_TRAY_MENU = getImage("nextTrayMenu.png");
    public static final ImageIcon NO_COVER = getImage("noCover.gif");
    public static final ImageIcon NO_COVER_AUDIOFILE_PROPERTIES = getImage("noCover_AudioFileProperties.png");
    public static final ImageIcon NORMALIZATION = getImage("normalization.png");
    public static final ImageIcon NUMBER = getImage("tag_number.png");
    public static final ImageIcon OSD = getImage("osd.png");
    public static final ImageIcon PAUSE = getImage("pause.png");
    public static final ImageIcon PAUSE_TINY = getImage("pauseTiny.png");
    public static final ImageIcon PAUSE_TRAY = SystemProperties.OS.isWindowsVista() ? getImage("pauseTrayVista.png") : getImage("pauseTray.png");
    public static final ImageIcon PAUSE_TRAY_MENU = getImage("pauseTrayMenu.png");
    public static final ImageIcon PLAY = getImage("play.png");
    public static final ImageIcon PLAY_LIST_CONTROLS = getImage("playListControls.png");
    public static final ImageIcon PLAY_TINY = getImage("playTiny.png");
    public static final ImageIcon PLAY_MENU = getImage("playMenu.png");
    public static final ImageIcon PLAY_TRAY = SystemProperties.OS.isWindowsVista() ? getImage("playTrayVista.png") : getImage("playTray.png");
    public static final ImageIcon PLAY_TRAY_MENU = getImage("playTrayMenu.png");
    public static final ImageIcon PLAYLIST = getImage("playlist.png");
    public static final ImageIcon POWERED_BY_LAST_FM = getImage("poweredByLastFm.png");
    public static final ImageIcon PREFS = getImage("prefs.png");
    public static final ImageIcon PREVIOUS = getImage("previous.png");
    public static final ImageIcon PREVIOUS_TRAY = SystemProperties.OS.isWindowsVista() ? getImage("previousTrayVista.png") : getImage("previousTray.png");
    public static final ImageIcon PREVIOUS_TRAY_MENU = getImage("previousTrayMenu.png");
    public static final ImageIcon RADIO = getImage("radio.png");
    public static final ImageIcon RADIO_BIG = getImage("radio_big.png");
    public static final ImageIcon RADIO_FAVORITE = getImage("radio_favorite.png");
    public static final ImageIcon RADIO_LITTLE = getImage("radio_little.png");
    public static final ImageIcon REFRESH = getImage("refresh.png");
    public static final ImageIcon REMOVE = getImage("remove.png");
    public static final ImageIcon REPEAT = getImage("repeat.png");
    public static final ImageIcon REPORT_BUG_OR_REQUEST_FEATURE = getImage("reportBugOrRequestFeature.png");
    public static final ImageIcon RSS_ADD = getImage("rss-add.png");
    public static final ImageIcon RSS_LITTLE = getImage("rss_little.png");
    public static final ImageIcon RSS = getImage("rss.png");
    public static final ImageIcon RSS_BIG = getImage("rss_big.png");
    public static final ImageIcon TAG = getImage("tag.png");
    public static final ImageIcon SAVE = getImage("save.png");
    public static final ImageIcon SCROLL_PLAYLIST = getImage("scrollPlayList.png");
    public static final ImageIcon SEARCH = getImage("search.png");
    public static final ImageIcon SEARCH_ARTIST = getImage("searchArtist.png");
    public static final ImageIcon SEARCH_AT = getImage("searchWith.png");
    public static final ImageIcon SHOW_LOG_FILE = getImage("showLogFile.png");
    public static final ImageIcon SHUFFLE = getImage("shuffle.png");
    public static final ImageIcon SHUFFLE_PLAYLIST = getImage("shufflePlaylist.png");
    public static final ImageIcon STATS = getImage("stats.png");
    public static final ImageIcon STATUS_BAR = getImage("statusBar.png");
    public static final ImageIcon STOP = getImage("stop.png");
    public static final ImageIcon STOP_TINY = getImage("stopTiny.png");
    public static final ImageIcon STOP_TRAY = SystemProperties.OS.isWindowsVista() ? getImage("stopTrayVista.png") : getImage("stopTray.png");
    public static final ImageIcon STOP_TRAY_MENU = getImage("stopTrayMenu.png");
    public static final ImageIcon TITLE = getImage("tag_title.png");
    public static final ImageIcon TOOL_BAR = getImage("toolBar.png");
    public static final ImageIcon UNDO = getImage("undo.png");
    public static final ImageIcon VOLUME = getImage("volume-tiny.png");
    public static final ImageIcon VOlUME_MAX = getImage("volume-max.png");
    public static final ImageIcon VOLUME_MED = getImage("volume-med.png");
    public static final ImageIcon VOLUME_MIN = getImage("volume-min.png");
    public static final ImageIcon VOLUME_MUTE = getImage("volume-mute.png");
    public static final ImageIcon VOLUME_MUTE_TRAY_MENU = getImage("volume-mute-tray-menu.png");
    public static final ImageIcon VOLUME_ZERO = getImage("volume-zero.png");
    public static final ImageIcon ONE_STAR = getImage("1_star.png");
    public static final ImageIcon TWO_STAR = getImage("2_star.png");
    public static final ImageIcon THREE_STAR = getImage("3_star.png");
    public static final ImageIcon FOUR_STAR = getImage("4_star.png");
    public static final ImageIcon FIVE_STAR = getImage("5_star.png");
    public static final ImageIcon WARNING = getImage("warning.png");
    public static final ImageIcon YOUTUBE = getImage("Youtube_16x16.png");

    /**
     * Returns an image.
     * 
     * @param imgName
     *            the img name
     * 
     * @return An ImageIcon
     */
    private static ImageIcon getImage(String imgName) {
        URL imgURL = ImageLoader.class.getResource("/images/" + imgName);
        return imgURL != null ? new ImageIcon(imgURL) : null;
    }
}
