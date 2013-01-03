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

import net.sourceforge.atunes.model.IUpdateHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Called to search for a new version of the application
 * 
 * @author fleax
 * 
 */
public class CheckUpdatesAction extends CustomAbstractAction {

    private static final long serialVersionUID = 999420226547524484L;

    private IUpdateHandler updateHandler;

    /**
     * @param updateHandler
     */
    public void setUpdateHandler(final IUpdateHandler updateHandler) {
	this.updateHandler = updateHandler;
    }

    CheckUpdatesAction() {
	super(I18nUtils.getString("CHECK_FOR_UPDATES"));
    }

    @Override
    protected void executeAction() {
	updateHandler.checkUpdates(true, true);
    }
}
