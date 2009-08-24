/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * This action moves selected rows in play list to bottom
 * 
 * @author fleax
 * 
 */
public class MoveToBottomAction extends Action {

    private static final long serialVersionUID = 8200322913128575526L;

    MoveToBottomAction() {
        super(LanguageTool.getString("MOVE_TO_BOTTOM"), ImageLoader.getImage(ImageLoader.GO_BOTTOM));
        putValue(SHORT_DESCRIPTION, LanguageTool.getString("MOVE_BOTTOM_TOOLTIP"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.ALT_MASK));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ControllerProxy.getInstance().getPlayListController().moveToBottom();
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<AudioObject> selection) {
        return !selection.isEmpty();
    }

}
