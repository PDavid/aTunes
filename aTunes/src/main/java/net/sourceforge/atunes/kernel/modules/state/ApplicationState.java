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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.IState;

public class ApplicationState implements IState {

    private static final String UNCHECKED = "unchecked";

	/**
     * Component responsible of store state
     */
    private IStateStore stateStore;
    
    /**
     * Sets state store
     * @param store
     */
    public void setStateStore(IStateStore store) {
		this.stateStore = store;
	}

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseCdErrorCorrection()
	 */
    @Override
	public boolean isUseCdErrorCorrection() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_CD_ERROR_CORRECTION, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseCdErrorCorrection(boolean)
	 */
    @Override
	public void setUseCdErrorCorrection(boolean useCdErrorCorrection) {
    	this.stateStore.storePreference(Preferences.USE_CD_ERROR_CORRECTION, useCdErrorCorrection);
    }
    
    

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getEncoder()
	 */
    @Override
	public String getEncoder() {
    	return (String) this.stateStore.retrievePreference(Preferences.ENCODER, "OGG");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEncoder(java.lang.String)
	 */
    @Override
	public void setEncoder(String encoder) {
    	this.stateStore.storePreference(Preferences.ENCODER, encoder);
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getEncoderQuality()
	 */
    @Override
	public String getEncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.ENCODER_QUALITY, "5");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEncoderQuality(java.lang.String)
	 */
    @Override
	public void setEncoderQuality(String encoderQuality) {
    	this.stateStore.storePreference(Preferences.ENCODER_QUALITY, encoderQuality);
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getMp3EncoderQuality()
	 */
    @Override
	public String getMp3EncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.MP3_ENCODER_QUALITY, "medium");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setMp3EncoderQuality(java.lang.String)
	 */
    @Override
	public void setMp3EncoderQuality(String mp3EncoderQuality) {
    	this.stateStore.storePreference(Preferences.MP3_ENCODER_QUALITY, mp3EncoderQuality);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getFlacEncoderQuality()
	 */
    @Override
	public String getFlacEncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.FLAC_ENCODER_QUALITY, "-5");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setFlacEncoderQuality(java.lang.String)
	 */
    @Override
	public void setFlacEncoderQuality(String flacEncoderQuality) {
    	this.stateStore.storePreference(Preferences.FLAC_ENCODER_QUALITY, flacEncoderQuality);
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getCdRipperFileNamePattern()
	 */
    @Override
	public String getCdRipperFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.CD_RIPPER_FILENAME_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setCdRipperFileNamePattern(java.lang.String)
	 */
    @Override
	public void setCdRipperFileNamePattern(String cdRipperFileNamePattern) {
    	this.stateStore.storePreference(Preferences.CD_RIPPER_FILENAME_PATTERN, cdRipperFileNamePattern);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getColumns()
	 */
    @Override
	@SuppressWarnings(UNCHECKED)
	public Map<String, ColumnBean> getColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore.retrievePreference(Preferences.COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setColumns(java.util.Map)
	 */
    @Override
	public void setColumns(Map<String, ColumnBean> columns) {
    	if (getColumns() == null || !getColumns().equals(columns)) {
    		this.stateStore.storePreference(Preferences.COLUMNS, new HashMap<String, ColumnBean>(columns));
    	}
    }
    
    


    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDefaultDeviceLocation()
	 */
    @Override
	public String getDefaultDeviceLocation() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEFAULT_DEVICE_LOCATION, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDefaultDeviceLocation(java.lang.String)
	 */
    @Override
	public void setDefaultDeviceLocation(String defaultDeviceLocation) {
    	this.stateStore.storePreference(Preferences.DEFAULT_DEVICE_LOCATION, defaultDeviceLocation);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getPodcastFeedEntriesRetrievalInterval()
	 */
    @Override
	public long getPodcastFeedEntriesRetrievalInterval() {
    	return (Long) this.stateStore.retrievePreference(Preferences.PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL, PodcastFeedHandler.DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL);        
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setPodcastFeedEntriesRetrievalInterval(long)
	 */
    @Override
	public void setPodcastFeedEntriesRetrievalInterval(long podcastFeedEntriesRetrievalInterval) {
    	this.stateStore.storePreference(Preferences.PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL, podcastFeedEntriesRetrievalInterval);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getPodcastFeedEntryDownloadPath()
	 */
    @Override
	public String getPodcastFeedEntryDownloadPath() {
    	return (String) this.stateStore.retrievePreference(Preferences.PODCAST_FEED_ENTRY_DOWNLOAD_PATH, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setPodcastFeedEntryDownloadPath(java.lang.String)
	 */
    @Override
	public void setPodcastFeedEntryDownloadPath(String podcastFeedEntryDownloadPath) {
    	this.stateStore.storePreference(Preferences.PODCAST_FEED_ENTRY_DOWNLOAD_PATH, podcastFeedEntryDownloadPath);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseDownloadedPodcastFeedEntries()
	 */
    @Override
	public boolean isUseDownloadedPodcastFeedEntries() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_DOWNLOADED_PODCAST_FEED_ENTRIES, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseDownloadedPodcastFeedEntries(boolean)
	 */
    @Override
	public void setUseDownloadedPodcastFeedEntries(boolean useDownloadedPodcastFeedEntries) {
    	this.stateStore.storePreference(Preferences.USE_DOWNLOADED_PODCAST_FEED_ENTRIES, useDownloadedPodcastFeedEntries);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isRemovePodcastFeedEntriesRemovedFromPodcastFeed()
	 */
    @Override
	public boolean isRemovePodcastFeedEntriesRemovedFromPodcastFeed() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setRemovePodcastFeedEntriesRemovedFromPodcastFeed(boolean)
	 */
    @Override
	public void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(boolean removePodcastFeedEntriesRemovedFromPodcastFeed) {
    	this.stateStore.storePreference(Preferences.REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED, removePodcastFeedEntriesRemovedFromPodcastFeed);
    }
    
    

       


    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isAutoScrollPlayListEnabled()
	 */
    @Override
	public boolean isAutoScrollPlayListEnabled() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.AUTO_SCROLL_PLAYLIST, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAutoScrollPlayListEnabled(boolean)
	 */
    @Override
	public void setAutoScrollPlayListEnabled(boolean autoScrollPlayListEnabled) {
    	this.stateStore.storePreference(Preferences.AUTO_SCROLL_PLAYLIST, autoScrollPlayListEnabled);
    }
    
    

    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLoadPlaylistPath()
	 */
    @Override
	public String getLoadPlaylistPath() {
    	return (String) this.stateStore.retrievePreference(Preferences.LOAD_PLAYLIST_PATH, null); 
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLoadPlaylistPath(java.lang.String)
	 */
    @Override
	public void setLoadPlaylistPath(String loadPlaylistPath) {
    	this.stateStore.storePreference(Preferences.LOAD_PLAYLIST_PATH, loadPlaylistPath);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getSavePlaylistPath()
	 */
    @Override
	public String getSavePlaylistPath() {
    	return (String) this.stateStore.retrievePreference(Preferences.SAVE_PLAYLIST_PATH, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSavePlaylistPath(java.lang.String)
	 */
    @Override
	public void setSavePlaylistPath(String savePlaylistPath) {
    	this.stateStore.storePreference(Preferences.SAVE_PLAYLIST_PATH, savePlaylistPath);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isStopPlayerOnPlayListSwitch()
	 */
    @Override
	public boolean isStopPlayerOnPlayListSwitch() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_SWITCH, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setStopPlayerOnPlayListSwitch(boolean)
	 */
    @Override
	public void setStopPlayerOnPlayListSwitch(boolean stopPlayerOnPlayListSwitch) {
    	this.stateStore.storePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_SWITCH, stopPlayerOnPlayListSwitch);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isStopPlayerOnPlayListClear()
	 */
    @Override
	public boolean isStopPlayerOnPlayListClear() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_CLEAR, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setStopPlayerOnPlayListClear(boolean)
	 */
    @Override
	public void setStopPlayerOnPlayListClear(boolean stopPlayerOnPlayListClear) {
    	this.stateStore.storePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_CLEAR, stopPlayerOnPlayListClear);
    }
    
    

    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDeviceFileNamePattern()
	 */
    @Override
	public String getDeviceFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEVICE_FILENAME_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDeviceFileNamePattern(java.lang.String)
	 */
    @Override
	public void setDeviceFileNamePattern(String deviceFileNamePattern) {
    	this.stateStore.storePreference(Preferences.DEVICE_FILENAME_PATTERN, deviceFileNamePattern);
    }
    
    
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDeviceFolderPathPattern()
	 */
    @Override
	public String getDeviceFolderPathPattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEVICE_FOLDER_PATH_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDeviceFolderPathPattern(java.lang.String)
	 */
    @Override
	public void setDeviceFolderPathPattern(String deviceFolderPathPattern) {
    	this.stateStore.storePreference(Preferences.DEVICE_FOLDER_PATH_PATTERN, deviceFolderPathPattern);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isAllowRepeatedSongsInDevice()
	 */
    @Override
	public boolean isAllowRepeatedSongsInDevice() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAllowRepeatedSongsInDevice(boolean)
	 */
    @Override
	public void setAllowRepeatedSongsInDevice(boolean allowRepeatedSongsInDevice) {
    	this.stateStore.storePreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE, allowRepeatedSongsInDevice);
    }
    
    

    

	
}
