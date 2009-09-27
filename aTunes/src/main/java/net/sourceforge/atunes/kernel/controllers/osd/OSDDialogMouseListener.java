/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.kernel.controllers.osd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.atunes.gui.views.dialogs.OSDDialog;

/**
 * The listener interface for receiving OSDDialogMouse events.
 */
public class OSDDialogMouseListener extends MouseAdapter {

    /** The osd dialog. */
    private OSDDialog osdDialog;

    /** The osd dialog controller. */
    private OSDDialogController osdDialogController;

    /**
     * Instantiates a new oSD dialog mouse listener.
     * 
     * @param osdDialog
     *            the osd dialog
     * @param osdDialogController
     *            the osd dialog controller
     */
    public OSDDialogMouseListener(OSDDialog osdDialog, OSDDialogController osdDialogController) {
        this.osdDialog = osdDialog;
        this.osdDialogController = osdDialogController;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource().equals(osdDialog)) {
            osdDialogController.stopAnimation();
        }
    }
}
