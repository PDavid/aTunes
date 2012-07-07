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

package net.sourceforge.atunes.kernel.modules.repository;

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Runnable to execute when import finishes
 * @author alex
 *
 */
public final class ImportToRepositoryProcessFinishedRunnable implements Runnable {
	
    private boolean ok;

    private IDialogFactory dialogFactory;
    
    private IRepositoryHandler repositoryHandler;
    
    /**
     * @param dialogFactory
     */
    public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
    
    /**
     * @param ok
     */
    public void setOk(boolean ok) {
		this.ok = ok;
	}
    
    @Override
    public void run() {
        // Force a refresh of repository to add new songs
        repositoryHandler.refreshRepository();

        if (!ok) {
        	// Show error message
        	dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(I18nUtils.getString("ERRORS_IN_COPYING_PROCESS"));
        }
    }
}