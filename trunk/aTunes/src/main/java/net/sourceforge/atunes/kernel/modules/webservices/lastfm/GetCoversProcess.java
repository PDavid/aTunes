/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.awt.Component;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.process.AbstractProcess;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class GetCoversProcess.
 */
public class GetCoversProcess extends AbstractProcess {

    /** The artist. */
    private Artist artist;

    /**
     * Instantiates a new gets the covers process.
     * 
     * @param artist
     *            the artist
     * @param progressDialog
     *            the progress dialog
     */
    public GetCoversProcess(Artist artist, Component owner) {
        this.artist = artist;
        setOwner(owner);
    }

    @Override
    protected long getProcessSize() {
        return artist.getAlbums().size();
    }

    @Override
    protected boolean runProcess() {
        long coversRetrieved = 0;
        List<Album> albums = new ArrayList<Album>(artist.getAlbums().values());
        for (int i = 0; i < albums.size() && !isCanceled(); i++) {
            Album album = albums.get(i);
            if (!album.hasCoverDownloaded()) {
                Image albumImage = LastFmService.getInstance().getAlbumImage(artist.getName(), album.getName());
                if (albumImage != null) {
                    try {
                        ImageUtils.writeImageToFile(albumImage, AudioFilePictureUtils.getFileNameForCover(album.getAudioFiles().get(0)));
                    } catch (IOException e1) {
                        new Logger().error(LogCategories.CONTEXT, StringUtils.getString("Error writing image for artist: ", artist.getName(), " album: ", album.getName(),
                                " Error: ", e1.getMessage()));
                    }
                }
            }
            coversRetrieved++;
            setCurrentProgress(coversRetrieved);
        }

        return true;
    }

    @Override
    public String getProgressDialogTitle() {
        return I18nUtils.getString("RETRIEVING_COVERS");
    }

    @Override
    protected void runCancel() {
        // Nothing to do
    }
}
