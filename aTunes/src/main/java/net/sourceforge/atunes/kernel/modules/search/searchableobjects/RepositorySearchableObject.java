/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.search.RawSearchResult;
import net.sourceforge.atunes.kernel.modules.search.SearchResult;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

public class RepositorySearchableObject extends CommonAudioFileSearchableObject {

    /**
     * Singleton instance of this class
     */
    private static RepositorySearchableObject instance;

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

    /*
     * (non-Javadoc)
     * 
     * @seenet.sourceforge.atunes.kernel.modules.search.SearchableObject#
     * getSearchableObjectName()
     */
    @Override
    public String getSearchableObjectName() {
        return LanguageTool.getString("REPOSITORY");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.modules.search.SearchableObject#getPathToIndex
     * ()
     */
    @Override
    public String getPathToIndex() {
        return StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.REPOSITORY_INDEX_DIR);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.modules.search.SearchableObject#getSearchResult
     * (java.util.List)
     */
    @Override
    public List<SearchResult> getSearchResult(List<RawSearchResult> rawSearchResults) {
        List<SearchResult> result = new ArrayList<SearchResult>();
        for (RawSearchResult rawSearchResult : rawSearchResults) {
            AudioFile audioFile = RepositoryHandler.getInstance().getFileIfLoaded(rawSearchResult.getDocument().get("url"));
            if (audioFile != null) {
                result.add(new SearchResult(audioFile, rawSearchResult.getScore()));
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @seenet.sourceforge.atunes.kernel.modules.search.SearchableObject#
     * getElementsToIndex()
     */
    @Override
    public List<AudioObject> getElementsToIndex() {
        if (RepositoryHandler.getInstance().getRepository() != null) {
            return new ArrayList<AudioObject>(RepositoryHandler.getInstance().getRepository().getAudioFilesList());
        }
        return new ArrayList<AudioObject>();
    }

}
