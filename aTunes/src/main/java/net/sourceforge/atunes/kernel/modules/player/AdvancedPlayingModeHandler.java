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

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAdvancedPlayingModeHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.PlaybackState;

/**
 * 
 * This class adds a new playing mode : add a random track of similar artist for current playing track.
 * Similar artist recognition uses Last FM services.	
 * 
 * @author Laurent Cathala
 *
 */
public class AdvancedPlayingModeHandler extends AbstractHandler implements IAdvancedPlayingModeHandler {

	private IStatePlayer statePlayer;

	private SimilarArtistMode similarArtistMode;
	
	/**
	 * @param similarArtistMode
	 */
	public void setSimilarArtistMode(SimilarArtistMode similarArtistMode) {
		this.similarArtistMode = similarArtistMode;
	}

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	@Override
	public void playbackStateChanged(PlaybackState newState, IAudioObject currentAudioObject) {
		if (statePlayer.isSimilarArtistMode()) {
			similarArtistMode.playbackStateChanged(newState, currentAudioObject);
		}
	}

	@Override
	public void enableSimilarArtistMode(boolean enabled) {
		similarArtistMode.setEnabled(enabled);
	}
}