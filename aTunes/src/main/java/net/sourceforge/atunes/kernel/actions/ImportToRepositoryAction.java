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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISelectorDialog;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action refreshes repository
 * 
 * @author fleax
 * 
 */
public class ImportToRepositoryAction extends CustomAbstractAction {

	private static final long serialVersionUID = -5708270585764283210L;

	private IRepositoryHandler repositoryHandler;

	private IDialogFactory dialogFactory;

	/**
	 * Default constructor
	 */
	public ImportToRepositoryAction() {
		super(StringUtils.getString(I18nUtils.getString("IMPORT"), "..."));
		setEnabled(false); // Initially disabled, will be enabled when
		// repository is loaded
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	@Override
	protected void executeAction() {
		// It's supposed to not happen, but check that repository contains
		// folders
		if (repositoryHandler.getFoldersCount() > 0) {
			IFolderSelectorDialog dialog = dialogFactory
					.newDialog(IFolderSelectorDialog.class);
			dialog.setTitle(I18nUtils.getString("IMPORT"));
			File folder = dialog.selectFolder((String) null);
			if (folder != null) {
				List<File> folders = new ArrayList<File>();
				folders.add(folder);
				repositoryHandler.importFolders(folders, getRepositoryPath());
			}
		} else {
			Logger.error("Importing folders to repository with no folders");
		}
	}

	/**
	 * @return repository path
	 */
	private String getRepositoryPath() {
		String path;
		String[] foldersList = new String[repositoryHandler.getFoldersCount()];
		for (int i = 0; i < repositoryHandler.getFolders().size(); i++) {
			foldersList[i] = FileUtils.getPath(repositoryHandler.getFolders()
					.get(i));
		}
		// If repository folders are more than one then user must select where
		// to import songs
		if (foldersList.length > 1) {
			ISelectorDialog selectorDialog = dialogFactory
					.newDialog(ISelectorDialog.class);
			selectorDialog.setTitle(I18nUtils
					.getString("SELECT_REPOSITORY_FOLDER_TO_IMPORT"));
			selectorDialog.setOptions(foldersList);
			selectorDialog.showDialog();
			path = selectorDialog.getSelection();
			// If user closed dialog then select first entry
			if (path == null) {
				path = foldersList[0];
			}
		} else {
			path = foldersList[0];
		}
		return path;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
}
