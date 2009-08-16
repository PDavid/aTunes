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

package net.sourceforge.atunes.kernel.modules.repository.tags.writer;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.EditTagInfo;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * The Class SetGenresProcess.
 */
public class SetCoversProcess extends ChangeTagProcess {

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
        this.filesAndCovers = ContextHandler.getInstance().getCoversForFiles(this.filesToChange);
    }

    @Override
    protected void changeTag(AudioFile file) throws IOException {
        BufferedImage bufferedCover = ImageUtils.toBufferedImage(this.filesAndCovers.get(file));
        Tag newTag = AudioFile.getNewTag(file, new EditTagInfo());
        newTag.setInternalImage(true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedCover, "PNG", byteArrayOutputStream);
        TagModifier.setInfo(file, newTag, true, byteArrayOutputStream.toByteArray());
    }
}
