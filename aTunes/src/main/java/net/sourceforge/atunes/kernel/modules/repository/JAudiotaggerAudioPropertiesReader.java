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

package net.sourceforge.atunes.kernel.modules.repository;

import net.sourceforge.atunes.model.IAudioPropertiesReader;
import net.sourceforge.atunes.model.ILocalAudioObject;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioHeader;

/**
 * Properties reader based on JAudiotagger
 * 
 * @author alex
 * 
 */
public class JAudiotaggerAudioPropertiesReader implements
		IAudioPropertiesReader {

	@Override
	public void readAudioProperties(final ILocalAudioObject ao,
			final AudioFile file) {
		if (file != null) {
			AudioHeader header = file.getAudioHeader();
			if (header != null) {
				ao.setDuration(header.getTrackLength());
				ao.setBitrate(header.getBitRateAsNumber());
				ao.setFrequency(header.getSampleRateAsNumber());
				ao.setVariableBitrate(header.isVariableBitRate());
			}
		}
	}
}
