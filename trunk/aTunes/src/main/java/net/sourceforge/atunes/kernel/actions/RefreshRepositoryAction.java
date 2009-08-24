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

import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * This action refreshes repository
 * 
 * @author fleax
 * 
 */
public class RefreshRepositoryAction extends Action {

    private static final long serialVersionUID = -5708270585764283210L;

    RefreshRepositoryAction() {
        super(LanguageTool.getString("REFRESH_REPOSITORY"), ImageLoader.getImage(ImageLoader.REFRESH));
        putValue(SHORT_DESCRIPTION, LanguageTool.getString("REFRESH_REPOSITORY"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RepositoryHandler.getInstance().refreshRepository();
    }

}
