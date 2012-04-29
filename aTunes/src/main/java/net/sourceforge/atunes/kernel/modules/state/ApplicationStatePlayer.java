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

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IStatePlayer;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStatePlayer implements IStatePlayer {

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

    @Override
	public boolean isShuffle() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHUFFLE, false);
    }

    @Override
	public void setShuffle(boolean shuffle) {
        this.stateStore.storePreference(Preferences.SHUFFLE, shuffle);
    }

    @Override
	public boolean isRepeat() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.REPEAT, false);
    }

    @Override
	public void setRepeat(boolean repeat) {
        this.stateStore.storePreference(Preferences.REPEAT, repeat);
    }

    @Override
	public boolean isPlayAtStartup() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.PLAY_AT_STARTUP, false);
    }

    @Override
	public void setPlayAtStartup(boolean playAtStartup) {
    	this.stateStore.storePreference(Preferences.PLAY_AT_STARTUP, playAtStartup);
    }
    
    @Override
	public boolean isCacheFilesBeforePlaying() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.CACHE_FILES_BEFORE_PLAYING, false);
    }

    @Override
	public void setCacheFilesBeforePlaying(boolean cacheFilesBeforePlaying) {
    	this.stateStore.storePreference(Preferences.CACHE_FILES_BEFORE_PLAYING, cacheFilesBeforePlaying);
    }

    @Override
	public boolean isUseNormalisation() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_NORMALIZATION, false);
    }

    @Override
	public void setUseNormalisation(boolean useNormalisation) {
    	this.stateStore.storePreference(Preferences.USE_NORMALIZATION, useNormalisation);
    }
    
    @Override
	public boolean isUseShortPathNames() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_SHORT_PATH_NAMES, true);
    }

    @Override
	public void setUseShortPathNames(boolean useShortPathNames) {
    	this.stateStore.storePreference(Preferences.USE_SHORT_PATH_NAMES, useShortPathNames);
    }
    
    @Override
	public float[] getEqualizerSettings() {
    	return (float[]) this.stateStore.retrievePreference(Preferences.EQUALIZER_SETTINGS, null);
    }

    @Override
	public void setEqualizerSettings(float[] equalizerSettings) {
    	this.stateStore.storePreference(Preferences.EQUALIZER_SETTINGS, equalizerSettings);
    }
    
    @Override
	public boolean isUseFadeAway() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_FADE_AWAY, false);
    }

    @Override
	public void setUseFadeAway(boolean useFadeAway) {
    	this.stateStore.storePreference(Preferences.USE_FADE_AWAY, useFadeAway);
    }

    @Override
	public int getVolume() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.VOLUME, 50);
    }

    @Override
	public void setVolume(int volume) {
    	this.stateStore.storePreference(Preferences.VOLUME, volume);
    }
    
    @Override
	public boolean isMuteEnabled() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.MUTE, false);
    }

    @Override
	public void setMuteEnabled(boolean muteEnabled) {
    	this.stateStore.storePreference(Preferences.MUTE, muteEnabled);
    }

    @Override
	public String getPlayerEngine() {
    	return (String) this.stateStore.retrievePreference(Preferences.PLAYER_ENGINE, Constants.DEFAULT_ENGINE);        
    }

    @Override
	public void setPlayerEngine(String playerEngine) {
    	this.stateStore.storePreference(Preferences.PLAYER_ENGINE, playerEngine);
    }
}
