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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.PlaybackState;
import net.sourceforge.atunes.kernel.PlaybackStateListener;
import net.sourceforge.atunes.kernel.PlaybackStateListeners;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine.SubmissionState;
import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.xine.XineEngine;
import net.sourceforge.atunes.kernel.modules.plugins.PluginsHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.statistics.StatisticsHandler;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.lang.ArrayUtils;
import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

/**
 * This class is responsible for handling the player engine.
 */
public final class PlayerHandler extends AbstractHandler implements PluginListener {

    /**
     * The player engine used
     */
    private static PlayerHandler instance;

    /**
     * Default player engine
     */
    public static final String DEFAULT_ENGINE = "MPlayer";

    /**
     * Names of all engines
     */
    private static String[] engineNames;

    /**
     * The player engine
     * NOTE: This attribute can be null if no player engine has been found, so it must be checked against null
     */
    private AbstractPlayerEngine playerEngine;

    /**
     * The current playback state
     */
    private PlaybackState playbackState;

    /**
     * Controller
     */
    private PlayerControlsController playerControlsController;
    
    /**
     * Instantiates a new player handler.
     */
    private PlayerHandler() {
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        // Set ticks for the player progress bar
    	getPlayerControlsController().getComponentControlled().setShowTicksAndLabels(newState.isShowTicks());
    	
    	// Show advanced controls
    	getPlayerControlsController().getComponentControlled().showAdvancedPlayerControls(newState.isShowAdvancedPlayerControls());
    }

    @Override
    public void applicationStarted(List<AudioObject> playList) {
    	// All initialization in this handler must be done after all handlers are initialized and ready
    }

    /**
     * Checks if engine is currently playing (<code>true</code>) or not (
     * <code>false</code>)
     * 
     * @return <code>true</code> if engine is currently playing
     */
    public boolean isEnginePlaying() {
        return playerEngine != null ? playerEngine.isEnginePlaying() : false;
    }

    /**
     * This method must be implemented by player engines. Applies volume value
     * in player engine
     * 
     * @param perCent
     *            0-100
     */
    public void setVolume(int perCent) {
    	if (playerEngine != null) {
    		playerEngine.setVolume(perCent);
    	}
        getPlayerControlsController().setVolume(perCent);
    }

    /**
     * This method must be implemented by player engines. Apply mute state in
     * player engine
     * 
     * @param state
     *            : enabled (<code>true</code>) or disabled (<code>false</code>)
     * 
     */
    public void applyMuteState(boolean state) {
    	if (playerEngine != null) {
    		playerEngine.applyMuteState(state);
    	}
    }

    /**
     * This method must be implemented by player engines. Method to apply
     * normalization in player engine
     * 
     * @param values
     */
    public void applyNormalization() {
    	if (playerEngine != null) {
    		playerEngine.applyNormalization();
    	}
    }

    /**
     * This methods checks if the specified player capability is supported by
     * this player engine
     * 
     * @param capability
     *            The capability that should be checked
     * @return If the specified player capability is supported by this player
     *         engine
     */
    public boolean supportsCapability(PlayerEngineCapability capability) {
        return playerEngine != null ? playerEngine.supportsCapability(capability) : false;
    }

    /**
     * This method must be implemented by player engines. Method to apply
     * equalizer values in player engine
     * 
     * @param values
     */
    void applyEqualization(float[] values) {
    	if (playerEngine != null) {
    		playerEngine.applyEqualization(values);
    	}
    }

    /**
     * This method must be implemented by player engines. Transform values
     * retrieved from equalizer dialog to values for player engine
     * 
     * @param values
     * @return
     */
    float[] transformEqualizerValues(float[] values) {
        return playerEngine != null ? playerEngine.transformEqualizerValues(values) : new float[0];
    }

    /**
     * Starts playing current audio object from play list
     * 
     * @param buttonPressed
     *            TODO: Add more javadoc
     */
    public final void playCurrentAudioObject(boolean buttonPressed) {
    	if (playerEngine != null) {
    		playerEngine.playCurrentAudioObject(buttonPressed);
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
    	if (playerEngine != null) {
    		playerEngine.stopCurrentAudioObject(userStopped);
    	}
    }

    /**
     * Starts playing previous audio object from play list
     */
    public final void playPreviousAudioObject() {
    	if (playerEngine != null) {
    		playerEngine.playPreviousAudioObject();
    	}
    }

    /**
     * Starts playing next audio object from play list
     */
    public final void playNextAudioObject() {
    	if (playerEngine != null) {
    		playerEngine.playNextAudioObject(false);
    	}
    }

    /**
     * Seek function: play current audio object from position defined by
     * parameter milliseconds
     * 
     * @param milliseconds
     *            Milliseconds from start of audio object
     * 
     */
    public final void seekCurrentAudioObject(long milliseconds) {
    	if (playerEngine != null) {
    		playerEngine.seekCurrentAudioObject(milliseconds);
    	}
    }

    /**
     * Lower volume
     */
    public final void volumeDown() {
    	if (playerEngine != null) {
    		playerEngine.volumeDown();
    	}
    }

    /**
     * Raise volume
     */
    public final void volumeUp() {
    	if (playerEngine != null) {
    		playerEngine.volumeUp();
    	}
    }

    @Override
    public void applicationFinish() {
        // Stop must be called explicitly to avoid playback after user closed app
        stopCurrentAudioObject(true);
        if (playerEngine != null) {
        	playerEngine.finishPlayer();
        }
    }

    /**
     * Returns the equalizer of this player engine
     * 
     * @return the equalizer of this player engine
     */
    public Equalizer getEqualizer() {
        return playerEngine != null ? playerEngine.getEqualizer() : null;
    }

    /**
     * Returns the list of player engines
     * 
     * @return list with player engines
     */
    private static List<AbstractPlayerEngine> getEngines() {
        List<AbstractPlayerEngine> result = new ArrayList<AbstractPlayerEngine>(2);
        result.add(new MPlayerEngine());
        result.add(new XineEngine());
        //result.add(new VlcPlayerEngine());
        //result.add(new GStreamerEngine());
        return result;
    }

    public AudioObject getAudioObject() {
    	return playerEngine != null ? playerEngine.getAudioObject() : null;
    }

    /**
     * Gets the single instance of PlayerHandler. This method returns player
     * engine configured by user or default if it's available on the system
     * 
     * @return single instance of PlayerHandler
     */
    public static final PlayerHandler getInstance() {
        if (instance == null) {
            instance = new PlayerHandler();
        }
        return instance;
    }
    
    /**
     * Initializes player engine
     */
    private void initPlayerEngine() {
        // Get engines list
        List<AbstractPlayerEngine> engines = getEngines();

        // Remove unsupported engines
        Iterator<AbstractPlayerEngine> it = engines.iterator();
        while (it.hasNext()) {
        	AbstractPlayerEngine engine = it.next();
        	// Engines must be supported for given OS and available
            if (!OsManager.isPlayerEngineSupported(engine) || !engine.isEngineAvailable()) {
                it.remove();
            }
        }

        if (!engines.isEmpty()) {
            // Update engine names
            engineNames = new String[engines.size()];
            for (int i = 0; i < engines.size(); i++) {
                engineNames[i] = engines.get(i).getEngineName();
            }

            Logger.info(LogCategories.PLAYER, "List of availables engines : ", ArrayUtils.toString(engineNames));

        	// Get engine of application state (default or selected by user)
            String selectedEngine = ApplicationState.getInstance().getPlayerEngine();

            // If selected engine is not available then try default engine or another one
            if (!ArrayUtils.contains(engineNames, selectedEngine)) {

                Logger.info(LogCategories.PLAYER, selectedEngine, " is not availaible");
                if (ArrayUtils.contains(engineNames, DEFAULT_ENGINE)) {
                    selectedEngine = DEFAULT_ENGINE;
                } else {
                    // If default engine is not available, then get the first engine of map returned
                    selectedEngine = engines.iterator().next().getEngineName();
                }
                // Update application state with this engine
                ApplicationState.getInstance().setPlayerEngine(selectedEngine);
            }

            for (AbstractPlayerEngine engine : engines) {
                if (engine.getEngineName().equals(selectedEngine)) {
                	playerEngine = engine;
                    Logger.info(LogCategories.PLAYER, "Engine initialized : " + selectedEngine);
                    break;
                }
            }
        }

        if (playerEngine == null) {
            handlePlayerError(new IllegalStateException(I18nUtils.getString("NO_PLAYER_ENGINE")));
        }

    }
    
    @Override
    public void allHandlersInitialized() {
    	initPlayerEngine();
    	
        // Set volume on visual components
        Volume.setVolume(ApplicationState.getInstance().getVolume(), false);

        // Mute
        applyMuteState(ApplicationState.getInstance().isMuteEnabled());
    	
        // Initial playback state is stopped
        if (playerEngine != null) {
        	playerEngine.callPlaybackStateListeners(PlaybackState.STOPPED);
        }

        if (ApplicationState.getInstance().isPlayAtStartup()) {
            playCurrentAudioObject(true);
        }
        
        // Progress bar ticks
        getPlayerControlsController().getComponentControlled().setShowTicksAndLabels(ApplicationState.getInstance().isShowTicks());
        
        // Show advanced controls
        getPlayerControlsController().getComponentControlled().showAdvancedPlayerControls(ApplicationState.getInstance().isShowAdvancedPlayerControls());

    	if (playerEngine != null) {
            // Init engine
            playerEngine.initializePlayerEngine();
    	}
    	
        // Add a shutdown hook to perform some actions before killing the JVM
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                Logger.debug(LogCategories.SHUTDOWN_HOOK, "Final check for Zombie player engines");
                if (playerEngine != null) {
                	playerEngine.killPlayer();
                }
                Logger.debug(LogCategories.SHUTDOWN_HOOK, "Closing player ...");
            }

        }));
    }

    private static void handlePlayerError(Throwable t) {
        Logger.error(LogCategories.PLAYER, StringUtils.getString("Player Error: ", t));
        Logger.error(LogCategories.PLAYER, t);
        GuiHandler.getInstance().showErrorDialog(t.getMessage());
    }

    /**
     * Return list of engine names as configured in settings file This method is
     * mainly designed to be used in preferences window to select a player
     * engine by its name
     * 
     * @return list of engine names
     */
    public final String[] getEngineNames() {
        return engineNames != null ? Arrays.copyOf(engineNames, engineNames.length) : new String[0];
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            PlaybackStateListener listener = (PlaybackStateListener) PluginsHandler.getInstance().getNewInstance(plugin);
            PlaybackStateListeners.addPlaybackStateListener(listener);
        } catch (PluginSystemException e) {
            Logger.error(LogCategories.PLUGINS, e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        Logger.info(LogCategories.PLUGINS, StringUtils.getString("Plugin deactivated: ", plugin.getName(), " (", plugin.getClassName(), ")"));
        for (Plugin createdInstance : createdInstances) {
        	PlaybackStateListeners.removePlaybackStateListener((PlaybackStateListener) createdInstance);
        }
    }

    @Override
    public void playbackStateChanged(PlaybackState newState, AudioObject currentAudioObject) {
        this.playbackState = newState;
    	Logger.debug(LogCategories.PLAYER, "Playback state changed to:", newState);
        
        if (newState == PlaybackState.PLAY_FINISHED || newState == PlaybackState.PLAY_INTERRUPTED || newState == PlaybackState.STOPPED) {
        	if (playerEngine != null && playerEngine.getSubmissionState() == SubmissionState.PENDING && currentAudioObject instanceof AudioFile) {
                LastFmService.getInstance().submitToLastFm((AudioFile) currentAudioObject, getCurrentAudioObjectPlayedTime() / 1000);
                StatisticsHandler.getInstance().setAudioFileStatistics((AudioFile) currentAudioObject);
                playerEngine.setSubmissionState(SubmissionState.SUBMITTED);
            }
        }
        
        if (playerEngine != null && newState == PlaybackState.STOPPED) {
            playerEngine.setCurrentAudioObjectPlayedTime(0);
            playerEngine.interruptPlayAudioObjectThread();
        }
    }

    /**
     * @return the playbackState
     */
    public PlaybackState getPlaybackState() {
        return playbackState;
    }

    /**
     * Returns time played for current audio object
     * 
     * @return
     */
    public long getCurrentAudioObjectPlayedTime() {
        return playerEngine != null ? playerEngine.getCurrentAudioObjectPlayedTime() : 0;
    }

    /**
     * Returns length for current audio object
     * 
     * @return
     */
    public long getCurrentAudioObjectLength() {
        return playerEngine != null ? playerEngine.getCurrentAudioObjectLength() : 0;
    }
    
    /**
     * Gets the player controls controller.
     * 
     * @return the player controls controller
     */
    private PlayerControlsController getPlayerControlsController() {
        if (playerControlsController == null) {
            PlayerControlsPanel panel = null;
            panel = GuiHandler.getInstance().getPlayerControls();
            playerControlsController = new PlayerControlsController(panel);
        }
        return playerControlsController;
    }

	public void setPlaying(boolean playing) {
		getPlayerControlsController().setPlaying(playing);
	}

	void setAudioObjectLength(long currentLength) {
		getPlayerControlsController().setAudioObjectLength(currentLength);		
	}

	void setCurrentAudioObjectTimePlayed(long actualPlayedTime, long currentAudioObjectLength) {
		getPlayerControlsController().setCurrentAudioObjectTimePlayed(actualPlayedTime, currentAudioObjectLength);
	}

	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(AudioObject audioObject) {
		getPlayerControlsController().updatePlayerControls(audioObject);
	}

	@Override
	protected void initHandler() {
		// TODO Auto-generated method stub
		
	}
}
