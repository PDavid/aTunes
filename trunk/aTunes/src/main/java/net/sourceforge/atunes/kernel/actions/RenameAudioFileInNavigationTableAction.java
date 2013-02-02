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
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

import org.apache.commons.io.FilenameUtils;

/**
 * Renames an audio file
 * 
 * @author alex
 * 
 */
public class RenameAudioFileInNavigationTableAction extends
		CustomAbstractAction {

	private static final long serialVersionUID = 5607758675193509752L;

	private INavigationHandler navigationHandler;

	private IRepositoryHandler repositoryHandler;

	private IDialogFactory dialogFactory;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * Default constructor
	 */
	public RenameAudioFileInNavigationTableAction() {
		super(I18nUtils.getString("RENAME_AUDIO_FILE_NAME"));
	}

	@Override
	protected void executeAction() {
		List<IAudioObject> audioFiles = navigationHandler
				.getFilesSelectedInNavigator();
		if (audioFiles.size() == 1
				&& audioFiles.get(0) instanceof ILocalAudioObject) {
			ILocalAudioObject ao = (ILocalAudioObject) audioFiles.get(0);
			IInputDialog dialog = dialogFactory.newDialog(IInputDialog.class);
			dialog.setTitle(I18nUtils.getString("RENAME_AUDIO_FILE_NAME"));
			dialog.setText(FilenameUtils.getBaseName(fileManager.getPath(ao)));
			dialog.showDialog();
			String name = dialog.getResult();
			if (name != null && !name.isEmpty()) {
				repositoryHandler.rename(ao, name);
			}
		}
	}

	@Override
	public boolean isEnabledForNavigationTableSelection(
			final List<IAudioObject> selection) {
		return selection.size() == 1;
	}
}
