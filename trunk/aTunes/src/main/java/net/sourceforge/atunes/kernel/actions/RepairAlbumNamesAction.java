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

import java.util.Collection;

import net.sourceforge.atunes.model.IChangeTagsProcess;
import net.sourceforge.atunes.model.IConfirmationDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * This action invokes process to repair album names in repository
 * 
 * @author fleax
 * 
 */
public class RepairAlbumNamesAction extends CustomAbstractAction {

    private static final class FilesWithEmptyAlbumFilter implements Predicate<ILocalAudioObject> {
		@Override
		public boolean apply(ILocalAudioObject ao) {
			return ao.getAlbum() == null || UnknownObjectCheck.isUnknownAlbum(ao.getAlbum()) || ao.getAlbum().isEmpty();
		}
	}

	private static final long serialVersionUID = -7828819966696617838L;

    private IRepositoryHandler repositoryHandler;
    
    private IProcessFactory processFactory;
    
    private IConfirmationDialogFactory confirmationDialogFactory;
    
    /**
     * @param confirmationDialogFactory
     */
    public void setConfirmationDialogFactory(IConfirmationDialogFactory confirmationDialogFactory) {
		this.confirmationDialogFactory = confirmationDialogFactory;
	}
    
    /**
     * @param processFactory
     */
    public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
    
    public RepairAlbumNamesAction() {
        super(I18nUtils.getString("REPAIR_ALBUM_NAMES"));
    }

    @Override
    protected void executeAction() {
        // Show confirmation dialog
        if (confirmationDialogFactory.getDialog().showDialog(I18nUtils.getString("REPAIR_ALBUM_NAMES_MESSAGE"))) {
            // Call album name edit
        	IChangeTagsProcess process = (IChangeTagsProcess) processFactory.getProcessByName("setAlbumNamesProcess");
        	process.setFilesToChange(getFilesWithEmptyAlbum(repositoryHandler.getAudioFilesList()));
            process.execute();
        }
    }
    
    /**
     * Returns files without album
     * @param audioFiles
     * @return
     */
    private Collection<ILocalAudioObject> getFilesWithEmptyAlbum(Collection<ILocalAudioObject> audioFiles) {
    	return Collections2.filter(audioFiles, new FilesWithEmptyAlbumFilter());
    }
}
