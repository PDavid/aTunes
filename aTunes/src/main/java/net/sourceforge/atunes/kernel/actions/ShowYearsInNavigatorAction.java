/*
 * aTunes 2.2.0-SNAPSHOT
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

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.DateImageIcon;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

public class ShowYearsInNavigatorAction extends ActionWithColorMutableIcon {

    private static final long serialVersionUID = -1192790723328398881L;

    private INavigationHandler navigationHandler;
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    public ShowYearsInNavigatorAction() {
        super(I18nUtils.getString("SHOW_YEARS"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SHOW_YEARS"));
    }

    @Override
    protected void initialize() {
        putValue(SELECTED_KEY, getState().getViewMode() == ViewMode.YEAR);
    }
    
    @Override
    protected void executeAction() {
        if (getState().getViewMode() != ViewMode.YEAR) {
            getState().setViewMode(ViewMode.YEAR);
            navigationHandler.refreshCurrentView();
        }
    }

    @Override
    public IColorMutableImageIcon getIcon(final ILookAndFeel lookAndFeel) {
    	return new IColorMutableImageIcon() {
			
			@Override
			public ImageIcon getIcon(Paint paint) {
				return DateImageIcon.getIcon(paint, lookAndFeel);
			}
		};
    }
}
