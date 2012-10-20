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

package net.sourceforge.atunes.kernel.actions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryActionsHelper;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Moves a folder
 * 
 * @author fleax
 * 
 */
public class MoveFolderFromNavigatorAction extends AbstractActionOverSelectedTreeObjects<IFolder> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6342857363521789862L;

	private IRepositoryHandler repositoryHandler;

	private IStateNavigation stateNavigation;

	private IDialogFactory dialogFactory;

	private IOSManager osManager;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IIndeterminateProgressDialog indeterminateDialog;

	private RepositoryActionsHelper repositoryActions;

	/**
	 * @param repositoryActions
	 */
	public void setRepositoryActions(final RepositoryActionsHelper repositoryActions) {
		this.repositoryActions = repositoryActions;
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(
			final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
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
	public MoveFolderFromNavigatorAction() {
		super(I18nUtils.getString("MOVE_FOLDER"));
		putValue(SHORT_DESCRIPTION, I18nUtils.getString("MOVE_FOLDER"));
	}

	@Override
	protected void executeAction(final List<IFolder> folders) {
		repositoryActions.enableRepositoryActions(false);
		final IFolder sourceFolder = folders.get(0);
		final File sourceFile = sourceFolder.getFolderPath(osManager);
		IFolderSelectorDialog dialog = dialogFactory.newDialog(IFolderSelectorDialog.class);
		dialog.setTitle(I18nUtils.getString("MOVE_FOLDER_TO"));
		final File destination = dialog.selectFolder(sourceFile.getParentFile());
		if (destination != null) {
			if (isValidDestination(sourceFolder, destination)) {
				indeterminateDialog = dialogFactory.newDialog(IIndeterminateProgressDialog.class);
				IBackgroundWorker<Boolean> worker = backgroundWorkerFactory.getWorker();
				worker.setActionsBeforeBackgroundStarts(new Runnable() {
					@Override
					public void run() {
						indeterminateDialog.showDialog();
					}
				});
				worker.setBackgroundActions(new Callable<Boolean>() {

					@Override
					public Boolean call() {
						try {
							org.apache.commons.io.FileUtils.moveDirectoryToDirectory(sourceFile, destination, true);
							return true;
						} catch (IOException e) {
							Logger.error(e);
							return false;
						}
					}
				});
				worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<Boolean>() {

					@Override
					public void call(final Boolean result) {
						repositoryActions.enableRepositoryActions(true);
						if (result) {
							repositoryHandler.folderMoved(sourceFolder, new File(StringUtils.getString(FileUtils.getPath(destination), osManager.getFileSeparator(), sourceFile.getName())));
							indeterminateDialog.hideDialog();
						} else {
							indeterminateDialog.hideDialog();
							dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(I18nUtils.getString("ERRORS_IN_MOVE_PROCESS"));
						}
					}
				});
				worker.execute();
			} else {
				IErrorDialog errorDialog = dialogFactory.newDialog(IErrorDialog.class);
				errorDialog.showErrorDialog(I18nUtils.getString("WRONG_MOVE_DESTINATION"));
			}
		}
	}

	/**
	 * @param sourceFolder
	 * @param destination
	 * @return
	 */
	private boolean isValidDestination(final IFolder sourceFolder, final File destination) {
		// Check destination is not parent folder (so we are moving to the same place)
		// and destination is not the folder itself
		File source = sourceFolder.getFolderPath(osManager);
		File sourceParent = source.getParentFile();
		return !FileUtils.getPath(source).equals(FileUtils.getPath(destination)) &&
		!FileUtils.getPath(sourceParent).equals(FileUtils.getPath(destination));
	}

	@Override
	public boolean isEnabledForNavigationTreeSelection(final boolean rootSelected, final List<ITreeObject<?>> selection) {
		if (selection.size() != 1) {
			return false;
		}

		if (stateNavigation.getViewMode() != ViewMode.FOLDER) {
			return false;
		}

		for (ITreeObject<?> node : selection) {
			if (!(node instanceof IFolder)) {
				return false;
			}
		}

		return true;
	}
}
