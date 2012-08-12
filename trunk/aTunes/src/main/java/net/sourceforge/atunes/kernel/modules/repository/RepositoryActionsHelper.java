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

import javax.swing.Action;

public class RepositoryActionsHelper {
	
	private Action addFolderToRepositoryAction;
	private Action refreshRepositoryAction;
	private Action importToRepositoryAction;
	private Action exportAction;
	private Action connectDeviceAction;
	private Action ripCDAction;
	private Action refreshFolderFromNavigatorAction;

	/**
	 * @param addFolderToRepositoryAction
	 */
	public void setAddFolderToRepositoryAction(Action addFolderToRepositoryAction) {
		this.addFolderToRepositoryAction = addFolderToRepositoryAction;
	}
	
	/**
	 * @param refreshFolderFromNavigatorAction
	 */
	public void setRefreshFolderFromNavigatorAction(
			Action refreshFolderFromNavigatorAction) {
		this.refreshFolderFromNavigatorAction = refreshFolderFromNavigatorAction;
	}
	
	/**
	 * @param importToRepositoryAction
	 */
	public void setImportToRepositoryAction(Action importToRepositoryAction) {
		this.importToRepositoryAction = importToRepositoryAction;
	}
	
	/**
	 * @param exportAction
	 */
	public void setExportAction(Action exportAction) {
		this.exportAction = exportAction;
	}
	
	/**
	 * @param connectDeviceAction
	 */
	public void setConnectDeviceAction(Action connectDeviceAction) {
		this.connectDeviceAction = connectDeviceAction;
	}
	
	/**
	 * @param ripCDAction
	 */
	public void setRipCDAction(Action ripCDAction) {
		this.ripCDAction = ripCDAction;
	}
	/**
	 * @param refreshRepositoryAction
	 */
	public void setRefreshRepositoryAction(Action refreshRepositoryAction) {
		this.refreshRepositoryAction = refreshRepositoryAction;
	}
	
    /**
     * Enables or disables actions that can't be performed while loading
     * repository
     * 
     * @param enable
     */
    public void enableRepositoryActions(boolean enable) {
        addFolderToRepositoryAction.setEnabled(enable);
        refreshFolderFromNavigatorAction.setEnabled(enable);
        importToRepositoryAction.setEnabled(enable);
        exportAction.setEnabled(enable);
        connectDeviceAction.setEnabled(enable);
        ripCDAction.setEnabled(enable);
        refreshRepositoryAction.setEnabled(enable);
    }
}
