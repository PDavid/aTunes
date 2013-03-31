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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.sourceforge.atunes.model.IHotkey;
import net.sourceforge.atunes.model.IHotkeyListener;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;

/**
 * Abstract implementation of hotkeys
 * 
 * @author alex
 * 
 */
abstract class AbstractHotkeys {

	private static final String NO_HOTKEYS_SUPPORTED = "No hotkeys supported";
	private final IHotkeyListener hotkeyListener;

	/**
	 * @param hotkeyListener
	 */
	protected AbstractHotkeys(final IHotkeyListener hotkeyListener) {
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

	/**
	 * Unregisters a hotkey in the system
	 * 
	 * @param hotkey
	 */
	public abstract void unregisterHotkey(IHotkey hotkey);

	/**
	 * Deactivates hotkeys
	 */
	public abstract void deactivate();

	/**
	 * Activates hotkeys
	 */
	public abstract void activate();

	/**
	 * Frees any resource needed by hotkeys
	 */
	public abstract void cleanUp();

	/**
	 * Creates an AbstractHotkeys instance
	 * 
	 * @param hotkeyListener
	 * @param osManager
	 * @return
	 */
	public static AbstractHotkeys createInstance(
			final IHotkeyListener hotkeyListener, final IOSManager osManager) {
		try {
			Class<?> clazz = getHotkeysClass(osManager);

			if (clazz != null) {
				Constructor<?> constructor = clazz
						.getConstructor(IHotkeyListener.class);
				return (AbstractHotkeys) constructor
						.newInstance(hotkeyListener);
			} else {
				return null;
			}
		} catch (SecurityException e) {
			Logger.info(NO_HOTKEYS_SUPPORTED, e.getMessage());
			return null;
		} catch (NoSuchMethodException e) {
			Logger.info(NO_HOTKEYS_SUPPORTED, e.getMessage());
			return null;
		} catch (IllegalArgumentException e) {
			Logger.info(NO_HOTKEYS_SUPPORTED, e.getMessage());
			return null;
		} catch (InstantiationException e) {
			Logger.info(NO_HOTKEYS_SUPPORTED, e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			Logger.info(NO_HOTKEYS_SUPPORTED, e.getMessage());
			return null;
		} catch (InvocationTargetException e) {
			Logger.info(NO_HOTKEYS_SUPPORTED, e.getMessage());
			return null;
		}
	}

	/**
	 * @param osManager
	 * @return
	 */
	private static Class<?> getHotkeysClass(final IOSManager osManager) {
		if (osManager.isLinux()) {
			return X11Hotkeys.class;
		} else if (osManager.isWindows()) {
			return WindowsHotkeys.isSupported(osManager) ? WindowsHotkeys.class
					: null;
		}
		return null;
	}

	/**
	 * @return hotkey listener
	 */
	protected IHotkeyListener getHotkeyListener() {
		return this.hotkeyListener;
	}
}
