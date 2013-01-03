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

package net.sourceforge.atunes.kernel.modules.pattern;

import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.FileNameUtils;

/**
 * Applies patterns to create file and folder names
 * @author alex
 *
 */
public class PatternToFileTranslator {
	
	private IOSManager osManager;
	
    private Patterns patterns;
    
    /**
     * @param patterns
     */
    public void setPatterns(Patterns patterns) {
		this.patterns = patterns;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * Creates a String using a pattern string and information about a cd track
	 * @param cdMetadata
	 * @param trackNumber
	 * @param patternString
	 * @return
	 */
	public String translateFromPatternToFileName(CDMetadata cdMetadata, int trackNumber, String patternString) {
		String result = patternString;
		for (AbstractPattern pattern : patterns.getPatternsList()) {
			result = applyPattern(pattern, result, cdMetadata, trackNumber, true);
		}
		return result;
	}
	
	/**
	 * Creates a String using a pattern string and information about a cd
	 * @param cdMetadata
	 * @param patternString
	 * @return
	 */
	public String translateFromPatternToFolderName(CDMetadata cdMetadata, String patternString) {
		String result = patternString;
		for (AbstractPattern pattern : patterns.getPatternsList()) {
			result = applyPattern(pattern, result, cdMetadata, 1, false);
		}
		return result;
	}

    /**
     * Returns a string, result of apply this pattern to a cd track
     * @param pattern
     * @param sourceString
     * @param metadata
     * @param trackNumber
     * @param file
     * @return
     */
    private String applyPattern(AbstractPattern pattern, String sourceString, CDMetadata metadata, int trackNumber, boolean file) {
        if (!pattern.equals(patterns.getAnyPattern())) {
        	String replace = pattern.getCDMetadataStringValue(metadata, trackNumber);
        	if (replace == null) {
        		replace = "";
        	}
        	if (file) {
        		replace = FileNameUtils.getValidFileName(replace, osManager);
        	} else {
        		replace = FileNameUtils.getValidFolderName(replace, osManager);
        	}
            return sourceString.replace(pattern.getPattern(), replace);
        }
        return sourceString;
    }
}
