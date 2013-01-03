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

import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action enables or disables OSD
 * 
 * @author fleax
 * 
 */
public class ToggleOSDSettingAction extends CustomAbstractAction {

    private static final long serialVersionUID = 646318992035897920L;

    private IStateUI stateUI;

    /**
     * @param stateUI
     */
    public void setStateUI(final IStateUI stateUI) {
	this.stateUI = stateUI;
    }

    /**
     * Default constructor
     */
    public ToggleOSDSettingAction() {
	super(I18nUtils.getString("SHOW_OSD"));
    }

    @Override
    protected void initialize() {
	super.initialize();
	putValue(SELECTED_KEY, stateUI.isShowOSD());
    }

    @Override
    protected void executeAction() {
	stateUI.setShowOSD((Boolean) getValue(SELECTED_KEY));
    }

}
