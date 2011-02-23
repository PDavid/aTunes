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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.pattern.AbstractPattern;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;

/**
 * The Class EditTagFromFileNamePatternProcess.
 */
public class EditTagFromFileNamePatternProcess extends AbstractChangeTagProcess {

    /**
     * Pattern used to get tag from file name
     */
    private String pattern;

    /** The files and tags. */
    private Map<AudioFile, EditTagInfo> filesAndTags;

    /**
     * Instantiates a new change titles process.
     * 
     * @param files
     *            the files
     */
    public EditTagFromFileNamePatternProcess(List<AudioFile> files, String pattern) {
        super(files);
        this.pattern = pattern;
    }

    @Override
    protected void retrieveInformationBeforeChangeTags() {
        super.retrieveInformationBeforeChangeTags();
        if (filesAndTags == null) {
            filesAndTags = new HashMap<AudioFile, EditTagInfo>();
            for (AudioFile file : getFilesToChange()) {
                Map<String, String> matches = AbstractPattern.getPatternMatches(pattern, file.getNameWithoutExtension(), false);
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
