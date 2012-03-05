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

package net.sourceforge.atunes.kernel.modules.hotkeys;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.sourceforge.atunes.model.IHotkey;
import net.sourceforge.atunes.model.IHotkeyListener;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;

abstract class AbstractHotkeys {

    private IHotkeyListener hotkeyListener;

    protected AbstractHotkeys(IHotkeyListener hotkeyListener) {
        this.hotkeyListener = hotkeyListener;
    }

    /**
     * Tries to register a hotkey in the system
     * 
     * @param hotkey
     * @return <code>true</code> if hotkey was registered, <code>false</code>
     *         otherwise
     */
    public abstract boolean registerHotkey(IHotkey hotkey);

    public abstract void unregisterHotkey(IHotkey hotkey);

    public abstract void deactivate();

    public abstract void activate();

    public abstract void cleanUp();

    public static AbstractHotkeys createInstance(IHotkeyListener hotkeyListener, IOSManager osManager) {
        try {
        	Class<?> clazz = getHotkeysClass(osManager);
        	
        	if (clazz != null) {
                Constructor<?> constructor = clazz.getConstructor(IHotkeyListener.class);
                return (AbstractHotkeys) constructor.newInstance(hotkeyListener);
            } else {
                return null;
            }
        } catch (SecurityException e) {
            Logger.info("No hotkeys supported", e.getMessage());
            return null;
		} catch (NoSuchMethodException e) {
            Logger.info("No hotkeys supported", e.getMessage());
            return null;
		} catch (IllegalArgumentException e) {
            Logger.info("No hotkeys supported", e.getMessage());
            return null;
		} catch (InstantiationException e) {
            Logger.info("No hotkeys supported", e.getMessage());
            return null;
		} catch (IllegalAccessException e) {
            Logger.info("No hotkeys supported", e.getMessage());
            return null;
		} catch (InvocationTargetException e) {
            Logger.info("No hotkeys supported", e.getMessage());
            return null;
		}
    }

	/**
	 * @param osManager
	 * @return
	 */
	private static Class<?> getHotkeysClass(IOSManager osManager) {
		if (osManager.isLinux()) {
			return X11Hotkeys.class;
		} else if (osManager.isWindows()) {
			return WindowsHotkeys.isSupported(osManager) ? WindowsHotkeys.class : null;
		}
		return null;
	}

    protected IHotkeyListener getHotkeyListener() {
        return hotkeyListener;
    }
}
