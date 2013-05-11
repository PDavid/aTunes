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
 * Responsible of playing music
 * @author alex
 *
 */
/**
 * @author alex
 * 
 */
public interface IPlayerHandler extends IHandler {

	/**
	 * Checks if engine is currently playing (<code>true</code>) or not (
	 * <code>false</code>)
	 * 
	 * @return <code>true</code> if engine is currently playing
	 */
	public boolean isEnginePlaying();

	/**
	 * Checks if engine is currently paused (<code>true</code>) or not (
	 * <code>false</code>)
	 * 
	 * @return <code>true</code> if engine is currently paused
	 */
	public boolean isEnginePaused();

	/**
	 * This method must be implemented by player engines. Applies volume value
	 * in player engine
	 * 
	 * @param perCent
	 *            0-100
	 */
	public void setVolume(int perCent);

	/**
	 * This method must be implemented by player engines. Apply mute state in
	 * player engine
	 * 
	 * @param state
	 *            : enabled (<code>true</code>) or disabled (<code>false</code>)
	 * 
	 */
	public void applyMuteState(boolean state);

	/**
	 * This method must be implemented by player engines. Method to apply
	 * normalization in player engine
	 * 
	 * @param values
	 */
	public void applyNormalization();

	/**
	 * This methods checks if the specified player capability is supported by
	 * this player engine
	 * 
	 * @param capability
	 *            The capability that should be checked
	 * @return If the specified player capability is supported by this player
	 *         engine
	 */
	public boolean supportsCapability(PlayerEngineCapability capability);

	/**
	 * Starts playing or pauses current audio object from play list
	 */
	public void resumeOrPauseCurrentAudioObject();

	/**
	 * Starts playing audio object in given position of visible play list
	 * 
	 * @param position
	 */
	public void playAudioObjectInPlayListPositionOfVisiblePlayList(int position);

	/**
	 * Stops playing current audio object
	 * 
	 * @param userStopped
	 *            <code>true</code> if user has stopped playback
	 * 
	 */
	public void stopCurrentAudioObject(boolean userStopped);

	/**
	 * Starts playing previous audio object from play list
	 */
	public void playPreviousAudioObject();

	/**
	 * Starts playing next audio object from play list
	 */
	public void playNextAudioObject();

	/**
	 * Seek function: play current audio object from position defined by
	 * percentage
	 * 
	 * @param percentage
	 */
	public void seekCurrentAudioObject(int percentage);

	/**
	 * Lower volume
	 */
	public void volumeDown();

	/**
	 * Raise volume
	 */
	public void volumeUp();

	/**
	 * Returns current audio object
	 * 
	 * @return
	 */
	public IAudioObject getAudioObject();

	/**
	 * @return the playbackState
	 */
	public PlaybackState getPlaybackState();

	/**
	 * Returns time played for current audio object
	 * 
	 * @return
	 */
	public long getCurrentAudioObjectPlayedTime();

	/**
	 * Returns length for current audio object
	 * 
	 * @return
	 */
	public long getCurrentAudioObjectLength();

	/**
	 * Initializes after user have selected player engine
	 */
	public void initializeAndCheck();

	/**
	 * @param currentLength
	 */
	public void setAudioObjectLength(long currentLength);

	/**
	 * @param actualPlayedTime
	 * @param currentAudioObjectLength
	 * @param fading
	 */
	public void setCurrentAudioObjectTimePlayed(long actualPlayedTime,
			long currentAudioObjectLength, boolean fading);

	/**
	 * This method must be implemented by player engines. Method to apply
	 * equalizer values in player engine
	 * 
	 * @param enabled
	 * @param eqSettings
	 */
	public void applyEqualization(boolean enabled, float[] eqSettings);

	/**
	 * Starts play back of current audio object in active play list
	 */
	public void startPlayingAudioObjectInActivePlayList();

	/**
	 * Asks handler to play next audio object. Handler will start playing if
	 * necessary
	 */
	public void requestToPlayNextAudioObject();

	/**
	 * Sets property to stop after current track
	 * 
	 * @param stopAfterCurrentTrack
	 */
	public void setStopAfterCurrentTrack(boolean stopAfterCurrentTrack);

}