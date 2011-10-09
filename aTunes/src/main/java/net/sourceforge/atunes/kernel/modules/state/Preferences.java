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

package net.sourceforge.atunes.kernel.modules.state;

public enum Preferences {
	
	ALBUM_COLUMNS,
	ALLOW_REPEATED_SONGS_IN_DEVICE, 
	APPLY_CHANGES_TO_SOURCE_FILES_BEFORE_IMPORT, 
	AUTO_LOVE_FAVORITE_SONG, 
	AUTO_REPOSITORY_REFRESH_TIME, 
	AUTO_SCROLL_PLAYLIST, 
	CACHE_FILES_BEFORE_PLAYING,
    CASE_SENSITIVE_REPOSITORY_STRUCTURE_KEYS,
	CD_RIPPER_FILENAME_PATTERN, 
	COLUMNS, 
	COMMAND_AFTER_ACCESS_REPOSITORY, 
	COMMAND_BEFORE_ACCESS_REPOSITORY, 
	CUSTOM_NAVIGATOR_COLUMNS, 
	DEFAULT_DEVICE_LOCATION, 
	NAVIGATOR_COLUMNS, 
	DEFAULT_SEARCH, 
	DEVICE_FILENAME_PATTERN, 
	DEVICE_FOLDER_PATH_PATTERN, 
	ENABLE_ADVANCED_SEARCH, 
	ENABLE_HOTKEYS, 
	ENCODER, 
	ENCODER_QUALITY, 
	EQUALIZER_SETTINGS, 
	EXTENDED_TOOLTIP_DELAY, 
	FLAC_ENCODER_QUALITY, 
	FONT_SETTINGS, 
	FRAME_CLASS, 
	FRAME_STATES, 
	FULL_SCREEN_BACKGROUND, 
	HIDE_VARIOUS_ARTISTS_ALBUMS, 
	HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS, 
	HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES, 
	HOTKEYS_CONFIG, 
	IMPORT_EXPORT_FILENAME_PATTERN, 
	IMPORT_EXPORT_FOLDER_PATH_PATTERN, 
	LAST_REPOSITORY_FOLDERS, 
	LASTFM_ENABLED, 
	LASTFM_PASSWORD, 
	LASTFM_USER, 
	LOAD_PLAYLIST_PATH, 
	LOCALE, 
	LYRICS_ENGINES_INFO, 
	MASSIVE_RECOGNITION_PATTERNS, 
	MINIMUM_SONG_NUMER_PER_ALBUM, 
	MP3_ENCODER_QUALITY, 
	MUTE, 
	NAVIGATION_VIEW, 
	NOTIFICATION_ENGINE,
	OLD_LOCALE, 
	OSD_DURATION, 
	OSD_HORIZONTAL_ALINGMENT, 
	OSD_VERTICAL_ALINGMENT, 
	OSD_WIDTH, 
	PLAY_AT_STARTUP, 
	PLAYER_ENGINE, 
	PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL, 
	PODCAST_FEED_ENTRY_DOWNLOAD_PATH, 
	PROXY, LOOK_AND_FEEL, 
	READ_INFO_FROM_RADIO_STREAM, 
	RECOGNITION_PATTERNS, 
	REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED, 
	REPEAT, 
	REVIEW_TAGS_BEFORE_IMPORT, 
	SAVE_CONTEXT_PICTURE, 
	SAVE_PLAYLIST_PATH, 
	SAVE_REPOSITORY_AS_XML, 
	SEARCH_RESULTS_COLUMNS, 
	SELECTED_CONTEXT_TAB, 
	SET_TITLES_WHEN_IMPORTING, 
	SET_TRACK_NUMBERS_WHEN_IMPORTING,
	SHOW_ADVANCED_PLAYER_CONTROLS,
	SHOW_ALL_RADIO_STATIONS,
	SHOW_AUDIO_OBJECT_PROPERTIES, 
	SHOW_CONTEXT_ALBUMS_IN_GRID, 
	SHOW_EXTENDED_TOOLTIP, 
	SHOW_FAVORITES_IN_NAVIGATOR, 
	SHOW_NAVIGATION_TABLE,
	SHOW_NAVIGATION_TREE, 
	SHOW_OSD, 
	SHOW_PLAYER_CONTROLS_ON_TOP,
	SHOW_STATUS_BAR, 
	SHOW_SYSTEM_TRAY, 
	SHOW_TICKS, 
	SHOW_TRAY_PLAYER, 
	SHUFFLE, 
	SIMILAR_ARTISTS_MODE,
	STOP_PLAYER_ON_PLAYLIST_CLEAR, 
	STOP_PLAYER_ON_PLAYLIST_SWITCH,
	TRAY_PLAYER_ICONS_COLOR,
	USE_CD_ERROR_CORRECTION, 
	USE_CONTEXT, 
	USE_DOWNLOADED_PODCAST_FEED_ENTRIES, 
	USE_FADE_AWAY, 
	USE_NORMALIZATION, 
	USE_PERSON_NAMES_ARTIST_TAG_SORTING, 
	USE_SHORT_PATH_NAMES, 
	USE_SMART_TAG_VIEW_SORTING, 
	VIEW_MODE, 
	VOLUME,
	PLUGINS_ENABLED;
}
