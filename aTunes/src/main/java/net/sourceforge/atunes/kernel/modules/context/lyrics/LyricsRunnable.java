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

package net.sourceforge.atunes.kernel.modules.context.lyrics;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextListener;
import net.sourceforge.atunes.kernel.modules.context.Lyrics;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.model.AudioObject;

public class LyricsRunnable implements Runnable {

    AudioObject audioObject;
    ContextListener listener;
    /** The retrieved lyrics */
    Lyrics lyrics = null;
    long id;

    /**
     * Instantiates a new lyrics runnable.
     * 
     * @param listener
     *            the context information listener
     * 
     * @param audioObject
     *            the audio object
     * @param id
     *            the id
     */
    public LyricsRunnable(ContextListener listener, AudioObject audioObject, long id) {
        this.listener = listener;
        this.audioObject = audioObject;
        this.id = id;
    }

    @Override
    public void run() {
        // First check if tag contains the lyrics. Favour this over internet services.
        if (!audioObject.getLyrics().trim().isEmpty()) {
            lyrics = new Lyrics(audioObject.getLyrics(), null);
        }
        // Query internet service for lyrics
        else {
            if (!audioObject.getTitle().trim().isEmpty() && !audioObject.getArtist().trim().isEmpty() && !Artist.isUnknownArtist(audioObject.getArtist())) {
                lyrics = ContextHandler.getInstance().getLyrics(audioObject.getArtist().trim(), audioObject.getTitle().trim());
            }
        }
        if (lyrics != null) {
            lyrics.setLyrics(lyrics.getLyrics().replaceAll("'", "\'"));
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.notifyLyricsRetrieved(audioObject, lyrics, id);
            }
        });
    }

}
