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



import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.text.JTextComponent;


/**
 * Common popup menu for text components with cut, copy, paste... options
 * 
 * @author fleax
 */
public class EditionPopUpMenu extends JPopupMenu {

    private static final long serialVersionUID = -3103254879422133063L;

    /**
     * @param textComponent
     */
    public EditionPopUpMenu(JTextComponent textComponent) {
        super();
        Action cutAction = new TextComponentCutAction(textComponent);
        Action copyAction = new TextComponentCopyAction(textComponent);
        Action pasteAction = new TextComponentPasteAction(textComponent);
        Action deleteAction = new TextComponentDeleteAction(textComponent);
        Action selectAllAction = new TextComponentSelectAllAction(textComponent);
        addPopUpMenu(textComponent, cutAction, copyAction, pasteAction, deleteAction, selectAllAction);
    }

    /**
     * Adds popup menu with edition options
     * @param cutAction 
     * @param copyAction 
     * @param pasteAction 
     * @param deleteAction 
     * @param selectAllAction 
     */
    private void addPopUpMenu(JTextComponent textComponent, Action cutAction, Action copyAction, Action pasteAction, Action deleteAction, Action selectAllAction) {
        add(cutAction);
        add(copyAction);
        add(pasteAction);
        add(deleteAction);
        add(new JSeparator());
        add(selectAllAction);
        textComponent.addMouseListener(new TextComponentPopupMenuMouseAdapter(this, textComponent, copyAction, deleteAction, selectAllAction, pasteAction, cutAction));
    }
}
