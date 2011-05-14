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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import net.sourceforge.atunes.utils.GuiUtils;

/**
 * The Class CustomModalFrame.
 */
public abstract class AbstractCustomModalFrame extends JFrame {

    private static final long serialVersionUID = -2809415541883627950L;

    /** The mouse listener. */
    private static MouseListener mouseListener = new MouseAdapter() {
        // Nothing to do
    };

    /** The owner. */
    private JFrame owner;

    /**
     * Instantiates a new custom modal frame.
     * 
     * @param owner
     *            the owner
     */
    public AbstractCustomModalFrame(JFrame owner, int width, int height) {
        super();
        setSize(width, height);
        // Use normal frame decoration to allow maximize window
        //        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        GuiUtils.addAppIcons(this);
        setLocationRelativeTo(owner);
        this.owner = owner;
    }

    /**
     * Enable close action with escape key.
     */
    public void enableCloseActionWithEscapeKey() {
        GuiUtils.addCloseActionWithEscapeKey(this, getRootPane());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Window#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (owner == null) {
            return;
        }

        if (b) {
            owner.getGlassPane().addMouseListener(mouseListener);
        } else {
            owner.getGlassPane().removeMouseListener(mouseListener);
        }

        owner.getGlassPane().setVisible(b);
    }
    
    @Override
    public void dispose() {
    	owner.getGlassPane().removeMouseListener(mouseListener);
    	super.dispose();
    }

}
