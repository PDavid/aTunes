/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import javax.swing.JSplitPane;

import net.sourceforge.atunes.utils.GuiUtils;

//JSplitPane does not support component orientation, so we must do this manually
// -> http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4265389
public class CustomSplitPane extends JSplitPane {

    private static final long serialVersionUID = 7760369696865269164L;

    public CustomSplitPane(int newOrientation) {
        super(newOrientation);
    }

    @Override
    public void setLeftComponent(Component comp) {
        if (!GuiUtils.getComponentOrientation().isLeftToRight() && getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            super.setRightComponent(comp);
        } else {
            super.setLeftComponent(comp);
        }
    }

    @Override
    public void setRightComponent(Component comp) {
        if (!GuiUtils.getComponentOrientation().isLeftToRight() && getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
            super.setLeftComponent(comp);
        } else {
            super.setRightComponent(comp);
        }
    }

}
