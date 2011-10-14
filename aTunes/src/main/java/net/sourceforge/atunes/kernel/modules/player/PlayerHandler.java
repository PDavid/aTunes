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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.kernel.PlaybackStateListeners;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine.SubmissionState;
import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.xine.XineEngine;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlaybackStateListener;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITemporalDiskStorage;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.lang.ArrayUtils;
import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

/**
 * This class is responsible for handling the player engine.
 */
public final class PlayerHandler extends AbstractHandler implements PluginListener, IPlayerHandler {

    /**
     * Names of all engines
     */
    private String[] engineNames;
    
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
    
    @Override
    public void applicationStateChanged(IState newState) {
        // Set ticks for the player progress bar
    	getPlayerControlsController().getComponentControlled().setShowTicksAndLabels(newState.isShowTicks());
    	
    	// Show advanced controls
    	getPlayerControlsController().getComponentControlled().showAdvancedPlayerControls(newState.isShowAdvancedPlayerControls());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#isEnginePlaying()
	 */
    @Override
	public boolean isEnginePlaying() {
        return playerEngine != null ? playerEngine.isEnginePlaying() : false;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#setVolume(int)
	 */
    @Override
	public void setVolume(int perCent) {
    	if (playerEngine != null) {
    		playerEngine.setVolume(perCent);
    	}
        getPlayerControlsController().setVolume(perCent);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#applyMuteState(boolean)
	 */
    @Override
	public void applyMuteState(boolean state) {
    	if (playerEngine != null) {
    		playerEngine.applyMuteState(state);
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#applyNormalization()
	 */
    @Override
	public void applyNormalization() {
    	if (playerEngine != null) {
    		playerEngine.applyNormalization();
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#supportsCapability(net.sourceforge.atunes.kernel.modules.player.PlayerEngineCapability)
	 */
    @Override
	public boolean supportsCapability(PlayerEngineCapability capability) {
        return playerEngine != null ? playerEngine.supportsCapability(capability) : false;
    }

    @Override
    public void applyEqualization(float[] values) {
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#playCurrentAudioObject(boolean)
	 */
    @Override
	public final void playCurrentAudioObject(boolean buttonPressed) {
    	if (playerEngine != null) {
    		playerEngine.playCurrentAudioObject(buttonPressed);
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#stopCurrentAudioObject(boolean)
	 */
    @Override
	public final void stopCurrentAudioObject(boolean userStopped) {
    	if (playerEngine != null) {
    		playerEngine.stopCurrentAudioObject(userStopped);
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#playPreviousAudioObject()
	 */
    @Override
	public final void playPreviousAudioObject() {
    	if (playerEngine != null) {
    		playerEngine.playPreviousAudioObject();
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#playNextAudioObject()
	 */
    @Override
	public final void playNextAudioObject() {
    	if (playerEngine != null) {
    		playerEngine.playNextAudioObject(false);
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#seekCurrentAudioObject(long)
	 */
    @Override
	public final void seekCurrentAudioObject(long milliseconds) {
    	if (playerEngine != null) {
    		playerEngine.seekCurrentAudioObject(milliseconds);
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#volumeDown()
	 */
    @Override
	public final void volumeDown() {
    	if (playerEngine != null) {
    		playerEngine.volumeDown();
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#volumeUp()
	 */
    @Override
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#getEqualizer()
	 */
    @Override
	public Equalizer getEqualizer() {
        return playerEngine != null ? playerEngine.getEqualizer() : null;
    }

    /**
     * Returns the list of player engines
     * 
     * @return list with player engines
     */
    private List<AbstractPlayerEngine> getEngines() {
        List<AbstractPlayerEngine> result = new ArrayList<AbstractPlayerEngine>(2);
        result.add(new MPlayerEngine(getState(), getFrame(), getOsManager(), getBean(IPlayListHandler.class), getBean(INavigationHandler.class), getBean(ITemporalDiskStorage.class), this, getBean(PlayListEventListeners.class)));
        result.add(new XineEngine(getState(), getFrame(), getOsManager(), getBean(IPlayListHandler.class), getBean(INavigationHandler.class), getBean(ITemporalDiskStorage.class), this, getBean(PlayListEventListeners.class)));
        //result.add(new VlcPlayerEngine());
        //result.add(new GStreamerEngine());
        return result;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#getAudioObject()
	 */
    @Override
	public IAudioObject getAudioObject() {
    	return playerEngine != null ? playerEngine.getAudioObject() : null;
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
            if (!getOsManager().isPlayerEngineSupported(engine) || !engine.isEngineAvailable()) {
                it.remove();
            }
        }

        if (!engines.isEmpty()) {
            // Update engine names
            engineNames = new String[engines.size()];
            for (int i = 0; i < engines.size(); i++) {
                engineNames[i] = engines.get(i).getEngineName();
            }

            Logger.info("List of availables engines : ", ArrayUtils.toString(engineNames));

        	// Get engine of application state (default or selected by user)
            String selectedEngine = getState().getPlayerEngine();

            // If selected engine is not available then try default engine or another one
            if (!ArrayUtils.contains(engineNames, selectedEngine)) {

                Logger.info(selectedEngine, " is not availaible");
                if (ArrayUtils.contains(engineNames, Constants.DEFAULT_ENGINE)) {
                    selectedEngine = Constants.DEFAULT_ENGINE;
                } else {
                    // If default engine is not available, then get the first engine of map returned
                    selectedEngine = engines.iterator().next().getEngineName();
                }
                // Update application state with this engine
                getState().setPlayerEngine(selectedEngine);
            }

            for (AbstractPlayerEngine engine : engines) {
                if (engine.getEngineName().equals(selectedEngine)) {
                	playerEngine = engine;
                    Logger.info("Engine initialized : " + selectedEngine);
                    break;
                }
            }
        }
    }
    
    @Override
    public void allHandlersInitialized() {
    	initialize();
    }
    
    @Override
    public int requestUserInteraction() {
    	return 1;
    }
    
    @Override
    public void doUserInteraction() {
        if (playerEngine == null) {
            manageNoPlayerEngine(getOsManager(), getFrame());
        }
    }
    
	/**
	 * Called when no player engine is available
	 * @param osManager
	 * @param frame
	 */
	private void manageNoPlayerEngine(IOSManager osManager, IFrame frame) {
		// Delegate to specific OS code
		osManager.manageNoPlayerEngine(frame);
	}
	
    /**
     * Initializes all related to player engine
     */
    private void initialize() {
    	initPlayerEngine();
    	
        // Set volume on visual components
        Volume.setVolume(getState().getVolume(), false, getState(), this);

        // Mute
        applyMuteState(getState().isMuteEnabled());
    	
        // Initial playback state is stopped
        if (playerEngine != null) {
        	playerEngine.callPlaybackStateListeners(PlaybackState.STOPPED);
        }

        if (getState().isPlayAtStartup()) {
            playCurrentAudioObject(true);
        }
        
        // Progress bar ticks
        getPlayerControlsController().getComponentControlled().setShowTicksAndLabels(getState().isShowTicks());
        
        // Show advanced controls
        getPlayerControlsController().getComponentControlled().showAdvancedPlayerControls(getState().isShowAdvancedPlayerControls());

    	if (playerEngine != null) {
            // Init engine
            playerEngine.initializePlayerEngine();
    	}
    	
        // Add a shutdown hook to perform some actions before killing the JVM
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                Logger.debug("Final check for Zombie player engines");
                if (playerEngine != null) {
                	playerEngine.killPlayer();
                }
                Logger.debug("Closing player ...");
            }

        }));        
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#getEngineNames()
	 */
    @Override
	public final String[] getEngineNames() {
        return engineNames != null ? Arrays.copyOf(engineNames, engineNames.length) : new String[0];
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            IPlaybackStateListener listener = (IPlaybackStateListener) getBean(IPluginsHandler.class).getNewInstance(plugin);
            Context.getBean(PlaybackStateListeners.class).addPlaybackStateListener(listener);
        } catch (PluginSystemException e) {
            Logger.error(e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        Logger.info(StringUtils.getString("Plugin deactivated: ", plugin.getName(), " (", plugin.getClassName(), ")"));
        for (Plugin createdInstance : createdInstances) {
        	Context.getBean(PlaybackStateListeners.class).removePlaybackStateListener((IPlaybackStateListener) createdInstance);
        }
    }

    @Override
    public void playbackStateChanged(PlaybackState newState, IAudioObject currentAudioObject) {
    	if (playbackState != newState) {
    		this.playbackState = newState;
    		Logger.debug("Playback state changed to:", newState);

    		if (newState == PlaybackState.PLAY_FINISHED || newState == PlaybackState.PLAY_INTERRUPTED || newState == PlaybackState.STOPPED) {
    			if (playerEngine != null && playerEngine.getSubmissionState() == SubmissionState.PENDING && currentAudioObject instanceof AudioFile) {
    				getBean(IWebServicesHandler.class).submit((AudioFile) currentAudioObject, getCurrentAudioObjectPlayedTime() / 1000);
    				getBean(IStatisticsHandler.class).updateAudioObjectStatistics(currentAudioObject);
    				playerEngine.setSubmissionState(SubmissionState.SUBMITTED);
    			}
    		}

    		if (playerEngine != null && newState == PlaybackState.STOPPED) {
    			playerEngine.setCurrentAudioObjectPlayedTime(0);
    			playerEngine.interruptPlayAudioObjectThread();
    		}
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#getPlaybackState()
	 */
    @Override
	public PlaybackState getPlaybackState() {
        return playbackState;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#getCurrentAudioObjectPlayedTime()
	 */
    @Override
	public long getCurrentAudioObjectPlayedTime() {
        return playerEngine != null ? playerEngine.getCurrentAudioObjectPlayedTime() : 0;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#getCurrentAudioObjectLength()
	 */
    @Override
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
            playerControlsController = new PlayerControlsController(getFrame().getPlayerControls(), getState(), this);
        }
        return playerControlsController;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#setPlaying(boolean)
	 */
	@Override
	public void setPlaying(boolean playing) {
		getPlayerControlsController().setPlaying(playing);
	}

	@Override
	public void setAudioObjectLength(long currentLength) {
		getPlayerControlsController().setAudioObjectLength(currentLength);		
	}

	@Override
	public void setCurrentAudioObjectTimePlayed(long actualPlayedTime, long currentAudioObjectLength) {
		getPlayerControlsController().setCurrentAudioObjectTimePlayed(actualPlayedTime, currentAudioObjectLength);
	}

	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {
		getPlayerControlsController().updatePlayerControls(audioObject);
	}

	@Override
	protected void initHandler() {
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#initializeAndCheck()
	 */
	@Override
	public void initializeAndCheck() {
		initialize();
        if (playerEngine == null) {
        	manageNoPlayerEngine(getOsManager(), getFrame());
        }
	}
}
