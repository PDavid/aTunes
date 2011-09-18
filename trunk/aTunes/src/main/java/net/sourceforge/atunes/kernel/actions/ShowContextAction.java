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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class ShowContextAction extends CustomAbstractAction {

    private static final long serialVersionUID = 5939730387818346294L;

    ShowContextAction() {
        super(I18nUtils.getString("SHOW_CONTEXT_INFORMATION"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SHOW_CONTEXT_INFORMATION"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
    }

    @Override
    protected void initialize() {
        putValue(SELECTED_KEY, getState().isUseContext());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	Context.getBean(IContextHandler.class).showContextPanel((Boolean) getValue(SELECTED_KEY));
    }
}
