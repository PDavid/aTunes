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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileSelectorDialog;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action saves current play list to file as M3U
 * 
 * @author fleax
 * 
 */
public class SavePlayListAction extends CustomAbstractAction {

    private static final long serialVersionUID = -303252911138284095L;

    private IPlayListHandler playListHandler;
    
    private IPlayListIOService playListIOService;
    
    private IStatePlaylist statePlaylist;
    
    private IDialogFactory dialogFactory;
    
    /**
     * @param dialogFactory
     */
    public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
    
    /**
     * @param statePlaylist
     */
    public void setStatePlaylist(IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}
    
    /**
     * @param playListIOService
     */
    public void setPlayListIOService(IPlayListIOService playListIOService) {
		this.playListIOService = playListIOService;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * Default constructor
     */
    public SavePlayListAction() {
        super(StringUtils.getString(I18nUtils.getString("SAVE"), "..."));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SAVE_PLAYLIST_TOOLTIP"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    }

    @Override
    protected void executeAction() {
    	IFileSelectorDialog dialog = dialogFactory.newDialog(IFileSelectorDialog.class);
    	dialog.setFileFilter(playListIOService.getPlaylistFileFilter());
    	File file = dialog.saveFile(statePlaylist.getSavePlaylistPath());
    	if (file != null) {

            statePlaylist.setSavePlaylistPath(file.getParentFile().getAbsolutePath());

            // If filename have incorrect extension, add it
            file = playListIOService.checkPlayListFileName(file);

            // If file does not exist, or exist and overwrite is confirmed, then write file
            boolean canWrite = !file.exists();
            if (!canWrite) {
				IConfirmationDialog confirmationDialog = dialogFactory.newDialog(IConfirmationDialog.class);
				confirmationDialog.setMessage(I18nUtils.getString("OVERWRITE_FILE"));
				confirmationDialog.showDialog();
				canWrite = confirmationDialog.userAccepted();
            }
            if (canWrite) {
            	playListIOService.write(playListHandler.getCurrentPlayList(true), file);
            }
        }
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        return !playListHandler.getCurrentPlayList(true).isEmpty();
    }
}
