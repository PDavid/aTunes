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
package net.sourceforge.atunes.gui.views.controls;

import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.utils.GuiUtils;

public abstract class CustomModalDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new custom modal dialog.
     * 
     * @param owner
     *            the owner
     * @param width
     *            the width
     * @param height
     *            the height
     * @param modal
     *            the modal
     */
    public CustomModalDialog(Window owner, int width, int height, boolean modal) {
        super(owner);
        setSize(width, height);
        setUndecorated(true);
        setModalityType(modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }

    /**
     * Enable close action with escape key.
     */
    public void enableCloseActionWithEscapeKey() {
        GuiUtils.addCloseActionWithEscapeKey(this, getRootPane());
    }

    /**
     * Enable dispose action with escape key.
     */
    public void enableDisposeActionWithEscapeKey() {
        GuiUtils.addDisposeActionWithEscapeKey(this, getRootPane());
    }

    /**
     * Sets the content.
     * 
     * @param content
     *            the new content
     */
    public void setContent(JPanel content) {
        content.setOpaque(false);
        content.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        add(content);
    }

}
