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

package net.sourceforge.atunes.kernel.modules.player;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;

import net.sourceforge.atunes.utils.Logger;

/**
 * Reads equalizer presets
 * 
 * @author alex
 * 
 */
public class EqualizerPresetsReader {

    private String presetsFile;

    /**
     * @param presetsFile
     */
    public void setPresetsFile(final String presetsFile) {
	this.presetsFile = presetsFile;
    }

    /**
     * Returns presets loaded from properties file. Keys are transformed to be
     * shown on GUI
     * 
     * @return the presets from bundle
     */
    public Map<String, Integer[]> getPresetsFromBundle() {
	Map<String, Integer[]> result = new HashMap<String, Integer[]>();

	try {
	    PropertyResourceBundle presetsBundle = new PropertyResourceBundle(
		    EqualizerPresetsReader.class
			    .getResourceAsStream(presetsFile));
	    Enumeration<String> keys = presetsBundle.getKeys();

	    while (keys.hasMoreElements()) {
		String key = keys.nextElement();
		String preset = presetsBundle.getString(key);

		// Transform key
		key = key.replace('.', ' ');

		// Parse preset
		StringTokenizer st = new StringTokenizer(preset, ",");
		Integer[] presetsArray = new Integer[10];
		int i = 0;
		while (st.hasMoreTokens()) {
		    String token = st.nextToken();
		    presetsArray[i++] = Integer.parseInt(token);
		}

		result.put(key, presetsArray);
	    }
	} catch (IOException ioe) {
	    Logger.error(ioe);
	} catch (NumberFormatException nfe) {
	    Logger.error(nfe);
	}
	return result;
    }

}
