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

import java.awt.event.InputEvent;

import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Default hotkeys configuration
 * 
 * @author alex
 * 
 */
public class DefaultHotkeysConfig extends HotkeysConfig {

    /**
	 * 
	 */
    private static final long serialVersionUID = -3940909569860041452L;

    /**
     * Default constructor
     */
    public DefaultHotkeysConfig() {
	super();
	putHotkey(new Hotkey(HotkeyConstants.HOTKEY_NEXT,
		InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK,
		HotkeyConstants.RIGHT_ARROW, I18nUtils.getString("NEXT")));
	putHotkey(new Hotkey(HotkeyConstants.HOTKEY_PREVIOUS,
		InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK,
		HotkeyConstants.LEFT_ARROW, I18nUtils.getString("PREVIOUS")));
	putHotkey(new Hotkey(HotkeyConstants.HOTKEY_VOLUME_UP,
		InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK,
		HotkeyConstants.UP_ARROW, I18nUtils.getString("VOLUME_UP")));
	putHotkey(new Hotkey(HotkeyConstants.HOTKEY_VOLUME_DOWN,
		InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK,
		HotkeyConstants.DOWN_ARROW, I18nUtils.getString("VOLUME_DOWN")));
	putHotkey(new Hotkey(HotkeyConstants.HOTKEY_PAUSE,
		InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'P',
		I18nUtils.getString("PAUSE")));
	putHotkey(new Hotkey(HotkeyConstants.HOTKEY_STOP,
		InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'S',
		I18nUtils.getString("STOP")));
	putHotkey(new Hotkey(HotkeyConstants.HOTKEY_TOGGLE_WINDOW_VISIBILITY,
		InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'W',
		I18nUtils.getString("TOGGLE_WINDOW_VISIBILITY")));
	putHotkey(new Hotkey(HotkeyConstants.HOTKEY_MUTE,
		InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'M',
		I18nUtils.getString("MUTE")));
	putHotkey(new Hotkey(HotkeyConstants.HOTKEY_SHOW_OSD,
		InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'I',
		I18nUtils.getString("SHOW_OSD")));
    }

}
