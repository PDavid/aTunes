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
import java.util.List;

import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.LyricsService;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.LocalAudioObject;

/**
 * The Class SetLyricsProcess.
 */
public class SetLyricsProcess extends AbstractChangeTagProcess {

	private LyricsService lyricsService;
	
    /**
     * Writes lyrics to files if any is found. Context handler is used for
     * finding lyrics
     * 
     * @param files
     * @param state
     * @param lyricsService
     */
    SetLyricsProcess(List<LocalAudioObject> files, IState state, LyricsService lyricsService) {
        super(files, state);
        this.lyricsService = lyricsService;
    }

    @Override
    protected void changeTag(LocalAudioObject file) throws IOException {
        // Check if no lyrics is present and we have enough info for a query
        if (file.getLyrics().isEmpty() && !file.getArtist().isEmpty() && !file.getTitle().isEmpty()) {
            Lyrics lyrics = lyricsService.getLyrics(file.getArtist(), file.getTitle());
            String lyricsString = lyrics != null ? lyrics.getLyrics().trim() : "";
            if (!lyricsString.isEmpty()) {
                TagModifier.setLyrics(file, lyricsString);
            }
        }
    }
}
