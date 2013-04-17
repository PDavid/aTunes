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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.File;
import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryAutoRefresher;
import net.sourceforge.atunes.model.IRipperProgressDialog;

/**
 * Rips tracks
 * 
 * @author alex
 * 
 */
public class RipTracksBackgroundWorker extends BackgroundWorker<Boolean, Void> {

	private CdRipper ripper;

	private File folder;

	private boolean useParanoia;

	private IRipperProgressDialog dialog;

	private RepositoryAutoRefresher repositoryRefresher;

	private List<File> filesImported;

	private RipperHandler ripperHandler;

	/**
	 * @param repositoryRefresher
	 */
	public void setRepositoryRefresher(
			RepositoryAutoRefresher repositoryRefresher) {
		this.repositoryRefresher = repositoryRefresher;
	}

	/**
	 * @param ripperHandler
	 */
	public void setRipperHandler(RipperHandler ripperHandler) {
		this.ripperHandler = ripperHandler;
	}

	void ripTracks(CdRipper ripper, IRipperProgressDialog dialog, File folder,
			boolean useParanoia, List<File> filesImported) {
		this.ripper = ripper;
		this.folder = folder;
		this.useParanoia = useParanoia;
		this.dialog = dialog;
		this.filesImported = filesImported;
		execute();
	}

	@Override
	protected void before() {
		dialog.showDialog();
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected Boolean doInBackground() {
		return this.ripper.ripTracks(folder, useParanoia);
	}

	@Override
	protected void done(Boolean result) {
		dialog.hideDialog();
		ripperHandler.notifyFinishImport(filesImported, folder);
		// Enable import cd option in menu
		getBeanFactory().getBean(RipCDAction.class).setEnabled(true);
		this.repositoryRefresher.start();
	}

}
