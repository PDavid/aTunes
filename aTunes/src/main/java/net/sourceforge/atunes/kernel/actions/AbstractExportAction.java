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
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectTransferProcess;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Abstract action to call export process with a list of audio objects
 * 
 * @author fleax
 * 
 */
public abstract class AbstractExportAction extends CustomAbstractAction {

	private static final long serialVersionUID = -6661702915765846089L;

	private IDialogFactory dialogFactory;

	private IOSManager osManager;

	private IProcessFactory processFactory;

	/**
	 * Constructor
	 * @param name
	 */
	public AbstractExportAction(String name) {
		super(name);
	}

	/**
	 * @param processFactory
	 */
	public final void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * @param osManager
	 */
	public final void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param dialogFactory
	 */
	public final void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @return audio objects to export
	 */
	public abstract List<ILocalAudioObject> getAudioObjectsToExport();
	
	@Override
	protected final void executeAction() {
		IFolderSelectorDialog dialog = dialogFactory.newDialog(IFolderSelectorDialog.class);
		dialog.setTitle(I18nUtils.getString("SELECT_FOLDER_TO_EXPORT"));
		File selectedFolder = dialog.selectFolder(osManager.getUserHome());
		if (selectedFolder != null) {
			List<ILocalAudioObject> audioObjects = getAudioObjectsToExport();
			if (!CollectionUtils.isEmpty(audioObjects)) {
				ILocalAudioObjectTransferProcess process = (ILocalAudioObjectTransferProcess) processFactory.getProcessByName("exportFilesProcess");
				process.setDestination(selectedFolder.getAbsolutePath());
				process.setFilesToTransfer(audioObjects);
				ExportProcessListener listener = new ExportProcessListener();
				listener.setDialogFactory(dialogFactory);
				process.addProcessListener(listener);
				process.execute();
			}
		}
	}

	private static class ExportProcessListener implements IProcessListener<List<File>> {

		private IDialogFactory dialogFactory;

		/**
		 * @param dialogFactory
		 */
		public void setDialogFactory(IDialogFactory dialogFactory) {
			this.dialogFactory = dialogFactory;
		}

		@Override
		public void processCanceled() { 
			// Nothing to do
		}

		@Override
		public void processFinished(final boolean ok, List<File> result) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (!ok) {
						dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(I18nUtils.getString("ERRORS_IN_EXPORT_PROCESS"));
					}
				}
			});
		}
	}
}
