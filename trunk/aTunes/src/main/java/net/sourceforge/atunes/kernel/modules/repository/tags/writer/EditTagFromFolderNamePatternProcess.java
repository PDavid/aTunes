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
package net.sourceforge.atunes.kernel.modules.repository.tags.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.pattern.AbstractPattern;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.EditTagInfo;

/**
 * The Class EditTagFromFolderNamePatternProcess.
 */
public class EditTagFromFolderNamePatternProcess extends ChangeTagProcess {

    /**
     * Pattern used to get tag from folder name
     */
    private String pattern;

    /** The files and tags. */
    private Map<AudioFile, EditTagInfo> filesAndTags;

    /**
     * Instantiates a new process
     * 
     * @param files
     *            the files
     */
    public EditTagFromFolderNamePatternProcess(List<AudioFile> files, String pattern) {
        super(files);
        this.pattern = pattern;
    }

    @Override
    protected void retrieveInformationBeforeChangeTags() {
        super.retrieveInformationBeforeChangeTags();
        if (filesAndTags == null) {
            filesAndTags = new HashMap<AudioFile, EditTagInfo>();
            for (AudioFile file : filesToChange) {
                Map<String, String> matches = AbstractPattern.getPatternMatches(pattern, file.getFile().getParentFile().getName(), true);
                EditTagInfo editTagInfo = AbstractPattern.getEditTagInfoFromMatches(matches);
                filesAndTags.put(file, editTagInfo);
            }
        }
    }

    @Override
    protected void changeTag(AudioFile file) {
        TagModifier.setInfo(file, AudioFile.getNewTag(file, filesAndTags.get(file)));
    }
}
