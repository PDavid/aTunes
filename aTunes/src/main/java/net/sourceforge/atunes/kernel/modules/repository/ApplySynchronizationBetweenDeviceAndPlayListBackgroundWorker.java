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
import java.util.Map;

import javax.swing.Action;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Synchronizes objects in device with play list
 * 
 * @author alex
 * 
 */
/**
 * @author alex
 * 
 */
public class ApplySynchronizationBetweenDeviceAndPlayListBackgroundWorker
		extends BackgroundWorker<Void, Void> {

	private IDeviceHandler deviceHandler;

	private IRepositoryHandler repositoryHandler;

	private IDialogFactory dialogFactory;

	private IFileManager fileManager;

	private Action syncDeviceWithPlayListAction;

	private final int filesRemoved = 0;

	private Map<String, List<ILocalAudioObject>> syncData;

	/**
	 * @param syncData
	 */
	public void setSyncData(final Map<String, List<ILocalAudioObject>> syncData) {
		this.syncData = syncData;
	}

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param syncDeviceWithPlayListAction
	 */
	public void setSyncDeviceWithPlayListAction(
			final Action syncDeviceWithPlayListAction) {
		this.syncDeviceWithPlayListAction = syncDeviceWithPlayListAction;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param deviceHandler
	 */
	public void setDeviceHandler(final IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}

	@Override
	protected void before() {
		this.syncDeviceWithPlayListAction.setEnabled(false);
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected Void doInBackground() {
		// Remove elements from device
		final List<ILocalAudioObject> filesToRemove = this.syncData
				.get("REMOVE");
		this.repositoryHandler.remove(filesToRemove);
		for (ILocalAudioObject audioFile : filesToRemove) {
			ApplySynchronizationBetweenDeviceAndPlayListBackgroundWorker.this.fileManager
					.delete(audioFile);
		}
		return null;
	}

	@Override
	protected void done(final Void result) {
		this.syncDeviceWithPlayListAction.setEnabled(true);

		// Copy elements to device if necessary, otherwise show message
		// and finish
		if (!this.syncData.get("ADD").isEmpty()) {
			// The process will show message when finish
			this.deviceHandler.copyFilesToDevice(this.syncData.get("ADD"),
					new IProcessListener<List<ILocalAudioObject>>() {

						@Override
						public void processFinished(final boolean ok,
								final List<ILocalAudioObject> result) {
							showMessage(
									ApplySynchronizationBetweenDeviceAndPlayListBackgroundWorker.this.filesRemoved,
									true);
						}

						@Override
						public void processCanceled() {
						}
					});
		} else {
			showMessage(this.filesRemoved, false);
		}
	}

	private void showMessage(final int filesRemoved, final boolean added) {
		// Show message
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ApplySynchronizationBetweenDeviceAndPlayListBackgroundWorker.this.dialogFactory
						.newDialog(IMessageDialog.class)
						.showMessage(
								StringUtils.getString(
										I18nUtils
												.getString("SYNCHRONIZATION_FINISHED"),
										" ",
										I18nUtils.getString("ADDED"),
										": ",
										added ? ApplySynchronizationBetweenDeviceAndPlayListBackgroundWorker.this.deviceHandler
												.getFilesCopiedToDevice() : 0,
										" ", I18nUtils.getString("REMOVED"),
										": ", filesRemoved));
			}
		});
	}
}
