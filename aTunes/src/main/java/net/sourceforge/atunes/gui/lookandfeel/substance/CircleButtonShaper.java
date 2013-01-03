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

import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import javax.swing.AbstractButton;

/**
 * Based on code from Xtreme Media Player
 * 
 * @author alex
 */
public final class CircleButtonShaper extends AbstractButtonShaper {

    @Override
    public String getDisplayName() {
	return "Circle";
    }

    @Override
    public Shape getButtonOutline(final AbstractButton button,
	    final Insets insets, final int w, final int h, final boolean isInner) {
	return new GeneralPath(new Ellipse2D.Double(0, 0, w - 1, h - 1));
    }

}
