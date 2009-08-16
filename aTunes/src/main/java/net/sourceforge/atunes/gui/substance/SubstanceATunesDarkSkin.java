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

import org.jvnet.substance.api.ComponentState;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceColorSchemeBundle;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.colorscheme.DarkGrayColorScheme;
import org.jvnet.substance.colorscheme.EbonyColorScheme;
import org.jvnet.substance.colorscheme.TintColorScheme;
import org.jvnet.substance.painter.border.ClassicBorderPainter;
import org.jvnet.substance.painter.decoration.ArcDecorationPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.gradient.GlassGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;

public class SubstanceATunesDarkSkin extends SubstanceSkin {

    /**
     * Creates a new skin.
     */
    public SubstanceATunesDarkSkin() {

        SubstanceColorScheme activeScheme = new TintColorScheme(new EbonyColorScheme(), 0.1f);
        SubstanceColorScheme defaultScheme = new EbonyColorScheme();
        SubstanceColorScheme disabledScheme = new TintColorScheme(new DarkGrayColorScheme(), 0.2);

        // the default theme bundle
        SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(activeScheme, defaultScheme, disabledScheme);
        defaultSchemeBundle.registerHighlightColorScheme(activeScheme, 0.7f, ComponentState.SELECTED);
        this.registerDecorationAreaSchemeBundle(defaultSchemeBundle, DecorationAreaType.NONE);

        // the special theme bundle
        SubstanceColorSchemeBundle specialSchemeBundle = new SubstanceColorSchemeBundle(activeScheme, activeScheme, disabledScheme);
        this.registerDecorationAreaSchemeBundle(specialSchemeBundle, DecorationAreaType.PRIMARY_TITLE_PANE, DecorationAreaType.SECONDARY_TITLE_PANE, DecorationAreaType.TOOLBAR,
                DecorationAreaType.FOOTER, DecorationAreaType.HEADER, DecorationAreaType.GENERAL);

        this.buttonShaper = new ClassicButtonShaper();
        this.gradientPainter = new GlassGradientPainter();
        this.borderPainter = new ClassicBorderPainter();
        this.decorationPainter = new ArcDecorationPainter();
        this.highlightPainter = new ClassicHighlightPainter();

    }

    @Override
    public String getDisplayName() {
        return "aTunes Dark skin";
    }

}
