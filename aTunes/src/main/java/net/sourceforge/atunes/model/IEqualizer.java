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

package net.sourceforge.atunes.model;

import javax.swing.JSlider;

/**
 * A component to control equalizer settings
 * @author alex
 *
 */
public interface IEqualizer {

	/**
	 * @return if equalizer enabled
	 */
	public boolean isEnabled();

	/**
	 * @param enabled
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Gets the presets names.
	 * 
	 * @return the presets
	 */
	public String[] getPresetsNames();

	/**
	 * Returns presets for a preset name.
	 * 
	 * @param presetName
	 *            the preset name
	 * 
	 * @return the preset by name for show in gui
	 */
	public Integer[] getPresetByNameForShowInGUI(String presetName);

	/**
	 * Gets preset from GUI and sets into application state.
	 * @param enabled if equalizer is enabled or disabled
	 * @param bands
	 */
	public void setEqualizerFromGUI(boolean enabled, JSlider[] bands);

	/**
	 * Returns bands transformed in [-32,32] scale to be shown in GUI.
	 * 
	 * @return the equalizer settings to show in gui
	 */
	public int[] getEqualizerSettingsToShowInGUI();

	/**
	 * Returns equalizer values or <code>null</code> if equalizer is disabled
	 * TODO: Add more explanation about equalizer values
	 * 
	 * @return the equalizer
	 */
	public float[] getEqualizerValues();

}