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

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.atunes.model.IHotkey;
import net.sourceforge.atunes.model.IHotkeysConfig;

/**
 * A configuration of hotkeys
 * 
 * @author alex
 * 
 */
public class HotkeysConfig implements IHotkeysConfig {

    private static final long serialVersionUID = -2980797763805865156L;

    private final SortedMap<Integer, IHotkey> config;

    /**
     * Default constructor
     */
    public HotkeysConfig() {
	this.config = new TreeMap<Integer, IHotkey>();
    }

    /**
     * Builds a configuration with hotkeys
     * 
     * @param hotkeys
     */
    public HotkeysConfig(final List<Hotkey> hotkeys) {
	this.config = new TreeMap<Integer, IHotkey>();
	for (Hotkey hotkey : hotkeys) {
	    config.put(hotkey.getId(), hotkey);
	}
    }

    /**
     * Builds a configuration
     * 
     * @param map
     */
    @ConstructorProperties({ "config" })
    public HotkeysConfig(final SortedMap<Integer, Hotkey> map) {
	this.config = new TreeMap<Integer, IHotkey>(map);
    }

    void putHotkey(final Hotkey hotkey) {
	config.put(hotkey.getId(), hotkey);
    }

    @Override
    public int size() {
	return config.size();
    }

    @Override
    public Iterator<IHotkey> iterator() {
	return config.values().iterator();
    }

    @Override
    public IHotkey getHotkeyByOrder(final int i) {
	return new ArrayList<IHotkey>(config.values()).get(i);
    }

    @Override
    public Set<Integer> conflicts() {
	Set<Integer> result = new HashSet<Integer>();
	for (int i = 0; i < size(); i++) {
	    IHotkey h1 = getHotkeyByOrder(i);
	    for (int j = 0; j < size(); j++) {
		IHotkey h2 = getHotkeyByOrder(j);
		if (i != j && h1.getMod() == h2.getMod()
			&& h1.getKey() == h2.getKey()) {
		    result.add(i);
		}
	    }
	}
	return result;
    }

    @Override
    public Set<Integer> notRecommendedKeys() {
	Set<Integer> result = new HashSet<Integer>();
	for (int i = 0; i < size(); i++) {
	    IHotkey h = getHotkeyByOrder(i);
	    if (!h.isRecommended()) {
		result.add(i);
	    }
	}
	return result;
    }
}
