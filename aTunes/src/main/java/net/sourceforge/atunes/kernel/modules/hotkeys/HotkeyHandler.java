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

package net.sourceforge.atunes.kernel.modules.hotkeys;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.kernel.actions.PlayAction;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IHotkey;
import net.sourceforge.atunes.model.IHotkeyHandler;
import net.sourceforge.atunes.model.IHotkeyListener;
import net.sourceforge.atunes.model.IHotkeysConfig;
import net.sourceforge.atunes.model.INotificationsHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Handler for global hotkeys keys.
 */
public final class HotkeyHandler extends AbstractHandler implements
	IHotkeyListener, IHotkeyHandler {

    private boolean supported;
    private boolean enabled;
    private AbstractHotkeys hotkeys;
    private IHotkeysConfig hotkeysConfig;
    private IStateCore stateCore;

    private IDialogFactory dialogFactory;

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(final IDialogFactory dialogFactory) {
	this.dialogFactory = dialogFactory;
    }

    /**
     * @param stateCore
     */
    public void setStateCore(final IStateCore stateCore) {
	this.stateCore = stateCore;
    }

    @Override
    public void deferredInitialization() {
	hotkeys = AbstractHotkeys.createInstance(this,
		getBean(IOSManager.class));
	IHotkeysConfig hc = stateCore.getHotkeysConfig();
	hotkeysConfig = hc != null ? hc : new DefaultHotkeysConfig();
	if (hotkeys != null) {
	    supported = true;
	} else {
	    supported = false;
	    Logger.info("Hotkeys are not supported");
	}

	if (stateCore.isEnableHotkeys()) {
	    enableHotkeys(stateCore.getHotkeysConfig());
	} else {
	    disableHotkeys();
	}
    }

    @Override
    public boolean areHotkeysSupported() {
	return supported;
    }

    @Override
    public void disableHotkeys() {
	if (areHotkeysSupported() && enabled) {
	    for (IHotkey entry : hotkeysConfig) {
		hotkeys.unregisterHotkey(entry);
	    }
	    hotkeys.deactivate();
	    enabled = false;
	}
    }

    @Override
    public void enableHotkeys(final IHotkeysConfig hc) {
	disableHotkeys();
	if (hc == null) {
	    this.hotkeysConfig = new DefaultHotkeysConfig();
	} else {
	    this.hotkeysConfig = hc;
	}
	registerAndActivateHotkeys();
    }

    @Override
    public IHotkeysConfig getDefaultHotkeysConfiguration() {
	HotkeysConfig config = new HotkeysConfig();
	for (IHotkey hotkey : new DefaultHotkeysConfig()) {
	    config.putHotkey(new Hotkey(hotkey.getId(), hotkey.getMod(), hotkey
		    .getKey(), hotkey.getDescription()));
	}
	return config;
    }

    private void registerAndActivateHotkeys() {
	if (areHotkeysSupported()) {
	    boolean allHotKeysRegistered = true;
	    for (IHotkey entry : hotkeysConfig) {
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
		for (IHotkey entry : hotkeysConfig) {
		    hotkeys.unregisterHotkey(entry);
		}

		// Disable hotkeys
		stateCore.setEnableHotkeys(false);

		// Show an error message
		dialogFactory
			.newDialog(IErrorDialog.class)
			.showErrorDialog(
				I18nUtils
					.getString("HOTKEYS_ACTIVATION_ERROR_MESSAGE"));
		Logger.error("Hotkeys were not activated successfully");
	    }
	}
    }

    /**
     * Clean up hotkey resources.
     */
    @Override
    public void applicationFinish() {
	if (areHotkeysSupported()) {
	    hotkeys.cleanUp();
	}
    }

    @Override
    public IHotkeysConfig getHotkeysConfig() {
	HotkeysConfig config = new HotkeysConfig();
	if (hotkeysConfig != null) {
	    for (IHotkey hotkey : hotkeysConfig) {
		config.putHotkey(new Hotkey(hotkey.getId(), hotkey.getMod(),
			hotkey.getKey(), hotkey.getDescription()));
	    }
	}
	return config;
    }

    @Override
    public void onHotkey(final int id) {
	Logger.debug("Hotkey ", id);
	switch (id) {
	case HotkeyConstants.HOTKEY_NEXT: {
	    getBean(IPlayerHandler.class).playNextAudioObject();
	    break;
	}
	case HotkeyConstants.HOTKEY_PREVIOUS: {
	    getBean(IPlayerHandler.class).playPreviousAudioObject();
	    break;
	}
	case HotkeyConstants.HOTKEY_VOLUME_UP: {
	    getBean(IPlayerHandler.class).volumeUp();
	    break;
	}
	case HotkeyConstants.HOTKEY_VOLUME_DOWN: {
	    getBean(IPlayerHandler.class).volumeDown();
	    break;
	}
	case HotkeyConstants.HOTKEY_PAUSE: {
	    getBean(PlayAction.class).actionPerformed(null);
	    break;
	}
	case HotkeyConstants.HOTKEY_STOP: {
	    getBean(IPlayerHandler.class).stopCurrentAudioObject(true);
	    break;
	}
	case HotkeyConstants.HOTKEY_TOGGLE_WINDOW_VISIBILITY: {
	    getBean(IUIHandler.class).toggleWindowVisibility();
	    break;
	}
	case HotkeyConstants.HOTKEY_MUTE: {
	    getBean(MuteAction.class).switchState();
	    break;
	}
	case HotkeyConstants.HOTKEY_SHOW_OSD: {
	    getBean(INotificationsHandler.class).showNotification(
		    getBean(IPlayListHandler.class)
			    .getCurrentAudioObjectFromCurrentPlayList());
	    break;
	}
	default: {
	    Logger.error(StringUtils.getString("Unknown hotkey id: ", id));
	}
	}
    }

    @Override
    public void applicationStateChanged() {
	if (stateCore.isEnableHotkeys()) {
	    enableHotkeys(stateCore.getHotkeysConfig());
	} else {
	    disableHotkeys();
	}
    }

}
