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
import org.pushingpixels.substance.api.colorscheme.BaseLightColorScheme;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;

public final class SubstanceATunesBlueSkin extends SubstanceSkin implements ICustomSubstanceSkin {

	private Color highlightColor;
	
	static class BlueColorScheme extends BaseLightColorScheme {

		public BlueColorScheme(String displayName) {
			super(displayName);
		}

		private Color ultraDarkColor = new Color(32, 71, 143);
		private Color darkColor = new Color(43, 100, 177);
		private Color midColor = new Color(50, 111, 180);
		private Color lightColor = new Color(58, 132, 186);
		private Color extraLightColor = new Color(81, 152, 195);
		private Color ultraLightColor = new Color(103, 174, 195);
		private Color foregroundColor = new Color(246, 246, 246);

		@Override
		public Color getUltraDarkColor() {
			return ultraDarkColor;
		}

		@Override
		public Color getDarkColor() {
			return darkColor;
		}

		@Override
		public Color getMidColor() {
			return midColor;
		}

		@Override
		public Color getLightColor() {
			return lightColor;
		}

		@Override
		public Color getExtraLightColor() {
			return extraLightColor;
		}

		@Override
		public Color getUltraLightColor() {
			return ultraLightColor;
		}

		@Override
		public Color getForegroundColor() {
			return foregroundColor;
		}

	}

	static class HighlightBlueColorScheme extends BaseLightColorScheme {

		public HighlightBlueColorScheme(String displayName) {
			super(displayName);
		}

		private Color color = new Color(54, 123, 183);
		private Color foreground = Color.WHITE;

		@Override
		public Color getUltraDarkColor() {
			return color;
		}

		@Override
		public Color getDarkColor() {
			return color;
		}

		@Override
		public Color getMidColor() {
			return color;
		}

		@Override
		public Color getLightColor() {
			return color;
		}

		@Override
		public Color getExtraLightColor() {
			return color;
		}

		@Override
		public Color getUltraLightColor() {
			return color;
		}

		@Override
		public Color getForegroundColor() {
			return foreground;
		}

	}

	static class MetallicColorScheme extends BaseLightColorScheme {

		public MetallicColorScheme(String displayName) {
			super(displayName);
		}

		private Color ultraDarkColor = new Color(87, 91, 95);
		private Color darkColor = new Color(123, 127, 131);
		private Color midColor = new Color(198, 202, 206);
		private Color lightColor = new Color(213, 221, 228);
		private Color extraLightColor = new Color(243, 247, 251);
		private Color ultraLightColor = new Color(251, 252, 255);
		private Color foregroundColor = new Color(80, 80, 80);

		@Override
		public Color getUltraDarkColor() {
			return ultraDarkColor;
		}

		@Override
		public Color getDarkColor() {
			return darkColor;
		}

		@Override
		public Color getMidColor() {
			return midColor;
		}

		@Override
		public Color getLightColor() {
			return lightColor;
		}

		@Override
		public Color getExtraLightColor() {
			return extraLightColor;
		}

		@Override
		public Color getUltraLightColor() {
			return ultraLightColor;
		}

		@Override
		public Color getForegroundColor() {
			return foregroundColor;
		}
	}

	static class DisabledMetallicColorScheme extends BaseLightColorScheme {

		public DisabledMetallicColorScheme(String displayName) {
			super(displayName);
		}

		private Color ultraDarkColor = new Color(100, 106, 112);
		private Color darkColor = new Color(180, 185, 190);
		private Color midColor = new Color(210, 214, 218);
		private Color lightColor = new Color(225, 228, 231);
		private Color extraLightColor = new Color(240, 242, 244);
		private Color ultraLightColor = new Color(250, 251, 252);
		private Color foregroundColor = new Color(120, 125, 130);

		@Override
		public Color getUltraDarkColor() {
			return ultraDarkColor;
		}

		@Override
		public Color getDarkColor() {
			return darkColor;
		}

		@Override
		public Color getMidColor() {
			return midColor;
		}

		@Override
		public Color getLightColor() {
			return lightColor;
		}

		@Override
		public Color getExtraLightColor() {
			return extraLightColor;
		}

		@Override
		public Color getUltraLightColor() {
			return ultraLightColor;
		}

		@Override
		public Color getForegroundColor() {
			return foregroundColor;
		}
	}

	/**
	 * Creates a new skin.
	 */
	public SubstanceATunesBlueSkin() {

		SubstanceColorScheme activeScheme = new BlueColorScheme("aTunes Blue");
		SubstanceColorScheme defaultScheme = new MetallicColorScheme("Metallic");
		SubstanceColorScheme disabledScheme = new DisabledMetallicColorScheme("Disabled Metallic");
		SubstanceColorScheme highlightScheme = new HighlightBlueColorScheme("Highlight");

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
