/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.gui.views.controls;

import javax.swing.JFrame;
import javax.swing.JWindow;

import net.sourceforge.atunes.utils.GuiUtils;

public abstract class CustomWindow extends JWindow {

    private static final long serialVersionUID = -8846047318549650938L;

    public CustomWindow(JFrame owner, int width, int height) {
        super(owner);
        setSize(width, height);
        setLocationRelativeTo(owner);
        GuiUtils.addAppIcons(this);
    }

    /**
     * Enable close action with escape key.
     */
    public void enableCloseActionWithEscapeKey() {
        GuiUtils.addCloseActionWithEscapeKey(this, getRootPane());
    }

    /**
     * Enable dispose action with escape key.
     */
    public void enableDisposeActionWithEscapeKey() {
        GuiUtils.addDisposeActionWithEscapeKey(this, getRootPane());
    }

}
