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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IProcessListener;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Listener for import to repository
 * @author alex
 *
 */
public class ImportToRepositoryProcessListener implements IProcessListener<List<File>> {

    private IDialogFactory dialogFactory;
    
    private IRepositoryHandler repositoryHandler;
    
	private IPlayListHandler playListHandler;
	
	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * @param dialogFactory
     */
    public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
    
    @Override
    public void processCanceled() { 
    	// Nothing to do
    }

    @Override
    public void processFinished(final boolean ok, List<File> result) {
        if (!ok) {
        	// Show error message
          	dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(I18nUtils.getString("ERRORS_IN_IMPORT_PROCESS"));
        } else {
			// If import is ok then add files to repository
			repositoryHandler.addFilesAndRefresh(result);
			
			// Create a playlist with imported files
			List<ILocalAudioObject> audioObjects = new ArrayList<ILocalAudioObject>();
			for (File file : result) {
				ILocalAudioObject ao = repositoryHandler.getFileIfLoaded(file.getAbsolutePath());
				if (ao != null) {
					audioObjects.add(ao);
				}
			}
			
			Date now = new Date();
			String dateString = DateFormat.getDateTimeInstance().format(now);
			
			String name = StringUtils.getString(I18nUtils.getString("FILES_IMPORTED"), " - ", dateString);
			playListHandler.newPlayList(name, audioObjects);
        }
    }
}