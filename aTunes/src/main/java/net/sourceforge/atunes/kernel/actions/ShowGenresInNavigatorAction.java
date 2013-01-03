/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Changes view mode to show genres
 * 
 * @author alex
 * 
 */
public class ShowGenresInNavigatorAction extends ActionWithColorMutableIcon {

    private static final long serialVersionUID = 8717980405436543347L;

    private INavigationHandler navigationHandler;

    private IIconFactory genreIcon;

    private IStateNavigation stateNavigation;

    /**
     * @param stateNavigation
     */
    public void setStateNavigation(final IStateNavigation stateNavigation) {
	this.stateNavigation = stateNavigation;
    }

    /**
     * @param genreIcon
     */
    public void setGenreIcon(final IIconFactory genreIcon) {
	this.genreIcon = genreIcon;
    }

    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(final INavigationHandler navigationHandler) {
	this.navigationHandler = navigationHandler;
    }

    /**
     * Default constructor
     */
    public ShowGenresInNavigatorAction() {
	super(I18nUtils.getString("SHOW_GENRE"));
    }

    @Override
    protected void initialize() {
	super.initialize();
	putValue(SELECTED_KEY, stateNavigation.getViewMode() == ViewMode.GENRE);
    }

    @Override
    protected void executeAction() {
	if (stateNavigation.getViewMode() != ViewMode.GENRE) {
	    stateNavigation.setViewMode(ViewMode.GENRE);
	    navigationHandler.refreshCurrentView();
	}
    }

    @Override
    public IColorMutableImageIcon getIcon(final ILookAndFeel lookAndFeel) {
	return genreIcon.getColorMutableIcon();
    }

}
