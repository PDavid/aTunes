/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.util.Map;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;

class TagFactory {

    /**
     * Gets the new tag from file tag and given properties
     * @param file
     * @param tagInformation
     * @return
     */
    public ITag getNewTag(ILocalAudioObject file, Map<String, Object> tagInformation) {
        return new DefaultTag().setTagFromProperties(file.getTag(), tagInformation);
    }

    /**
     * Creates a new empty tag
     * @return
     */
    public ITag getNewTag() {
    	return new DefaultTag();
    }
}
