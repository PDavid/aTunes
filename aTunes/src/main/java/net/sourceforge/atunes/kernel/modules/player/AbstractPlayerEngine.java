/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.awt.Cursor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.PlaybackState;
import net.sourceforge.atunes.kernel.PlaybackStateListeners;
import net.sourceforge.atunes.kernel.modules.fullscreen.FullScreenHandler;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.webservices.WebServicesHandler;
import net.sourceforge.atunes.misc.TempFolder;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class has common logic for all player engines.
 */
public abstract class AbstractPlayerEngine {

	/**
	 * Runnable to play audio objects and (if needed) cache files
	 * @author fleax
	 *
	 */
    private final class PlayAudioObjectRunnable implements Runnable {
    	
		private final AudioObject audioObject;
		AudioObject audioObjectToPlay = null;
		
		private PlayAudioObjectRunnable(AudioObject audioObject) {
			this.audioObject = audioObject;			
			GuiHandler.getInstance().getFrame().getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
				
		@Override
		public void run() {
		    audioObjectToPlay = cacheAudioObject(audioObject);
			// Set default cursor again
			GuiHandler.getInstance().getFrame().getFrame().setCursor(Cursor.getDefaultCursor());
			
			playAudioObjectAfterCache(audioObjectToPlay, audioObject);
			
			playAudioObjectThread = null;
		}		
	}

	private static final class ApplyUserSelectionRunnable implements Runnable {
        private AbstractPlayerEngine engine;
        private final boolean ignorePlaybackError;

        private ApplyUserSelectionRunnable(AbstractPlayerEngine engine, boolean ignorePlaybackError) {
            this.engine = engine;
            this.ignorePlaybackError = ignorePlaybackError;
        }

        @Override
        public void run() {
            if (ignorePlaybackError) {
                // Move to the next audio object
                engine.playNextAudioObject(true);
            } else {
                // Stop playback
                engine.stopCurrentAudioObject(false);
            }
        }
    }

    private static final class ShowPlaybackErrorRunnable implements Runnable {
        private final String[] errorMessages;
        private boolean ignore;

        private ShowPlaybackErrorRunnable(String[] errorMessages) {
            this.errorMessages = errorMessages;
        }

        @Override
        public void run() {
        	StringBuilder sb = new StringBuilder();
        	for (String errorMessage : errorMessages) {
        		sb.append(errorMessage).append(" ");
        	}
            String selection = (String) GuiHandler.getInstance().showMessage(StringUtils.getString(sb.toString()), I18nUtils.getString("ERROR"),
                    JOptionPane.ERROR_MESSAGE, new String[] { I18nUtils.getString("IGNORE"), I18nUtils.getString("CANCEL") });
            ignore = selection.equals(I18nUtils.getString("IGNORE"));
        }

        /**
         * @return the ignore
         */
        protected boolean isIgnore() {
            return ignore;
        }
    }

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
    private AudioObject audioObject;

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
    private Equalizer equalizer;
    
    private IState state;
    
    /**
     * A thread invoking play in engine
     */
    private Thread playAudioObjectThread;

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
    protected abstract void startPlayback(AudioObject audioObjectToPlay, AudioObject audioObject);

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
    		PlaybackStateListeners.playbackStateChanged(newState, audioObject);
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
            AudioObject nextAudioObject = null;
            try {
                if (paused && buttonPressed) { // Resume
                    if (!PlayListHandler.getInstance().getCurrentPlayList(false).isEmpty()) {
                        paused = false;
                        resumePlayback();
                        callPlaybackStateListeners(PlaybackState.RESUMING);
                        Logger.info("Resumed paused song");
                    }
                } else {
                    nextAudioObject = PlayListHandler.getInstance().getCurrentAudioObjectFromVisiblePlayList();
                    if (nextAudioObject != null) {
                        if (isEnginePlaying() || paused) {
                            stopCurrentAudioObject(false);
                        }

                        // We need to update current object and active playlist first
                        PlayListHandler.setVisiblePlayListActive();
                        PlayListHandler.getInstance().selectedAudioObjectHasChanged(nextAudioObject);

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

        switchPlaybackTo(PlayListHandler.getInstance().getPreviousAudioObject(), false, false);
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
        switchPlaybackTo(PlayListHandler.getInstance().getNextAudioObject(), true, audioObjectFinished);
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
        ShowPlaybackErrorRunnable r = new ShowPlaybackErrorRunnable(errorMessages);
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
            if (!PlayListHandler.getInstance().getCurrentPlayList(false).isEmpty()) {
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
        Volume.setVolume(state.getVolume() - 5, state);
    }

    /**
     * Raise volume
     */
    public final void volumeUp() {
        Volume.setVolume(state.getVolume() + 5, state);
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
        GuiHandler.getInstance().showExceptionDialog(I18nUtils.getString("ERROR"), e);
    }

    /**
     * Instantiates a new player handler.
     * @param state
     */
    protected AbstractPlayerEngine(IState state) {
        // To properly init player must call method "initPlayerEngine"
        this.equalizer = new Equalizer(state);
        this.state = state;
    }

    /**
     * Returns the equalizer of this player engine
     * 
     * @return the equalizer of this player engine
     */
    public Equalizer getEqualizer() {
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
        PlayerHandler.getInstance().setAudioObjectLength(currentLength);
        FullScreenHandler.getInstance().setAudioObjectLength(currentLength);
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
        PlayerHandler.getInstance().setCurrentAudioObjectTimePlayed(actualPlayedTime, currentAudioObjectLength);
        FullScreenHandler.getInstance().setCurrentAudioObjectPlayedTime(actualPlayedTime, currentAudioObjectLength);

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
    private void playAudioObject(final AudioObject audioObject) {
        Logger.info(StringUtils.getString("Started play of file ", audioObject));

        if (state.isCacheFilesBeforePlaying()) {

        	PlayAudioObjectRunnable r = new PlayAudioObjectRunnable(audioObject);
        	
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
     * @return
     */
    private AudioObject cacheAudioObject(AudioObject audioObject) {
    	AudioObject audioObjectToPlay = null;
    	
        // If cacheFilesBeforePlaying is true and audio object is an audio file, copy it to temp folder
        // and start player process from this copied file
	    if (audioObject instanceof LocalAudioObject && getState().isCacheFilesBeforePlaying()) {
	    	
	    	Logger.debug("Start caching file: ", audioObject.getUrl());
	    	
	        // Remove previous cached file
	        if (lastFileCached != null) {
	            TempFolder.getInstance().removeFile(lastFileCached);
	        }

	        File tempFile = TempFolder.getInstance().copyToTempFolder(((AudioFile) audioObject).getFile());
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
    private void playAudioObjectAfterCache(AudioObject audioObjectToPlay, AudioObject audioObject) {
		// This audio object has not been listened yet
		submissionState = SubmissionState.NOT_SUBMITTED;

		// If we are playing a podcast, mark entry as listened
		if (audioObject instanceof PodcastFeedEntry) {
			((PodcastFeedEntry) audioObject).setListened(true);
			// Update pod cast navigator
			NavigationHandler.getInstance().refreshView(PodcastNavigationView.class);
		}

		// Send Now Playing info to Last.fm
		if (audioObject instanceof AudioFile) {
			WebServicesHandler.getInstance().getLastFmService().submitNowPlayingInfoToLastFm((AudioFile) audioObject);
		}

		AbstractPlayerEngine.this.audioObject = audioObject;

		// Add audio object to playback history
		PlayListHandler.getInstance().addToPlaybackHistory(audioObject);

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
    private void switchPlaybackTo(AudioObject audioObjectToSwitchTo, boolean resetIfNoObject, boolean autoNext) {
        if (audioObjectToSwitchTo != null) {
            try {
                PlayListHandler.getInstance().selectedAudioObjectHasChanged(audioObjectToSwitchTo);
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
                PlayListHandler.getInstance().resetCurrentPlayList();
                PlayListHandler.getInstance().selectedAudioObjectHasChanged(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
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
    
    public AudioObject getAudioObject() {
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

}
