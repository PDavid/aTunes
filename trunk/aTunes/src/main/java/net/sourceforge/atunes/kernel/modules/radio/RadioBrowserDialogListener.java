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

package net.sourceforge.atunes.kernel.modules.radio;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.tree.TreePath;

import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 * The listener interface for receiving radioBrowserDialog events.
 */
final class RadioBrowserDialogListener extends MouseAdapter {

	/** The radio browser dialog. */
	private final RadioBrowserDialog radioBrowserDialog;

	private final IRadioHandler radioHandler;

	/**
	 * Instantiates a new radio browser dialog listener.
	 * 
	 * @param radioBrowserDialog
	 * @param radioHandler
	 */
	RadioBrowserDialogListener(final RadioBrowserDialog radioBrowserDialog,
			final IRadioHandler radioHandler) {
		this.radioBrowserDialog = radioBrowserDialog;
		this.radioHandler = radioHandler;
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (e.getSource() == this.radioBrowserDialog.getTreeTable()) {
			JXTreeTable treeTable = this.radioBrowserDialog.getTreeTable();
			if (e.getClickCount() == 2) {
				TreePath path = treeTable
						.getPathForLocation(e.getX(), e.getY());
				if (path != null) {
					Object objectSelected = ((DefaultMutableTreeTableNode) path
							.getLastPathComponent()).getUserObject();
					if (objectSelected instanceof IRadio) {
						// User selected a radio (not a label node or row)
						this.radioHandler.addRadio((IRadio) objectSelected);
					}
				}
			}
		}
	}
}
