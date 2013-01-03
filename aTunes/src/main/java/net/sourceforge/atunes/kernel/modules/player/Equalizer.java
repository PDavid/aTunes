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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JSlider;

import net.sourceforge.atunes.model.IEqualizer;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.PlayerEngineCapability;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The equalizer for player engines.
 */
public class Equalizer implements IEqualizer {

	private Map<String, Integer[]> presets;

	private IPlayerHandler playerHandler;

	private IStatePlayer statePlayer;

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param presets
	 */
	public void setPresets(final Map<String, Integer[]> presets) {
		this.presets = presets;
	}

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	@Override
	public String[] getPresetsNames() {
		List<String> names = new ArrayList<String>(presets.keySet());
		Collections.sort(names);
		return names.toArray(new String[names.size()]);
	}

	@Override
	public Integer[] getPresetByNameForShowInGUI(final String presetName) {
		if (presets == null) {
			throw new IllegalStateException("Presets not loaded");
		}
		Integer[] preset = presets.get(presetName);
		if (preset == null) {
			throw new IllegalStateException(StringUtils.getString("Preset ", presetName, " not found"));
		}
		// As preset is transformed to be shown in GUI, we must clone Integer[]
		// in order that this transformation does not affect original preset value
		Integer[] clonedPreset = new Integer[10];
		for (int i = 0; i < preset.length; i++) {
			clonedPreset[i] = preset[i] - 31;
		}
		return clonedPreset;
	}

	@Override
	public void setEqualizerFromGUI(final boolean enabled, final JSlider[] bands) {
		float[] eqSettings = new float[10];

		// Transform from [-32,32] to [-12,12] with float values and inversion
		for (int i = 0; i < bands.length; i++) {
			eqSettings[i] = bands[i].getValue() * 12 / -32f;
		}

		statePlayer.setEqualizerSettings(eqSettings);

		if (playerHandler.supportsCapability(PlayerEngineCapability.EQUALIZER_CHANGE)) {
			playerHandler.applyEqualization(enabled, eqSettings);
		}
	}

	@Override
	public boolean isEnabled() {
		return statePlayer.isEqualizerEnabled();
	}

	@Override
	public void setEnabled(final boolean enabled) {
		statePlayer.setEqualizerEnabled(enabled);
	}

	@Override
	public int[] getEqualizerSettingsToShowInGUI() {
		float[] eqSettings = getEqualizerValues();
		if (eqSettings == null || eqSettings.length == 0) {
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
		float[] equalizerSettings = statePlayer.getEqualizerSettings();
		return equalizerSettings != null ? Arrays.copyOf(equalizerSettings, equalizerSettings.length) : new float[0];
	}
}
