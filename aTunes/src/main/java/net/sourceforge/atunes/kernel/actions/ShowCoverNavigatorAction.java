/*
 * aTunes 2.0.0
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

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action shows cover navigator
 * 
 * @author fleax
 * 
 */
public class ShowCoverNavigatorAction extends Action {

    private static final long serialVersionUID = 4927892497869144235L;

    ShowCoverNavigatorAction() {
        super(I18nUtils.getString("COVER_NAVIGATOR"), ImageLoader.getImage(ImageLoader.CD_COVER));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("COVER_NAVIGATOR"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        VisualHandler.getInstance().showCoverNavigator();
    }

}
