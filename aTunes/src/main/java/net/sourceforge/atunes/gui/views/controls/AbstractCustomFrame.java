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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JFrame;

import net.sourceforge.atunes.utils.GuiUtils;

public abstract class AbstractCustomFrame extends JFrame {

    private static final long serialVersionUID = -7162399690169458143L;

    /**
     * Default constructor
     */
    public AbstractCustomFrame() {
        super();
        GuiUtils.addAppIcons(this);
    }

    @Override
    public Component add(Component comp) {
    	if (comp instanceof JComponent) {
    		((JComponent)comp).setOpaque(false);
    	}
        Component c = super.add(comp);
        GuiUtils.applyComponentOrientation(this);
    	return c;
    }    

}
