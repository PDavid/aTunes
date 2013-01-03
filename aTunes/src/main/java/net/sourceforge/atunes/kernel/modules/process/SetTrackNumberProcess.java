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

package net.sourceforge.atunes.kernel.modules.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * The Class SetTrackNumberProcess.
 */
public class SetTrackNumberProcess extends AbstractChangeTagProcess {

    /** The files and tracks. */
    private Map<ILocalAudioObject, Integer> filesAndTracks;

    /**
     * @param filesAndTracks
     */
    public void setFilesAndTracks(Map<ILocalAudioObject, Integer> filesAndTracks) {
    	setFilesToChange(new ArrayList<ILocalAudioObject>(filesAndTracks.keySet()));
		this.filesAndTracks = filesAndTracks;
	}

    @Override
    protected void changeTag(ILocalAudioObject file) throws IOException {
        getTagHandler().setTrackNumber(file, filesAndTracks.get(file));
    }
}
