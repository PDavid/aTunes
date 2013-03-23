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

package net.sourceforge.atunes.gui.lookandfeel.substance;

import java.awt.Color;

import org.pushingpixels.substance.api.SubstanceColorScheme;

/**
 * Custom skin
 * 
 * @author alex
 * 
 */
public final class SubstanceATunesBlueSkin extends
		net.sourceforge.atunes.gui.lookandfeel.substance.CustomSubstanceSkin {

	/**
	 * Creates a new skin.
	 */
	public SubstanceATunesBlueSkin() {

		SubstanceColorScheme activeScheme = new SubstanceBaseColorScheme(
				"aTunes Blue", false, new Color(32, 71, 143), new Color(43,
						100, 177), new Color(50, 111, 180), new Color(58, 132,
						186), new Color(81, 152, 195),
				new Color(103, 174, 195), new Color(255, 255, 255));

		SubstanceColorScheme defaultScheme = new SubstanceBaseColorScheme(
				"Metallic", false, new Color(90, 91, 92), new Color(125, 126,
						127), new Color(175, 176, 177),
				new Color(195, 196, 197), new Color(220, 221, 222), new Color(
						225, 226, 227), new Color(0, 0, 0));

		SubstanceColorScheme disabledScheme = new SubstanceBaseColorScheme(
				"Disabled Metallic", false, new Color(80, 81, 82), new Color(
						115, 116, 117), new Color(165, 166, 167), new Color(
						185, 186, 187), new Color(210, 211, 212), new Color(
						215, 216, 217), new Color(100, 100, 100));

		Color hightlightColor = new Color(32, 161, 228);
		SubstanceColorScheme highlightScheme = new SubstanceBaseColorScheme(
				"Highlight", false, hightlightColor, hightlightColor,
				hightlightColor, hightlightColor, hightlightColor,
				hightlightColor, new Color(255, 255, 255));

		initializeSkin(activeScheme, defaultScheme, disabledScheme,
				highlightScheme);

	}

	@Override
	public String getDisplayName() {
		return "aTunes Blue skin";
	}
}
