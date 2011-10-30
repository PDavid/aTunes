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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * The Class SetGenresProcess.
 */
public class SetCoversProcess extends AbstractChangeTagProcess {

    private Map<ILocalAudioObject, Image> filesAndCovers;

    /**
     * Instantiates a new sets the covers process.
     * 
     * @param files
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     */
    SetCoversProcess(List<ILocalAudioObject> files, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler) {
        super(files, state, playListHandler, repositoryHandler, playerHandler);
    }

    @Override
    protected void retrieveInformationBeforeChangeTags() {
        super.retrieveInformationBeforeChangeTags();
        this.filesAndCovers = getCoversForFiles(this.getFilesToChange());
    }

    @Override
    protected void changeTag(ILocalAudioObject file) throws IOException {
        BufferedImage bufferedCover = ImageUtils.toBufferedImage(this.filesAndCovers.get(file));
        ITag newTag = TagFactory.getNewTag(file, new EditTagInfo());
        newTag.setInternalImage(true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedCover, "PNG", byteArrayOutputStream);
        TagModifier.setInfo(file, newTag, true, byteArrayOutputStream.toByteArray());
    }

    /**
     * Gets the covers for files.
     * 
     * @param files
     *            the files
     * 
     * @return the covers for files
     */
    private Map<ILocalAudioObject, Image> getCoversForFiles(List<ILocalAudioObject> files) {
        Map<ILocalAudioObject, Image> result = new HashMap<ILocalAudioObject, Image>();

        Map<Integer, Image> coverCache = new HashMap<Integer, Image>();

        IWebServicesHandler  webServicesHandler = Context.getBean(IWebServicesHandler.class);
        for (ILocalAudioObject f : files) {
            if (!Artist.isUnknownArtist(f.getArtist()) && !Album.isUnknownAlbum(f.getAlbum())) {
                Image cover = null;
                int cacheKey = f.getArtist().hashCode() + f.getAlbum().hashCode();
                if (coverCache.containsKey(cacheKey)) {
                    cover = coverCache.get(cacheKey);
                } else {
                    IAlbumInfo albumInfo = webServicesHandler.getAlbum(f.getArtist(), f.getAlbum());
                    if (albumInfo == null) {
                        continue;
                    }
                    cover = webServicesHandler.getAlbumImage(albumInfo);
                    if (cover == null) {
                        continue;
                    }
                    coverCache.put(cacheKey, cover);
                    // Wait one second to avoid IP banning
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // Nothing to do
                    }
                }
                if (cover != null) {
                    result.put(f, cover);
                }
            }
        }
        return result;
    }

}
