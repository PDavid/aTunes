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
import java.util.Arrays;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;

final class FileSystemTreeTreeWillExpandListener implements TreeWillExpandListener {
	
	private CustomFileSelectionDialog dialog;
	
	/**
	 * @param dialog
	 */
	FileSystemTreeTreeWillExpandListener(CustomFileSelectionDialog dialog) {
		super();
		this.dialog = dialog;
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
	    // Nothing to do
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
	    DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) dialog.getFileSystemTree().getSelectionPath().getLastPathComponent();
	    nodeSelected.removeAllChildren();
	    CustomFileSelectionDialogDirectory dir = (CustomFileSelectionDialogDirectory) nodeSelected.getUserObject();
	    File[] files = CustomFileSelectionDialog.fsView.getFiles(dir.getFile(), true);
	    Arrays.sort(files);
	    for (File f : files) {
	        if (CustomFileSelectionDialog.fsView.isTraversable(f)) {
	            DefaultMutableTreeNode treeNode2 = new DefaultMutableTreeNode(new CustomFileSelectionDialogDirectory(f));
	            nodeSelected.add(treeNode2);
	            treeNode2.add(new DefaultMutableTreeNode("Dummy node"));
	        }
	    }
	}
}