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

import javax.swing.JComponent;
import javax.swing.JDialog;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * Abstract dialog
 * @author alex
 *
 */
public abstract class AbstractCustomDialog extends JDialog implements IDialog {

	private static final long serialVersionUID = -4593025984520110706L;
	
	private ILookAndFeelManager lookAndFeelManager;
	
    /**
     * Instantiates a new custom modal dialog.
     * 
     * @param frame
     * @param width
     * @param height
     */
    public AbstractCustomDialog(IFrame frame, int width, int height) {
    	this(frame, width, height, true, CloseAction.DISPOSE);
    }

    /**
     * Instantiates a new custom dialog.
     * 
     * @param frame
     * @param width
     * @param height
     * @param modal
     * @param closeAction
     */
    public AbstractCustomDialog(IFrame frame, int width, int height, boolean modal, CloseAction closeAction) {
        super(frame.getFrame());
		setSize(width, height);
        setLocationRelativeTo(frame.getFrame().getWidth() == 0 ? null : frame.getFrame());
        initializeDialog(modal, closeAction);
    }

	/**
	 * @param modal
	 * @param closeAction
	 */
	private void initializeDialog(boolean modal, CloseAction closeAction) {
        this.lookAndFeelManager = Context.getBean(ILookAndFeelManager.class);
        setUndecorated(getLookAndFeel().isDialogUndecorated());
        setModalityType(modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        setDefaultCloseOperation(closeAction.getConstant());
        if (closeAction == CloseAction.DISPOSE) {
            GuiUtils.addDisposeActionWithEscapeKey(this, getRootPane());
        } else if (closeAction == CloseAction.HIDE) {
            GuiUtils.addCloseActionWithEscapeKey(this, getRootPane());
        }
	}

    /**
     * @return look and feel
     */
    protected ILookAndFeel getLookAndFeel() {
		return lookAndFeelManager.getCurrentLookAndFeel();
	}
    
    /**
     * @return look and feel manager
     */
    protected ILookAndFeelManager getLookAndFeelManager() {
    	return lookAndFeelManager;
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
    
    @Override
    public void hideDialog() {
    	setVisible(false);
    }
    
    @Override
    public void showDialog() {
    	setVisible(true);
    }
    
    @Override
    public void initialize() {
    	// Empty method to override
    }
}
