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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.GuiUtils;

public abstract class AbstractCustomDialog extends JDialog {

	protected ILookAndFeel lookAndFeel;
	
	protected enum CloseAction {
		
		DISPOSE(WindowConstants.DISPOSE_ON_CLOSE), HIDE(WindowConstants.HIDE_ON_CLOSE), NOTHING(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		private int constant;
		
		private CloseAction(int constant) {
			this.constant = constant;
		}
		
		public int getConstant() {
			return constant;
		}
	}
	
    private static final long serialVersionUID = 1L;

    /**
     * Convenience constructor, dialog is not closed if user presses close button
     * Use this when dialog has a "cancel" or "close" button
     * @param owner
     * @param width
     * @param height
     * @param modal
     * @param lookAndFeel
     */
    public AbstractCustomDialog(Window owner, int width, int height, boolean modal, ILookAndFeel lookAndFeel) {
    	this(owner, width, height, modal, CloseAction.NOTHING, lookAndFeel);
    }
    
    /**
     * Instantiates a new custom modal dialog.
     * 
     * @param owner
     * @param width
     * @param height
     * @param modal
     * @param closeAction
     * @param lookAndFeel
     */
    @Deprecated
    public AbstractCustomDialog(Window owner, int width, int height, boolean modal, CloseAction closeAction, ILookAndFeel lookAndFeel) {
        super(owner);
        setSize(width, height);
        this.lookAndFeel = lookAndFeel;
        setUndecorated(lookAndFeel.isDialogUndecorated());
        setModalityType(modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        setLocationRelativeTo(owner.getWidth() == 0 ? null : owner);
        setDefaultCloseOperation(closeAction.getConstant());
        if (closeAction == CloseAction.DISPOSE) {
        	enableDisposeActionWithEscapeKey();
        } else if (closeAction == CloseAction.HIDE) {
        	enableCloseActionWithEscapeKey();
        }
    }
    
    /**
     * Instantiates a new custom modal dialog.
     * 
     * @param frame
     * @param width
     * @param height
     * @param modal
     * @param closeAction
     * @param lookAndFeel
     */
    public AbstractCustomDialog(IFrame frame, int width, int height, boolean modal, CloseAction closeAction, ILookAndFeel lookAndFeel) {
        super(frame.getFrame());
        setSize(width, height);
        this.lookAndFeel = lookAndFeel;
        setUndecorated(lookAndFeel.isDialogUndecorated());
        setModalityType(modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        setLocationRelativeTo(frame.getFrame().getWidth() == 0 ? null : frame.getFrame());
        setDefaultCloseOperation(closeAction.getConstant());
        if (closeAction == CloseAction.DISPOSE) {
        	enableDisposeActionWithEscapeKey();
        } else if (closeAction == CloseAction.HIDE) {
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
