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

package net.sourceforge.atunes.kernel.modules.player;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.PlayerEngineCapability;

/**
 * Player engine used when no player engine selected
 * 
 * @author alex
 * 
 */
class VoidPlayerEngine extends AbstractPlayerEngine {

	@Override
	public boolean isEnginePlaying() {
		return false;
	}

	@Override
	public boolean isEngineAvailable() {
		return false;
	}

	@Override
	public void startPlayback(final IAudioObject audioObjectToPlay,
			final IAudioObject audioObject) {
	}

	@Override
	public void pausePlayback() {
	}

	@Override
	public void resumePlayback() {
	}

	@Override
	public void stopPlayback(final boolean userStopped,
			final boolean useFadeAway) {
	}

	@Override
	public void seekTo(final int perCent) {
	}

	@Override
	public void setVolume(final int perCent) {
	}

	@Override
	public void applyMuteState(final boolean state) {
	}

	@Override
	public void applyNormalization() {
	}

	@Override
	public boolean supportsCapability(final PlayerEngineCapability capability) {
		return false;
	}

	@Override
	public void applyEqualization(final boolean enabled, final float[] values) {
	}

	@Override
	public float[] transformEqualizerValues(final float[] values) {
		return new float[0];
	}

	@Override
	public void finishPlayer() {
	}

	@Override
	public String getEngineName() {
		return null;
	}

	@Override
	public void destroyPlayer() {
	}

	@Override
	public void initializePlayerEngine() {
	}
}
