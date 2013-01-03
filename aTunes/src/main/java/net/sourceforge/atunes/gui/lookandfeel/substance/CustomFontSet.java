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

import java.awt.Font;

import javax.swing.plaf.FontUIResource;

import org.pushingpixels.substance.api.fonts.FontSet;

/**
 * Several types of fonts for different UI components
 * 
 * @author alex
 * 
 */
final class CustomFontSet implements FontSet {

    private final FontUIResource windowTitleFont;
    private final FontUIResource titleFont;
    private final FontUIResource smallFont;
    private final FontUIResource messageFont;
    private final FontUIResource menuFont;
    private final FontUIResource controlFont;

    /**
     * @param baseFont
     */
    public CustomFontSet(final Font baseFont) {
	this.windowTitleFont = new FontUIResource(baseFont.deriveFont(
		Font.BOLD, baseFont.getSize() + 1f));
	this.titleFont = new FontUIResource(
		baseFont.deriveFont((float) baseFont.getSize()));
	this.smallFont = new FontUIResource(baseFont.deriveFont(baseFont
		.getSize() - 1f));
	this.messageFont = new FontUIResource(baseFont.deriveFont(baseFont
		.getSize() - 1f));
	this.menuFont = new FontUIResource(baseFont.deriveFont((float) baseFont
		.getSize()));
	this.controlFont = new FontUIResource(
		baseFont.deriveFont((float) baseFont.getSize()));
    }

    @Override
    public FontUIResource getWindowTitleFont() {
	return windowTitleFont;
    }

    @Override
    public FontUIResource getTitleFont() {
	return titleFont;
    }

    @Override
    public FontUIResource getSmallFont() {
	return smallFont;
    }

    @Override
    public FontUIResource getMessageFont() {
	return messageFont;
    }

    @Override
    public FontUIResource getMenuFont() {
	return menuFont;
    }

    @Override
    public FontUIResource getControlFont() {
	return controlFont;
    }
}