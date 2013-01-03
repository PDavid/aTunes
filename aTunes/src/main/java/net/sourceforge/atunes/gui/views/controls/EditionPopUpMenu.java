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

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.text.JTextComponent;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClipboardFacade;

/**
 * Common popup menu for text components with cut, copy, paste... options
 * 
 * @author fleax
 */
public class EditionPopUpMenu extends JPopupMenu {

	private static final long serialVersionUID = -3103254879422133063L;

	/**
	 * @param textComponent
	 * @param clipboard
	 * @param osManager
	 */
	EditionPopUpMenu(final JTextComponent textComponent,
			final ClipboardFacade clipboard, final IOSManager osManager) {
		super();
		Action cutAction = new TextComponentCutAction(textComponent, clipboard,
				osManager);
		Action copyAction = new TextComponentCopyAction(textComponent,
				clipboard, osManager);
		Action pasteAction = new TextComponentPasteAction(textComponent,
				clipboard, osManager);
		Action deleteAction = new TextComponentDeleteAction(textComponent);
		Action selectAllAction = new TextComponentSelectAllAction(textComponent);
		addPopUpMenu(textComponent, cutAction, copyAction, pasteAction,
				deleteAction, selectAllAction, clipboard, osManager);
	}

	/**
	 * Adds popup menu with edition options
	 * 
	 * @param textComponent
	 * @param cutAction
	 * @param copyAction
	 * @param pasteAction
	 * @param deleteAction
	 * @param selectAllAction
	 * @param clipboard
	 * @param osManager
	 */
	private void addPopUpMenu(final JTextComponent textComponent,
			final Action cutAction, final Action copyAction,
			final Action pasteAction, final Action deleteAction,
			final Action selectAllAction, final ClipboardFacade clipboard,
			final IOSManager osManager) {
		add(cutAction);
		add(copyAction);
		add(pasteAction);
		add(deleteAction);
		add(new JSeparator());
		add(selectAllAction);
		textComponent.addMouseListener(new TextComponentPopupMenuMouseAdapter(
				osManager, this, textComponent, copyAction, deleteAction,
				selectAllAction, pasteAction, cutAction, clipboard));
	}
}
