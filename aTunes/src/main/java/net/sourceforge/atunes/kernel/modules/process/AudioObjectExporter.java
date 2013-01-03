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

import java.io.File;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObjectExporter;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectTransferProcess;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Calls export process with a list of audio objects
 * 
 * @author fleax
 * 
 */
public class AudioObjectExporter implements IAudioObjectExporter {

	private static final long serialVersionUID = -6661702915765846089L;

	private IDialogFactory dialogFactory;

	private IOSManager osManager;

	private IProcessFactory processFactory;

	/**
	 * @param processFactory
	 */
	public final void setProcessFactory(final IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * @param osManager
	 */
	public final void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param dialogFactory
	 */
	public final void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	@Override
	public void exportAudioObject(final List<ILocalAudioObject> audioObjects) {
		if (!CollectionUtils.isEmpty(audioObjects)) {
			File selectedFolder = selectExportFolder();
			if (selectedFolder != null) {
				ILocalAudioObjectTransferProcess process = (ILocalAudioObjectTransferProcess) processFactory.getProcessByName("exportFilesProcess");
				process.setDestination(FileUtils.getPath(selectedFolder));
				process.setFilesToTransfer(audioObjects);
				ExportProcessListener listener = new ExportProcessListener();
				listener.setDialogFactory(dialogFactory);
				process.addProcessListener(listener);
				process.execute();
			}
		}
	}

	/**
	 * @return folder selected
	 */
	private File selectExportFolder() {
		IFolderSelectorDialog dialog = dialogFactory.newDialog(IFolderSelectorDialog.class);
		dialog.setTitle(I18nUtils.getString("SELECT_FOLDER_TO_EXPORT"));
		File selectedFolder = dialog.selectFolder(osManager.getUserHome());
		return selectedFolder;
	}
}
