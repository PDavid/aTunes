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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.utils.ClipboardFacade;

/**
 * Binds a text component with a popup with text actions
 * @author alex
 *
 */
final class TextComponentPopupMenuMouseAdapter extends MouseAdapter {
	
	private final JPopupMenu menu;
	private final JTextComponent textComponent;
    private final Action copyAction;
    private final Action deleteAction;
    private final Action selectAllAction;
    private final Action pasteAction;
    private final Action cutAction;

    /**
     * @param menu
     * @param textComponent
     * @param copyAction
     * @param deleteAction
     * @param selectAllAction
     * @param pasteAction
     * @param cutAction
     */
    TextComponentPopupMenuMouseAdapter(JPopupMenu menu, JTextComponent textComponent, Action copyAction, Action deleteAction, Action selectAllAction, Action pasteAction, Action cutAction) {
    	this.menu = menu;
    	this.textComponent = textComponent;
        this.copyAction = copyAction;
        this.deleteAction = deleteAction;
        this.selectAllAction = selectAllAction;
        this.pasteAction = pasteAction;
        this.cutAction = cutAction;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (GuiUtils.isSecondaryMouseButton(e) && e.getComponent().isEnabled()) {
            // Cut and delete if text selected and component editable
            boolean textSelected = textComponent.getSelectionStart() < textComponent.getSelectionEnd();
            cutAction.setEnabled(textSelected && textComponent.isEditable());
            deleteAction.setEnabled(textSelected && textComponent.isEditable());

            // Copy if text selected
            copyAction.setEnabled(textSelected);

            // Paste if clipboard contains text and component editable
            pasteAction.setEnabled(Context.getBean(ClipboardFacade.class).clipboardContainsText() && textComponent.isEditable());

            // Select all if text field contains text
            selectAllAction.setEnabled(textComponent.getText().length() > 0);

            // Show menu
            menu.show(textComponent, e.getX(), e.getY());
        }
    }
}