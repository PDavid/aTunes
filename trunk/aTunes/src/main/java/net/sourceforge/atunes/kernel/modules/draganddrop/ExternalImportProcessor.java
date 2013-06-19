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

package net.sourceforge.atunes.kernel.modules.draganddrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.TransferHandler.TransferSupport;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectLocator;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * Handles drag and drop with source from external application
 * 
 * @author alex
 * 
 */
public class ExternalImportProcessor {

	private ILocalAudioObjectFactory localAudioObjectFactory;

	private ILocalAudioObjectValidator localAudioObjectValidator;

	private ILocalAudioObjectLocator localAudioObjectLocator;

	private IPlayListIOService playListIOService;

	private IPlayListTable playListTable;

	private IPlayListHandler playListHandler;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(
			final ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}

	/**
	 * @param localAudioObjectLocator
	 */
	public void setLocalAudioObjectLocator(
			final ILocalAudioObjectLocator localAudioObjectLocator) {
		this.localAudioObjectLocator = localAudioObjectLocator;
	}

	/**
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(
			final ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}

	/**
	 * @param playListIOService
	 */
	public void setPlayListIOService(final IPlayListIOService playListIOService) {
		this.playListIOService = playListIOService;
	}

	/**
	 * @param playListTable
	 */
	public void setPlayListTable(final IPlayListTable playListTable) {
		this.playListTable = playListTable;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	boolean processExternalImport(final TransferSupport support) {
		List<File> filesDragged = beanFactory.getBean(
				ExternalDragAndDropDataExtractor.class)
				.getFilesDragged(support);

		if (!CollectionUtils.isEmpty(filesDragged)) {
			List<IAudioObject> filesToAdd = getFilesToAdd(filesDragged);
			if (!CollectionUtils.isEmpty(filesToAdd)) {
				beanFactory.getBean(IAudioObjectComparator.class).sort(
						filesToAdd);
				int dropRow = playListTable.rowAtPoint(support
						.getDropLocation().getDropPoint());
				playListHandler.addToVisiblePlayList(dropRow, filesToAdd);
				// Keep selected rows: if drop row is the bottom of play
				// list
				// (-1) then select last row
				if (dropRow == -1) {
					dropRow = playListHandler.getVisiblePlayList().size()
							- filesToAdd.size();
				}
				playListTable.getSelectionModel().addSelectionInterval(dropRow,
						dropRow + filesToAdd.size() - 1);
			}
			return true;
		}
		return false;
	}

	/**
	 * @param filesDragged
	 * @return
	 */
	private List<IAudioObject> getFilesToAdd(final List<File> filesDragged) {
		List<IAudioObject> filesToAdd = new ArrayList<IAudioObject>();
		for (File f : filesDragged) {
			if (f.isDirectory()) {
				filesToAdd.addAll(localAudioObjectLocator
						.locateLocalAudioObjectsInFolder(f, null));
			} else if (localAudioObjectValidator.isValidAudioFile(f)) {
				filesToAdd.add(localAudioObjectFactory.getLocalAudioObject(f));
			} else if (f.getName().toLowerCase().endsWith("m3u")) {
				filesToAdd.addAll(playListIOService.getFilesFromList(f));
			}
		}
		return filesToAdd;
	}

}
