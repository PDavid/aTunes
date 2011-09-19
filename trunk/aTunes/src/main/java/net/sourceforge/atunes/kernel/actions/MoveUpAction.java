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
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action moves selected rows in play list one row up
 * 
 * @author fleax
 * 
 */
public class MoveUpAction extends CustomAbstractAction {

    private static final long serialVersionUID = 8200322913128575526L;

    MoveUpAction() {
        super(I18nUtils.getString("MOVE_UP"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("MOVE_UP_TOOLTIP"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP, ActionEvent.ALT_MASK));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	Context.getBean(IPlayListHandler.class).moveUp();
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        return !selection.isEmpty();
    }

}
