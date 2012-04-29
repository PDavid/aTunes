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

package net.sourceforge.atunes.model;

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
	public void setCacheFilesBeforePlaying(boolean cacheFilesBeforePlaying) {
	}
	
	@Override
	public void setEqualizerSettings(float[] equalizerSettings) {
	}
	
	@Override
	public void setMuteEnabled(boolean muteEnabled) {
	}
	
	@Override
	public void setPlayAtStartup(boolean playAtStartup) {
	}
	
	@Override
	public void setPlayerEngine(String playerEngine) {
	}
	
	@Override
	public void setRepeat(boolean repeat) {
	}
	
	@Override
	public void setShuffle(boolean shuffle) {
	}
	
	@Override
	public void setUseFadeAway(boolean useFadeAway) {
	}
	
	@Override
	public void setUseNormalisation(boolean useNormalisation) {
	}
	
	@Override
	public void setUseShortPathNames(boolean useShortPathNames) {
	}
	
	@Override
	public void setVolume(int volume) {
	}
}
