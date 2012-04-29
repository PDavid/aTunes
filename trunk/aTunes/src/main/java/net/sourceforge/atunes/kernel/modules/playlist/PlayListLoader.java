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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.kernel.modules.process.LoadPlayListProcess;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.I18nUtils;

public class PlayListLoader {
	
	private IFrame frame;
	
    private IPlayListIOService playListIOService;

	private IErrorDialogFactory errorDialogFactory;
	
    private IProcessFactory processFactory;
    
    private IStatePlaylist statePlaylist;
    
    /**
     * @param statePlaylist
     */
    public void setStatePlaylist(IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}

    /**
     * @param processFactory
     */
    public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}
    
	/**
	 * @param errorDialogFactory
	 */
	public void setErrorDialogFactory(IErrorDialogFactory errorDialogFactory) {
		this.errorDialogFactory = errorDialogFactory;
	}
	
	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * @param playListIOService
	 */
	public void setPlayListIOService(IPlayListIOService playListIOService) {
		this.playListIOService = playListIOService;
	}
	
	/**
	 * Load play list from file
	 */
	void loadPlaylist() {
        JFileChooser fileChooser = new JFileChooser(statePlaylist.getLoadPlaylistPath());
        FileFilter filter = playListIOService.getPlaylistFileFilter();
        // Open file chooser
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(frame.getFrame()) == JFileChooser.APPROVE_OPTION) {
            // Get selected file
            File file = fileChooser.getSelectedFile();

            // If exists...
            if (file.exists()) {
                statePlaylist.setLoadPlaylistPath(file.getParentFile().getAbsolutePath());
                // Read file names
                List<String> filesToLoad = playListIOService.read(file);
                // Background loading - but only when returned array is not null (Progress dialog hangs otherwise)
                if (filesToLoad != null) {
                    LoadPlayListProcess process = (LoadPlayListProcess) processFactory.getProcessByName("loadPlayListProcess");
                    process.setFilenamesToLoad(filesToLoad);
                    process.execute();
                }
            } else {
            	errorDialogFactory.getDialog().showErrorDialog(frame, I18nUtils.getString("FILE_NOT_FOUND"));
            }
        }
    }
}
