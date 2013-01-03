/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClipboardFacade;

/**
 * Binds a text component with a popup with text actions
 * 
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
	private final ClipboardFacade clipboard;
	private final IOSManager osManager;

	/**
	 * @param osManager
	 * @param menu
	 * @param textComponent
	 * @param copyAction
	 * @param deleteAction
	 * @param selectAllAction
	 * @param pasteAction
	 * @param cutAction
	 * @param clipboard
	 */
	TextComponentPopupMenuMouseAdapter(final IOSManager osManager,
			final JPopupMenu menu, final JTextComponent textComponent,
			final Action copyAction, final Action deleteAction,
			final Action selectAllAction, final Action pasteAction,
			final Action cutAction, final ClipboardFacade clipboard) {
		this.osManager = osManager;
		this.menu = menu;
		this.textComponent = textComponent;
		this.copyAction = copyAction;
		this.deleteAction = deleteAction;
		this.selectAllAction = selectAllAction;
		this.pasteAction = pasteAction;
		this.cutAction = cutAction;
		this.clipboard = clipboard;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		super.mouseClicked(e);
		if (GuiUtils.isSecondaryMouseButton(this.osManager, e)
				&& e.getComponent().isEnabled()) {
			// Cut and delete if text selected and component editable
			boolean textSelected = this.textComponent.getSelectionStart() < this.textComponent
					.getSelectionEnd();
			this.cutAction.setEnabled(textSelected
					&& this.textComponent.isEditable());
			this.deleteAction.setEnabled(textSelected
					&& this.textComponent.isEditable());

			// Copy if text selected
			this.copyAction.setEnabled(textSelected);

			// Paste if clipboard contains text and component editable
			this.pasteAction.setEnabled(this.clipboard.clipboardContainsText()
					&& this.textComponent.isEditable());

			// Select all if text field contains text
			this.selectAllAction.setEnabled(this.textComponent.getText()
					.length() > 0);

			// Show menu
			this.menu.show(this.textComponent, e.getX(), e.getY());
		}
	}
}