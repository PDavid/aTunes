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

package net.sourceforge.atunes.kernel.modules.cdripper;

import net.sourceforge.atunes.kernel.modules.pattern.PatternToFileTranslator;
import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Gets file names for files ripped from CD
 * @author alex
 *
 */
public class CdRipperFileNameCreator {
	
	private IStateRepository stateRepository;
	
	private PatternToFileTranslator patternToFileTranslator;
	
	/**
	 * @param patternToFileTranslator
	 */
	public void setPatternToFileTranslator(PatternToFileTranslator patternToFileTranslator) {
		this.patternToFileTranslator = patternToFileTranslator;
	}
	
	/**
	 * @param stateRepository
	 */
	public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

    /**
     * This prepares the filename for the encoder.
	 * @param cdMetadata
	 * @param trackNumber
	 * @param extension
	 * @return
	 */
	String getFileName(CDMetadata cdMetadata, int trackNumber, String extension) {
    	String fileNamePattern = stateRepository.getImportFileNamePattern();
    	String result;
    	if (StringUtils.isEmpty(fileNamePattern)) {
    		result = StringUtils.getString("track", trackNumber, '.', extension);
    	} else {
    		result = StringUtils.getString(patternToFileTranslator.translateFromPatternToFileName(cdMetadata, trackNumber, fileNamePattern), '.', extension);
    	}
        return result;
	}
}
