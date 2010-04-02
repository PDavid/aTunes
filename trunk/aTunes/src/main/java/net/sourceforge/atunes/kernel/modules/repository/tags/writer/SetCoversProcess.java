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
package net.sourceforge.atunes.kernel.modules.repository.tags.writer;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.EditTagInfo;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.AbstractTag;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * The Class SetGenresProcess.
 */
public class SetCoversProcess extends AbstractChangeTagProcess {

    private Map<AudioFile, Image> filesAndCovers;

    /**
     * Instantiates a new sets the covers process.
     * 
     * @param files
     *            the files
     */
    SetCoversProcess(List<AudioFile> files) {
        super(files);
    }

    @Override
    protected void retrieveInformationBeforeChangeTags() {
        super.retrieveInformationBeforeChangeTags();
        this.filesAndCovers = getCoversForFiles(this.getFilesToChange());
    }

    @Override
    protected void changeTag(AudioFile file) throws IOException {
        BufferedImage bufferedCover = ImageUtils.toBufferedImage(this.filesAndCovers.get(file));
        AbstractTag newTag = AudioFile.getNewTag(file, new EditTagInfo());
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
    private Map<AudioFile, Image> getCoversForFiles(List<AudioFile> files) {
        Map<AudioFile, Image> result = new HashMap<AudioFile, Image>();

        Map<Integer, Image> coverCache = new HashMap<Integer, Image>();

        for (AudioFile f : files) {
            if (!Artist.isUnknownArtist(f.getArtist()) && !Album.isUnknownAlbum(f.getAlbum())) {
                Image cover = null;
                int cacheKey = f.getArtist().hashCode() + f.getAlbum().hashCode();
                if (coverCache.containsKey(cacheKey)) {
                    cover = coverCache.get(cacheKey);
                } else {
                    AlbumInfo albumInfo = LastFmService.getInstance().getAlbum(f.getArtist(), f.getAlbum());
                    if (albumInfo == null) {
                        continue;
                    }
                    cover = LastFmService.getInstance().getImage(albumInfo);
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
