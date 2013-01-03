/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.model;

/**
 * State of player
 * 
 * @author alex
 * 
 */
public interface IStatePlayer extends IState {

	/**
	 * Shuffle playback
	 * 
	 * @return
	 */
	public boolean isShuffle();

	/**
	 * Shuffle playback
	 * 
	 * @param shuffle
	 */
	public void setShuffle(boolean shuffle);

	/**
	 * Repeat playback
	 * 
	 * @return
	 */
	public boolean isRepeat();

	/**
	 * Repeat playback
	 * 
	 * @param repeat
	 */
	public void setRepeat(boolean repeat);

	/**
	 * Play when app starts
	 * 
	 * @return
	 */
	public boolean isPlayAtStartup();

	/**
	 * Play when app starts
	 * 
	 * @param playAtStartup
	 */
	public void setPlayAtStartup(boolean playAtStartup);

	/**
	 * Cache files before playing
	 * 
	 * @return
	 */
	public boolean isCacheFilesBeforePlaying();

	/**
	 * Cache files before playing
	 * 
	 * @param cacheFilesBeforePlaying
	 */
	public void setCacheFilesBeforePlaying(boolean cacheFilesBeforePlaying);

	/**
	 * Sound normalization
	 * 
	 * @return
	 */
	public boolean isUseNormalisation();

	/**
	 * Similar Artist mode
	 * 
	 * @return
	 */
	public boolean isSimilarArtistMode();

	/**
	 * Similar Artist mode
	 * 
	 * @param similarArtistMode
	 */
	public void setSimilarArtistMode(boolean similarArtistMode);

	/**
	 * Sound normalization
	 * 
	 * @param useNormalisation
	 */
	public void setUseNormalisation(boolean useNormalisation);

	/**
	 * Use short path names (windows)
	 * 
	 * @return
	 */
	public boolean isUseShortPathNames();

	/**
	 * Use short path names (windows)
	 * 
	 * @param useShortPathNames
	 */
	public void setUseShortPathNames(boolean useShortPathNames);

	/**
	 * Equalizer
	 * 
	 * @return
	 */
	public float[] getEqualizerSettings();

	/**
	 * Equalizere
	 * 
	 * @param equalizerSettings
	 */
	public void setEqualizerSettings(float[] equalizerSettings);

	/**
	 * Fade away when finishing song
	 * 
	 * @return
	 */
	public boolean isUseFadeAway();

	/**
	 * Fade away when finishing song
	 * 
	 * @param useFadeAway
	 */
	public void setUseFadeAway(boolean useFadeAway);

	/**
	 * Volume
	 * 
	 * @return
	 */
	public int getVolume();

	/**
	 * Volume
	 * 
	 * @param volume
	 */
	public void setVolume(int volume);

	/**
	 * Mute
	 * 
	 * @return
	 */
	public boolean isMuteEnabled();

	/**
	 * Mute
	 * 
	 * @param muteEnabled
	 */
	public void setMuteEnabled(boolean muteEnabled);

	/**
	 * Player engine
	 * 
	 * @return
	 */
	public String getPlayerEngine();

	/**
	 * Player engine
	 * 
	 * @param playerEngine
	 */
	public void setPlayerEngine(String playerEngine);

	/**
	 * @return if equalizer is enabled
	 */
	public boolean isEqualizerEnabled();

	/**
	 * Enables equalizer
	 * 
	 * @param enabled
	 */
	public void setEqualizerEnabled(boolean enabled);
}
