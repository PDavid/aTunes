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

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * The Class SetLyricsProcess.
 */
public class SetLyricsProcess extends AbstractChangeTagProcess {

    /**
     * Writes lyrics to files if any is found. Context handler is used for
     * finding lyrics
     * 
     * @param files
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     */
    SetLyricsProcess(List<ILocalAudioObject> files, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler) {
        super(files, state, playListHandler, repositoryHandler);
    }

    @Override
    protected void changeTag(ILocalAudioObject file) throws IOException {
        // Check if no lyrics is present and we have enough info for a query
        if (file.getLyrics().isEmpty() && !file.getArtist().isEmpty() && !file.getTitle().isEmpty()) {
            ILyrics lyrics = Context.getBean(IWebServicesHandler.class).getLyrics(file.getArtist(), file.getTitle());
            String lyricsString = lyrics != null ? lyrics.getLyrics().trim() : "";
            if (!lyricsString.isEmpty()) {
                TagModifier.setLyrics(file, lyricsString);
            }
        }
    }
}
