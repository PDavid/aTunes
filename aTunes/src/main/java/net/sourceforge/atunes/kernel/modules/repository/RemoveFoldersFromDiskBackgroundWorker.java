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

import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorkerWithIndeterminateProgress;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;

/**
 * Removes folders from disk
 * 
 * @author alex
 * 
 */
public class RemoveFoldersFromDiskBackgroundWorker extends
		BackgroundWorkerWithIndeterminateProgress<Void, Void> {

	private IOSManager osManager;

	private List<IFolder> foldersToRemove;

	/**
	 * @param foldersToRemove
	 */
	public void setFoldersToRemove(final List<IFolder> foldersToRemove) {
		this.foldersToRemove = foldersToRemove;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	protected String getDialogTitle() {
		return I18nUtils.getString("PLEASE_WAIT");
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected Void doInBackground() {
		for (IFolder folder : this.foldersToRemove) {
			try {
				FileUtils.deleteDirectory(folder.getFolderPath(this.osManager));
				Logger.info(StringUtils.getString("Removed folder ", folder));
			} catch (IOException e) {
				Logger.info(StringUtils.getString("Could not remove folder ",
						folder, e.getMessage()));
			}
		}
		return null;
	}

	@Override
	protected void doneAndDialogClosed(Void result) {
	}
}
