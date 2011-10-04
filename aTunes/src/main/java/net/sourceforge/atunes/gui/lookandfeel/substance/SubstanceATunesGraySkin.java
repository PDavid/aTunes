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
import org.pushingpixels.substance.api.colorscheme.BaseColorScheme;
import org.pushingpixels.substance.api.colorscheme.BaseLightColorScheme;
import org.pushingpixels.substance.api.painter.border.ClassicBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.ClassicFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;

public final class SubstanceATunesGraySkin extends SubstanceSkin implements ICustomSubstanceSkin {

	private Color highlightColor;
	
	static class GrayColorScheme extends BaseLightColorScheme {

		public GrayColorScheme(String displayName) {
			super(displayName);
		}

		private Color ultraDarkColor = new Color(101, 101, 101);
		private Color darkColor = new Color(130, 130, 130);
		private Color midColor = new Color(141, 141, 141);
		private Color lightColor = new Color(162, 162, 162);
		private Color extraLightColor = new Color(182, 182, 182);
		private Color ultraLightColor = new Color(204, 204, 204);
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

	static class HighlightGrayColorScheme extends BaseColorScheme {

		public HighlightGrayColorScheme(String displayName) {
			super(displayName, false);
		}

		private Color color = new Color(162, 162, 162);
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

		private Color ultraDarkColor = new Color(91, 91, 91);
		private Color darkColor = new Color(127, 127, 127);
		private Color midColor = new Color(202, 202, 202);
		private Color lightColor = new Color(221, 221, 221);
		private Color extraLightColor = new Color(247, 247, 247);
		private Color ultraLightColor = new Color(252, 252, 252);
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

		private Color ultraDarkColor = new Color(106, 106, 106);
		private Color darkColor = new Color(185, 185, 185);
		private Color midColor = new Color(214, 214, 214);
		private Color lightColor = new Color(228, 228, 228);
		private Color extraLightColor = new Color(242, 242, 242);
		private Color ultraLightColor = new Color(251, 251, 251);
		private Color foregroundColor = new Color(125, 125, 125);

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
	public SubstanceATunesGraySkin() {

		SubstanceColorScheme activeScheme = new GrayColorScheme("aTunes Gray");
		SubstanceColorScheme defaultScheme = new MetallicColorScheme("Metallic");
		SubstanceColorScheme disabledScheme = new DisabledMetallicColorScheme("Disabled Metallic");
		SubstanceColorScheme highlightScheme = new HighlightGrayColorScheme("Highlight");

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
		return "aTunes Gray skin";
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
