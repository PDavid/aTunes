/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.data.Format;
import net.sourceforge.atunes.kernel.modules.tags.TagDetector;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;

public class LocalAudioObjectFactory implements ILocalAudioObjectFactory {
	
	@Override
	public ILocalAudioObject getLocalAudioObject(File file) {
		ILocalAudioObject audioObject = new AudioFile(file);
		readAudioObject(audioObject);
		return audioObject;
	}
	
	@Override
	public ILocalAudioObject refreshAudioObject(ILocalAudioObject audioObject) {
        audioObject.setTag(null);
        readInformation(audioObject, false);
        audioObject.setReadTime(System.currentTimeMillis());
        return audioObject;
	}
	
    /**
     * Reads a file
     * @param audioObject
     */
    private void readAudioObject(ILocalAudioObject audioObject) {
        // Don't read from formats not supported by Jaudiotagger
        if (!LocalAudioObjectValidator.isValidAudioFile(audioObject.getUrl(), Format.APE, Format.MPC)) {
            readInformation(audioObject, true);
        }
        audioObject.setReadTime(System.currentTimeMillis());
    }
    
    /**
     * Introspect tags. Get the tag for the file.
     * @param audioObject
     * @param readAudioProperties
     */
    private void readInformation(ILocalAudioObject audioObject, boolean readAudioProperties) {
        TagDetector.readInformation(audioObject, readAudioProperties);
    }
    
}
