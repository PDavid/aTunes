/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Show or hide navigation table
 * 
 * @author fleax
 * 
 */
public class ShowNavigationTableAction extends AbstractAction {

    private static final long serialVersionUID = -3275592274940501407L;

    ShowNavigationTableAction() {
        super(I18nUtils.getString("SHOW_NAVIGATION_TABLE"), Images.getImage(Images.NAVIGATION_TABLE));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SHOW_NAVIGATION_TABLE"));
        putValue(SELECTED_KEY, ApplicationState.getInstance().isShowNavigationTable());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ApplicationState.getInstance().setShowNavigationTable((Boolean) getValue(SELECTED_KEY));
        GuiHandler.getInstance().showNavigationTable((Boolean) getValue(SELECTED_KEY));
        NavigationHandler.getInstance().updateTableContent(NavigationHandler.getInstance().getCurrentView().getTree());
    }
}
