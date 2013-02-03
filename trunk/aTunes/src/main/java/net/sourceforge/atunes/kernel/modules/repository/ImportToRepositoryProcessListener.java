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

import java.util.List;

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Listener for import to repository
 * 
 * @author alex
 * 
 */
public class ImportToRepositoryProcessListener implements
		IProcessListener<List<ILocalAudioObject>> {

	private IDialogFactory dialogFactory;

	private IRepositoryHandler repositoryHandler;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	public void processCanceled() {
		// Nothing to do
	}

	@Override
	public void processFinished(final boolean ok,
			final List<ILocalAudioObject> result) {
		if (!ok) {
			// Show error message
			this.dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(
					I18nUtils.getString("ERRORS_IN_IMPORT_PROCESS"));
		} else {
			// If import is ok then add files to repository
			this.repositoryHandler.addAudioObjectsAndRefresh(result);
		}
	}
}