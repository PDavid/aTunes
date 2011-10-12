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

package net.sourceforge.atunes.kernel.modules.tags;

import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.process.AbstractProcess;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This class represents a process which performs changes in tag of a set of
 * LocalAudioObject objects
 * 
 * @author fleax
 * 
 */
public abstract class AbstractChangeTagProcess extends AbstractProcess {

    /**
     * List of LocalAudioObject objects to change
     */
    private List<ILocalAudioObject> filesToChange;
    
    private IPlayListHandler playListHandler;
    
    private IRepositoryHandler repositoryHandler;
    
    private IPlayerHandler playerHandler;

    /**
     * Constructor, initialized with AudioFiles to be changed
     * 
     * @param filesToChange
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     */
    protected AbstractChangeTagProcess(List<ILocalAudioObject> filesToChange, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler) {
    	super(state);
        this.filesToChange = filesToChange;
        this.playListHandler = playListHandler;
        this.repositoryHandler = repositoryHandler;
        this.playerHandler = playerHandler;
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
            for (int i = 0; i < this.filesToChange.size() && !isCanceled(); i++) {
                // Change every AudioFile
                changeTag(this.filesToChange.get(i));
                // Reread every file after being writen
                repositoryHandler.refreshFile(this.filesToChange.get(i));
                setCurrentProgress(i + 1);
            }
        } catch (Exception e) {
            addErrorLog(e);
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
    protected List<ILocalAudioObject> getFilesToChange() {
        return filesToChange;
    }

    /**
     * @param filesToChange
     *            the filesToChange to set
     */
    protected void setFilesToChange(List<ILocalAudioObject> filesToChange) {
        this.filesToChange = filesToChange;
    }
}
