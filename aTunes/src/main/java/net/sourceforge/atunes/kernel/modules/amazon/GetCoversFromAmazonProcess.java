/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.kernel.modules.amazon;

import java.awt.Component;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.process.Process;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class GetCoversFromAmazonProcess.
 */
public class GetCoversFromAmazonProcess extends Process {

    /** The artist. */
    private Artist artist;

    /**
     * Instantiates a new gets the covers from amazon process.
     * 
     * @param artist
     *            the artist
     * @param progressDialog
     *            the progress dialog
     * @param coverNavigatorController
     *            the cover navigator controller
     */
    public GetCoversFromAmazonProcess(Artist artist, Component owner) {
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
        for (int i = 0; i < albums.size() && !cancel; i++) {
            Album album = albums.get(i);
            if (!album.hasCoverDownloaded()) {
                AmazonAlbum amazonAlbum = AmazonService.getInstance().getAlbum(artist.getName(), album.getName());
                if (amazonAlbum != null) {
                    Image image = AmazonService.getInstance().getImage(amazonAlbum.getImageURL());
                    try {
                        ImageUtils.writeImageToFile(image, AudioFilePictureUtils.getFileNameForCover(album.getAudioFiles().get(0)));
                    } catch (IOException e1) {
                        // Don't save image
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
