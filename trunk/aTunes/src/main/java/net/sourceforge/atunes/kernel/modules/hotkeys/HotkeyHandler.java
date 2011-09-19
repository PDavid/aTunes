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

package net.sourceforge.atunes.kernel.modules.hotkeys;

import java.awt.event.InputEvent;
import java.util.List;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.kernel.modules.notify.NotificationsHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Handler for global hotkeys keys.
 */
public final class HotkeyHandler extends AbstractHandler implements HotkeyListener {

    private static HotkeyHandler instance;

    private static final int RIGHT_ARROW = 39;
    private static final int LEFT_ARROW = 37;
    private static final int UP_ARROW = 38;
    private static final int DOWN_ARROW = 40;

    public static final int HOTKEY_NEXT = 1;
    public static final int HOTKEY_PREVIOUS = 2;
    public static final int HOTKEY_VOLUME_UP = 3;
    public static final int HOTKEY_VOLUME_DOWN = 4;
    public static final int HOTKEY_PAUSE = 5;
    public static final int HOTKEY_STOP = 6;
    public static final int HOTKEY_TOGGLE_WINDOW_VISIBILITY = 7;
    public static final int HOTKEY_MUTE = 8;
    public static final int HOTKEY_SHOW_OSD = 9;

    private boolean supported;
    private boolean enabled;
    private AbstractHotkeys hotkeys;
    private HotkeysConfig hotkeysConfig;

    private static final HotkeysConfig DEFAULT_HOTKEYS_CONFIG;
    static {
        // Create Hotkey objects
        DEFAULT_HOTKEYS_CONFIG = new HotkeysConfig();
        DEFAULT_HOTKEYS_CONFIG.putHotkey(new Hotkey(HOTKEY_NEXT, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, RIGHT_ARROW, I18nUtils.getString("NEXT")));
        DEFAULT_HOTKEYS_CONFIG.putHotkey(new Hotkey(HOTKEY_PREVIOUS, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, LEFT_ARROW, I18nUtils.getString("PREVIOUS")));
        DEFAULT_HOTKEYS_CONFIG.putHotkey(new Hotkey(HOTKEY_VOLUME_UP, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, UP_ARROW, I18nUtils.getString("VOLUME_UP")));
        DEFAULT_HOTKEYS_CONFIG.putHotkey(new Hotkey(HOTKEY_VOLUME_DOWN, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, DOWN_ARROW, I18nUtils.getString("VOLUME_DOWN")));
        DEFAULT_HOTKEYS_CONFIG.putHotkey(new Hotkey(HOTKEY_PAUSE, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'P', I18nUtils.getString("PAUSE")));
        DEFAULT_HOTKEYS_CONFIG.putHotkey(new Hotkey(HOTKEY_STOP, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'S', I18nUtils.getString("STOP")));
        DEFAULT_HOTKEYS_CONFIG.putHotkey(new Hotkey(HOTKEY_TOGGLE_WINDOW_VISIBILITY, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'W', I18nUtils
                .getString("TOGGLE_WINDOW_VISIBILITY")));
        DEFAULT_HOTKEYS_CONFIG.putHotkey(new Hotkey(HOTKEY_MUTE, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'M', I18nUtils.getString("MUTE")));
        DEFAULT_HOTKEYS_CONFIG.putHotkey(new Hotkey(HOTKEY_SHOW_OSD, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'I', I18nUtils.getString("SHOW_OSD")));
    }

    /**
     * Instantiates a new hotkey handler.
     */
    private HotkeyHandler() {
    }

    @Override
    protected void initHandler() {
        hotkeys = AbstractHotkeys.createInstance(this);
        HotkeysConfig hc = getState().getHotkeysConfig();
        hotkeysConfig = hc != null ? hc : DEFAULT_HOTKEYS_CONFIG;
        if (hotkeys != null) {
            supported = true;
        } else {
            supported = false;
            Logger.info("Hotkeys are not supported");
        }
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
    }
    
    @Override
    public void allHandlersInitialized() {
        // Hotkeys
        if (getState().isEnableHotkeys()) {
            enableHotkeys(getState().getHotkeysConfig());
        } else {
            disableHotkeys();
        }
    }

    /**
     * Checks if hotkeys are supported.
     * 
     * @return if hotkeys are supported
     */
    public boolean areHotkeysSupported() {
        return supported;
    }

    /**
     * Gets the single instance of HotkeyHandler.
     * 
     * @return single instance of HotkeyHandler
     */
    public static synchronized HotkeyHandler getInstance() {
        if (instance == null) {
            instance = new HotkeyHandler();
        }
        return instance;
    }

    /**
     * Disables hotkeys.
     */
    public void disableHotkeys() {
        if (areHotkeysSupported() && enabled) {
            for (Hotkey entry : hotkeysConfig) {
                hotkeys.unregisterHotkey(entry);
            }
            hotkeys.deactivate();
            enabled = false;
        }
    }

    /**
     * Enables hotkeys with a given configuration,
     * 
     * @param hc
     *            the hotkeys configuration
     */
    public void enableHotkeys(HotkeysConfig hc) {
        disableHotkeys();
        if (hc == null) {
            this.hotkeysConfig = DEFAULT_HOTKEYS_CONFIG;
        } else {
            this.hotkeysConfig = hc;
        }
        registerAndActivateHotkeys();
    }

    /**
     * Returns the default hotkey configuration
     * 
     * @return the default hotkey configuration
     */
    public HotkeysConfig getDefaultHotkeysConfiguration() {
        HotkeysConfig config = new HotkeysConfig();
        for (Hotkey hotkey : DEFAULT_HOTKEYS_CONFIG) {
            config.putHotkey(new Hotkey(hotkey.getId(), hotkey.getMod(), hotkey.getKey(), hotkey.getDescription()));
        }
        return config;
    }

    private void registerAndActivateHotkeys() {
        if (areHotkeysSupported()) {
            boolean allHotKeysRegistered = true;
            for (Hotkey entry : hotkeysConfig) {
                if (!hotkeys.registerHotkey(entry)) {
                    allHotKeysRegistered = false;
                    break;
                }
            }
            // If all hotkeys were registered then activate them
            if (allHotKeysRegistered) {
                hotkeys.activate();
                enabled = true;
                Logger.info("Hotkeys activated successfully");
            } else {
                // If not, then unregister hotkeys
                for (Hotkey entry : hotkeysConfig) {
                    hotkeys.unregisterHotkey(entry);
                }

                // Disable hotkeys
                getState().setEnableHotkeys(false);

                // Show an error message
                getBean(IErrorDialog.class).showErrorDialog(getFrame(), I18nUtils.getString("HOTKEYS_ACTIVATION_ERROR_MESSAGE"));
                Logger.error("Hotkeys were not activated successfully");
            }
        }
    }

    /**
     * Clean up hotkey ressources.
     */
    public void applicationFinish() {
        if (areHotkeysSupported()) {
            hotkeys.cleanUp();
        }
    }

    /**
     * Returns the current hotkeys configuration
     * 
     * @return the current hotkeys configuration
     */
    public HotkeysConfig getHotkeysConfig() {
        HotkeysConfig config = new HotkeysConfig();
        for (Hotkey hotkey : hotkeysConfig) {
            config.putHotkey(new Hotkey(hotkey.getId(), hotkey.getMod(), hotkey.getKey(), hotkey.getDescription()));
        }
        return config;
    }

    @Override
    public void onHotKey(final int id) {
        Logger.debug("Hotkey ", id);
        switch (id) {
        case HOTKEY_NEXT: {
            PlayerHandler.getInstance().playNextAudioObject();
            break;
        }
        case HOTKEY_PREVIOUS: {
            PlayerHandler.getInstance().playPreviousAudioObject();
            break;
        }
        case HOTKEY_VOLUME_UP: {
            PlayerHandler.getInstance().volumeUp();
            break;
        }
        case HOTKEY_VOLUME_DOWN: {
            PlayerHandler.getInstance().volumeDown();
            break;
        }
        case HOTKEY_PAUSE: {
            PlayerHandler.getInstance().playCurrentAudioObject(true);
            break;
        }
        case HOTKEY_STOP: {
            PlayerHandler.getInstance().stopCurrentAudioObject(true);
            break;
        }
        case HOTKEY_TOGGLE_WINDOW_VISIBILITY: {
            getBean(IUIHandler.class).toggleWindowVisibility();
            break;
        }
        case HOTKEY_MUTE: {
            Actions.getAction(MuteAction.class).actionPerformed(null);
            break;
        }
        case HOTKEY_SHOW_OSD: {
            NotificationsHandler.getInstance().showNotification(getBean(IPlayListHandler.class).getCurrentAudioObjectFromCurrentPlayList());
            break;
        }
        default: {
            Logger.error(StringUtils.getString("Unknown hotkey id: ", id));
        }
        }
    }

    @Override
    public void applicationStateChanged(IState newState) {
        if (newState.isEnableHotkeys()) {
            enableHotkeys(newState.getHotkeysConfig());
        } else {
            disableHotkeys();
        }
    }
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

}
