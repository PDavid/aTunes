/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.kernel.ApplicationFinishListener;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.notify.NotifyHandler;
import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.xine.XineEngine;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.lang.ArrayUtils;
import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginListener;
import org.commonjukebox.plugins.PluginSystemException;

/**
 * This class is resopnsible for handling the player engine.
 */
public final class PlayerHandler implements ApplicationFinishListener, PluginListener {

    /**
     * The logger used in player handler
     */
    protected static Logger logger = new Logger();

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
     */
    private PlayerEngine playerEngine;

    /**
     * Instantiates a new player handler.
     */
    private PlayerHandler() {
    }

    /**
     * Checks if engine is currently playing (<code>true</code>) or not (
     * <code>false</code>)
     * 
     * @return <code>true</code> if engine is currently playing
     */
    public boolean isEnginePlaying() {
        return playerEngine.isEnginePlaying();
    }

    /**
     * This method must be implemented by player engines. Applies volume value
     * in player engine
     * 
     * @param perCent
     *            0-100
     */
    public void setVolume(int perCent) {
        playerEngine.setVolume(perCent);
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
        playerEngine.applyMuteState(state);
    }

    /**
     * This method must be implemented by player engines. Method to apply
     * normalization in player engine
     * 
     * @param values
     */
    public void applyNormalization() {
        playerEngine.applyNormalization();
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
        return playerEngine.supportsCapability(capability);
    }

    /**
     * This method must be implemented by player engines. Method to apply
     * equalizer values in player engine
     * 
     * @param values
     */
    void applyEqualization(float[] values) {
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

    /**
     * Adds a new playback state listener
     */
    public void addPlaybackStateListener(PlaybackStateListener listener) {
        playerEngine.addPlaybackStateListener(listener);
    }

    /**
     * Removes a playback state listener
     * 
     * @param listener
     */
    public void removePlaybackStateListener(PlaybackStateListener listener) {
        playerEngine.removePlaybackStateListener(listener);
    }

    /**
     * Starts playing current audio object from play list
     * 
     * @param buttonPressed
     *            TODO: Add more javadoc
     */
    public final void playCurrentAudioObject(boolean buttonPressed) {
        playerEngine.playCurrentAudioObject(buttonPressed);
    }

    /**
     * Stops playing current audio object
     * 
     * @param userStopped
     *            <code>true</code> if user has stopped playback
     * 
     */
    public final void stopCurrentAudioObject(boolean userStopped) {
        playerEngine.stopCurrentAudioObject(userStopped);
    }

    /**
     * Starts playing previous audio object from play list
     */
    public final void playPreviousAudioObject() {
        playerEngine.playPreviousAudioObject();
    }

    /**
     * Starts playing next audio object from play list
     * 
     * @param autoNext
     *            <code>true</code> if this method is called because current
     *            audio object has finished, <code>false</code> if this method
     *            is called because user has pressed the "NEXT" button
     * 
     */
    public final void playNextAudioObject(boolean autoNext) {
        playerEngine.playNextAudioObject(autoNext);
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
        playerEngine.seekCurrentAudioObject(position);
    }

    /**
     * Lower volume
     */
    public final void volumeDown() {
        playerEngine.volumeDown();
    }

    /**
     * Raise volume
     */
    public final void volumeUp() {
        playerEngine.volumeUp();
    }

    @Override
    public void applicationFinish() {
        playerEngine.finishPlayer();
    }

    /**
     * Returns the equalizer of this player engine
     * 
     * @return the equalizer of this player engine
     */
    public Equalizer getEqualizer() {
        return playerEngine.getEqualizer();
    }

    /**
     * Returns the list of player engines
     * 
     * @return list with player engines
     */
    private static List<PlayerEngine> getEngines() {
        List<PlayerEngine> result = new ArrayList<PlayerEngine>();
        result.add(new MPlayerEngine());
        result.add(new XineEngine());
        //result.add(new VlcPlayerEngine());
        //result.add(new GStreamerEngine());
        return result;
    }

    public AudioObject getAudioObject() {
        return playerEngine.getAudioObject();
    }

    /**
     * Gets the single instance of PlayerHandler. This method returns player
     * engine configured by user or default if it's available on the system
     * 
     * @return single instance of PlayerHandler
     */
    public static final synchronized PlayerHandler getInstance() {
        if (instance == null) {
            instance = new PlayerHandler();
            // Get engines list
            List<PlayerEngine> engines = getEngines();

            // Remove unsupported engines
            Iterator<PlayerEngine> it = engines.iterator();
            while (it.hasNext()) {
                if (!it.next().isEngineAvailable()) {
                    it.remove();
                }
            }

            // Update engine names
            engineNames = new String[engines.size()];
            for (int i = 0; i < engines.size(); i++) {
                engineNames[i] = engines.get(i).getEngineName();
            }

            Arrays.sort(engineNames);

            logger.info(LogCategories.PLAYER, "List of availables engines : " + ArrayUtils.toString(engineNames));

            if (engines.isEmpty()) {
                handlePlayerError(new IllegalStateException(LanguageTool.getString("NO_PLAYER_ENGINE")));
            } else {
                // Get engine of application state (default or selected by user)
                String selectedEngine = ApplicationState.getInstance().getPlayerEngine();

                // If selected engine is not available then try default engine or another one
                if (!ArrayUtils.contains(engineNames, selectedEngine)) {

                    logger.info(LogCategories.PLAYER, selectedEngine + " is not availaible");
                    if (ArrayUtils.contains(engineNames, DEFAULT_ENGINE)) {
                        selectedEngine = DEFAULT_ENGINE;
                    } else {
                        // If default engine is not available, then get the first engine of map returned
                        selectedEngine = engines.iterator().next().getEngineName();
                    }
                    // Update application state with this engine
                    ApplicationState.getInstance().setPlayerEngine(selectedEngine);
                }

                for (PlayerEngine engine : engines) {
                    if (engine.getEngineName().equals(selectedEngine)) {
                        instance.playerEngine = engine;
                        logger.info(LogCategories.PLAYER, "Engine initialized : " + selectedEngine);
                    }
                }

                if (instance.playerEngine == null) {
                    handlePlayerError(new IllegalStateException(LanguageTool.getString("NO_PLAYER_ENGINE")));
                }

                // Init engine
                instance.playerEngine.initializePlayerEngine();
                Kernel.getInstance().addFinishListener(instance);

                // Add core playback listeners
                instance.playerEngine.addPlaybackStateListener(instance.playerEngine);
                instance.playerEngine.addPlaybackStateListener(VisualHandler.getInstance());
                instance.playerEngine.addPlaybackStateListener(NotifyHandler.getInstance());

                // Initial playback state is stopped
                instance.playerEngine.callPlaybackStateListeners(PlaybackState.STOPPED);
            }

            // Add a shutdown hook to perform some actions before killing the JVM
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                @Override
                public void run() {
                    logger.debug(LogCategories.SHUTDOWN_HOOK, "Final check for Zombie player engines");
                    instance.playerEngine.killPlayer();
                    logger.debug(LogCategories.SHUTDOWN_HOOK, "Closing player ...");
                }

            }));
        }
        return instance;
    }

    private static void handlePlayerError(Throwable t) {
        logger.error(LogCategories.PLAYER, StringUtils.getString("Player Error: ", t));
        logger.error(LogCategories.PLAYER, t);
        VisualHandler.getInstance().showErrorDialog(t.getMessage());
    }

    /**
     * Return list of engine names as configured in settings file This method is
     * mainly designed to be used in preferences window to select a player
     * engine by its name
     * 
     * @return list of engine names
     */
    public final String[] getEngineNames() {
        return engineNames != null ? Arrays.copyOf(engineNames, engineNames.length) : null;
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            PlaybackStateListener listener = (PlaybackStateListener) plugin.getInstance();
            addPlaybackStateListener(listener);
        } catch (PluginSystemException e) {
            logger.error(LogCategories.PLUGINS, e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        logger.info(LogCategories.PLUGINS, StringUtils.getString("Plugin deactivated: ", plugin.getName(), " (", plugin.getClassName(),")"));
        for (Plugin createdInstance : createdInstances) {
            PlayerHandler.getInstance().removePlaybackStateListener((PlaybackStateListener) createdInstance);
        }
    }


}
