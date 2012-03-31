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

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeSlider;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.PlaybackStateListeners;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine.SubmissionState;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IEqualizer;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlaybackStateListener;
import net.sourceforge.atunes.model.IPlayerControlsPanel;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.model.PlayerEngineCapability;
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
     */
    private AbstractPlayerEngine playerEngine = new VoidPlayerEngine();

    /**
     * The current playback state
     */
    private PlaybackState playbackState;

    /**
     * Controller
     */
    private PlayerControlsController playerControlsController;
    
    private List<AbstractPlayerEngine> engines;
    
    private IEqualizer equalizer;
    
    private VolumeSlider volumeSlider;
    
    private Volume volumeController;
    
    /**
     * @param volumeController
     */
    public void setVolumeController(Volume volumeController) {
		this.volumeController = volumeController;
	}
    
    /**
     * @param volumeSlider
     */
    public void setVolumeSlider(VolumeSlider volumeSlider) {
		this.volumeSlider = volumeSlider;
	}
    
    /**
     * @param equalizer
     */
    public void setEqualizer(IEqualizer equalizer) {
		this.equalizer = equalizer;
	}
    
    /**
     * @param engines
     */
    public void setEngines(List<AbstractPlayerEngine> engines) {
		this.engines = engines;
	}
    
    @Override
    public void applicationStateChanged(IState newState) {
    	// Show advanced controls
    	getPlayerControlsController().getComponentControlled().showAdvancedPlayerControls(newState.isShowAdvancedPlayerControls());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#isEnginePlaying()
	 */
    @Override
	public boolean isEnginePlaying() {
        return playerEngine.isEnginePlaying();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#setVolume(int)
	 */
    @Override
	public void setVolume(int perCent) {
    	playerEngine.setVolume(perCent);
        getPlayerControlsController().setVolume(perCent);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#applyMuteState(boolean)
	 */
    @Override
	public void applyMuteState(boolean state) {
    	playerEngine.applyMuteState(state);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#applyNormalization()
	 */
    @Override
	public void applyNormalization() {
    	playerEngine.applyNormalization();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#supportsCapability(net.sourceforge.atunes.kernel.modules.player.PlayerEngineCapability)
	 */
    @Override
	public boolean supportsCapability(PlayerEngineCapability capability) {
        return playerEngine.supportsCapability(capability);
    }

    @Override
    public void applyEqualization(float[] values) {
    	playerEngine.applyEqualization(values);
    }

    /**
     * This method must be implemented by player engines. Transform values
     * retrieved from equalizer dialog to values for player engine
     * 
     * @param values
     * @return
     */
    float[] transformEqualizerValues(float[] values) {
        return playerEngine.transformEqualizerValues(values);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#playCurrentAudioObject(boolean)
	 */
    @Override
	public final void playCurrentAudioObject(boolean buttonPressed) {
    	playerEngine.playCurrentAudioObject(buttonPressed);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#stopCurrentAudioObject(boolean)
	 */
    @Override
	public final void stopCurrentAudioObject(boolean userStopped) {
    	playerEngine.stopCurrentAudioObject(userStopped);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#playPreviousAudioObject()
	 */
    @Override
	public final void playPreviousAudioObject() {
    	playerEngine.playPreviousAudioObject();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#playNextAudioObject()
	 */
    @Override
	public final void playNextAudioObject() {
    	playerEngine.playNextAudioObject(false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#seekCurrentAudioObject(long)
	 */
    @Override
	public final void seekCurrentAudioObject(long milliseconds) {
    	playerEngine.seekCurrentAudioObject(milliseconds);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#volumeDown()
	 */
    @Override
	public final void volumeDown() {
    	playerEngine.volumeDown();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#volumeUp()
	 */
    @Override
	public final void volumeUp() {
    	playerEngine.volumeUp();
    }

    @Override
    public void applicationFinish() {
        // Stop must be called explicitly to avoid playback after user closed app
        stopCurrentAudioObject(true);
        playerEngine.finishPlayer();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#getEqualizer()
	 */
    @Override
	public IEqualizer getEqualizer() {
        return equalizer;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#getAudioObject()
	 */
    @Override
	public IAudioObject getAudioObject() {
    	return playerEngine.getAudioObject();
    }

    /**
     * Initializes player engine
     */
    private void initPlayerEngine() {
        // Remove unsupported engines
    	// To do that create a clone of list to be able to remove from
    	List<AbstractPlayerEngine> availableEngines = new ArrayList<AbstractPlayerEngine>(engines);
        Iterator<AbstractPlayerEngine> it = availableEngines.iterator();
        while (it.hasNext()) {
        	AbstractPlayerEngine engine = it.next();
        	// Engines must be supported for given OS and available
            if (!getOsManager().isPlayerEngineSupported(engine) || !engine.isEngineAvailable()) {
                it.remove();
            }
        }

        if (!availableEngines.isEmpty()) {
            // Update engine names
            engineNames = new String[availableEngines.size()];
            for (int i = 0; i < availableEngines.size(); i++) {
                engineNames[i] = availableEngines.get(i).getEngineName();
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
                    selectedEngine = availableEngines.iterator().next().getEngineName();
                }
                // Update application state with this engine
                getState().setPlayerEngine(selectedEngine);
            }

            for (AbstractPlayerEngine engine : availableEngines) {
                if (engine.getEngineName().equals(selectedEngine)) {
                	playerEngine = engine;
                    Logger.info("Engine initialized : ", selectedEngine);
                    break;
                }
            }
        }
    }
    
    @Override
    public void allHandlersInitialized() {
        // Add volume behaviour
        volumeSlider.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                if (notches < 0) {
                	volumeSlider.setValue(volumeSlider.getValue() + 5);
                } else {
                	volumeSlider.setValue(volumeSlider.getValue() - 5);
                }

                volumeController.setVolume(volumeSlider.getValue());
            }
        });

        volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            	volumeController.setVolume(volumeSlider.getValue());
            }
        });
    	initialize();
    }
    
    @Override
    public int requestUserInteraction() {
    	return 1;
    }
    
    @Override
    public void doUserInteraction() {
        if (playerEngine instanceof VoidPlayerEngine) {
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
    	volumeController.setVolume(getState().getVolume(), false);

        // Mute
        applyMuteState(getState().isMuteEnabled());
    	
        // Initial playback state is stopped
        playerEngine.callPlaybackStateListeners(PlaybackState.STOPPED);

        if (getState().isPlayAtStartup()) {
            playCurrentAudioObject(true);
        }
        
        // Show advanced controls
        getPlayerControlsController().getComponentControlled().showAdvancedPlayerControls(getState().isShowAdvancedPlayerControls());

        // Init engine
        playerEngine.initializePlayerEngine();
    	
        // Add a shutdown hook to perform some actions before killing the JVM
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                Logger.debug("Final check for Zombie player engines");
                playerEngine.killPlayer();
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
    	if (playbackState == null || !playbackState.equals(newState)) {
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
	private void submitAudioObjectIfNecessary(PlaybackState newState, IAudioObject currentAudioObject) {
		if (isSubmissionNeeded(newState, currentAudioObject)) { 
			getBean(IWebServicesHandler.class).submit((ILocalAudioObject) currentAudioObject, getCurrentAudioObjectPlayedTime() / 1000);
			getBean(IStatisticsHandler.class).updateAudioObjectStatistics(currentAudioObject);
			playerEngine.setSubmissionState(SubmissionState.SUBMITTED);
		}
	}
	
	private boolean isSubmissionNeeded(PlaybackState newState, IAudioObject currentAudioObject) {
		if (isStateForSubmission(newState) && playerEngine.getSubmissionState() == SubmissionState.PENDING && currentAudioObject instanceof ILocalAudioObject) {
			return true;
		}
		return false;
	}
	
	private boolean isStateForSubmission(PlaybackState newState) {
		return newState == PlaybackState.PLAY_FINISHED || 
		       newState == PlaybackState.PLAY_INTERRUPTED || 
		       newState == PlaybackState.STOPPED;
	}

	/**
	 * @param newState
	 */
	private void stopPlayerEngineIfNecessary(PlaybackState newState) {
		if (newState == PlaybackState.STOPPED) {
			playerEngine.setCurrentAudioObjectPlayedTime(0);
			playerEngine.interruptPlayAudioObjectThread();
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
        return playerEngine.getCurrentAudioObjectPlayedTime();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.player.IPlayerHandler#getCurrentAudioObjectLength()
	 */
    @Override
	public long getCurrentAudioObjectLength() {
        return playerEngine.getCurrentAudioObjectLength();
    }
    
    /**
     * Gets the player controls controller.
     * 
     * @return the player controls controller
     */
    private PlayerControlsController getPlayerControlsController() {
        if (playerControlsController == null) {
            playerControlsController = new PlayerControlsController(getBean(IPlayerControlsPanel.class), getState(), this);
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
        if (playerEngine instanceof VoidPlayerEngine) {
        	manageNoPlayerEngine(getOsManager(), getFrame());
        }
	}
}
