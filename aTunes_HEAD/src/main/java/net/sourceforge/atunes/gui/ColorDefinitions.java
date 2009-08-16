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

package net.sourceforge.atunes.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import net.sourceforge.atunes.utils.GuiUtils;

/**
 * Colors used on app.
 */
public class ColorDefinitions {

    /** The Constant TITLE_DIALOG_FONT_COLOR. */
    public static final Color TITLE_DIALOG_FONT_COLOR = Color.WHITE;

    /** The Constant WARNING_COLOR. */
    public static final Color WARNING_COLOR = Color.RED;

    /** The Constant GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR. */
    public static final Color GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR = Color.RED;

    /**
     * Inits the colors.
     */
    public static void initColors() {
        UIManager.put("ToolTip.border", BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        UIManager.put("ToolTip.background", new ColorUIResource(Color.WHITE));
        UIManager.put("ToolTip.foreground", new ColorUIResource(Color.BLACK));
    }

}
