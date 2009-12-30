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
package net.sourceforge.atunes.kernel.modules.repository.tags.reader;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.DefaultTag;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

/**
 * Reads the tag of an audio file using JAudiotagger.
 * 
 * @author fleax
 */
public class TagDetector {

    /** The logger. */
    private static Logger logger = new Logger();

    /**
     * Calls appropriate tag reader and sends tag to AudioFile class.
     * 
     * @param file
     *            File to be read
     */
    public static void getTags(AudioFile file) {

        try {
            logger.debug(LogCategories.FILE_READ, file);

            org.jaudiotagger.audio.AudioFile f = org.jaudiotagger.audio.AudioFileIO.read(file.getFile().getAbsoluteFile());
            org.jaudiotagger.tag.Tag tag = f.getTag();

            if (tag != null) {
                DefaultTag tagForAudioFile = new DefaultTag(tag);
                file.setTag(tagForAudioFile);
            }

        } catch (Exception e) {
            logger.error(LogCategories.FILE_READ, e.getMessage());
        }
    }
}
