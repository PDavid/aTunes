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
package net.sourceforge.atunes.kernel.modules.search.searchableobjects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.search.RawSearchResult;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

public final class RepositorySearchableObject extends CommonAudioFileSearchableObject {

    /**
     * Singleton instance of this class
     */
    private static RepositorySearchableObject instance;

    private FSDirectory indexDirectory;

    /**
     * Default constructor
     */
    private RepositorySearchableObject() {
        // Nothing to do
    }

    /**
     * Returns singleton instance of RepositorySearchableObject
     * 
     * @return
     */
    public static RepositorySearchableObject getInstance() {
        if (instance == null) {
            instance = new RepositorySearchableObject();
        }
        return instance;
    }

    @Override
    public String getSearchableObjectName() {
        return I18nUtils.getString("REPOSITORY");
    }

    @Override
    public FSDirectory getIndexDirectory() throws IOException {
        if (indexDirectory == null) {
            indexDirectory = new SimpleFSDirectory(new File(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.isDebug()), "/", Constants.REPOSITORY_INDEX_DIR)));
        }
        return indexDirectory;
    }

    @Override
    public List<AudioObject> getSearchResult(List<RawSearchResult> rawSearchResults) {
        List<AudioObject> result = new ArrayList<AudioObject>();
        for (RawSearchResult rawSearchResult : rawSearchResults) {
            AudioFile audioFile = RepositoryHandler.getInstance().getFileIfLoaded(rawSearchResult.getDocument().get("url"));
            if (audioFile != null) {
                result.add(audioFile);
            }
        }
        return result;
    }

    @Override
    public List<AudioObject> getElementsToIndex() {
        return new ArrayList<AudioObject>(RepositoryHandler.getInstance().getAudioFilesList());
    }

}
