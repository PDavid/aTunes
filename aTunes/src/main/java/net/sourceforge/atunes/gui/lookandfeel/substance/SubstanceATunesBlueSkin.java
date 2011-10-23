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

package net.sourceforge.atunes.gui.lookandfeel.substance;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;

import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;

public final class SubstanceATunesBlueSkin extends SubstanceSkin implements ICustomSubstanceSkin {

	private Color highlightColor;
	
	/**
	 * Creates a new skin.
	 */
	public SubstanceATunesBlueSkin() {

		SubstanceColorScheme activeScheme = new SubstanceBaseColorScheme("aTunes Blue", false,
				new Color(32, 71, 143),
				new Color(43, 100, 177),
				new Color(50, 111, 180),
				new Color(58, 132, 186),
				new Color(81, 152, 195),
				new Color(103, 174, 195),
				new Color(246, 246, 246));
		
		SubstanceColorScheme defaultScheme = new SubstanceBaseColorScheme("Metallic", false,
				new Color(87, 91, 95),
				new Color(123, 127, 131),
				new Color(198, 202, 206),
				new Color(213, 221, 228),
				new Color(243, 247, 251),
				new Color(251, 252, 255),
				new Color(80, 80, 80));

		SubstanceColorScheme disabledScheme = new SubstanceBaseColorScheme("Disabled Metallic", false,
				new Color(100, 106, 112),
				new Color(180, 185, 190),
				new Color(210, 214, 218),
				new Color(225, 228, 231),
				new Color(240, 242, 244),
				new Color(250, 251, 252),
				new Color(120, 125, 130));

		Color hightlightColor = new Color(54, 123, 183);
		SubstanceColorScheme highlightScheme = new SubstanceBaseColorScheme("Highlight", false, 
				hightlightColor,
				hightlightColor,
				hightlightColor,
				hightlightColor,
				hightlightColor,
				hightlightColor,
				Color.WHITE);

		// the default theme bundle
		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(defaultScheme, defaultScheme, disabledScheme);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1f, ComponentState.ROLLOVER_UNSELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1f, ComponentState.SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1f, ComponentState.ROLLOVER_SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1f, ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);
		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle, DecorationAreaType.NONE);

		// the special theme bundle
		SubstanceColorSchemeBundle specialSchemeBundle = new SubstanceColorSchemeBundle(activeScheme, activeScheme, disabledScheme);
		this.registerDecorationAreaSchemeBundle(specialSchemeBundle, DecorationAreaType.PRIMARY_TITLE_PANE, DecorationAreaType.SECONDARY_TITLE_PANE);

		this.setSelectedTabFadeStart(1);
		this.setSelectedTabFadeEnd(1);

		this.buttonShaper = new ClassicButtonShaper();
		this.fillPainter = new ClassicFillPainter();
		this.borderPainter = new ClassicBorderPainter();
		this.decorationPainter = new MatteDecorationPainter();
		this.highlightPainter = new ClassicHighlightPainter();
		
		this.highlightColor = highlightScheme.getLightColor();

	}

	@Override
	public String getDisplayName() {
		return "aTunes Blue skin";
	}

    @Override
	public Paint getPaintForColorMutableIcon(Component component, boolean isSelected) {
    	if (isSelected) {
    		return component.getForeground();    		
    	} else {
    		Color c = highlightColor.darker();    		
    		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 200);
    	}
	}
	
    @Override
	public Paint getPaintForSpecialControls() {
		return highlightColor.darker();    		
	}

    @Override
	public Paint getPaintForDisabledSpecialControls() {
		Color c = highlightColor.darker();    		
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), 140);
	}

}
