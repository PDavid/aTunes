/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action shows full screen mode
 * 
 * @author fleax
 * 
 */
public class FullScreenAction extends Action {

    private static final long serialVersionUID = 916565212685861604L;

    FullScreenAction() {
        super(I18nUtils.getString("FULL_SCREEN"), Images.getImage(Images.FULLSCREEN));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("FULL_SCREEN"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Toggle full screen visibility
        GuiHandler.getInstance().getFullScreenWindow().setVisible(!GuiHandler.getInstance().getFullScreenWindow().isVisible());
    }

    @Override
    public String getCommandName() {
        return "fullscreen";
    }

}
