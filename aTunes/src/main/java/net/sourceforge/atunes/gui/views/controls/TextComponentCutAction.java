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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClipboardFacade;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Cut action
 */
class TextComponentCutAction extends AbstractAction {

	private static final long serialVersionUID = -499875218309361915L;

	/**
	 * Text component associated
	 */
	private final JTextComponent textComponent;

	private final ClipboardFacade clipboard;

	/**
	 * @param textComponent
	 * @param clipboard
	 * @param osManager
	 */
	public TextComponentCutAction(final JTextComponent textComponent,
			final ClipboardFacade clipboard, final IOSManager osManager) {
		super(I18nUtils.getString("CUT"));
		this.textComponent = textComponent;
		this.clipboard = clipboard;
		putValue(
				ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_X,
						GuiUtils.getCtrlOrMetaActionEventMask(osManager)));
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		String text = this.textComponent.getText();
		String selectedText = text.substring(
				this.textComponent.getSelectionStart(),
				this.textComponent.getSelectionEnd());
		String nonSelectedText = StringUtils.getString(
				text.substring(0, this.textComponent.getSelectionStart()),
				text.substring(this.textComponent.getSelectionEnd()));
		this.clipboard.copyToClipboard(selectedText);
		this.textComponent.setText(nonSelectedText);
	}
}