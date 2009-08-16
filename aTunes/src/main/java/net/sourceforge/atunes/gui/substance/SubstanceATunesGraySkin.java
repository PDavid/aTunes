/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.gui.substance;

import java.awt.Color;

import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceColorSchemeBundle;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.colorscheme.BaseDarkColorScheme;
import org.jvnet.substance.colorscheme.TintColorScheme;
import org.jvnet.substance.painter.border.GlassBorderPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.decoration.MatteDecorationPainter;
import org.jvnet.substance.painter.gradient.GlassGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;
import org.jvnet.substance.watermark.SubstanceNullWatermark;

/**
 * The Class SubstanceATunesSkin.
 */
public class SubstanceATunesGraySkin extends SubstanceSkin {

    static class GrayColorScheme extends BaseDarkColorScheme {

        public GrayColorScheme(String displayName) {
            super(displayName);
        }

        private Color ultraDarkColor = new Color(95, 95, 95);
        private Color darkColor = new Color(125, 125, 125);
        private Color midColor = new Color(180, 180, 180);
        private Color lightColor = new Color(200, 200, 200);
        private Color extraLightColor = new Color(210, 210, 210);
        private Color ultraLightColor = new Color(220, 220, 220);
        private Color foregroundColor = new Color(55, 55, 55);

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

    static class MetallicColorScheme extends BaseDarkColorScheme {

        protected MetallicColorScheme(String displayName) {
            super(displayName);
        }

        private Color ultraDarkColor = new Color(135, 135, 135);
        private Color darkColor = new Color(155, 155, 155); // Controls' border
        private Color midColor = new Color(175, 175, 175); // Used for buttons, table headers and scroll bars
        private Color lightColor = new Color(190, 190, 190); // Background
        private Color extraLightColor = new Color(200, 200, 200); // Background
        private Color ultraLightColor = new Color(210, 210, 210); // Used for buttons, table headers and scroll bars
        private Color foregroundColor = new Color(50, 50, 50);

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
        SubstanceColorScheme disabledScheme = new TintColorScheme(defaultScheme, 0.3);

        // the default theme bundle
        SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(activeScheme, defaultScheme, disabledScheme);
        this.registerDecorationAreaSchemeBundle(defaultSchemeBundle, DecorationAreaType.NONE);

        // the special theme bundle
        SubstanceColorSchemeBundle specialSchemeBundle = new SubstanceColorSchemeBundle(activeScheme, activeScheme, disabledScheme);
        this.registerDecorationAreaSchemeBundle(specialSchemeBundle, DecorationAreaType.PRIMARY_TITLE_PANE, DecorationAreaType.SECONDARY_TITLE_PANE, DecorationAreaType.TOOLBAR,
                DecorationAreaType.FOOTER, DecorationAreaType.HEADER, DecorationAreaType.GENERAL);

        this.buttonShaper = new ClassicButtonShaper();
        this.watermark = new SubstanceNullWatermark();
        this.gradientPainter = new GlassGradientPainter();
        this.decorationPainter = new MatteDecorationPainter();
        this.highlightPainter = new ClassicHighlightPainter();
        this.borderPainter = new GlassBorderPainter();

    }

    @Override
    public String getDisplayName() {
        return "aTunes Gray skin";
    }

}
