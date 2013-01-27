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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.Map;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStatePlayer implements IStatePlayer {

	private PreferenceHelper preferenceHelper;

	/**
	 * @param preferenceHelper
	 */
	public void setPreferenceHelper(final PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	@Override
	public boolean isShuffle() {
		return this.preferenceHelper.getPreference(Preferences.SHUFFLE,
				Boolean.class, false);
	}

	@Override
	public void setShuffle(final boolean shuffle) {
		this.preferenceHelper.setPreference(Preferences.SHUFFLE, shuffle);
	}

	@Override
	public boolean isRepeat() {
		return this.preferenceHelper.getPreference(Preferences.REPEAT,
				Boolean.class, false);
	}

	@Override
	public void setRepeat(final boolean repeat) {
		this.preferenceHelper.setPreference(Preferences.REPEAT, repeat);
	}

	@Override
	public boolean isPlayAtStartup() {
		return this.preferenceHelper.getPreference(Preferences.PLAY_AT_STARTUP,
				Boolean.class, false);
	}

	@Override
	public void setPlayAtStartup(final boolean playAtStartup) {
		this.preferenceHelper.setPreference(Preferences.PLAY_AT_STARTUP,
				playAtStartup);
	}

	@Override
	public boolean isCacheFilesBeforePlaying() {
		return this.preferenceHelper.getPreference(
				Preferences.CACHE_FILES_BEFORE_PLAYING, Boolean.class, false);
	}

	@Override
	public void setCacheFilesBeforePlaying(final boolean cacheFilesBeforePlaying) {
		this.preferenceHelper
				.setPreference(Preferences.CACHE_FILES_BEFORE_PLAYING,
						cacheFilesBeforePlaying);
	}

	@Override
	public boolean isUseNormalisation() {
		return this.preferenceHelper.getPreference(
				Preferences.USE_NORMALIZATION, Boolean.class, false);
	}

	@Override
	public void setUseNormalisation(final boolean useNormalisation) {
		this.preferenceHelper.setPreference(Preferences.USE_NORMALIZATION,
				useNormalisation);
	}

	@Override
	public boolean isUseShortPathNames() {
		return this.preferenceHelper.getPreference(
				Preferences.USE_SHORT_PATH_NAMES, Boolean.class, true);
	}

	@Override
	public void setUseShortPathNames(final boolean useShortPathNames) {
		this.preferenceHelper.setPreference(Preferences.USE_SHORT_PATH_NAMES,
				useShortPathNames);
	}

	@Override
	public float[] getEqualizerSettings() {
		return this.preferenceHelper.getPreference(
				Preferences.EQUALIZER_SETTINGS, float[].class, null);
	}

	@Override
	public void setEqualizerSettings(final float[] equalizerSettings) {
		this.preferenceHelper.setPreference(Preferences.EQUALIZER_SETTINGS,
				equalizerSettings);
	}

	@Override
	public boolean isUseFadeAway() {
		return this.preferenceHelper.getPreference(Preferences.USE_FADE_AWAY,
				Boolean.class, false);
	}

	@Override
	public void setUseFadeAway(final boolean useFadeAway) {
		this.preferenceHelper.setPreference(Preferences.USE_FADE_AWAY,
				useFadeAway);
	}

	@Override
	public int getVolume() {
		return this.preferenceHelper.getPreference(Preferences.VOLUME,
				Integer.class, 50);
	}

	@Override
	public void setVolume(final int volume) {
		this.preferenceHelper.setPreference(Preferences.VOLUME, volume);
	}

	@Override
	public boolean isMuteEnabled() {
		return this.preferenceHelper.getPreference(Preferences.MUTE,
				Boolean.class, false);
	}

	@Override
	public void setMuteEnabled(final boolean muteEnabled) {
		this.preferenceHelper.setPreference(Preferences.MUTE, muteEnabled);
	}

	@Override
	public String getPlayerEngine() {
		return this.preferenceHelper.getPreference(Preferences.PLAYER_ENGINE,
				String.class, Constants.DEFAULT_ENGINE);
	}

	@Override
	public void setPlayerEngine(final String playerEngine) {
		this.preferenceHelper.setPreference(Preferences.PLAYER_ENGINE,
				playerEngine);
	}

	@Override
	public boolean isSimilarArtistMode() {
		return this.preferenceHelper.getPreference(Preferences.SIMILAR_MODE,
				Boolean.class, false);
	}

	@Override
	public void setSimilarArtistMode(final boolean similarArtistMode) {
		this.preferenceHelper.setPreference(Preferences.SIMILAR_MODE,
				similarArtistMode);
	}

	@Override
	public boolean isEqualizerEnabled() {
		return this.preferenceHelper.getPreference(
				Preferences.EQUALIZER_ENABLED, Boolean.class, false);
	}

	@Override
	public void setEqualizerEnabled(final boolean enabled) {
		this.preferenceHelper.setPreference(Preferences.EQUALIZER_ENABLED,
				enabled);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}

}
