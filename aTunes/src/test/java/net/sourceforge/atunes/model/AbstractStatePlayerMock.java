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

import java.util.Map;

public class AbstractStatePlayerMock implements IStatePlayer {

	@Override
	public float[] getEqualizerSettings() {
		return null;
	}

	@Override
	public String getPlayerEngine() {
		return null;
	}

	@Override
	public int getVolume() {
		return 0;
	}

	@Override
	public boolean isCacheFilesBeforePlaying() {
		return false;
	}

	@Override
	public boolean isMuteEnabled() {
		return false;
	}

	@Override
	public boolean isPlayAtStartup() {
		return false;
	}

	@Override
	public boolean isRepeat() {
		return false;
	}

	@Override
	public boolean isShuffle() {
		return false;
	}

	@Override
	public boolean isUseFadeAway() {
		return false;
	}

	@Override
	public boolean isUseNormalisation() {
		return false;
	}

	@Override
	public boolean isUseShortPathNames() {
		return false;
	}

	@Override
	public void setCacheFilesBeforePlaying(final boolean cacheFilesBeforePlaying) {
	}

	@Override
	public void setEqualizerSettings(final float[] equalizerSettings) {
	}

	@Override
	public void setMuteEnabled(final boolean muteEnabled) {
	}

	@Override
	public void setPlayAtStartup(final boolean playAtStartup) {
	}

	@Override
	public void setPlayerEngine(final String playerEngine) {
	}

	@Override
	public void setRepeat(final boolean repeat) {
	}

	@Override
	public void setShuffle(final boolean shuffle) {
	}

	@Override
	public void setUseFadeAway(final boolean useFadeAway) {
	}

	@Override
	public void setUseNormalisation(final boolean useNormalisation) {
	}

	@Override
	public void setUseShortPathNames(final boolean useShortPathNames) {
	}

	@Override
	public void setVolume(final int volume) {
	}

	@Override
	public boolean isSimilarArtistMode() {
		return false;
	}

	@Override
	public void setSimilarArtistMode(final boolean similarArtistMode) {
	}

	@Override
	public boolean isEqualizerEnabled() {
		return false;
	}

	@Override
	public void setEqualizerEnabled(final boolean enabled) {
	}

	@Override
	public Map<String, String> describeState() {
		return null;
	}
}
