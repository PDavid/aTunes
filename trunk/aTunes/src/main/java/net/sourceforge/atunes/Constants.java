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

package net.sourceforge.atunes;

import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;
import net.sourceforge.atunes.kernel.modules.updates.VersionType;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Constants used by application.
 * 
 * @author fleax
 */
public final class Constants {

    private Constants() {
    }

    public static final ApplicationVersion VERSION = new ApplicationVersion("", 2, 1, 0, VersionType.RELEASE_CANDIDATE, "", "http://www.atunes.org/update.php");

    /** File containing log4j properties. */
    public static final String LOG4J_FILE = "/settings/log4j.properties";

    /** File containing aTunes log (must be the same as in LOG4J_FILE). */
    public static final String LOG_FILE = "aTunes.log";

    /** Application Home Page. */
    public static final String APP_WEB = "http://www.atunes.org";

    /** Application Wiki Page. */
    public static final String APP_WIKI = "http://www.atunes.org/wiki";

    /** Application Home Page at Sourceforge. */
    public static final String APP_SOURCEFORGE_WEB = "http://sourceforge.net/projects/atunes";

    /** The Constant CONTRIBUTORS_WEB. */
    public static final String CONTRIBUTORS_WEB = "http://www.atunes.org/?page_id=7";

    /** The Constant CONTRIBUTORS_WANTED. */
    public static final String CONTRIBUTORS_WANTED = "http://www.atunes.org/wiki/index.php?title=Contributing#Create_or_update_a_translation";

    /** Radio preset list. Sourceforge SVN page. */
    public static final String RADIO_LIST_DOWNLOAD = "http://atunes.svn.sourceforge.net/viewvc/*checkout*/atunes/aTunes_HEAD/settings/preset_radios.xml?revision=HEAD";

    /** Radio preset list. Assembla common-jukebox SVN page. */
    public static final String RADIO_LIST_DOWNLOAD_COMMON_JUKEBOX = "http://svn2.assembla.com/svn/common-jukebox/common-jukebox/src/main/resources/preset_radios.xml";

    /** Web site for reporting bug and requesting features. */
    public static final String REPORT_BUG_OR_REQUEST_FEATURE_URL = "http://sourceforge.net/tracker/?group_id=161929";

    /** Application name. */
    public static final String APP_NAME = "aTunes";

    /** Application description. */
    public static final String APP_DESCRIPTION = "GPL Audio Player";

    /** Version string. */
    public static final String APP_VERSION = StringUtils.getString("Version ", VERSION.toString());

    /** Author and year. */
    public static final String APP_AUTHOR = "2006-2010 The aTunes Team";

    /** File where repository information is stored. */
    public static final String CACHE_REPOSITORY_NAME = "repository.dat";

    /** The Constant XML_CACHE_REPOSITORY_NAME. */
    public static final String XML_CACHE_REPOSITORY_NAME = "repository.xml";

    /** The Constant CACHE_FAVORITES_NAME. */
    public static final String CACHE_FAVORITES_NAME = "favorites.dat";

    /** The Constant XML_CACHE_FAVORITES_NAME. */
    public static final String XML_CACHE_FAVORITES_NAME = "favorites.xml";

    /** The Constant CACHE_STATISTICS_NAME. */
    public static final String CACHE_STATISTICS_NAME = "statistics.dat";

    /** The Constant XML_CACHE_STATISTICS_NAME. */
    public static final String XML_CACHE_STATISTICS_NAME = "statistics.xml";

    /** Image size at audio object properties panel. */
    public static final ImageSize IMAGE_SIZE = ImageSize.SIZE_90;

    /** Image size at dialogs. */
    public static final ImageSize DIALOG_IMAGE_SIZE = ImageSize.SIZE_120;

    /** Large image width at dialogs. */
    public static final int DIALOG_LARGE_IMAGE_WIDTH = 400;

    /** Large image height at dialogs. */
    public static final int DIALOG_LARGE_IMAGE_HEIGHT = 400;

    /** Image width at tooltips. */
    public static final int TOOLTIP_IMAGE_WIDTH = 100;

    /** Image height at tooltips. */
    public static final int TOOLTIP_IMAGE_HEIGHT = TOOLTIP_IMAGE_WIDTH;

    /** Thumbs window width. */
    public static final int THUMBS_WINDOW_WIDTH = 900;

    /** Thumbs window height. */
    public static final int THUMBS_WINDOW_HEIGHT = 700;

    /** Max number of repositories saved. */
    public static final int MAX_RECENT_REPOSITORIES = 5;

    /** File where playlists are stored. */
    public static final String PLAYLISTS_FILE = "playLists.xml";

    /** File where playlists contents are stored. */
    public static final String PLAYLISTS_CONTENTS_FILE = "playListsContents.dat";

    /** Directory where Mac OS X binaries are found (i.e. mplayer, lame, etc) */
    public static final String MAC_TOOLS_DIR = "mac_tools";

    /** Image Width at context panel. */
    public static final int CONTEXT_IMAGE_WIDTH = 75;

    /** Image Height at context panel. */
    public static final int CONTEXT_IMAGE_HEIGHT = 75;

    /** File where radios are stored. */
    public static final String RADIO_CACHE = "radios.xml";

    /** File where preset radios are stored. */
    public static final String PRESET_RADIO_CACHE = "preset_radios.xml";

    /** File where podcast feeds are stored. */
    public static final String PODCAST_FEED_CACHE = "podcastFeeds.xml";

    /** Size in pixels of Full Screen Covers. */
    public static final int FULL_SCREEN_COVER = 300;

    /** Port number for multiple instances socket communication. */
    public static final int MULTIPLE_INSTANCES_SOCKET = 7777;

    /** Directory where are translations. */
    public static final String TRANSLATIONS_DIR = "translations";

    /** Cache dir. */
    public static final String CACHE_DIR = "cache";

    /** Last.fm cache dir. */
    public static final String LAST_FM_CACHE_DIR = "lastfm";

    /** Last.fm artist submission cache dir. */
    public static final String LAST_FM_SUBMISSION_CACHE_DIR = "submission";

    /** Equalizer presets file. */
    public static final String EQUALIZER_PRESETS_FILE = "/settings/presets.properties";

    /** Lucene index dir. */
    public static final String LUCENE_INDEX_DIR = "index";

    /** Repository index dir. */
    public static final String REPOSITORY_INDEX_DIR = LUCENE_INDEX_DIR + "/repository";

    /** Device index dir. */
    public static final String DEVICE_INDEX_DIR = LUCENE_INDEX_DIR + "/device";

    /** Favorites index dir. */
    public static final String FAVORITES_INDEX_DIR = LUCENE_INDEX_DIR + "/favorites";

    /** Radio index dir. */
    public static final String RADIO_INDEX_DIR = LUCENE_INDEX_DIR + "/radio";

    /** Default podcast download dir. */
    public static final String DEFAULT_PODCAST_FEED_ENTRY_DOWNLOAD_DIR = "/podcasts";

    /** Temp dir. */
    public static final String TEMP_DIR = "temp";


    /**
     * Name of the file used to store device identifier
     */
    public static final String DEVICE_ID_FILE = ".aTunesDevice";

    /**
     * Prefix of the file used to store device cache
     */
    public static final String DEVICE_CACHE_FILE_PREFIX = "device.dat";

    /**
     * Size of artist image in ContextPanel. Images are scaled to fit this size
     */
    public static final int ARTIST_IMAGE_SIZE = 225;

    /**
     * Size of album image in ContextPanel. Images are scaled to fit this size
     */
    public static final ImageSize ALBUM_IMAGE_SIZE = ImageSize.SIZE_200;

    /**
     * Size of images in cover navigator
     */
    public static final ImageSize COVER_NAVIGATOR_IMAGE_SIZE = ImageSize.SIZE_150;

    /** VLC config file */
    public static final String VLC_CONFIG_FILE = "/settings/vlc.properties";

    /** Plugins dir */
    public static final String PLUGINS_DIR = "plugins";
    
}
