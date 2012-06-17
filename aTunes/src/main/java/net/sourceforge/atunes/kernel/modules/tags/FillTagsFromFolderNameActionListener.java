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

package net.sourceforge.atunes.kernel.modules.tags;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.pattern.PatternInputDialog;
import net.sourceforge.atunes.kernel.modules.pattern.PatternMatcher;
import net.sourceforge.atunes.kernel.modules.pattern.Patterns;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

final class FillTagsFromFolderNameActionListener implements ActionListener {
	
	private ReviewImportDialog dialog;
	
	/**
	 * @param dialog
	 */
	FillTagsFromFolderNameActionListener(ReviewImportDialog dialog) {
		super();
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    TreePath[] selectedNodes = dialog.getTreeTable().getTreeSelectionModel().getSelectionPaths();
	    if (selectedNodes.length > 0) {
	        PatternInputDialog inputDialog = new PatternInputDialog(dialog, true, dialog.getStateRepository());
	        Object node = selectedNodes[0].getLastPathComponent();
	        Object folder = ((DefaultMutableTreeTableNode)node).getUserObject();
	        inputDialog.show(Patterns.getMassiveRecognitionPatterns(), ((File)folder).getAbsolutePath());
	        String pattern = inputDialog.getResult();
	        for (TreePath treePath : selectedNodes) {
	            node = treePath.getLastPathComponent();                        
	            folder = ((DefaultMutableTreeTableNode)node).getUserObject();
	            Map<String, String> matches = PatternMatcher.getPatternMatches(pattern, ((File)folder).getAbsolutePath(), true);
	            for (Entry<String, String> entry : matches.entrySet()) {
	                ((ReviewImportTreeTableModel) dialog.getTreeTable().getTreeTableModel()).setValueForColumn(dialog.getTreeTable().getRowForPath(treePath), entry.getKey(), entry.getValue());
	            }
	        }
	    }
	}
}