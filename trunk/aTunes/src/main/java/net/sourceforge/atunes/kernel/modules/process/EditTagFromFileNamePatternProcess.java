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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.pattern.PatternMatcher;
import net.sourceforge.atunes.kernel.modules.pattern.Patterns;
import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * The Class EditTagFromFileNamePatternProcess.
 */
public class EditTagFromFileNamePatternProcess extends AbstractChangeTagProcess {

	/**
	 * Pattern used to get tag from file name
	 */
	private String pattern;

	/** The files and tags. */
	private Map<ILocalAudioObject, Map<String, Object>> filesAndTags;

	private Patterns patterns;

	private PatternMatcher patternMatcher;

	/**
	 * @param patternMatcher
	 */
	public void setPatternMatcher(PatternMatcher patternMatcher) {
		this.patternMatcher = patternMatcher;
	}

	/**
	 * @param patterns
	 */
	public void setPatterns(Patterns patterns) {
		this.patterns = patterns;
	}

	/**
	 * @param pattern
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@Override
	protected void retrieveInformationBeforeChangeTags() {
		super.retrieveInformationBeforeChangeTags();
		if (filesAndTags == null) {
			filesAndTags = new HashMap<ILocalAudioObject, Map<String, Object>>();
			for (ILocalAudioObject file : getFilesToChange()) {
				Map<String, String> matches = patternMatcher.getPatternMatches(
						pattern, file.getNameWithoutExtension(), false);
				Map<String, Object> editTagInfo = patterns
						.getEditTagInfoFromMatches(matches);
				filesAndTags.put(file, editTagInfo);
			}
		}
	}

	@Override
	protected void changeTag(ILocalAudioObject file) {
		getTagHandler().setTag(file,
				getTagHandler().getNewTag(file, filesAndTags.get(file)));
	}
}
