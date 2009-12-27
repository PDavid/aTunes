/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.EditTagInfo;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;

/**
 * The Class EditTagsProcess.
 */
public class EditTagsProcess extends ChangeTagProcess {

    private EditTagInfo editTagInfo;

    /**
     * Process for writing tag to files. Receives AudioFiles to be written and
     * new properties (meta-information)
     * 
     * @param audioFilesToEdit
     *            Files that must be edited (tag)
     * @param editTagInfo
     *            Information to be written
     */
    public EditTagsProcess(List<AudioFile> audioFilesToEdit, EditTagInfo editTagInfo) {
        super(audioFilesToEdit);
        this.editTagInfo = editTagInfo;
    }

    @Override
    protected void changeTag(AudioFile audioFile) {
        Tag newTag = AudioFile.getNewTag(audioFile, editTagInfo);
        Tag oldTag = audioFile.getTag();

        byte[] c = null;
        boolean shouldEditCover = editTagInfo.isTagEdited("COVER");
        if (shouldEditCover) {
            Object cover = editTagInfo.get("COVER");
            if (cover != null) {
                c = (byte[]) cover;
            }
            newTag.setInternalImage((oldTag != null && oldTag.hasInternalImage() && !shouldEditCover) || (shouldEditCover && cover != null));
        }
        TagModifier.setInfo(audioFile, newTag, shouldEditCover, c);
        AudioFile.getImageCache().clear(audioFile);
    }
}
