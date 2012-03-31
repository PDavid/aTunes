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

package net.sourceforge.atunes.kernel.modules.player;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.PlayerEngineCapability;

/**
 * Player engine used when no player engine selected
 * @author alex
 *
 */
class VoidPlayerEngine extends AbstractPlayerEngine {

	@Override
	public boolean isEnginePlaying() {
		return false;
	}

	@Override
	protected boolean isEngineAvailable() {
		return false;
	}

	@Override
	protected void startPlayback(IAudioObject audioObjectToPlay, IAudioObject audioObject) {
	}

	@Override
	protected void pausePlayback() {
	}

	@Override
	protected void resumePlayback() {
	}

	@Override
	protected void stopPlayback(boolean userStopped, boolean useFadeAway) {
	}

	@Override
	protected void seekTo(long milliseconds) {
	}

	@Override
	public void setVolume(int perCent) {
	}

	@Override
	public void applyMuteState(boolean state) {
	}

	@Override
	public void applyNormalization() {
	}

	@Override
	public boolean supportsCapability(PlayerEngineCapability capability) {
		return false;
	}

	@Override
	protected void applyEqualization(float[] values) {
	}

	@Override
	protected float[] transformEqualizerValues(float[] values) {
		return new float[0];
	}

	@Override
	protected void finishPlayer() {
	}

	@Override
	protected String getEngineName() {
		return null;
	}

	@Override
	protected void killPlayer() {
	}
}
