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

package net.sourceforge.atunes.kernel.modules.repository;

import javax.swing.Action;

import net.sourceforge.atunes.model.IRepository;

/**
 * Enables or disables actions related to repository
 * 
 * @author alex
 * 
 */
public class RepositoryActionsHelper {

	private Action addFolderToRepositoryAction;
	private Action refreshRepositoryAction;
	private Action importToRepositoryAction;
	private Action connectDeviceAction;
	private Action ripCDAction;
	private Action refreshFolderFromNavigatorAction;

	/**
	 * @param addFolderToRepositoryAction
	 */
	public void setAddFolderToRepositoryAction(
			final Action addFolderToRepositoryAction) {
		this.addFolderToRepositoryAction = addFolderToRepositoryAction;
	}

	/**
	 * @param refreshFolderFromNavigatorAction
	 */
	public void setRefreshFolderFromNavigatorAction(
			final Action refreshFolderFromNavigatorAction) {
		this.refreshFolderFromNavigatorAction = refreshFolderFromNavigatorAction;
	}

	/**
	 * @param importToRepositoryAction
	 */
	public void setImportToRepositoryAction(
			final Action importToRepositoryAction) {
		this.importToRepositoryAction = importToRepositoryAction;
	}

	/**
	 * @param connectDeviceAction
	 */
	public void setConnectDeviceAction(final Action connectDeviceAction) {
		this.connectDeviceAction = connectDeviceAction;
	}

	/**
	 * @param ripCDAction
	 */
	public void setRipCDAction(final Action ripCDAction) {
		this.ripCDAction = ripCDAction;
	}

	/**
	 * @param refreshRepositoryAction
	 */
	public void setRefreshRepositoryAction(final Action refreshRepositoryAction) {
		this.refreshRepositoryAction = refreshRepositoryAction;
	}

	/**
	 * Disables all actions to repository
	 */
	public void disableAllRepositoryActions() {
		this.addFolderToRepositoryAction.setEnabled(false);
		this.refreshFolderFromNavigatorAction.setEnabled(false);
		this.importToRepositoryAction.setEnabled(false);
		this.connectDeviceAction.setEnabled(false);
		this.ripCDAction.setEnabled(false);
		this.refreshRepositoryAction.setEnabled(false);
	}

	/**
	 * Enables repository actions for current repository
	 */
	public void enableRepositoryActions() {
		this.addFolderToRepositoryAction.setEnabled(true);
		this.refreshFolderFromNavigatorAction.setEnabled(true);
		this.importToRepositoryAction.setEnabled(true);
		this.connectDeviceAction.setEnabled(true);
		this.ripCDAction.setEnabled(true);
		this.refreshRepositoryAction.setEnabled(true);
	}

	/**
	 * Disable all repository actions except the one to add a folder to
	 * repository
	 */
	public void onlyAllowAddFolderToRepository() {
		this.addFolderToRepositoryAction.setEnabled(true);
		this.refreshFolderFromNavigatorAction.setEnabled(false);
		this.importToRepositoryAction.setEnabled(false);
		this.connectDeviceAction.setEnabled(false);
		this.ripCDAction.setEnabled(false);
		this.refreshRepositoryAction.setEnabled(false);
	}

	/**
	 * Enables actions or allow only add folders depending if repository is null
	 * or void
	 * 
	 * @param repository
	 */
	public void enableActionsDependingOnRepository(final IRepository repository) {
		if (repository != null && !(repository instanceof VoidRepository)) {
			enableRepositoryActions();
		} else {
			onlyAllowAddFolderToRepository();
		}
	}
}
