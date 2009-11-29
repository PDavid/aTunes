/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.controllers.radioBrowser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.atunes.gui.views.dialogs.RadioBrowserDialog;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 * The listener interface for receiving radioBrowserDialog events.
 */
public final class RadioBrowserDialogListener extends MouseAdapter {

    /** The radio browser dialog. */
    private RadioBrowserDialog radioBrowserDialog;

    /**
     * Instantiates a new radio browser dialog listener.
     * 
     * @param radioBrowserDialog
     *            the radio browser dialog
     */
    public RadioBrowserDialogListener(RadioBrowserDialog radioBrowserDialog) {
        this.radioBrowserDialog = radioBrowserDialog;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == radioBrowserDialog.getTreeTable()) {
            JXTreeTable treeTable = radioBrowserDialog.getTreeTable();
            if (e.getClickCount() == 2) {
                Radio radio = (Radio) ((DefaultMutableTreeTableNode) treeTable.getPathForLocation(e.getX(), e.getY()).getLastPathComponent()).getUserObject();
                RadioHandler.getInstance().addRadio(radio);
            }

        }

    }

}
