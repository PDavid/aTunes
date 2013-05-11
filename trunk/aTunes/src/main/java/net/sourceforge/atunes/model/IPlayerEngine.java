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
 * Classes implementing this interface are responsible of work with native
 * player engine
 * 
 * @author alex
 * 
 */
public interface IPlayerEngine {

	/**
	 * Checks if engine is currently playing (<code>true</code>) or not (
	 * <code>false</code>)
	 * 
	 * @return <code>true</code> if engine is currently playing
	 */
	boolean isEnginePlaying();

	/**
	 * This method must be implemented by player engines. Applies volume value
	 * in player engine
	 * 
	 * @param perCent
	 *            0-100
	 */
	void setVolume(int perCent);

	/**
	 * This method must be implemented by player engines. Apply mute state in
	 * player engine
	 * 
	 * @param state
	 *            : enabled (<code>true</code>) or disabled (<code>false</code>)
	 * 
	 */
	void applyMuteState(boolean state);

	/**
	 * This method must be implemented by player engines. Method to apply
	 * normalization in player engine
	 * 
	 * @param values
	 */
	void applyNormalization();

	/**
	 * This methods checks if the specified player capability is supported by
	 * this player engine
	 * 
	 * @param capability
	 *            The capability that should be checked
	 * @return If the specified player capability is supported by this player
	 *         engine
	 */
	boolean supportsCapability(PlayerEngineCapability capability);

	/**
	 * Starts playing or pauses current audio object from play list
	 */
	void playCurrentAudioObject();

	/**
	 * Starts playing audio object in given position of visible play list
	 * 
	 * @param position
	 */
	void playAudioObjectInPlayListPositionOfVisiblePlayList(int position);

	/**
	 * Starts playing audio object in active play list
	 * 
	 * @param position
	 */
	void playAudioObjectInPlayListPositionOfActivePlayList(int position);

	/**
	 * Stops playing current audio object
	 * 
	 * @param userStopped
	 *            <code>true</code> if user has stopped playback
	 * 
	 */
	void stopCurrentAudioObject(boolean userStopped);

	/**
	 * This method must be called by engine when audio object finishes its
	 * playback
	 */
	void currentAudioObjectFinished();

	/**
	 * This method must be called by engine when audio object finishes its
	 * playback with error
	 * 
	 * @param exception
	 */
	void currentAudioObjectFinishedWithError(Exception exception);

	/**
	 * Seek function: play current audio object from percentage defined by
	 * parameter
	 * 
	 * @param percentage
	 */
	void seekCurrentAudioObject(int percentage);

	/**
	 * Lower volume
	 */
	void volumeDown();

	/**
	 * Raise volume
	 */
	void volumeUp();

	/**
	 * Called when a exception is thrown related with player engine
	 * 
	 * @param e
	 *            The exception thrown
	 */
	void handlePlayerEngineError(final Exception e);

	/**
	 * Returns current audio object being played
	 * 
	 * @return
	 */
	IAudioObject getAudioObject();

	/**
	 * Time played of current audio object
	 * 
	 * @return
	 */
	long getCurrentAudioObjectPlayedTime();

	/**
	 * Total length of current audio object
	 * 
	 * @return
	 */
	long getCurrentAudioObjectLength();

	/**
	 * Starts playing next audio object from play list
	 * 
	 * @param audioObjectFinished
	 *            <code>true</code> if this method is called because current
	 *            audio object has finished, <code>false</code> if this method
	 *            is called because user has pressed the "NEXT" button
	 * 
	 */
	void playNextAudioObject(boolean audioObjectFinished);

	/**
	 * This method must be implemented by player engines. Method to apply
	 * equalizer values in player engine
	 * 
	 * @param enabled
	 * @param values
	 */
	void applyEqualization(boolean enabled, float[] values);

	/**
	 * This method must be implemented by player engines. Transform values
	 * retrieved from equalizer dialog to values for player engine
	 * 
	 * @param values
	 * @return
	 */
	float[] transformEqualizerValues(float[] values);

	/**
	 * Starts playing previous audio object from play list
	 */
	void playPreviousAudioObject();

	/**
	 * This method must be implemented by player engines It's called when
	 * application finishes
	 */
	void finishPlayer();

	/**
	 * Actions to initialize engine. This method is called just after selecting
	 * an available player engine.
	 */
	void initializePlayerEngine();

	/**
	 * Destroys player resources
	 */
	void destroyPlayer();

	/**
	 * @param submissionState
	 *            the submissionState to set
	 */
	void setSubmissionState(SubmissionState submissionState);

	/**
	 * @return the submissionState
	 */
	SubmissionState getSubmissionState();

	/**
	 * Sets the time played for the current audio object as playback advances
	 * 
	 * @param playedTime
	 *            played time in milliseconds (ms)
	 * @param fading
	 */
	void setCurrentAudioObjectPlayedTime(long playedTime, boolean fading);

	/**
	 * Interrupts playing thread
	 */
	void interruptPlayAudioObjectThread();

	/**
	 * This method must be implemented by player engines. This method must check
	 * system to determine if player engine is available (check for libraries or
	 * commands)
	 * 
	 * @return <code>true</code> if engine is available in the system and can be
	 *         used to play, <code>false</code> otherwise
	 */
	boolean isEngineAvailable();

	/**
	 * play this audio object
	 * 
	 * @param audioObjectToPlay
	 *            audio object to play. May be cashed to temp dirs or the same
	 *            as audioObject.
	 * @param audioObject
	 *            original audio object to update statistics
	 */
	void startPlayback(IAudioObject audioObjectToPlay, IAudioObject audioObject);

	/**
	 * This method must be implemented by player engines. This method pauses
	 * playback of current audio object without stopping it. Resuming after this
	 * called should continue playback from the position when paused
	 */
	void pausePlayback();

	/**
	 * This method must be implemented by player engines. This method resumes
	 * playback of current audio object previously paused. Call this method
	 * should continue playback from the position when paused
	 */
	void resumePlayback();

	/**
	 * This method must be implemented by player engines. Stop playing current
	 * song
	 * 
	 * @param userStopped
	 *            {@code true} if stopped by user input, {@code false}
	 *            otherwise.
	 * @param useFadeAway
	 *            if {@code true} - fade away then stop. Stop immediately
	 *            otherwise.
	 */
	void stopPlayback(boolean userStopped, boolean useFadeAway);

	/**
	 * This method must be implemented by player engines. Applies a seek
	 * operation in player engine.
	 * 
	 * @param percentage
	 */
	void seekTo(int percentage);

	/**
	 * Returns the name of this engine
	 * 
	 * @return the name of this engine
	 */
	String getEngineName();

	/**
	 * Checks if play back is paused.
	 * 
	 * @return true, if is paused
	 */
	boolean isEnginePaused();
}