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
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.I18nUtils;

public class ShowGenresInNavigatorAction extends AbstractAction {

    private static final long serialVersionUID = 8717980405436543347L;

    ShowGenresInNavigatorAction() {
        super(I18nUtils.getString("SHOW_GENRE"), Images.getImage(Images.GENRE));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SHOW_GENRE"));
        putValue(SELECTED_KEY, ApplicationState.getInstance().getViewMode() == ViewMode.GENRE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ApplicationState.getInstance().getViewMode() != ViewMode.GENRE) {
            ApplicationState.getInstance().setViewMode(ViewMode.GENRE);
            NavigationHandler.getInstance().refreshCurrentView();
            Actions.getAction(CollapseTreesAction.class).setEnabled(true);
            Actions.getAction(ExpandTreesAction.class).setEnabled(true);
        }
    }

}
