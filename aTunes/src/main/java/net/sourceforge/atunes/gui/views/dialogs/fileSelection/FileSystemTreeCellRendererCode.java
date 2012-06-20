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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.AbstractTreeCellRendererCode;

class FileSystemTreeCellRendererCode extends AbstractTreeCellRendererCode<JLabel, DefaultMutableTreeNode> {
	
	private FileSystemView fileSystemView;
	
	/**
	 * @param fileSystemView
	 */
	public FileSystemTreeCellRendererCode(FileSystemView fileSystemView) {
		this.fileSystemView = fileSystemView;
	}
	
    @Override
    public JComponent getComponent(JLabel superComponent, JTree tree, DefaultMutableTreeNode value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean isHasFocus) {
    	Object userObject = value.getUserObject();
    	if (userObject instanceof String) {
    		superComponent.setText(null);
    	} else if (userObject instanceof CustomFileSelectionDialogDirectory) {
    		superComponent.setIcon(fileSystemView.getSystemIcon(((CustomFileSelectionDialogDirectory) userObject).getFile()));
    	}
        return superComponent;
    }
}