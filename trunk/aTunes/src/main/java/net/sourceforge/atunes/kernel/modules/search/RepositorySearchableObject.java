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

package net.sourceforge.atunes.kernel.modules.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

public final class RepositorySearchableObject extends AbstractCommonAudioFileSearchableObject {

    private FSDirectory indexDirectory;
    
    private IOSManager osManager;
    
    private IApplicationArguments applicationArguments;
    
    private IRepositoryHandler repositoryHandler;
    
    /**
     * @param osManager
     */
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    /**
     * @param applicationArguments
     */
    public void setApplicationArguments(IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

    @Override
    public String getSearchableObjectName() {
        return I18nUtils.getString("REPOSITORY");
    }

    @Override
    public FSDirectory getIndexDirectory() throws IOException {
        if (indexDirectory == null) {
            indexDirectory = new SimpleFSDirectory(new File(StringUtils.getString(osManager.getUserConfigFolder(applicationArguments.isDebug()), "/", Constants.REPOSITORY_INDEX_DIR)));
        }
        return indexDirectory;
    }

    @Override
    public List<IAudioObject> getSearchResult(List<RawSearchResult> rawSearchResults) {
        List<IAudioObject> result = new ArrayList<IAudioObject>();
        for (RawSearchResult rawSearchResult : rawSearchResults) {
        	ILocalAudioObject audioFile = repositoryHandler.getFileIfLoaded(rawSearchResult.getDocument().get("url"));
            if (audioFile != null) {
                result.add(audioFile);
            }
        }
        return result;
    }

    @Override
    public List<IAudioObject> getElementsToIndex() {
        return new ArrayList<IAudioObject>(repositoryHandler.getAudioFilesList());
    }
}
