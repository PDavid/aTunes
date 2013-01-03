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
import java.awt.event.KeyEvent;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IHotkey;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * A representation of a hotkey
 * 
 * @author alex
 * 
 */
public class Hotkey implements IHotkey {

    /**
	 * 
	 */
    private static final long serialVersionUID = -1438992610049688867L;

    private static final List<Integer> F_KEYS = new ArrayList<Integer>();

    private static final List<Integer> KEY_MODIFIERS = new ArrayList<Integer>();

    static {
	F_KEYS.add(KeyEvent.VK_F1);
	F_KEYS.add(KeyEvent.VK_F2);
	F_KEYS.add(KeyEvent.VK_F3);
	F_KEYS.add(KeyEvent.VK_F4);
	F_KEYS.add(KeyEvent.VK_F5);
	F_KEYS.add(KeyEvent.VK_F6);
	F_KEYS.add(KeyEvent.VK_F7);
	F_KEYS.add(KeyEvent.VK_F8);
	F_KEYS.add(KeyEvent.VK_F9);
	F_KEYS.add(KeyEvent.VK_F10);
	F_KEYS.add(KeyEvent.VK_F11);
	F_KEYS.add(KeyEvent.VK_F12);

	KEY_MODIFIERS.add(KeyEvent.VK_ALT);
	KEY_MODIFIERS.add(KeyEvent.VK_ALT_GRAPH);
	KEY_MODIFIERS.add(KeyEvent.VK_CONTROL);
	KEY_MODIFIERS.add(KeyEvent.VK_META);
	KEY_MODIFIERS.add(KeyEvent.VK_SHIFT);
    }

    private final int id;
    private int mod;
    private int key;
    private final String description;

    /**
     * @param id
     * @param mod
     * @param key
     * @param description
     */
    @ConstructorProperties({ "id", "mod", "key", "description" })
    public Hotkey(final int id, final int mod, final int key,
	    final String description) {
	this.id = id;
	this.mod = mod;
	this.key = key;
	this.description = description;
    }

    @Override
    public int getId() {
	return id;
    }

    @Override
    public int getMod() {
	return mod;
    }

    @Override
    public int getKey() {
	return key;
    }

    @Override
    public String getDescription() {
	return description;
    }

    @Override
    public void setMod(final int mod) {
	this.mod = mod;
    }

    @Override
    public void setKey(final int key) {
	this.key = key;
    }

    @Override
    public String getKeyDescription() {
	String keyText = KeyEvent.getKeyText(getKey());
	if (getMod() != 0) {
	    String modifiersExText = InputEvent.getModifiersExText(getMod());
	    if (keyText.equals(modifiersExText)) {
		return keyText;
	    } else {
		return StringUtils.getString(modifiersExText, "+", keyText);
	    }
	} else {
	    return keyText;
	}
    }

    @Override
    public boolean isRecommended() {
	boolean recommended = false;
	if (getMod() != 0 && getKey() != 0 && !isKeyModifier(getKey())) {
	    recommended = true;
	} else if (isFKey(getKey())) {
	    recommended = true;
	}
	return recommended;
    }

    private boolean isFKey(final int key) {
	return F_KEYS.contains(key);
    }

    private boolean isKeyModifier(final int keyCode) {
	return KEY_MODIFIERS.contains(keyCode);
    }

    @Override
    public String toString() {
	return StringUtils.getString(description, " ", getKeyDescription());
    }
}
