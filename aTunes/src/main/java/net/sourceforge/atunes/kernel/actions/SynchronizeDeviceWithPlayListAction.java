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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListObjectFilter;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateDevice;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Synchronizes play list and device: device is updated with play list content
 * 
 * @author fleax
 */
public class SynchronizeDeviceWithPlayListAction extends CustomAbstractAction {

	private IIndeterminateProgressDialog dialog;

	private static final long serialVersionUID = -1885495996370465881L;

	private IDeviceHandler deviceHandler;

	private IPlayListHandler playListHandler;

	private IRepositoryHandler repositoryHandler;

	private IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter;

	private IStateDevice stateDevice;

	private IDialogFactory dialogFactory;

	private ILocalAudioObjectFilter localAudioObjectFilter;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param localAudioObjectFilter
	 */
	public void setLocalAudioObjectFilter(
			final ILocalAudioObjectFilter localAudioObjectFilter) {
		this.localAudioObjectFilter = localAudioObjectFilter;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateDevice
	 */
	public void setStateDevice(final IStateDevice stateDevice) {
		this.stateDevice = stateDevice;
	}

	/**
	 * @param playListLocalAudioObjectFilter
	 */
	public void setPlayListLocalAudioObjectFilter(
			final IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter) {
		this.playListLocalAudioObjectFilter = playListLocalAudioObjectFilter;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param deviceHandler
	 */
	public void setDeviceHandler(final IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}

	/**
	 * Default constructor
	 */
	public SynchronizeDeviceWithPlayListAction() {
		super(I18nUtils.getString("SYNCHRONIZE_DEVICE_WITH_PLAYLIST"));
		setEnabled(false);
	}

	@Override
	protected void executeAction() {
		SwingWorker<Map<String, List<ILocalAudioObject>>, Void> worker = new SynchronizeDeviceWithPlayListSwingWorker();
		worker.execute();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog = dialogFactory
						.newDialog(IIndeterminateProgressDialog.class);
				dialog.setTitle(I18nUtils.getString("PLEASE_WAIT"));
				dialog.showDialog();
			}
		});
	}

	private void showMessage(final int filesRemoved, final boolean added) {
		// Show message
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialogFactory
						.newDialog(IMessageDialog.class)
						.showMessage(
								StringUtils.getString(
										I18nUtils
												.getString("SYNCHRONIZATION_FINISHED"),
										" ",
										I18nUtils.getString("ADDED"),
										": ",
										added ? deviceHandler
												.getFilesCopiedToDevice() : 0,
										" ", I18nUtils.getString("REMOVED"),
										": ", filesRemoved));
			}
		});
	}

	private final class SynchronizeDeviceWithPlayListSwingWorker extends
			SwingWorker<Map<String, List<ILocalAudioObject>>, Void> {

		private int filesRemoved = 0;

		@Override
		protected Map<String, List<ILocalAudioObject>> doInBackground() {
			// Get play list elements
			List<ILocalAudioObject> playListObjects;
			if (SynchronizeDeviceWithPlayListAction.this.stateDevice
					.isAllowRepeatedSongsInDevice()) {
				// Repeated songs allowed, filter only if have same artist and
				// album
				playListObjects = localAudioObjectFilter
						.filterRepeatedObjectsWithAlbums(playListLocalAudioObjectFilter
								.getObjects(playListHandler
										.getVisiblePlayList()));
			} else {
				// Repeated songs not allows, filter even if have different
				// album
				playListObjects = localAudioObjectFilter
						.filterRepeatedObjects(playListLocalAudioObjectFilter
								.getObjects(playListHandler
										.getVisiblePlayList()));
			}

			// Get elements present in play list and not in device -> objects to
			// be copied to device
			List<ILocalAudioObject> objectsToCopyToDevice = deviceHandler
					.getElementsNotPresentInDevice(playListObjects);

			// Get elements present in device and not in play list -> objects to
			// be removed from device
			List<ILocalAudioObject> objectsToRemoveFromDevice = deviceHandler
					.getElementsNotPresentInList(playListObjects);

			Map<String, List<ILocalAudioObject>> result = new HashMap<String, List<ILocalAudioObject>>();
			result.put("ADD", objectsToCopyToDevice);
			result.put("REMOVE", objectsToRemoveFromDevice);
			filesRemoved = objectsToRemoveFromDevice.size();
			return result;
		}

		@Override
		protected void done() {
			try {
				Map<String, List<ILocalAudioObject>> files = get();

				dialog.hideDialog();

				// Remove elements from device
				final List<ILocalAudioObject> filesToRemove = files
						.get("REMOVE");
				repositoryHandler.remove(filesToRemove);
				SynchronizeDeviceWithPlayListAction.this.setEnabled(false);
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() {
						for (ILocalAudioObject audioFile : filesToRemove) {
							fileManager.delete(audioFile);
						}
						return null;
					}

					@Override
					protected void done() {
						SynchronizeDeviceWithPlayListAction.this
								.setEnabled(true);
					}
				}.execute();

				// Copy elements to device if necessary, otherwise show message
				// and finish
				if (!files.get("ADD").isEmpty()) {
					// The process will show message when finish
					deviceHandler.copyFilesToDevice(files.get("ADD"),
							new IProcessListener<List<File>>() {

								@Override
								public void processFinished(final boolean ok,
										final List<File> result) {
									showMessage(filesRemoved, true);
								}

								@Override
								public void processCanceled() {
								}
							});
				} else {
					showMessage(filesRemoved, false);
				}
			} catch (InterruptedException e) {
				Logger.error(e);
			} catch (ExecutionException e) {
				Logger.error(e);
			}
		}
	}
}
