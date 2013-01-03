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

import javax.swing.SwingUtilities;

import jxgrabkey.HotkeyConflictException;
import jxgrabkey.JXGrabKey;
import net.sourceforge.atunes.model.IHotkey;
import net.sourceforge.atunes.model.IHotkeyListener;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Responsible of handle X11 hotkeys
 * 
 * @author alex
 * 
 */
public class X11Hotkeys extends AbstractHotkeys implements
	jxgrabkey.HotkeyListener {

    /**
     * @param hotkeyListener
     */
    public X11Hotkeys(final IHotkeyListener hotkeyListener) {
	super(hotkeyListener);
	init();
    }

    private void init() {
	// Load JXGrabKey lib
	System.loadLibrary("JXGrabKey");
	JXGrabKey.setDebugOutput(false);
    }

    @Override
    public void activate() {
	JXGrabKey.getInstance().addHotkeyListener(this);
    }

    @Override
    public void cleanUp() {
	JXGrabKey.getInstance().cleanUp();
    }

    @Override
    public void deactivate() {
	JXGrabKey.getInstance().removeHotkeyListener(this);
    }

    @Override
    public boolean registerHotkey(final IHotkey hotkey) {
	try {
	    JXGrabKey.getInstance().registerAwtHotkey(hotkey.getId(),
		    hotkey.getMod(), hotkey.getKey());
	} catch (HotkeyConflictException e) {
	    Logger.error(StringUtils.getString("Hotkey '",
		    hotkey.getKeyDescription(),
		    "' is in use by another application"));
	    return false;
	}
	return true;
    }

    @Override
    public void unregisterHotkey(final IHotkey hotkey) {
	JXGrabKey.getInstance().unregisterHotKey(hotkey.getId());
    }

    @Override
    public void onHotkey(final int id) {
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		getHotkeyListener().onHotkey(id);
	    }
	});
    }

}
