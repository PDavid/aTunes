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

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.kernel.PlaybackStateListeners;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IEqualizer;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITemporalDiskStorage;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class has common logic for all player engines.
 */
public abstract class AbstractPlayerEngine {

	enum SubmissionState {
        NOT_SUBMITTED, PENDING, SUBMITTED;
    }

    /**
     * Setting this attribute to <code>true</code> avoid calling playback state listeners
     */
    private boolean callToPlaybackStateListenersDisabled = false;

    /**
     * Paused property: <code>true</code> if audio object playback is paused
     */
    private boolean paused;

    /**
     * Audio object
     */
    private IAudioObject audioObject;

    /**
     * Length of the current audio object
     */
    private long currentAudioObjectLength;

    /**
     * This attribute is used when caching files option is enabled. When caching
     * a new file, file pointed by this object is removed from temp folder
     */
    private File lastFileCached;

    /**
     * The submission state of the current audio object
     */
    private SubmissionState submissionState = SubmissionState.NOT_SUBMITTED;

    /**
     * The time the current audio object has already played
     */
    private long currentAudioObjectPlayedTime;

    /**
     * Equalizer used
     */
    protected IEqualizer equalizer;
    
    protected IState state;
    
    protected IFrame frame;
    
    protected IOSManager osManager;
    
    protected IPlayListHandler playListHandler;
    
    protected INavigationHandler navigationHandler;
    
    protected ITemporalDiskStorage temporalDiskStorage;
    
    protected IPlayerHandler playerHandler;
    
    protected PlayListEventListeners playListEventListeners;
    
    /**
     * A thread invoking play in engine
     */
    Thread playAudioObjectThread;

    /**
     * Checks if engine is currently playing (<code>true</code>) or not (
     * <code>false</code>)
     * 
     * @return <code>true</code> if engine is currently playing
     */
    public abstract boolean isEnginePlaying();

    /**
     * This method must be implemented by player engines. This method must check
     * system to determine if player engine is available (check for libraries or
     * commands)
     * 
     * @return <code>true</code> if engine is available in the system and can be
     *         used to play, <code>false</code> otherwise
     */
    protected abstract boolean isEngineAvailable();

    /**
     * play this audio object
     * 
     * @param audioObjectToPlay
     *            audio object to play. May be cashed to temp dirs or the same
     *            as audioObject.
     * @param audioObject
     *            original audio object to update statistics
     */
    protected abstract void startPlayback(IAudioObject audioObjectToPlay, IAudioObject audioObject);

    /**
     * This method must be implemented by player engines. This method pauses
     * playback of current audio object without stopping it. Resuming after this
     * called should continue playback from the position when paused
     */
    protected abstract void pausePlayback();

    /**
     * This method must be implemented by player engines. This method resumes
     * playback of current audio object previously paused. Call this method
     * should continue playback from the position when paused
     */
    protected abstract void resumePlayback();

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
    protected abstract void stopPlayback(boolean userStopped, boolean useFadeAway);

    /**
     * This method must be implemented by player engines. Applies a seek
     * operation in player engine
     * 
     * @param milliseconds
     *           
     */
    protected abstract void seekTo(long milliseconds);

    /**
     * This method must be implemented by player engines. Applies volume value
     * in player engine
     * 
     * @param perCent
     *            0-100
     */
    public abstract void setVolume(int perCent);

    /**
     * This method must be implemented by player engines. Apply mute state in
     * player engine
     * 
     * @param state
     *            : enabled (<code>true</code>) or disabled (<code>false</code>)
     * 
     */
    public abstract void applyMuteState(boolean state);

    /**
     * This method must be implemented by player engines. Method to apply
     * normalization in player engine
     * 
     * @param values
     */
    public abstract void applyNormalization();

    /**
     * This methods checks if the specified player capability is supported by
     * this player engine
     * 
     * @param capability
     *            The capability that should be checked
     * @return If the specified player capability is supported by this player
     *         engine
     */
    public abstract boolean supportsCapability(PlayerEngineCapability capability);

    /**
     * This method must be implemented by player engines. Method to apply
     * equalizer values in player engine
     * 
     * @param values
     */
    protected abstract void applyEqualization(float[] values);

    /**
     * This method must be implemented by player engines. Transform values
     * retrieved from equalizer dialog to values for player engine
     * 
     * @param values
     * @return
     */
    protected abstract float[] transformEqualizerValues(float[] values);

    /**
     * This method must be implemented by player engines It's called when
     * application finishes
     */
    protected abstract void finishPlayer();

    /**
     * Returns the name of this engine
     * 
     * @return the name of this engine
     */
    protected abstract String getEngineName();

    ///////////////////////////////////////// END OF METHODS TO IMPLEMENT BY ENGINES ////////////////////////////////////////

    /**
     * Calls all playback listeners with new state and current audio object When
     * audio object changes this method must be called after change to call
     * listeners with new audio object
     * 
     * @param newState
     * @param currentAudioObject
     */
    final void callPlaybackStateListeners(PlaybackState newState) {
    	if (!isCallToPlaybackStateListenersDisabled()) {
    		Context.getBean(PlaybackStateListeners.class).playbackStateChanged(newState, audioObject);
    	}
    }

    /**
     * Starts playing current audio object from play list
     * 
     * @param buttonPressed
     *            TODO: Add more javadoc
     */
    public final void playCurrentAudioObject(boolean buttonPressed) {
        if (isEnginePlaying() && buttonPressed) { // Pause
            try {
                paused = true;
                pausePlayback();
                Logger.info("Pause");
                callPlaybackStateListeners(PlaybackState.PAUSED);
            } catch (Exception e) {
                handlePlayerEngineError(e);
                stopCurrentAudioObject(false);
            }
        } else {
            IAudioObject nextAudioObject = null;
            try {
                if (paused && buttonPressed) { // Resume
                    if (!playListHandler.getCurrentPlayList(false).isEmpty()) {
                        paused = false;
                        resumePlayback();
                        callPlaybackStateListeners(PlaybackState.RESUMING);
                        Logger.info("Resumed paused song");
                    }
                } else {
                    nextAudioObject = playListHandler.getCurrentAudioObjectFromVisiblePlayList();
                    if (nextAudioObject != null) {
                        if (isEnginePlaying() || paused) {
                            stopCurrentAudioObject(false);
                        }

                        // We need to update current object and active playlist first
                        playListHandler.setVisiblePlayListActive();
                        playListEventListeners.selectedAudioObjectHasChanged(nextAudioObject);

                        playAudioObject(nextAudioObject);
                    }
                }
            } catch (Exception e) {
                handlePlayerEngineError(e);
                stopCurrentAudioObject(false);
            }
        }

    }

    /**
     * Stops playing current audio object
     * 
     * @param userStopped
     *            <code>true</code> if user has stopped playback
     * 
     */
    public final void stopCurrentAudioObject(boolean userStopped) {
        try {
            boolean activateFadeAway = userStopped && state.isUseFadeAway() && !paused;
            if (paused) {
                paused = false;
            }

            stopPlayback(userStopped, activateFadeAway);
            callPlaybackStateListeners(PlaybackState.STOPPED);
            Logger.info("Stop");
        } catch (Exception e) {
            handlePlayerEngineError(e);
        }

    }

    /**
     * Starts playing previous audio object from play list
     */
    final void playPreviousAudioObject() {
        // Call listeners to notify playback was interrupted
        callPlaybackStateListeners(PlaybackState.PLAY_INTERRUPTED);

        switchPlaybackTo(playListHandler.getPreviousAudioObject(), false, false);
    }

    /**
     * Starts playing next audio object from play list
     * 
     * @param audioObjectFinished
     *            <code>true</code> if this method is called because current
     *            audio object has finished, <code>false</code> if this method
     *            is called because user has pressed the "NEXT" button
     * 
     */
    final void playNextAudioObject(boolean audioObjectFinished) {
        if (!audioObjectFinished) {
            // Call listeners to notify playback was interrupted
            callPlaybackStateListeners(PlaybackState.PLAY_INTERRUPTED);
        }
        switchPlaybackTo(playListHandler.getNextAudioObject(), true, audioObjectFinished);
    }

    /**
     * This method must be called by engine when audio object finishes its
     * playback
     * 
     * @param ok
     *            <code>true</code> if playback finishes ok, <code>false</code>
     *            otherwise
     * @param messages
     *            Messages when playback finishes with error
     */
    public final void currentAudioObjectFinished(boolean ok, final String... errorMessages) {
        // Call listeners to notify playback finished
        callPlaybackStateListeners(PlaybackState.PLAY_FINISHED);

        if (!ok) {
        	StringBuilder sb = new StringBuilder();
        	for (String errorMessage : errorMessages) {
        		sb.append(errorMessage).append(" ");
        	}
            Logger.info(StringUtils.getString("Playback finished with errors: ", sb.toString()));

            boolean ignore = showPlaybackError(errorMessages);
            applyUserSelection(ignore);

        } else {
            Logger.info("Playback finished");

            // Move to the next audio object
            playNextAudioObject(true);
        }
    }

    /**
     * Lets user decide if want to ignore playback error or cancel
     * 
     * @param errorMessages
     * @return
     */
    private boolean showPlaybackError(String... errorMessages) {
        ShowPlaybackErrorRunnable r = new ShowPlaybackErrorRunnable(errorMessages, frame);
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InterruptedException e) {
                Logger.error(e);
            } catch (InvocationTargetException e) {
                Logger.error(e);
            }
        }
        return r.isIgnore();
    }

    /**
     * if user wants to ignore play back error then play back continues, if not
     * it's stopped
     * 
     * @param ignorePlaybackError
     */
    private void applyUserSelection(boolean ignorePlaybackError) {
        ApplyUserSelectionRunnable r = new ApplyUserSelectionRunnable(this, ignorePlaybackError);
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }
    }

    /**
     * Seek function: play current audio object from milliseconds defined by parameter
     * 
     * @param milliseconds
     *            
     * 
     */
    public final void seekCurrentAudioObject(long milliseconds) {
        // If paused first resume and then seek
        if (paused) {
            paused = false;
            if (!playListHandler.getCurrentPlayList(false).isEmpty()) {
                callPlaybackStateListeners(PlaybackState.RESUMING);
                Logger.info("Resumed paused song");
            }
        }

        seekTo(milliseconds);
    }

    /**
     * Lower volume
     */
    public final void volumeDown() {
        Volume.setVolume(state.getVolume() - 5, state, playerHandler);
    }

    /**
     * Raise volume
     */
    public final void volumeUp() {
        Volume.setVolume(state.getVolume() + 5, state, playerHandler);
    }

    /**
     * Called when a exception is thrown related with player engine
     * 
     * @param e
     *            The exception thrown
     */
    public final void handlePlayerEngineError(final Exception e) {
        Logger.error(StringUtils.getString("Player Error: ", e));
        Logger.error(e);
        Context.getBean(IErrorDialog.class).showExceptionDialog(I18nUtils.getString("ERROR"), e);
    }

    /**
     * Returns the equalizer of this player engine
     * 
     * @return the equalizer of this player engine
     */
    public IEqualizer getEqualizer() {
        return equalizer;
    }

    /**
     * Checks if sound normalization is enabled (<code>true</code>) or not (
     * <code>false</code>)
     * 
     * @return <code>true</code> if sound normalization is enabled
     */
    protected final boolean isSoundNormalizationEnabled() {
        return state.isUseNormalisation();
    }

    /**
     * Kills player resources
     */
    protected abstract void killPlayer();

    /**
     * Checks if mute is enabled (<code>true</code>) or not (<code>false</code>)
     * 
     * @return <code>true</code> if mute is enabled
     */
    protected final boolean isMuteEnabled() {
        return state.isMuteEnabled();
    }

    /**
     * Sets the length of the current audio object
     * 
     * @param currentLength
     *            The length of the current audio object in milliseconds (ms)
     * 
     */
    protected final void setCurrentAudioObjectLength(long currentLength) {
        this.currentAudioObjectLength = currentLength;
        // Update sliders with max length
        playerHandler.setAudioObjectLength(currentLength);
        Context.getBean(IFullScreenHandler.class).setAudioObjectLength(currentLength);
    }

    /**
     * Sets the time played for the current audio object as playback advances
     * 
     * @param playedTime
     *            played time in milliseconds (ms)
     */
    protected final void setCurrentAudioObjectPlayedTime(long playedTime) {
        long actualPlayedTime = playedTime;
        this.currentAudioObjectPlayedTime = actualPlayedTime;
        playerHandler.setCurrentAudioObjectTimePlayed(actualPlayedTime, currentAudioObjectLength);
        Context.getBean(IFullScreenHandler.class).setCurrentAudioObjectPlayedTime(actualPlayedTime, currentAudioObjectLength);

        // Conditions to submit an object:
        // - Not submitted before
        // - Length of object is not 0
        // - Played time is greater than half the length of the object OR is greater than 4 minutes
        if (submissionState == SubmissionState.NOT_SUBMITTED && 
        	currentAudioObjectLength > 0 &&
            (actualPlayedTime > currentAudioObjectLength / 2 || actualPlayedTime >= 240000)) {
            submissionState = SubmissionState.PENDING;
        }
    }

    /**
     * Checks if playback is paused.
     * 
     * @return true, if is paused
     */
    protected final boolean isPaused() {
        return paused;
    }

    /**
     * Notifies the handler that the radio or podcast feed entry has started
     * playing (MPlayer bug workaround).
     */
    protected final void notifyRadioOrPodcastFeedEntryStarted() {
        Logger.debug("radio or podcast feed entry has started playing");
        // send volume command
        setVolume(state.getVolume());
        // if muted set mute again
        if (state.isMuteEnabled()) {
            applyMuteState(true);
        }
        Logger.debug("MPlayer bug (ignoring muting and volume settings after streamed file starts playing) workaround applied");
    }

    /**
     * Actions to initialize engine. This method is called just after selecting
     * an available player engine.
     * 
     * <b>NOTE: The overriding method MUST call
     * super.initializePlayerEngine()</b>
     */
    protected void initializePlayerEngine() {
    }

    /**
     * Starts playing an audio object.
     * 
     * @param audioObject
     */
    private void playAudioObject(final IAudioObject audioObject) {
        Logger.info(StringUtils.getString("Started play of file ", audioObject));

        if (state.isCacheFilesBeforePlaying()) {

        	PlayAudioObjectRunnable r = new PlayAudioObjectRunnable(this, audioObject, frame, temporalDiskStorage);
        	
        	// NOTE: This thread was initially a SwingWorker but as number of concurrent SwingWorkers is limited if context panel SwingWorker were working
        	// this one was blocked so use a Thread to avoid blocking no matters if a SwingWorker is active or not
        	playAudioObjectThread = new Thread(r);
        	playAudioObjectThread.start();
        } else {
        	playAudioObjectAfterCache(audioObject, audioObject);
        }
    }

    /**
     * Caches audio object
     * @param audioObject
     * @param temporalDiskStorage
     * @return
     */
    IAudioObject cacheAudioObject(IAudioObject audioObject, ITemporalDiskStorage temporalDiskStorage) {
    	IAudioObject audioObjectToPlay = null;
    	
        // If cacheFilesBeforePlaying is true and audio object is an audio file, copy it to temp folder
        // and start player process from this copied file
	    if (audioObject instanceof ILocalAudioObject && getState().isCacheFilesBeforePlaying()) {
	    	
	    	Logger.debug("Start caching file: ", audioObject.getUrl());
	    	
	        // Remove previous cached file
	        if (lastFileCached != null) {
	        	temporalDiskStorage.removeFile(lastFileCached);
	        }

	        File tempFile = temporalDiskStorage.addFile(((AudioFile) audioObject).getFile());
	        if (tempFile != null) {
	            audioObjectToPlay = new AudioFile(tempFile);
	            lastFileCached = tempFile;
	        } else {
	            lastFileCached = null;
	        }
	        
	        Logger.debug("End caching file: ", audioObject.getUrl());
	    } else {
	        audioObjectToPlay = audioObject;
	    }
	    
	    return audioObjectToPlay;
    }
    
    /**
     * Calls to play an audio object after being cached if necessary
     * @param audioObjectToPlay Cached audio object
     * @param audioObject real audio object
     */
    void playAudioObjectAfterCache(IAudioObject audioObjectToPlay, IAudioObject audioObject) {
		// This audio object has not been listened yet
		submissionState = SubmissionState.NOT_SUBMITTED;

		// If we are playing a podcast, mark entry as listened
		if (audioObject instanceof IPodcastFeedEntry) {
			((IPodcastFeedEntry) audioObject).setListened(true);
			// Update pod cast navigator
			navigationHandler.refreshView(PodcastNavigationView.class);
		}

		// Send Now Playing info to Last.fm
		if (audioObject instanceof AudioFile) {
			Context.getBean(IWebServicesHandler.class).submitNowPlayingInfo(audioObject);
		}

		AbstractPlayerEngine.this.audioObject = audioObject;

		// Add audio object to playback history
		playListHandler.addToPlaybackHistory(audioObject);

		startPlayback(audioObjectToPlay, audioObject);

		// Setting volume and balance
		if (state.isMuteEnabled()) {
			applyMuteState(true);
		} else {
			setVolume(state.getVolume());
		}

		// Call listeners
		callPlaybackStateListeners(PlaybackState.PLAYING);
    }
    
    /**
     * Starts playing audio object passed as parameter
     * 
     * @param audioObjectToSwitchTo
     *            The audio object to play
     * @param resetIfNoObject
     *            If <code>true</code> reset play list position if audio object
     *            is null
     * @param autoNext
     *            <code>true</code> if this method is called because of previous
     *            audio object has finished playing or <code>false</code> if
     *            it's called because of an action of the user (previous, next,
     *            ...)
     */
    private void switchPlaybackTo(IAudioObject audioObjectToSwitchTo, boolean resetIfNoObject, boolean autoNext) {
        if (audioObjectToSwitchTo != null) {
            try {
            	playListEventListeners.selectedAudioObjectHasChanged(audioObjectToSwitchTo);
                if (isEnginePlaying() || isPaused() || autoNext) {
                    stopCurrentAudioObject(false);
                    if (!isPaused()) {
                        playAudioObject(audioObjectToSwitchTo);
                    }
                }
            } catch (Exception e) {
                handlePlayerEngineError(e);
                stopCurrentAudioObject(false);
            }
        } else {
            if (resetIfNoObject) {
                stopCurrentAudioObject(false);
                playListHandler.resetCurrentPlayList();
                playListEventListeners.selectedAudioObjectHasChanged(playListHandler.getCurrentAudioObjectFromCurrentPlayList());
            }
        }
    }

    /**
     * Restarts playback (stops and starts playback, seeking to previous position)
     * This is normally used after apply a change in configuration (normalization, equalization)
     */
    protected void restartPlayback() {
    	long position = getCurrentAudioObjectPlayedTime();
        // Disable playback state listeners while restarting playback
        setCallToPlaybackStateListenersDisabled(true);
        
        finishPlayer();        
        playCurrentAudioObject(true);
        seekCurrentAudioObject(position);
        
        // Enable playback state listeners again
        setCallToPlaybackStateListenersDisabled(false);
    }
    
    public IAudioObject getAudioObject() {
        return audioObject;
    }

    public long getCurrentAudioObjectPlayedTime() {
        return currentAudioObjectPlayedTime;
    }

    public long getCurrentAudioObjectLength() {
        return currentAudioObjectLength;
    }

	/**
	 * @return the callToPlaybackStateListenersDisabled
	 */
	private boolean isCallToPlaybackStateListenersDisabled() {
		return callToPlaybackStateListenersDisabled;
	}

	/**
	 * @param callToPlaybackStateListenersDisabled the callToPlaybackStateListenersDisabled to set
	 */
	private void setCallToPlaybackStateListenersDisabled(
			boolean callToPlaybackStateListenersDisabled) {
		this.callToPlaybackStateListenersDisabled = callToPlaybackStateListenersDisabled;
	}

	/**
	 * Interrupts playing thread
	 */
	void interruptPlayAudioObjectThread() {
        if (playAudioObjectThread != null) {
        	playAudioObjectThread.interrupt();
        }
	}

	/**
	 * @return the submissionState
	 */
	protected SubmissionState getSubmissionState() {
		return submissionState;
	}

	/**
	 * @param submissionState the submissionState to set
	 */
	protected void setSubmissionState(SubmissionState submissionState) {
		this.submissionState = submissionState;
	}
	
	protected IState getState() {
		return state;
	}
	
	protected IFrame getFrame() {
		return frame;
	}

	/**
	 * @return the osManager
	 */
	protected IOSManager getOsManager() {
		return osManager;
	}

    /**
     * @param equalizer
     */
    public void setEqualizer(IEqualizer equalizer) {
		this.equalizer = equalizer;
	}
    
    /**
     * @param state
     */
    public void setState(IState state) {
		this.state = state;
	}
    
    /**
     * @param frame
     */
    public void setFrame(IFrame frame) {
		this.frame = frame;
	}
    
    /**
     * @param osManager
     */
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @param temporalDiskStorage
     */
    public void setTemporalDiskStorage(ITemporalDiskStorage temporalDiskStorage) {
		this.temporalDiskStorage = temporalDiskStorage;
	}
    
    /**
     * @param playerHandler
     */
    public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
    
    /**
     * @param playListEventListeners
     */
    public void setPlayListEventListeners(PlayListEventListeners playListEventListeners) {
		this.playListEventListeners = playListEventListeners;
	}
    
}
