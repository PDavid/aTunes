/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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
import net.sourceforge.atunes.model.IDialogFactory;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

final class FillTagsFromFolderNameActionListener implements ActionListener {

	private final ReviewImportDialog dialog;

	private final IDialogFactory dialogFactory;

	private final Patterns patterns;

	private final PatternMatcher patternMatcher;

	/**
	 * @param dialog
	 * @param dialogFactory
	 * @param patterns
	 * @param patternMatcher
	 */
	FillTagsFromFolderNameActionListener(final ReviewImportDialog dialog, final IDialogFactory dialogFactory, final Patterns patterns, final PatternMatcher patternMatcher) {
		super();
		this.dialog = dialog;
		this.dialogFactory = dialogFactory;
		this.patterns = patterns;
		this.patternMatcher = patternMatcher;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		TreePath[] selectedNodes = dialog.getTreeTable().getTreeSelectionModel().getSelectionPaths();
		if (selectedNodes.length > 0) {
			PatternInputDialog inputDialog = dialogFactory.newDialog("massivePatternInputDialog", PatternInputDialog.class);
			Object node = selectedNodes[0].getLastPathComponent();
			Object folder = ((DefaultMutableTreeTableNode)node).getUserObject();
			inputDialog.show(patterns.getMassiveRecognitionPatterns(), net.sourceforge.atunes.utils.FileUtils.getPath(((File)folder)));
			String pattern = inputDialog.getResult();
			for (TreePath treePath : selectedNodes) {
				node = treePath.getLastPathComponent();
				folder = ((DefaultMutableTreeTableNode)node).getUserObject();
				Map<String, String> matches = patternMatcher.getPatternMatches(pattern, net.sourceforge.atunes.utils.FileUtils.getPath(((File)folder)), true);
				for (Entry<String, String> entry : matches.entrySet()) {
					((ReviewImportTreeTableModel) dialog.getTreeTable().getTreeTableModel()).setValueForColumn(dialog.getTreeTable().getRowForPath(treePath), entry.getKey(), entry.getValue());
				}
			}
		}
	}
}