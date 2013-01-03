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

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds a radio as favorite
 * 
 * @author alex
 * 
 */
public class AddFavoriteRadioAction extends
	AbstractActionOverSelectedObjects<IRadio> {

    private static final long serialVersionUID = 82199784140877040L;

    private IRadioHandler radioHandler;

    /**
     * @param radioHandler
     */
    public void setRadioHandler(final IRadioHandler radioHandler) {
	this.radioHandler = radioHandler;
    }

    /**
     * Default constructor
     */
    public AddFavoriteRadioAction() {
	super(I18nUtils.getString("ADD_FAVORITE_RADIO"));
    }

    @Override
    protected void initialize() {
	super.initialize();
	putValue(SHORT_DESCRIPTION, I18nUtils.getString("RADIO_VIEW_TOOLTIP"));
    }

    @Override
    protected void executeAction(final List<IRadio> objects) {
	for (IRadio r : objects) {
	    radioHandler.addRadio(r);
	}
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	if (rootSelected) {
	    return false;
	}

	for (ITreeNode node : selection) {
	    if (!(node.getUserObject() instanceof IRadio)) {
		return false;
	    }
	}

	return true;
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(
	    final List<IAudioObject> selection) {
	return true;
    }
}
