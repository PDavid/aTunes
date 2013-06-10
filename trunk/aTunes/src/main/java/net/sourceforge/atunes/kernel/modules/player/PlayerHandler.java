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

import java.util.List;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.StopAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListEventListener;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.model.PlayerEngineCapability;
import net.sourceforge.atunes.model.SubmissionState;
import net.sourceforge.atunes.utils.Logger;

/**
 * This class is responsible for handling the player engine.
 */
public final class PlayerHandler extends AbstractHandler implements
		IPlayerHandler, IPlayListEventListener {

	/**
	 * The player engine
	 */
	private IPlayerEngine playerEngine = new VoidPlayerEngine();

	/**
	 * The current playback state
	 */
	private PlaybackState playbackState = PlaybackState.STOPPED;

	/**
	 * Controller
	 */
	private PlayerControlsController playerControlsController;

	private Volume volumeController;

	private IStateUI stateUI;

	private IStatePlayer statePlayer;

	private IPlayListHandler playListHandler;

	private boolean stopAfterCurrentTrack;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * @param volumeController
	 */
	public void setVolumeController(final Volume volumeController) {
		this.volumeController = volumeController;
	}

	/**
	 * @param playerControlsController
	 */
	public void setPlayerControlsController(
			final PlayerControlsController playerControlsController) {
		this.playerControlsController = playerControlsController;
	}

	@Override
	public void applicationStateChanged() {
		// Show advanced controls
		this.playerControlsController.getComponentControlled()
				.showAdvancedPlayerControls(
						this.stateUI.isShowAdvancedPlayerControls());
	}

	@Override
	public boolean isEnginePlaying() {
		return this.playerEngine.isEnginePlaying();
	}

	@Override
	public boolean isEnginePaused() {
		return this.playerEngine.isEnginePaused();
	}

	@Override
	public void setVolume(final int perCent) {
		this.playerEngine.setVolume(perCent);
		this.playerControlsController.setVolume(perCent);
	}

	@Override
	public void applyMuteState(final boolean state) {
		this.playerEngine.applyMuteState(state);
	}

	@Override
	public void applyNormalization() {
		this.playerEngine.applyNormalization();
	}

	@Override
	public boolean supportsCapability(final PlayerEngineCapability capability) {
		return this.playerEngine.supportsCapability(capability);
	}

	@Override
	public void applyEqualization(final boolean enabled, final float[] values) {
		this.playerEngine.applyEqualization(enabled, values);
	}

	/**
	 * This method must be implemented by player engines. Transform values
	 * retrieved from equalizer dialog to values for player engine
	 * 
	 * @param values
	 * @return
	 */
	float[] transformEqualizerValues(final float[] values) {
		return this.playerEngine.transformEqualizerValues(values);
	}

	@Override
	public final void resumeOrPauseCurrentAudioObject() {
		this.playerEngine.playCurrentAudioObject();
	}

	@Override
	public void playAudioObjectInPlayListPositionOfVisiblePlayList(
			final int position) {
		// Only allow this if player engine is valid
		if (!(this.playerEngine instanceof VoidPlayerEngine)) {
			this.playListHandler.getVisiblePlayList()
					.setCurrentAudioObjectIndex(position);
			this.playerEngine
					.playAudioObjectInPlayListPositionOfVisiblePlayList(position);
		} else {
			manageNoPlayerEngine(getOsManager(), getFrame());
		}
	}

	@Override
	public void startPlayingAudioObjectInActivePlayList() {
		// Only allow this if player engine is valid
		if (!(this.playerEngine instanceof VoidPlayerEngine)) {
			this.playerEngine
					.playAudioObjectInPlayListPositionOfActivePlayList(this.playListHandler
							.getActivePlayList().getCurrentAudioObjectIndex());
		} else {
			manageNoPlayerEngine(getOsManager(), getFrame());
		}
	}

	@Override
	public final void stopCurrentAudioObject(final boolean userStopped) {
		if (isEnginePlaying() || isEnginePaused()) {
			this.playerEngine.stopCurrentAudioObject(userStopped);
		}
	}

	@Override
	public final void playPreviousAudioObject() {
		// Only allow this if player engine is valid
		if (!(this.playerEngine instanceof VoidPlayerEngine)) {
			this.playerEngine.playPreviousAudioObject();
		} else {
			manageNoPlayerEngine(getOsManager(), getFrame());
		}
	}

	@Override
	public final void playNextAudioObject() {
		// Only allow this if player engine is valid
		if (!(this.playerEngine instanceof VoidPlayerEngine)) {
			this.playerEngine.playNextAudioObject(false);
		} else {
			manageNoPlayerEngine(getOsManager(), getFrame());
		}
	}

	@Override
	public final void seekCurrentAudioObject(final int perCent) {
		this.playerEngine.seekCurrentAudioObject(perCent);
	}

	@Override
	public final void volumeDown() {
		this.playerEngine.volumeDown();
	}

	@Override
	public final void volumeUp() {
		this.playerEngine.volumeUp();
	}

	@Override
	public void applicationFinish() {
		// Stop must be called explicitly to avoid playback after user closed
		// app
		stopCurrentAudioObject(true);
		this.playerEngine.finishPlayer();
	}

	@Override
	public IAudioObject getAudioObject() {
		return this.playerEngine.getAudioObject();
	}

	@Override
	public void applicationStarted() {
		initialize();
	}

	@Override
	public void deferredInitialization() {
		if (this.playerEngine instanceof VoidPlayerEngine) {
			manageNoPlayerEngine(getOsManager(), getFrame());
		}
	}

	/**
	 * Called when no player engine is available
	 * 
	 * @param osManager
	 * @param frame
	 */
	private void manageNoPlayerEngine(final IOSManager osManager,
			final IFrame frame) {
		// Delegate to specific OS code
		osManager.manageNoPlayerEngine(frame);
	}

	/**
	 * Initializes all related to player engine
	 */
	private void initialize() {
		IPlayerEngine selectedPlayerEngine = getBean(PlayerEngineSelector.class)
				.selectPlayerEngine();
		if (selectedPlayerEngine != null) {
			this.playerEngine = selectedPlayerEngine;
			Logger.info("Selected player engine: ", this.playerEngine);
		}

		// Set volume on visual components
		this.volumeController.setVolume(this.statePlayer.getVolume(), false);

		// Mute
		applyMuteState(this.statePlayer.isMuteEnabled());

		if (this.statePlayer.isPlayAtStartup()) {
			resumeOrPauseCurrentAudioObject();
		}

		// Show advanced controls
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				PlayerHandler.this.playerControlsController
						.getComponentControlled().showAdvancedPlayerControls(
								PlayerHandler.this.stateUI
										.isShowAdvancedPlayerControls());
			}
		});

		// Init engine
		this.playerEngine.initializePlayerEngine();

		// Add a shutdown hook to perform some actions before killing the JVM
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				Logger.debug("Final check for Zombie player engines");
				PlayerHandler.this.playerEngine.destroyPlayer();
				Logger.debug("Closing player ...");
			}

		}));
	}

	@Override
	public void playbackStateChanged(final PlaybackState newState,
			final IAudioObject currentAudioObject) {
		setPlaying(newState == PlaybackState.RESUMING
				|| newState == PlaybackState.PLAYING);
		if (this.playbackState == null || !this.playbackState.equals(newState)) {
			this.playbackState = newState;
			Logger.debug("Playback state changed to:", newState);
			submitAudioObjectIfNecessary(newState, currentAudioObject);
			stopPlayerEngineIfNecessary(newState);
		}
	}

	/**
	 * @param newState
	 * @param currentAudioObject
	 */
	private void submitAudioObjectIfNecessary(final PlaybackState newState,
			final IAudioObject currentAudioObject) {
		if (isSubmissionNeeded(newState, currentAudioObject)) {
			getBean(IWebServicesHandler.class).submit(currentAudioObject,
					getCurrentAudioObjectPlayedTime() / 1000);
			getBean(IStatisticsHandler.class).updateAudioObjectStatistics(
					currentAudioObject);
			this.playerEngine.setSubmissionState(SubmissionState.SUBMITTED);
		}
	}

	private boolean isSubmissionNeeded(final PlaybackState newState,
			final IAudioObject currentAudioObject) {
		if (isStateForSubmission(newState)
				&& this.playerEngine.getSubmissionState() == SubmissionState.PENDING
				&& currentAudioObject instanceof ILocalAudioObject) {
			return true;
		}
		return false;
	}

	private boolean isStateForSubmission(final PlaybackState newState) {
		return newState == PlaybackState.PLAY_FINISHED
				|| newState == PlaybackState.PLAY_INTERRUPTED
				|| newState == PlaybackState.STOPPED;
	}

	/**
	 * @param newState
	 */
	private void stopPlayerEngineIfNecessary(final PlaybackState newState) {
		if (newState == PlaybackState.STOPPED) {
			this.playerEngine.setCurrentAudioObjectPlayedTime(0, false);
			this.playerEngine.interruptPlayAudioObjectThread();
		}
	}

	@Override
	public PlaybackState getPlaybackState() {
		return this.playbackState;
	}

	@Override
	public long getCurrentAudioObjectPlayedTime() {
		return this.playerEngine.getCurrentAudioObjectPlayedTime();
	}

	@Override
	public long getCurrentAudioObjectLength() {
		return this.playerEngine.getCurrentAudioObjectLength();
	}

	private void setPlaying(final boolean playing) {
		this.playerControlsController.setPlaying(playing);
	}

	@Override
	public void setAudioObjectLength(final long currentLength) {
		this.playerControlsController.setAudioObjectLength(currentLength);
	}

	@Override
	public void setCurrentAudioObjectTimePlayed(final long actualPlayedTime,
			final long currentAudioObjectLength, final boolean fading) {
		this.playerControlsController.setCurrentAudioObjectTimePlayed(
				actualPlayedTime, currentAudioObjectLength, fading);
	}

	@Override
	public void selectedAudioObjectChanged(final IAudioObject audioObject) {
	}

	@Override
	public void initializeAndCheck() {
		initialize();
		if (this.playerEngine instanceof VoidPlayerEngine) {
			manageNoPlayerEngine(getOsManager(), getFrame());
		}
	}

	@Override
	public void requestToPlayNextAudioObject() {
		if (!this.stopAfterCurrentTrack) {
			this.playerEngine.playNextAudioObject(true);
		} else {
			Logger.debug("Stopping playback as user selected to stop after current audio object");
			this.playerEngine.stopCurrentAudioObject(true);
			this.stopAfterCurrentTrack = false;
			getBean(StopAfterCurrentAudioObjectAction.class)
					.setStopAfterCurrentAudioObject(false);
		}
	}

	@Override
	public void setStopAfterCurrentTrack(final boolean stopAfterCurrentTrack) {
		this.stopAfterCurrentTrack = stopAfterCurrentTrack;
	}

	@Override
	public void audioObjectsAdded(
			final List<IPlayListAudioObject> playListAudioObjects) {
	}

	@Override
	public void audioObjectsRemoved(
			final List<IPlayListAudioObject> audioObjectList) {
	}

	@Override
	public void playListCleared() {
	}
}
