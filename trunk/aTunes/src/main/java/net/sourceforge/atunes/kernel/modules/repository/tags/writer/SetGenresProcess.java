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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;

/**
 * The Class SetGenresProcess.
 */
public class SetGenresProcess extends ChangeTagProcess {

    private Map<AudioFile, String> filesAndGenres;

    /**
     * Instantiates a new sets the genres process.
     * 
     * @param files
     *            the files
     */
    SetGenresProcess(List<AudioFile> files) {
        super(files);
    }

    @Override
    protected void retrieveInformationBeforeChangeTags() {
        super.retrieveInformationBeforeChangeTags();
        this.filesAndGenres = ContextHandler.getInstance().getGenresForFiles(filesToChange);
    }

    @Override
    protected void changeTag(AudioFile file) throws IOException {
        String genre = this.filesAndGenres.get(file);
        // If file has already genre setted, avoid
        if (!file.getGenre().equals(genre)) {
            TagModifier.setGenre(file, genre);
        }
    }

}
