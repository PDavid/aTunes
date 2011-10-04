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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.beans.ConstructorProperties;

public class Hotkey {

    private int id;
    private int mod;
    private int key;
    private String description;

    @ConstructorProperties( { "id", "mod", "key", "description" })
    public Hotkey(int id, int mod, int key, String description) {
        this.id = id;
        this.mod = mod;
        this.key = key;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getMod() {
        return mod;
    }

    public int getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getKeyDescription() {
        String keyText = KeyEvent.getKeyText(getKey());
        if (getMod() != 0) {
            String modifiersExText = InputEvent.getModifiersExText(getMod());
            if (keyText.equals(modifiersExText)) {
                return keyText;
            } else {
                return modifiersExText + "+" + keyText;
            }
        } else {
            return keyText;
        }
    }

    public boolean isRecommended() {
        boolean recommended = false;
        if (getMod() != 0 && getKey() != 0 && !isKeyModifier(getKey())) {
            recommended = true;
        } else if (isFKey(getKey())) {
            recommended = true;
        }
        return recommended;
    }

    private boolean isFKey(int key) {
        return key == KeyEvent.VK_F1 || key == KeyEvent.VK_F2 || key == KeyEvent.VK_F3 || key == KeyEvent.VK_F4 || key == KeyEvent.VK_F5 || key == KeyEvent.VK_F6
                || key == KeyEvent.VK_F7 || key == KeyEvent.VK_F8 || key == KeyEvent.VK_F9 || key == KeyEvent.VK_F10 || key == KeyEvent.VK_F11 || key == KeyEvent.VK_F12;
    }

    private boolean isKeyModifier(int keyCode) {
        return (keyCode == KeyEvent.VK_ALT || keyCode == KeyEvent.VK_ALT_GRAPH || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_META || keyCode == KeyEvent.VK_SHIFT);
    }

    @Override
    public String toString() {
        return description + " " + getKeyDescription();
    }
}
