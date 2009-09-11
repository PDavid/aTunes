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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;

/**
 * The Class ChangeTitlesProcess.
 */
public class EditTitlesProcess extends ChangeTagProcess {

    /** The files and titles. */
    private Map<AudioFile, String> filesAndTitles;

    /**
     * Instantiates a new change titles process.
     * 
     * @param files
     *            the files
     */
    public EditTitlesProcess(List<AudioFile> files) {
        super(files);
    }

    @Override
    protected void retrieveInformationBeforeChangeTags() {
        super.retrieveInformationBeforeChangeTags();
        if (filesAndTitles == null) {
            filesAndTitles = getTitlesForFiles(filesToChange);
        }
    }

    @Override
    protected void changeTag(AudioFile file) {
        String newTitle = filesAndTitles.get(file);
        TagModifier.setTitles(file, newTitle);
    }

    /**
     * @param filesAndTitles
     *            the filesAndTitles to set
     */
    public void setFilesAndTitles(Map<AudioFile, String> filesAndTitles) {
        this.filesAndTitles = filesAndTitles;
    }

    /**
     * Returns a hash of files with its songs titles.
     * 
     * @param files
     *            the files
     * 
     * @return the titles for files
     */

    public Map<AudioFile, String> getTitlesForFiles(List<AudioFile> files) {
        Map<AudioFile, String> result = new HashMap<AudioFile, String>();

        // For each file
        for (AudioFile f : files) {
            String title = LastFmService.getInstance().getTitleForFile(f);
            if (title != null) {
                result.put(f, title);
            }
        }

        // Return files matched
        return result;
    }

}
