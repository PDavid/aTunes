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

import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IConfirmationDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action saves current play list to file
 * 
 * @author fleax
 * 
 */
public class SavePlayListAction extends CustomAbstractAction {

    private static final long serialVersionUID = -303252911138284095L;

    private IFrame frame;
    
    private IPlayListHandler playListHandler;
    
    private IConfirmationDialogFactory confirmationDialogFactory;
    
    private IPlayListIOService playListIOService;
    
    /**
     * @param playListIOService
     */
    public void setPlayListIOService(IPlayListIOService playListIOService) {
		this.playListIOService = playListIOService;
	}
    
    /**
     * @param frame
     */
    public void setFrame(IFrame frame) {
		this.frame = frame;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * @param confirmationDialogFactory
     */
    public void setConfirmationDialogFactory(IConfirmationDialogFactory confirmationDialogFactory) {
		this.confirmationDialogFactory = confirmationDialogFactory;
	}
    
    public SavePlayListAction() {
        super(StringUtils.getString(I18nUtils.getString("SAVE"), "..."));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SAVE_PLAYLIST_TOOLTIP"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    }

    @Override
    protected void executeAction() {
        JFileChooser fileChooser = new JFileChooser(getState().getSavePlaylistPath());
        FileFilter filter = playListIOService.getPlaylistFileFilter();
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(frame.getFrame()) == JFileChooser.APPROVE_OPTION) {

            // Get selected file
            File file = fileChooser.getSelectedFile();

            getState().setSavePlaylistPath(file.getParentFile().getAbsolutePath());

            // If filename have incorrect extension, add it
            file = playListIOService.checkPlayListFileName(file);

            // If file does not exist, or exist and overwrite is confirmed, then write file
            if (!file.exists()
                    || (file.exists() && confirmationDialogFactory.getDialog().showDialog(I18nUtils.getString("OVERWRITE_FILE")))) {
            	playListIOService.write(playListHandler.getCurrentPlayList(true), file);
            }
        }
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        return !playListHandler.getCurrentPlayList(true).isEmpty();
    }
}
