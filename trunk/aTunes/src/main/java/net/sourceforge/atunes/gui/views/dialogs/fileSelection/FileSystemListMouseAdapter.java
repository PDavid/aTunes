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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

final class FileSystemListMouseAdapter extends MouseAdapter {
	
	private CustomFileSelectionDialog dialog;
	
	/**
	 * @param dialog
	 */
	public FileSystemListMouseAdapter(CustomFileSelectionDialog dialog) {
		this.dialog = dialog;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	    File f = (File) dialog.getFileSystemList().getSelectedValue();
	    dialog.setSelectionText(f);
	    if (e.getClickCount() == 2) {
	        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) dialog.getFileSystemTree().getSelectionPath().getLastPathComponent();
	        TreePath path = new TreePath(parentNode.getPath());
	        dialog.getFileSystemTree().expandPath(path);
	        int i = 0;
	        DefaultMutableTreeNode childToSelect = null;
	        while (i < parentNode.getChildCount() || childToSelect == null) {
	            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
	            if (((CustomFileSelectionDialogDirectory) child.getUserObject()).getFile().equals(f)) {
	                childToSelect = child;
	            }
	            i++;
	        }
	        TreeNode[] newPath = new TreeNode[parentNode.getPath().length + 1];
	        for (int j = 0; j < parentNode.getPath().length; j++) {
	            newPath[j] = parentNode.getPath()[j];
	        }
	        newPath[parentNode.getPath().length] = childToSelect;

	        dialog.getFileSystemTree().setSelectionPath(new TreePath(newPath));
	    }
	}
}