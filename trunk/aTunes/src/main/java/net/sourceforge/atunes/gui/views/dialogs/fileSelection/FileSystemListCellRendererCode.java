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

package net.sourceforge.atunes.gui.views.dialogs.fileSelection;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import net.sourceforge.atunes.gui.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.GuiUtils;

class FileSystemListCellRendererCode extends AbstractListCellRendererCode<JLabel, File> {
    @Override
    public JComponent getComponent(JLabel superComponent, JList list, File value, int index, boolean isSelected, boolean cellHasFocus) {
    	superComponent.setText(CustomFileSelectionDialog.fsView.getSystemDisplayName(value));
    	superComponent.setIcon(CustomFileSelectionDialog.fsView.getSystemIcon(value));
    	superComponent.setHorizontalAlignment(GuiUtils.getComponentOrientationAsSwingConstant());
    	return superComponent;
    }
}