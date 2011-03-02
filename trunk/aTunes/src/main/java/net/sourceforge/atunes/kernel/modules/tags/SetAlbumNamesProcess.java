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

import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.Album;

/**
 * The Class SetAlbumNamesProcess.
 */
public class SetAlbumNamesProcess extends AbstractChangeTagProcess {

    /**
     * Instantiates a new sets the album names process.
     * 
     * @param files
     *            the files
     */
    SetAlbumNamesProcess(List<AudioFile> files) {
        super(files);
    }

    @Override
    protected void changeTag(AudioFile file) {
        if (Album.isUnknownAlbum(file.getAlbum())) {
            // Take name from folder
            String albumName = file.getFile().getParentFile().getName();
            TagModifier.setAlbum(file, albumName);
        }
    }
}
