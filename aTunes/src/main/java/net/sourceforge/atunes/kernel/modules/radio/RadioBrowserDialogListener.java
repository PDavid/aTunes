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

package net.sourceforge.atunes.kernel.modules.radio;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 * The listener interface for receiving radioBrowserDialog events.
 */
final class RadioBrowserDialogListener extends MouseAdapter {

    /** The radio browser dialog. */
    private RadioBrowserDialog radioBrowserDialog;
    
    private IRadioHandler radioHandler;

    /**
     * Instantiates a new radio browser dialog listener.
     * 
     * @param radioBrowserDialog
     * @param radioHandler
     */
    RadioBrowserDialogListener(RadioBrowserDialog radioBrowserDialog, IRadioHandler radioHandler) {
        this.radioBrowserDialog = radioBrowserDialog;
        this.radioHandler = radioHandler;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == radioBrowserDialog.getTreeTable()) {
            JXTreeTable treeTable = radioBrowserDialog.getTreeTable();
            if (e.getClickCount() == 2) {
                IRadio radio = (IRadio) ((DefaultMutableTreeTableNode) treeTable.getPathForLocation(e.getX(), e.getY()).getLastPathComponent()).getUserObject();
                radioHandler.addRadio(radio);
            }

        }
    }
}
