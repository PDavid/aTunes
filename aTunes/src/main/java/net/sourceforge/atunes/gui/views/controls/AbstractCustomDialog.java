/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.utils.GuiUtils;

public abstract class AbstractCustomDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new custom modal dialog.
     * 
     * @param owner
     * @param width
     * @param height
     * @param modal
     * @param disposeOnClose
     */
    public AbstractCustomDialog(Window owner, int width, int height, boolean modal, boolean disposeOnClose) {
        super(owner);
        setSize(width, height);
        setUndecorated(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().isDialogUndecorated());
        setModalityType(modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        setLocationRelativeTo(owner.getWidth() == 0 ? null : owner);
        setDefaultCloseOperation(disposeOnClose ? WindowConstants.DISPOSE_ON_CLOSE : WindowConstants.HIDE_ON_CLOSE);
        if (disposeOnClose) {
        	enableDisposeActionWithEscapeKey();
        } else {
        	enableCloseActionWithEscapeKey();
        }
    }

    /**
     * Enable close action with escape key.
     */
    private void enableCloseActionWithEscapeKey() {
        GuiUtils.addCloseActionWithEscapeKey(this, getRootPane());
    }

    /**
     * Enable dispose action with escape key.
     */
    private void enableDisposeActionWithEscapeKey() {
        GuiUtils.addDisposeActionWithEscapeKey(this, getRootPane());
    }

    @Override
    public Component add(Component comp) {
    	if (comp instanceof JComponent) {
    		((JComponent)comp).setOpaque(false);
    	}
        Component c = super.add(comp);
        GuiUtils.applyComponentOrientation(this);
    	return c;
    }    
}