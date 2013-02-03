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

package net.sourceforge.atunes.kernel.modules.process;

import java.util.List;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * A process to copy files to repository
 * 
 * @author alex
 * 
 */
public class TransferToRepositoryProcess extends
		AbstractLocalAudioObjectTransferProcess {

	private IRepositoryHandler repositoryHandler;

	private IProcessListener<List<ILocalAudioObject>> importToRepositoryProcessListener;

	/**
	 * @param importToRepositoryProcessListener
	 */
	public void setImportToRepositoryProcessListener(
			final IProcessListener<List<ILocalAudioObject>> importToRepositoryProcessListener) {
		this.importToRepositoryProcessListener = importToRepositoryProcessListener;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	public String getProgressDialogTitle() {
		return I18nUtils.getString("COPYING_TO_REPOSITORY");
	}

	@Override
	protected String getDestination() {
		return this.repositoryHandler.getRepositoryPath();
	}

	@Override
	public void setFilesToTransfer(final List<ILocalAudioObject> filesToTransfer) {
		super.setFilesToTransfer(filesToTransfer);
		addProcessListener(this.importToRepositoryProcessListener);
	}

	@Override
	protected String getFileNamePattern() {
		return getStateRepository().getImportFileNamePattern();
	}

	@Override
	protected String getFolderPathPattern() {
		return getStateRepository().getImportFolderPathPattern();
	}
}
