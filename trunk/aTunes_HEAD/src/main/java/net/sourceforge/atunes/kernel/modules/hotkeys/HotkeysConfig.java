/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class HotkeysConfig implements Iterable<Hotkey> {

    private SortedMap<Integer, Hotkey> config;

    public HotkeysConfig() {
        this.config = new TreeMap<Integer, Hotkey>();
    }

    public HotkeysConfig(List<Hotkey> hotkeys) {
        this.config = new TreeMap<Integer, Hotkey>();
        for (Hotkey hotkey : hotkeys) {
            config.put(hotkey.getId(), hotkey);
        }
    }

    @ConstructorProperties( { "config" })
    public HotkeysConfig(SortedMap<Integer, Hotkey> map) {
        this.config = new TreeMap<Integer, Hotkey>(map);
    }

    public void putHotkey(Hotkey hotkey) {
        config.put(hotkey.getId(), hotkey);
    }

    public Hotkey getHotkey(int id) {
        return config.get(id);
    }

    public int size() {
        return config.size();
    }

    public SortedMap<Integer, Hotkey> getConfig() {
        return config;
    }

    @Override
    public Iterator<Hotkey> iterator() {
        return config.values().iterator();
    }

    public Hotkey getHotkeyByOrder(int i) {
        return new ArrayList<Hotkey>(config.values()).get(i);
    }

    public Set<Integer> conflicts() {
        Set<Integer> result = new HashSet<Integer>();
        for (int i = 0; i < size(); i++) {
            Hotkey h1 = getHotkeyByOrder(i);
            for (int j = 0; j < size(); j++) {
                Hotkey h2 = getHotkeyByOrder(j);
                if (i != j && h1.getMod() == h2.getMod() && h1.getKey() == h2.getKey()) {
                    result.add(i);
                }
            }
        }
        return result;
    }

    public Set<Integer> notRecommendedKeys() {
        Set<Integer> result = new HashSet<Integer>();
        for (int i = 0; i < size(); i++) {
            Hotkey h = getHotkeyByOrder(i);
            if (!h.isRecommended()) {
                result.add(i);
            }
        }
        return result;
    }

}
