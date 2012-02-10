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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;

import javax.swing.JSlider;

import net.sourceforge.atunes.gui.GuiUtils;

public final class VolumeSlider extends JSlider {

    private static final long serialVersionUID = -7802263658163323018L;

    /**
     * Slider to control volume
     * @param state
     * @param playerHandler
     */
    public VolumeSlider() {
        super();
        setOpaque(false);
        setMinimum(0);
        setMaximum(100);
        setValue(50);
        setFocusable(false);
        setPreferredSize(new Dimension(GuiUtils.getComponentWidthForResolution(0.05f, 100), 20));
    }
}
