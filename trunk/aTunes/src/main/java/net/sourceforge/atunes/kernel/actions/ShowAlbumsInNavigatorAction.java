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

import java.awt.Paint;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.AlbumImageIcon;
import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

public class ShowAlbumsInNavigatorAction extends ActionWithColorMutableIcon {

    private static final long serialVersionUID = -3691606154694473768L;

    public ShowAlbumsInNavigatorAction() {
        super(I18nUtils.getString("SHOW_ALBUMS"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SHOW_ALBUMS"));
    }

    @Override
    protected void initialize() {
        putValue(SELECTED_KEY, getState().getViewMode() == ViewMode.ALBUM);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (getState().getViewMode() != ViewMode.ALBUM) {
            getState().setViewMode(ViewMode.ALBUM);
            getBean(INavigationHandler.class).refreshCurrentView();
            Actions.getAction(CollapseTreesAction.class).setEnabled(true);
            Actions.getAction(ExpandTreesAction.class).setEnabled(true);
        }
    }

    @Override
    public ColorMutableImageIcon getIcon() {
    	return new ColorMutableImageIcon() {
			
			@Override
			public ImageIcon getIcon(Paint paint) {
				return AlbumImageIcon.getIcon(paint);
			}
		};
    }
}
