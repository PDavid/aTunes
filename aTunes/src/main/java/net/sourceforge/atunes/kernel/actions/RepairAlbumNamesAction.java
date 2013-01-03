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
 * This action invokes process to repair album names in repository
 * 
 * @author fleax
 * 
 */
public class RepairAlbumNamesAction extends CustomAbstractAction {

	private final class FilesWithEmptyAlbumFilter implements Predicate<ILocalAudioObject> {
		@Override
		public boolean apply(@Nonnull final ILocalAudioObject ao) {
			return ao.getAlbum(unknownObjectChecker) == null || unknownObjectChecker.isUnknownAlbum(ao.getAlbum(unknownObjectChecker)) || ao.getAlbum(unknownObjectChecker).isEmpty();
		}
	}

	private static final long serialVersionUID = -7828819966696617838L;


	private IRepositoryHandler repositoryHandler;

	private IProcessFactory processFactory;

	private IUnknownObjectChecker unknownObjectChecker;

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
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
	 * Default constructor
	 */
	public RepairAlbumNamesAction() {
		super(I18nUtils.getString("REPAIR_ALBUM_NAMES"));
	}

	@Override
	protected void executeAction() {
		// Show confirmation dialog
		IConfirmationDialog dialog = dialogFactory.newDialog(IConfirmationDialog.class);
		dialog.setMessage(I18nUtils.getString("REPAIR_ALBUM_NAMES_MESSAGE"));
		dialog.showDialog();
		if (dialog.userAccepted()) {
			// Call album name edit
			IChangeTagsProcess process = (IChangeTagsProcess) processFactory.getProcessByName("setAlbumNamesProcess");
			process.setFilesToChange(getFilesWithEmptyAlbum(repositoryHandler.getAudioFilesList()));
			process.execute();
		}
	}

	/**
	 * Returns files without album
	 * @param audioFiles
	 * @return
	 */
	private Collection<ILocalAudioObject> getFilesWithEmptyAlbum(final Collection<ILocalAudioObject> audioFiles) {
		return Collections2.filter(audioFiles, new FilesWithEmptyAlbumFilter());
	}
}
