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


import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.colorscheme.DarkGrayColorScheme;
import org.pushingpixels.substance.api.colorscheme.EbonyColorScheme;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;
import org.pushingpixels.substance.internal.colorscheme.TintColorScheme;

/**
 * Custom skin
 * 
 * @author alex
 * 
 */
public final class SubstanceATunesDarkSkin extends SubstanceSkin {

    /**
     * Creates a new skin.
     */
    public SubstanceATunesDarkSkin() {

	SubstanceColorScheme activeScheme = new TintColorScheme(
		new EbonyColorScheme(), 0.1f);
	SubstanceColorScheme defaultScheme = new TintColorScheme(
		new CustomDarkGrayColorScheme(), 0.1f);
	SubstanceColorScheme disabledScheme = new TintColorScheme(
		new DarkGrayColorScheme(), 0.5f);

	// the default theme bundle
	SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
		activeScheme, defaultScheme, disabledScheme);
	defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.7f,
		ComponentState.ROLLOVER_UNSELECTED);
	defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.7f,
		ComponentState.SELECTED);
	defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.7f,
		ComponentState.ROLLOVER_SELECTED);
	defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.7f,
		ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);
	this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
		DecorationAreaType.NONE);

	// the special theme bundle
	SubstanceColorSchemeBundle specialSchemeBundle = new SubstanceColorSchemeBundle(
		activeScheme, activeScheme, disabledScheme);
	this.registerDecorationAreaSchemeBundle(specialSchemeBundle,
		DecorationAreaType.PRIMARY_TITLE_PANE,
		DecorationAreaType.SECONDARY_TITLE_PANE);

	this.setSelectedTabFadeStart(1);
	this.setSelectedTabFadeEnd(1);

	this.buttonShaper = new ClassicButtonShaper();
	this.fillPainter = new ClassicFillPainter();
	this.borderPainter = new ClassicBorderPainter();
	this.decorationPainter = new MatteDecorationPainter();
	this.highlightPainter = new ClassicHighlightPainter();

    }

    @Override
    public String getDisplayName() {
	return "aTunes Dark skin";
    }

}
