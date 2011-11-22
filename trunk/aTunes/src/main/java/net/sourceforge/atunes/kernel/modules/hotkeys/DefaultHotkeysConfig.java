package net.sourceforge.atunes.kernel.modules.hotkeys;

import java.awt.event.InputEvent;

import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Default hotkeys configuration
 * @author alex
 *
 */
public class DefaultHotkeysConfig extends HotkeysConfig {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3940909569860041452L;

	public DefaultHotkeysConfig() {
		super();
        putHotkey(new Hotkey(HotkeyConstants.HOTKEY_NEXT, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, HotkeyConstants.RIGHT_ARROW, I18nUtils.getString("NEXT")));
        putHotkey(new Hotkey(HotkeyConstants.HOTKEY_PREVIOUS, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, HotkeyConstants.LEFT_ARROW, I18nUtils.getString("PREVIOUS")));
        putHotkey(new Hotkey(HotkeyConstants.HOTKEY_VOLUME_UP, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, HotkeyConstants.UP_ARROW, I18nUtils.getString("VOLUME_UP")));
        putHotkey(new Hotkey(HotkeyConstants.HOTKEY_VOLUME_DOWN, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, HotkeyConstants.DOWN_ARROW, I18nUtils.getString("VOLUME_DOWN")));
        putHotkey(new Hotkey(HotkeyConstants.HOTKEY_PAUSE, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'P', I18nUtils.getString("PAUSE")));
        putHotkey(new Hotkey(HotkeyConstants.HOTKEY_STOP, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'S', I18nUtils.getString("STOP")));
        putHotkey(new Hotkey(HotkeyConstants.HOTKEY_TOGGLE_WINDOW_VISIBILITY, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'W', I18nUtils.getString("TOGGLE_WINDOW_VISIBILITY")));
        putHotkey(new Hotkey(HotkeyConstants.HOTKEY_MUTE, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'M', I18nUtils.getString("MUTE")));
        putHotkey(new Hotkey(HotkeyConstants.HOTKEY_SHOW_OSD, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK, 'I', I18nUtils.getString("SHOW_OSD")));
	}

}
