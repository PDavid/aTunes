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

import net.sourceforge.atunes.kernel.OsManager;

import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.melloware.jintellitype.JIntellitypeConstants;

public class WindowsHotkeys extends AbstractHotkeys implements com.melloware.jintellitype.HotkeyListener, IntellitypeListener {

    public WindowsHotkeys(HotkeyListener hotkeyListener) {
        super(hotkeyListener);
    }

    @Override
    public boolean registerHotkey(Hotkey hotkey) {
        JIntellitype.getInstance().registerSwingHotKey(hotkey.getId(), hotkey.getMod(), hotkey.getKey());
        return true;
    }

    @Override
    public void unregisterHotkey(Hotkey hotkey) {
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
    public void onHotKey(int arg0) {
        getHotkeyListener().onHotKey(arg0);
    }

    @Override
    public void onIntellitype(int command) {
        switch (command) {
        case JIntellitypeConstants.APPCOMMAND_MEDIA_NEXTTRACK: {
            getHotkeyListener().onHotKey(HotkeyHandler.HOTKEY_NEXT);
            break;
        }
        case JIntellitypeConstants.APPCOMMAND_MEDIA_PREVIOUSTRACK: {
            getHotkeyListener().onHotKey(HotkeyHandler.HOTKEY_PREVIOUS);
            break;
        }
        case JIntellitypeConstants.APPCOMMAND_MEDIA_PLAY_PAUSE: {
            getHotkeyListener().onHotKey(HotkeyHandler.HOTKEY_PAUSE);
            break;
        }
        case JIntellitypeConstants.APPCOMMAND_MEDIA_STOP: {
            getHotkeyListener().onHotKey(HotkeyHandler.HOTKEY_STOP);
            break;
        }
        case JIntellitypeConstants.APPCOMMAND_VOLUME_UP: {
            getHotkeyListener().onHotKey(HotkeyHandler.HOTKEY_VOLUME_UP);
            break;
        }
        case JIntellitypeConstants.APPCOMMAND_VOLUME_DOWN: {
            getHotkeyListener().onHotKey(HotkeyHandler.HOTKEY_VOLUME_DOWN);
            break;
        }
        case JIntellitypeConstants.APPCOMMAND_VOLUME_MUTE: {
            getHotkeyListener().onHotKey(HotkeyHandler.HOTKEY_MUTE);
            break;
        }
        default: {
            // Nothing to do
        }
        }
    }

    public static boolean isSupported() {
    	if(OsManager.is64Bit()) {
    		JIntellitype.setLibraryLocation("JIntellitype64.dll");
    	}
        return JIntellitype.isJIntellitypeSupported();
    }
}
