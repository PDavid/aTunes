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

package net.sourceforge.atunes.model;


/**
 * Responsible of handling hotkeys
 * @author alex
 *
 */
public interface IHotkeyHandler extends IHandler {

	/**
	 * Checks if hotkeys are supported.
	 * 
	 * @return if hotkeys are supported
	 */
	public boolean areHotkeysSupported();

	/**
	 * Disables hotkeys.
	 */
	public void disableHotkeys();

	/**
	 * Enables hotkeys with a given configuration,
	 * 
	 * @param hc
	 *            the hotkeys configuration
	 */
	public void enableHotkeys(IHotkeysConfig hc);

	/**
	 * Returns the default hotkey configuration
	 * 
	 * @return the default hotkey configuration
	 */
	public IHotkeysConfig getDefaultHotkeysConfiguration();

	/**
	 * Returns the current hotkeys configuration
	 * 
	 * @return the current hotkeys configuration
	 */
	public IHotkeysConfig getHotkeysConfig();

	/**
	 * Called when a hotkey is pressed
	 * @param id
	 */
	public void onHotkey(final int id);

}