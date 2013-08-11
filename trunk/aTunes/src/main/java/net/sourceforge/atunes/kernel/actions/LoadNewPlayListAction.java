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

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.modules.process.LoadPlayListProcess;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFileSelectorDialog;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FilenameUtils;

/**
 * This action loads a play list
 * 
 * @author fleax
 * 
 */
public class LoadNewPlayListAction extends CustomAbstractAction {

	private static final long serialVersionUID = 3409230917351152853L;

	private IPlayListIOService playListIOService;

	private IProcessFactory processFactory;

	private IStatePlaylist statePlaylist;

	private IDialogFactory dialogFactory;

	private IOSManager osManager;

	private final boolean replacePlayList;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * Default constructor
	 */
	public LoadNewPlayListAction() {
		super(StringUtils.getString(
				I18nUtils.getString("LOAD_AS_NEW_PLAYLIST"), "..."));
		this.replacePlayList = false;
	}

	@Override
	protected void initialize() {
		putValue(
				ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_L,
						GuiUtils.getCtrlOrMetaActionEventMask(this.osManager)));
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param statePlaylist
	 */
	public void setStatePlaylist(final IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(final IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * @param playListIOService
	 */
	public void setPlayListIOService(final IPlayListIOService playListIOService) {
		this.playListIOService = playListIOService;
	}

	@Override
	protected void executeAction() {
		IFileSelectorDialog dialog = this.dialogFactory
				.newDialog(IFileSelectorDialog.class);
		dialog.setFileFilter(this.playListIOService
				.getAllAcceptedPlaylistsFileFilter());
		File file = dialog.loadFile(this.statePlaylist.getLoadPlaylistPath());
		if (file != null) {
			// If exists...
			if (file.exists()) {
				this.statePlaylist.setLoadPlaylistPath(FileUtils.getPath(file
						.getParentFile()));

				if (this.playListIOService.isDynamicPlayList(file)) {
					this.playListIOService.readDynamicPlayList(file);
				} else {

					// Read file names
					List<String> filesToLoad = this.playListIOService
							.read(file);
					// Background loading - but only when returned array is not
					// null
					// (Progress dialog hangs otherwise)
					if (filesToLoad != null) {
						LoadPlayListProcess process = (LoadPlayListProcess) this.processFactory
								.getProcessByName("loadPlayListProcess");
						process.setFilenamesToLoad(filesToLoad);
						process.setReplacePlayList(this.replacePlayList);
						process.setPlayListName(FilenameUtils.getBaseName(file
								.getName()));
						process.execute();
					}
				}
			} else {
				this.dialogFactory.newDialog(IErrorDialog.class)
						.showErrorDialog(I18nUtils.getString("FILE_NOT_FOUND"));
			}
		}
	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		return true;
	}
}
