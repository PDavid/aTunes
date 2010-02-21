/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.statistics.StatisticsHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.misc.TempFolder;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class has common logic for all player engines.
 */
public abstract class PlayerEngine implements PlaybackStateListener {

    private static class ApplyUserSelectionRunnable implements Runnable {
    	private PlayerEngine engine;
		private final boolean ignorePlaybackError;

		private ApplyUserSelectionRunnable(PlayerEngine engine, boolean ignorePlaybackError) {
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

	private static class ShowPlaybackErrorRunnable implements Runnable {
		private final String[] errorMessages;
		private boolean ignore;

		private ShowPlaybackErrorRunnable(String[] errorMessages) {
			this.errorMessages = errorMessages;
		}

		@Override
		public void run() {
		    String selection = (String) GuiHandler.getInstance().showMessage(StringUtils.getString(errorMessages), I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE,
		            new String[] { I18nUtils.getString("IGNORE"), I18nUtils.getString("CANCEL") });
		    ignore = selection.equals(I18nUtils.getString("IGNORE"));
		}

		/**
		 * @return the ignore
		 */
		protected boolean isIgnore() {
			return ignore;
		}
	}

	private static class ShowErrorDialog implements Runnable {
		private final Exception e;

		private ShowErrorDialog(Exception e) {
			this.e = e;
		}

		@Override
		public void run() {
		    GuiHandler.getInstance().showErrorDialog(e.getMessage());
		}
	}

	private enum SubmissionState {
        NOT_SUBMITTED, PENDING, SUBMITTED;
    }

    /**
     * The logger used in player engines
     */
    private Logger logger;

    /**
     * Listeners of playback state
     */
    private List<PlaybackStateListener> playbackStateListeners;

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

    private Equalizer equalizer;

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
     * @param perCent
     *            0-1
     */
    protected abstract void seekTo(double perCent);

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
     * Adds a new playback state listener
     */
    public final void addPlaybackStateListener(PlaybackStateListener listener) {
        if (playbackStateListeners == null) {
            playbackStateListeners = new ArrayList<PlaybackStateListener>();
        }
        playbackStateListeners.add(listener);
    }

    /**
     * Removes a playback state listener
     * 
     * @param listener
     */
    public final void removePlaybackStateListener(PlaybackStateListener listener) {
        if (playbackStateListeners == null || !playbackStateListeners.contains(listener)) {
            return;
        }
        playbackStateListeners.remove(listener);
    }

    /**
     * Calls all playback listeners with new state and current audio object When
     * audio object changes this method must be called after change to call
     * listeners with new audio object
     * 
     * @param newState
     * @param currentAudioObject
     */
    final void callPlaybackStateListeners(PlaybackState newState) {
        for (PlaybackStateListener listener : playbackStateListeners) {
            listener.playbackStateChanged(newState, audioObject);
        }
    }

    @Override
    public void playbackStateChanged(PlaybackState newState, AudioObject currentAudioObject) {
        if (newState == PlaybackState.PLAY_FINISHED || newState == PlaybackState.PLAY_INTERRUPTED || newState == PlaybackState.STOPPED) {
            submitToLastFmAndUpdateStats();
        }
        if (newState == PlaybackState.STOPPED) {
            setCurrentAudioObjectPlayedTime(0);
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
                getLogger().info(LogCategories.PLAYER, "Pause");
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
                        getLogger().info(LogCategories.PLAYER, "Resumed paused song");
                    }
                } else {
                    nextAudioObject = PlayListHandler.getInstance().getCurrentAudioObjectFromVisiblePlayList();
                    if (nextAudioObject != null) {
                        if (isEnginePlaying() || paused) {
                            stopCurrentAudioObject(false);
                        }

                        // We need to update current object and active playlist first
                        PlayListHandler.setVisiblePlayListActive();
                        PlayListHandler.getInstance().selectedAudioObjectChanged(nextAudioObject);

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
            boolean activateFadeAway = userStopped && ApplicationState.getInstance().isUseFadeAway() && !paused;
            if (paused) {
                paused = false;
            }

            stopPlayback(userStopped, activateFadeAway);
            callPlaybackStateListeners(PlaybackState.STOPPED);
            getLogger().info(LogCategories.PLAYER, "Stop");
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
     * @param ok
     * 			<code>true</code> if playback finishes ok, <code>false</code> otherwise
     * @param messages
     * 			Messages when playback finishes with error
     */
    public final void currentAudioObjectFinished(boolean ok, final String...errorMessages) {
		// Call listeners to notify playback finished
		callPlaybackStateListeners(PlaybackState.PLAY_FINISHED);

		if (!ok) {
            getLogger().info(LogCategories.PLAYER, StringUtils.getString("Playback finished with errors: ", errorMessages));
            
            boolean ignore = showPlaybackError(errorMessages);
            applyUserSelection(ignore);

    	} else {
    		getLogger().info(LogCategories.PLAYER, "Playback finished");

    		// Move to the next audio object
    		playNextAudioObject(true);
    	}
    }
    
    /**
     * Lets user decide if want to ignore playback error or cancel
     * @param errorMessages
     * @return
     */
    private boolean showPlaybackError(String...errorMessages) {
    	ShowPlaybackErrorRunnable r = new ShowPlaybackErrorRunnable(errorMessages);
    	if (SwingUtilities.isEventDispatchThread()) {
    		r.run();
    	} else {
    		try {
    			SwingUtilities.invokeAndWait(r);
    		} catch (InterruptedException e) {
    			getLogger().error(LogCategories.PLAYER, e);
    		} catch (InvocationTargetException e) {
    			getLogger().error(LogCategories.PLAYER, e);
    		}
    	}
    	return r.isIgnore();
    }
    
    /**
     * if user wants to ignore play back error then play back continues, if not it's stopped
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
     * Seek function: play current audio object from position defined by
     * parameter (0-100%)
     * 
     * @param position
     *            From start of audio object (0) to end of audio object (100)
     * 
     */
    public final void seekCurrentAudioObject(double position) {
        // If paused first resume and then seek
        if (paused) {
            paused = false;
            if (!PlayListHandler.getInstance().getCurrentPlayList(false).isEmpty()) {
                callPlaybackStateListeners(PlaybackState.RESUMING);
                getLogger().info(LogCategories.PLAYER, "Resumed paused song");
            }
        }

        seekTo(position);
    }

    /**
     * Lower volume
     */
    public final void volumeDown() {
        getLogger().debug(LogCategories.PLAYER);
        Volume.setVolume(ApplicationState.getInstance().getVolume() - 5);
    }

    /**
     * Raise volume
     */
    public final void volumeUp() {
        getLogger().debug(LogCategories.PLAYER);
        Volume.setVolume(ApplicationState.getInstance().getVolume() + 5);
    }

    /**
     * Called when a exception is thrown related with player engine
     * 
     * @param e
     *            The exception thrown
     */
    public final void handlePlayerEngineError(final Exception e) {
        getLogger().error(LogCategories.PLAYER, StringUtils.getString("Player Error: ", e));
        getLogger().error(LogCategories.PLAYER, e);
        SwingUtilities.invokeLater(new ShowErrorDialog(e));
    }

    /**
     * Instantiates a new player handler.
     */
    protected PlayerEngine() {
        // To properly init player must call method "initPlayerEngine"
        equalizer = new Equalizer();
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
        return ApplicationState.getInstance().isUseNormalisation();
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
        return ApplicationState.getInstance().isMuteEnabled();
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
        ControllerProxy.getInstance().getPlayerControlsController().setAudioObjectLength(currentLength);
        GuiHandler.getInstance().getFullScreenWindow().setAudioObjectLength(currentLength);
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
        ControllerProxy.getInstance().getPlayerControlsController().setCurrentAudioObjectTimePlayed(actualPlayedTime, currentAudioObjectLength);
        GuiHandler.getInstance().getFullScreenWindow().setCurrentAudioObjectPlayedTime(actualPlayedTime, currentAudioObjectLength);

        if ((submissionState == SubmissionState.NOT_SUBMITTED) && (actualPlayedTime > currentAudioObjectLength / 2 || actualPlayedTime >= 240000)) {
            submissionState = SubmissionState.PENDING;
        }
    }

    /**
     * Submits the current audio object to Last.fm and updates stats
     */
    private void submitToLastFmAndUpdateStats() {
        if ((submissionState == SubmissionState.PENDING) && audioObject instanceof AudioFile) {
            LastFmService.getInstance().submitToLastFm((AudioFile) audioObject, currentAudioObjectPlayedTime / 1000);
            StatisticsHandler.getInstance().setAudioFileStatistics((AudioFile) audioObject);
            if (GuiHandler.getInstance().isStatsDialogVisible()) {
                ControllerProxy.getInstance().getStatsDialogController().updateStats();
            }
            submissionState = SubmissionState.SUBMITTED;
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
        getLogger().debug(LogCategories.PLAYER, "radio or podcast feed entry has started playing");
        // send volume command
        setVolume(ApplicationState.getInstance().getVolume());
        // if muted set mute again
        if (ApplicationState.getInstance().isMuteEnabled()) {
            applyMuteState(true);
        }
        getLogger().debug(LogCategories.PLAYER, "MPlayer bug (ignoring muting and volume settings after streamed file starts playing) workaround applied");
    }

    /**
     * Actions to initialize engine. This method is called just after selecting
     * an available player engine.
     * 
     * <b>NOTE: The overriding method MUST call
     * super.initializePlayerEngine()</b>
     */
    protected void initializePlayerEngine() {
        getLogger().debug(LogCategories.PLAYER);
    }

    /**
     * Starts playing an audio object.
     * 
     * @param audioObject
     */
    private void playAudioObject(AudioObject audioObject) {
        getLogger().info(LogCategories.PLAYER, StringUtils.getString("Started play of file ", audioObject));

        // If cacheFilesBeforePlaying is true and audio object is an audio file, copy it to temp folder
        // and start mplayer process from this copied file
        AudioObject audioObjectToPlay = null;
        if (audioObject instanceof AudioFile && ApplicationState.getInstance().isCacheFilesBeforePlaying()) {
            // Remove previous cached file
            if (lastFileCached != null) {
                TempFolder.getInstance().removeFile(lastFileCached);
            }

            File tempFile = TempFolder.getInstance().copyToTempFolder(((AudioFile) audioObject).getFile());
            if (tempFile != null) {
                audioObjectToPlay = new AudioFile(tempFile.getAbsolutePath());
                lastFileCached = tempFile;
            } else {
                lastFileCached = null;
            }
        } else {
            audioObjectToPlay = audioObject;
        }

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
            LastFmService.getInstance().submitNowPlayingInfoToLastFm((AudioFile) audioObject);
        }

        this.audioObject = audioObject;

        // Add audio object to playback history
        PlayListHandler.getInstance().addToPlaybackHistory(audioObject);

        startPlayback(audioObjectToPlay, audioObject);

        // Setting volume and balance
        if (ApplicationState.getInstance().isMuteEnabled()) {
            applyMuteState(true);
        } else {
            setVolume(ApplicationState.getInstance().getVolume());
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
                PlayListHandler.getInstance().selectedAudioObjectChanged(audioObjectToSwitchTo);
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
                PlayListHandler.getInstance().setPositionToPlayInCurrentPlayList(0);
                PlayListHandler.getInstance().selectedAudioObjectChanged(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
            }
        }
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
	 * @return the logger
	 */
	protected Logger getLogger() {
		if (logger == null) {
			logger = new Logger();
		}
		return logger;
	}

}
