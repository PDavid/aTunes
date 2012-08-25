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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.utils.ClipboardFacade;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Paste action
 */
class TextComponentPasteAction extends AbstractAction {

    private static final long serialVersionUID = -7600198128040448381L;

    /**
     * Text component associated
     */
    private JTextComponent textComponent;

    public TextComponentPasteAction(JTextComponent textComponent) {
        super(I18nUtils.getString("PASTE"));
        this.textComponent = textComponent;
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, GuiUtils.getCtrlOrMetaActionEventMask()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = this.textComponent.getText();
        String copiedText = Context.getBean(ClipboardFacade.class).getClipboardContent();
        String newText = StringUtils.getString(text.substring(0, this.textComponent.getSelectionStart()), copiedText, text.substring(this.textComponent.getSelectionEnd()));
        this.textComponent.setText(newText);
    }
}