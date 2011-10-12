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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * The Class ChangeTitlesProcess.
 */
public class EditTitlesProcess extends AbstractChangeTagProcess {

    /** The files and titles. */
    private Map<ILocalAudioObject, String> filesAndTitles;

    /**
     * Instantiates a new change titles process.
     * 
     * @param files
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     */
    public EditTitlesProcess(List<ILocalAudioObject> files, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler) {
        super(files, state, playListHandler, repositoryHandler, playerHandler);
    }

    @Override
    protected void retrieveInformationBeforeChangeTags() {
        super.retrieveInformationBeforeChangeTags();
        if (filesAndTitles == null) {
            filesAndTitles = getTitlesForFiles(getFilesToChange());
        }
    }

    @Override
    protected void changeTag(ILocalAudioObject file) {
        String newTitle = filesAndTitles.get(file);
        TagModifier.setTitles(file, newTitle);
    }

    /**
     * @param filesAndTitles
     *            the filesAndTitles to set
     */
    public void setFilesAndTitles(Map<ILocalAudioObject, String> filesAndTitles) {
        this.filesAndTitles = filesAndTitles;
    }

    /**
     * Returns a hash of files with its songs titles.
     * 
     * @param files
     *            the files
     * 
     * @return the titles for files
     */

    public Map<ILocalAudioObject, String> getTitlesForFiles(List<ILocalAudioObject> files) {
        Map<ILocalAudioObject, String> result = new HashMap<ILocalAudioObject, String>();

        // For each file
        IWebServicesHandler webServicesHandler = Context.getBean(IWebServicesHandler.class);
        for (ILocalAudioObject f : files) {
            String title = webServicesHandler.getTitleForAudioObject(f);
            if (title != null) {
                result.put(f, title);
            }
        }

        // Return files matched
        return result;
    }

}
