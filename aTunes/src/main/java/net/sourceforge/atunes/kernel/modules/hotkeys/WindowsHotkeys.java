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

import net.sourceforge.atunes.model.IHotkey;
import net.sourceforge.atunes.model.IHotkeyListener;
import net.sourceforge.atunes.model.IOSManager;

import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

/**
 * Resposible of handle Windows hotkeys
 * 
 * @author alex
 * 
 */
public class WindowsHotkeys extends AbstractHotkeys implements
	com.melloware.jintellitype.HotkeyListener, IntellitypeListener {

    /**
     * @param hotkeyListener
     */
    public WindowsHotkeys(final IHotkeyListener hotkeyListener) {
	super(hotkeyListener);
    }

    @Override
    public boolean registerHotkey(final IHotkey hotkey) {
	JIntellitype.getInstance().registerSwingHotKey(hotkey.getId(),
		hotkey.getMod(), hotkey.getKey());
	return true;
    }

    @Override
    public void unregisterHotkey(final IHotkey hotkey) {
	JIntellitype.getInstance().unregisterHotKey(hotkey.getId());
    }

    @Override
    public void cleanUp() {
	JIntellitype.getInstance().cleanUp();
    }

    @Override
    public void activate() {
	JIntellitype.getInstance().addHotKeyListener(this);
	JIntellitype.getInstance().addIntellitypeListener(this);
    }

    @Override
    public void deactivate() {
	JIntellitype.getInstance().removeHotKeyListener(this);
	JIntellitype.getInstance().removeIntellitypeListener(this);
    }

    @Override
    public void onHotKey(final int arg0) {
	getHotkeyListener().onHotkey(arg0);
    }

    @Override
    public void onIntellitype(final int command) {
	switch (command) {
	case JIntellitypeConstants.APPCOMMAND_MEDIA_NEXTTRACK: {
	    getHotkeyListener().onHotkey(HotkeyConstants.HOTKEY_NEXT);
	    break;
	}
	case JIntellitypeConstants.APPCOMMAND_MEDIA_PREVIOUSTRACK: {
	    getHotkeyListener().onHotkey(HotkeyConstants.HOTKEY_PREVIOUS);
	    break;
	}
	case JIntellitypeConstants.APPCOMMAND_MEDIA_PLAY_PAUSE: {
	    getHotkeyListener().onHotkey(HotkeyConstants.HOTKEY_PAUSE);
	    break;
	}
	case JIntellitypeConstants.APPCOMMAND_MEDIA_STOP: {
	    getHotkeyListener().onHotkey(HotkeyConstants.HOTKEY_STOP);
	    break;
	}
	case JIntellitypeConstants.APPCOMMAND_VOLUME_UP: {
	    getHotkeyListener().onHotkey(HotkeyConstants.HOTKEY_VOLUME_UP);
	    break;
	}
	case JIntellitypeConstants.APPCOMMAND_VOLUME_DOWN: {
	    getHotkeyListener().onHotkey(HotkeyConstants.HOTKEY_VOLUME_DOWN);
	    break;
	}
	case JIntellitypeConstants.APPCOMMAND_VOLUME_MUTE: {
	    getHotkeyListener().onHotkey(HotkeyConstants.HOTKEY_MUTE);
	    break;
	}
	default: {
	    // Nothing to do
	}
	}
    }

    /**
     * Test if operating system is supported
     * 
     * @param osManager
     * @return
     */
    public static boolean isSupported(final IOSManager osManager) {
	if (osManager.is64Bit()) {
	    JIntellitype.setLibraryLocation("JIntellitype64.dll");
	}
	return JIntellitype.isJIntellitypeSupported();
    }
}
