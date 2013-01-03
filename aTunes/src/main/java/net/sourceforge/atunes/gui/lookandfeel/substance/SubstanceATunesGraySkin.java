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
public final class SubstanceATunesGraySkin extends
	net.sourceforge.atunes.gui.lookandfeel.substance.CustomSubstanceSkin {

    /**
     * Creates a new skin.
     */
    public SubstanceATunesGraySkin() {

	SubstanceColorScheme activeScheme = new SubstanceBaseColorScheme(
		"aTunes Gray", false, new Color(101, 101, 101), new Color(130,
			130, 130), new Color(141, 141, 141), new Color(162,
			162, 162), new Color(182, 182, 182), new Color(204,
			204, 204), new Color(246, 246, 246));

	SubstanceColorScheme defaultScheme = new SubstanceBaseColorScheme(
		"Metallic", false, new Color(91, 91, 91), new Color(127, 127,
			127), new Color(202, 202, 202),
		new Color(221, 221, 221), new Color(247, 247, 247), new Color(
			252, 252, 252), new Color(80, 80, 80));

	SubstanceColorScheme disabledScheme = new SubstanceBaseColorScheme(
		"Disabled Metallic", false, new Color(106, 106, 106),
		new Color(185, 185, 185), new Color(214, 214, 214), new Color(
			228, 228, 228), new Color(242, 242, 242), new Color(
			251, 251, 251), new Color(125, 125, 125));

	Color highlightColor = new Color(162, 162, 162);
	SubstanceColorScheme highlightScheme = new SubstanceBaseColorScheme(
		"Highlight", false, highlightColor, highlightColor,
		highlightColor, highlightColor, highlightColor, highlightColor,
		Color.WHITE);

	initializeSkin(activeScheme, defaultScheme, disabledScheme,
		highlightScheme);
    }

    @Override
    public String getDisplayName() {
	return "aTunes Gray skin";
    }
}