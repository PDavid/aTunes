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

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import net.sourceforge.atunes.utils.Logger;

import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ColorSchemeSingleColorQuery;
import org.pushingpixels.substance.api.ColorSchemeTransform;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.border.CompositeBorderPainter;
import org.pushingpixels.substance.api.painter.border.DelegateBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.FlatDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.FractionBasedFillPainter;
import org.pushingpixels.substance.api.painter.highlight.FractionBasedHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;

/**
 * <code>Graphite Aqua</code> skin. This class is part of officially supported
 * API.
 * 
 * @author Kirill Grouchnikov
 * @since version 5.3
 */
public class SubstanceATunesNewSkin extends SubstanceSkin {
	/**
	 * Display name for <code>this</code> skin.
	 */
	public static final String NAME = "aTunes New";

	/**
	 * Creates a new skin.
	 */
	public SubstanceATunesNewSkin() {
		SubstanceSkin.ColorSchemes schemes = null;
		try {
			schemes = SubstanceSkin.getColorSchemes(this.getClass()
					.getResource("/settings/atunes.colorschemes").toURI()
					.toURL());
		} catch (MalformedURLException e) {
			Logger.error(e);
		} catch (URISyntaxException e) {
			Logger.error(e);
		}

		// If schemes are not loaded then let it fail...
		SubstanceColorScheme selectedDisabledScheme = schemes
				.get("Graphite Selected Disabled");
		SubstanceColorScheme disabledScheme = schemes.get("Graphite Disabled");

		SubstanceColorScheme enabledScheme = schemes.get("Graphite Enabled");
		SubstanceColorScheme backgroundScheme = schemes
				.get("Graphite Background");

		// use the same color scheme for active and enabled controls
		SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
				enabledScheme, enabledScheme, disabledScheme);

		// highlight fill scheme + custom alpha for rollover unselected state
		SubstanceColorScheme highlightScheme = schemes.get("Graphite Aqua");
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme,
				0.75f, ComponentState.ROLLOVER_UNSELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 0.9f,
				ComponentState.SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1.0f,
				ComponentState.ROLLOVER_SELECTED);
		defaultSchemeBundle.registerHighlightColorScheme(highlightScheme, 1.0f,
				ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);

		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ColorSchemeAssociationKind.BORDER,
				ComponentState.ROLLOVER_ARMED,
				ComponentState.ROLLOVER_SELECTED,
				ComponentState.ROLLOVER_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ColorSchemeAssociationKind.FILL, ComponentState.SELECTED,
				ComponentState.ROLLOVER_SELECTED);

		// border schemes
		SubstanceColorScheme borderScheme = schemes.get("Graphite Border");
		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ColorSchemeAssociationKind.HIGHLIGHT_BORDER,
				ComponentState.getActiveStates());
		defaultSchemeBundle.registerColorScheme(borderScheme,
				ColorSchemeAssociationKind.BORDER);
		defaultSchemeBundle.registerColorScheme(borderScheme,
				ColorSchemeAssociationKind.MARK);

		// text highlight scheme
		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ColorSchemeAssociationKind.TEXT_HIGHLIGHT,
				ComponentState.SELECTED, ComponentState.ROLLOVER_SELECTED);

		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ComponentState.ARMED, ComponentState.ROLLOVER_ARMED);

		defaultSchemeBundle.registerColorScheme(disabledScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED);
		defaultSchemeBundle.registerColorScheme(selectedDisabledScheme, 0.5f,
				ComponentState.DISABLED_SELECTED);

		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ComponentState.ROLLOVER_SELECTED);
		defaultSchemeBundle.registerColorScheme(highlightScheme,
				ComponentState.SELECTED);

		SubstanceColorScheme tabHighlightScheme = schemes
				.get("Graphite Tab Highlight");
		defaultSchemeBundle.registerColorScheme(tabHighlightScheme,
				ColorSchemeAssociationKind.TAB,
				ComponentState.ROLLOVER_SELECTED);

		this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
				backgroundScheme, DecorationAreaType.NONE);

		this.setSelectedTabFadeStart(0.15);
		this.setSelectedTabFadeEnd(0.25);

		this.buttonShaper = new ClassicButtonShaper();
		this.watermark = null;
		this.fillPainter = new FractionBasedFillPainter("Graphite Aqua",
				new float[] { 0.0f, 0.5f, 1.0f },
				new ColorSchemeSingleColorQuery[] {
						ColorSchemeSingleColorQuery.LIGHT,
						ColorSchemeSingleColorQuery.MID,
						ColorSchemeSingleColorQuery.MID });

		this.decorationPainter = new FlatDecorationPainter();
		this.highlightPainter = new FractionBasedHighlightPainter(
				"Graphite Aqua", new float[] { 0.0f, 0.5f, 1.0f },
				new ColorSchemeSingleColorQuery[] {
						ColorSchemeSingleColorQuery.EXTRALIGHT,
						ColorSchemeSingleColorQuery.LIGHT,
						ColorSchemeSingleColorQuery.MID });
		this.borderPainter = new CompositeBorderPainter("Graphite Aqua",
				new ClassicBorderPainter(), new DelegateBorderPainter(
						"Graphite Aqua Inner", new ClassicBorderPainter(),
						0xC0FFFFFF, 0x90FFFFFF, 0x30FFFFFF,
						new ColorSchemeTransform() {
							@Override
							public SubstanceColorScheme transform(
									SubstanceColorScheme scheme) {
								return scheme.tint(0.25f);
							}
						}));
		this.highlightBorderPainter = new ClassicBorderPainter();
	}

	@Override
	public String getDisplayName() {
		return NAME;
	}
}
