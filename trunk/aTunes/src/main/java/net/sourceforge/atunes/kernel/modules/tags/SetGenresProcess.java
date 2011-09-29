/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * The Class SetGenresProcess.
 */
public class SetGenresProcess extends AbstractChangeTagProcess {

    private Map<ILocalAudioObject, String> filesAndGenres;

    /**
     * Instantiates a new sets the genres process.
     * 
     * @param files
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     */
    SetGenresProcess(List<ILocalAudioObject> files, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler) {
        super(files, state, playListHandler, repositoryHandler);
    }

    @Override
    protected void retrieveInformationBeforeChangeTags() {
        super.retrieveInformationBeforeChangeTags();
        this.filesAndGenres = getGenresForFiles(getFilesToChange());
    }

    @Override
    protected void changeTag(ILocalAudioObject file) throws IOException {
        String genre = this.filesAndGenres.get(file);
        // If file has already genre setted, avoid
        if (!file.getGenre().equals(genre)) {
            TagModifier.setGenre(file, genre);
        }
    }

    /**
     * Gets the genres for files.
     * 
     * @param files
     *            the files
     * 
     * @return the genres for files
     */
    private Map<ILocalAudioObject, String> getGenresForFiles(List<ILocalAudioObject> files) {
        Map<ILocalAudioObject, String> result = new HashMap<ILocalAudioObject, String>();

        Map<String, String> tagCache = new HashMap<String, String>();
        
        IWebServicesHandler webServicesHandler = Context.getBean(IWebServicesHandler.class);
        for (ILocalAudioObject f : files) {
            if (!Artist.isUnknownArtist(f.getArtist())) {
                String tag = null;
                if (tagCache.containsKey(f.getArtist())) {
                    tag = tagCache.get(f.getArtist());
                } else {
                    tag = webServicesHandler.getArtistTopTag(f.getArtist());
                    tagCache.put(f.getArtist(), tag);
                    // Wait one second to avoid IP banning
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // Nothing to do
                    }
                }
                if (tag != null) {
                    result.put(f, tag);
                }
            }
        }

        return result;
    }

}
