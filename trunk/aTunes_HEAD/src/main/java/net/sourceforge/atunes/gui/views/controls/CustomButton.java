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

package net.sourceforge.atunes.gui.views.controls;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import net.sourceforge.atunes.gui.Fonts;

/**
 * The Class CustomButton.
 */
public class CustomButton extends JButton {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new custom button.
     * 
     * @param icon
     *            the icon
     * @param text
     *            the text
     */
    public CustomButton(Icon icon, String text) {
        super(text, icon);
        setFont(Fonts.BUTTON_FONT);
    }

    /**
     * Instantiates a new custom button
     * 
     * @param action
     */
    public CustomButton(Action action) {
        super(action);
    }

    /**
     * Instantiates a new custom button
     */
    public CustomButton() {
        super();
    }
}
