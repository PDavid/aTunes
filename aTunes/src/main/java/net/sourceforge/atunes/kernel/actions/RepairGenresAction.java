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

import java.util.Collection;

import javax.annotation.Nonnull;

import net.sourceforge.atunes.model.IChangeTagsProcess;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * This action invokes process to repair genres of repository
 * 
 * @author fleax
 */
public class RepairGenresAction extends CustomAbstractAction {

	private final class FilesWithEmptyGenre implements Predicate<ILocalAudioObject> {

		@Override
		public boolean apply(@Nonnull final ILocalAudioObject ao) {
			return ao.getGenre(unknownObjectChecker) == null || ao.getGenre(unknownObjectChecker).isEmpty();
		}
	}

	private static final long serialVersionUID = -7789897583007508598L;

	private IProcessFactory processFactory;

	private IRepositoryHandler repositoryHandler;

	private IDialogFactory dialogFactory;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(final IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * Constructor
	 */
	public RepairGenresAction() {
		super(I18nUtils.getString("REPAIR_GENRES"));
	}

	@Override
	protected void executeAction() {
		// Show confirmation dialog
		IConfirmationDialog dialog = dialogFactory.newDialog(IConfirmationDialog.class);
		dialog.setMessage(I18nUtils.getString("REPAIR_GENRES_MESSAGE"));
		dialog.showDialog();
		if (dialog.userAccepted()) {
			// Call genre edit
			IChangeTagsProcess process = (IChangeTagsProcess) processFactory.getProcessByName("setGenresProcess");
			process.setFilesToChange(getFilesWithEmptyGenre(repositoryHandler.getAudioFilesList()));
			process.execute();
		}
	}

	/**
	 * Returns files without genre
	 * @param audioFiles
	 * @return
	 */
	private Collection<ILocalAudioObject> getFilesWithEmptyGenre(final Collection<ILocalAudioObject> audioFiles) {
		return Collections2.filter(audioFiles, new FilesWithEmptyGenre());
	}
}
