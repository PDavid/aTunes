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

package net.sourceforge.atunes.kernel.modules.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JSlider;

import net.sourceforge.atunes.model.IEqualizer;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.PlayerEngineCapability;

/**
 * The equalizer for player engines.
 */
public class Equalizer implements IEqualizer {

    private Map<String, Integer[]> presets;

    private IState state;
    
    private IPlayerHandler playerHandler;

    /**
     * @param presets
     */
    public void setPresets(Map<String, Integer[]> presets) {
		this.presets = presets;
	}
    
    /**
     * @param state
     */
    public void setState(IState state) {
		this.state = state;
	}
    
    /**
     * @param playerHandler
     */
    public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

    @Override
	public String[] getPresetsNames() {
        List<String> names = new ArrayList<String>(presets.keySet());
        Collections.sort(names);
        return names.toArray(new String[names.size()]);
    }

    @Override
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

    @Override
	public void setEqualizerFromGUI(JSlider[] bands) {
        float[] eqSettings = new float[10];

        // Transform from [-32,32] to [-12,12] with float values and inversion
        for (int i = 0; i < bands.length; i++) {
            eqSettings[i] = bands[i].getValue() * 12 / -32f;
        }

        state.setEqualizerSettings(eqSettings);

        if (playerHandler.supportsCapability(PlayerEngineCapability.EQUALIZER_CHANGE)) {
        	playerHandler.applyEqualization(eqSettings);
        }
    }

    @Override
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

    @Override
	public float[] getEqualizerValues() {
        float[] equalizerSettings = state.getEqualizerSettings();
        return equalizerSettings != null ? Arrays.copyOf(equalizerSettings, equalizerSettings.length) : null;
    }
}
