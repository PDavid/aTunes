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

package net.sourceforge.atunes.kernel.modules.process;

import java.io.IOException;
import java.util.Collection;

import net.sourceforge.atunes.kernel.modules.tags.TagModifier;
import net.sourceforge.atunes.model.IChangeTagsProcess;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * This class represents a process which performs changes in tag of a set of
 * LocalAudioObject objects
 * 
 * @author fleax
 * 
 */
public abstract class AbstractChangeTagProcess extends AbstractProcess implements IChangeTagsProcess {

    /**
     * List of LocalAudioObject objects to change
     */
    private Collection<ILocalAudioObject> filesToChange;
    
    private IPlayListHandler playListHandler;
    
    private IRepositoryHandler repositoryHandler;
    
    private IPlayerHandler playerHandler;

    /**
     * @param playerHandler
     */
    public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

    @Override
    public String getProgressDialogTitle() {
        return I18nUtils.getString("PERFORMING_CHANGES");
    }

    @Override
    protected long getProcessSize() {
        return this.filesToChange.size();
    }

    @Override
    protected boolean runProcess() {
        // Retrieve information needed to change tags
        retrieveInformationBeforeChangeTags();
        repositoryHandler.startTransaction();
        boolean errors = false;
        try {
        	int i = 0;
        	for (ILocalAudioObject lao : this.filesToChange) {
        		if (!isCanceled()) {
        			// Change every AudioFile
        			changeTag(lao);
        			// Reread every file after being writen
        			repositoryHandler.refreshFile(lao);
        			setCurrentProgress(i + 1);
        			i++;
        		}
            }
        } catch (IOException e) {
        	Logger.error(e);
            errors = true;
        }
        // Refresh swing components
        TagModifier.refreshAfterTagModify(filesToChange, playListHandler, playerHandler);

        repositoryHandler.endTransaction();
        
        return !errors;
    }

    @Override
    protected void runCancel() {
        // Change tag has no possible cancel operation
    }

    /**
     * Code to change tag of an AudioFile
     * 
     * @param file
     * @throws IOException
     */
    protected abstract void changeTag(ILocalAudioObject file) throws IOException;

    /**
     * Some processes need an initial task to get some information needed to
     * change tags. These processes should override this method
     */
    protected void retrieveInformationBeforeChangeTags() {
        // Nothing to do
    }

    /**
     * @return the filesToChange
     */
    protected Collection<ILocalAudioObject> getFilesToChange() {
        return filesToChange;
    }

    /**
     * @param filesToChange
     *            the filesToChange to set
     */
    @Override
	public void setFilesToChange(Collection<ILocalAudioObject> filesToChange) {
        this.filesToChange = filesToChange;
    }
}
