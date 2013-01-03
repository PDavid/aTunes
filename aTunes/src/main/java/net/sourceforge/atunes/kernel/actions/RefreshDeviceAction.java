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

import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Action to refresh device
 * 
 * @author fleax
 * 
 */
public class RefreshDeviceAction extends CustomAbstractAction {

    private static final long serialVersionUID = -5047885921099142L;

    private IDeviceHandler deviceHandler;

    /**
     * @param deviceHandler
     */
    public void setDeviceHandler(final IDeviceHandler deviceHandler) {
	this.deviceHandler = deviceHandler;
    }

    /**
     * Default constructor
     */
    public RefreshDeviceAction() {
	super(I18nUtils.getString("REFRESH"));
	setEnabled(false);
    }

    @Override
    protected void executeAction() {
	deviceHandler.refreshDevice();
    }

}
