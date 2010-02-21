/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JSlider;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

/**
 * The equalizer for player engines.
 */
public class Equalizer {

    private Logger logger;

    private Map<String, Integer[]> presets;

    /**
     * Instantiates a new equalizer handler.
     */
    public Equalizer() {
        presets = getPresetsFromBundle();
    }

    /**
     * Returns presets loaded from properties file. Keys are transformed to be
     * shown on GUI
     * 
     * @return the presets from bundle
     */
    private Map<String, Integer[]> getPresetsFromBundle() {
        Map<String, Integer[]> result = new HashMap<String, Integer[]>();

        try {
            PropertyResourceBundle presetsBundle = new PropertyResourceBundle(Equalizer.class.getResourceAsStream(Constants.EQUALIZER_PRESETS_FILE));
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
            getLogger().error(LogCategories.PLAYER, ioe);
        } catch (NumberFormatException nfe) {
            getLogger().error(LogCategories.PLAYER, nfe);
        }

        return result;
    }

    /**
     * Gets the presets names.
     * 
     * @return the presets
     */
    public String[] getPresetsNames() {
        List<String> names = new ArrayList<String>(presets.keySet());
        Collections.sort(names);
        return names.toArray(new String[names.size()]);
    }

    /**
     * Returns presets for a preset name.
     * 
     * @param presetName
     *            the preset name
     * 
     * @return the preset by name for show in gui
     */
    public Integer[] getPresetByNameForShowInGUI(String presetName) {
        Integer[] preset = presets.get(presetName);
        // As preset is transformed to be shown in GUI, we must clone Integer[]
        // in order that this transformation does not affect original preset value
        Integer[] clonedPreset = new Integer[10];
        for (int i = 0; i < preset.length; i++) {
            clonedPreset[i] = preset[i] - 31;
        }
        return clonedPreset;
    }

    /**
     * Gets preset from GUI and sets into application state.
     * 
     * @param bands
     *            the bands
     */
    public void setEqualizerFromGUI(JSlider[] bands) {
        float[] eqSettings = new float[10];

        // Transform from [-32,32] to [-12,12] with float values and inversion
        for (int i = 0; i < bands.length; i++) {
            eqSettings[i] = bands[i].getValue() * 12 / -32f;
        }

        ApplicationState.getInstance().setEqualizerSettings(eqSettings);

        if (PlayerHandler.getInstance().supportsCapability(PlayerEngineCapability.EQUALIZER_CHANGE)) {
            PlayerHandler.getInstance().applyEqualization(eqSettings);
        }
    }

    /**
     * Returns bands transformed in [-32,32] scale to be shown in GUI.
     * 
     * @return the equalizer settings to show in gui
     */
    public int[] getEqualizerSettingsToShowInGUI() {
        float[] eqSettings = getEqualizerValues();
        if (eqSettings == null) {
            return new int[10];
        }
        int[] result = new int[eqSettings.length];

        // Transform from [-12,12] to [-32,32] with int values and inversion
        for (int i = 0; i < eqSettings.length; i++) {
            result[i] = (int) (eqSettings[i] * 32 / -12);
        }
        return result;
    }

    /**
     * Returns equalizer values or <code>null</code> if equalizer is disabled
     * TODO: Add more explanation about equalizer values
     * 
     * @return the equalizer
     */
    public float[] getEqualizerValues() {
        float[] equalizerSettings = ApplicationState.getInstance().getEqualizerSettings();
        return equalizerSettings != null ? Arrays.copyOf(equalizerSettings, equalizerSettings.length) : null;
    }
    
    /**
     * Getter for logger
     * @return
     */
    private Logger getLogger() {
    	if (logger == null) {
    		logger = new Logger();
    	}
    	return logger;
    }

}
