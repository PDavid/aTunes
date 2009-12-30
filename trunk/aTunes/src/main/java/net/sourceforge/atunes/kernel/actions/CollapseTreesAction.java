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

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationView;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public class CollapseTreesAction extends Action {

    private static final long serialVersionUID = 4230335834253793622L;

    CollapseTreesAction() {
        super(I18nUtils.getString("COLLAPSE"), Images.getImage(Images.COLLAPSE));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("COLLAPSE"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (NavigationView view : NavigationHandler.getInstance().getNavigationViews()) {
            GuiUtils.collapseTree(view.getTree());
        }
    }

}
