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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

final class FileSystemTreeTreeSelectionListener implements TreeSelectionListener {
	
	private CustomFileSelectionDialog dialog;
	
	/**
	 * @param dialog
	 */
	FileSystemTreeTreeSelectionListener(CustomFileSelectionDialog dialog) {
		super();
		this.dialog = dialog;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
	    CustomFileSelectionDialogDirectory dir = (CustomFileSelectionDialogDirectory) node.getUserObject();
	    dialog.setSelectionText(dir.getFile());
	    File[] files = dialog.getFiles(dir.getFile());
	    List<File> dirsList = new ArrayList<File>();
	    List<File> filesList = new ArrayList<File>();
	    for (File element : files) {
	        if (element.isDirectory()) {
	            dirsList.add(element);
	        } else {
	            filesList.add(element);
	        }
	    }
	    Collections.sort(dirsList);
	    Collections.sort(filesList);
	    dirsList.addAll(filesList);
	    dialog.getFileSystemList().setListData(dirsList.toArray());
	}
}