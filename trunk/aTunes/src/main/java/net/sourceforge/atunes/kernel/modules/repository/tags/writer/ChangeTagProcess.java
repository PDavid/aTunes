/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.repository.tags.writer;

import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.process.Process;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This class represents a process which performs changes in tag of a set of
 * AudioFile objects
 * 
 * @author fleax
 * 
 */
public abstract class ChangeTagProcess extends Process {

    /**
     * List of AudioFile objects to change
     */
    protected List<AudioFile> filesToChange;

    /**
     * Constructor, initialized with AudioFiles to be changed
     * 
     * @param filesToChange
     */
    protected ChangeTagProcess(List<AudioFile> filesToChange) {
        this.filesToChange = filesToChange;
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
        boolean errors = false;
        try {
            for (int i = 0; i < this.filesToChange.size() && !cancel; i++) {
                // Change every AudioFile
                changeTag(this.filesToChange.get(i));
                // Reread every file after being writen
                RepositoryHandler.getInstance().refreshFile(this.filesToChange.get(i));
                setCurrentProgress(i + 1);
            }
        } catch (Exception e) {
            addErrorLog(e);
            errors = true;
        }
        // Refresh swing components
        TagModifier.refreshAfterTagModify(filesToChange);

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
    protected abstract void changeTag(AudioFile file) throws IOException;

    /**
     * Some processes need an initial task to get some information needed to
     * change tags. These processes should override this method
     */
    protected void retrieveInformationBeforeChangeTags() {
        // Nothing to do
    }
}
