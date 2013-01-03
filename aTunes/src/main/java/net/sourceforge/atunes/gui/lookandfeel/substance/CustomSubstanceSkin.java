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
import java.awt.Component;

import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;


/**
 * Common code for custom skins
 * @author alex
 *
 */
abstract class CustomSubstanceSkin extends org.pushingpixels.substance.api.SubstanceSkin implements ICustomSubstanceSkin {

	private Color highlightColor;
	
	private Color colorMutableIcon;
	
	private Color colorForDisabledSpecialControl;

	/**
	 * @param activeScheme
	 * @param defaultScheme
	 * @param disabledScheme
	 * @param highlightScheme
	 */
	protected void initializeSkin(SubstanceColorScheme activeScheme, 
						SubstanceColorScheme defaultScheme, 
						SubstanceColorScheme disabledScheme,
						SubstanceColorScheme highlightScheme) {
		
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
	public Color getPaintForColorMutableIcon(Component component, boolean isSelected) {
    	if (isSelected) {
    		return component.getForeground();    		
    	} else {
    		if (colorMutableIcon == null) {
        		Color c = highlightColor.darker();    		
        		colorMutableIcon = new Color(c.getRed(), c.getGreen(), c.getBlue(), 200);
    		}
    		return colorMutableIcon;
    	}
	}
	
    @Override
	public Color getPaintForSpecialControls() {
		return highlightColor.darker();    		
	}

    @Override
	public Color getPaintForDisabledSpecialControls() {
    	if (colorForDisabledSpecialControl == null) {
    		Color c = highlightColor.darker();    		
    		colorForDisabledSpecialControl = new Color(c.getRed(), c.getGreen(), c.getBlue(), 140);
    	}
    	return colorForDisabledSpecialControl;
	}
}
