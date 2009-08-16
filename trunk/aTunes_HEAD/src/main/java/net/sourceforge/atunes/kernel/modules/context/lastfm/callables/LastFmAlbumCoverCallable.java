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

package net.sourceforge.atunes.kernel.modules.context.lastfm.callables;

import java.awt.Image;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.lastfm.LastFmService;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;

/**
 * The Class LastFmAlbumCoverCallable.
 */
public class LastFmAlbumCoverCallable implements Callable<Image> {

    /** The service. */
    private LastFmService service;

    /** The file. */
    private AudioFile file;

    /**
     * Instantiates a new Last.fm album cover runnable.
     * 
     * @param service
     *            the service
     * @param file
     *            the file
     */
    public LastFmAlbumCoverCallable(LastFmService service, AudioFile file) {
        this.service = service;
        this.file = file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Image call() throws Exception {
        AlbumInfo album = null;
        Image image = null;
        album = service.getAlbum(file.getArtist(), file.getAlbum());
        if (album != null) {
            image = service.getImage(album);
        }
        return image;
    }
}
