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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * Returns all possible patterns
 * 
 * @author alex
 * 
 */
public final class Patterns {

	/**
	 * All available patterns
	 */
	private List<AbstractPattern> patternsList;

	/**
	 * DUMMY PATTERN, USED TO MATCH ANYTHING
	 */
	private AbstractPattern anyPattern;

	/**
	 * @param anyPattern
	 */
	public void setAnyPattern(final AbstractPattern anyPattern) {
		this.anyPattern = anyPattern;
	}

	/**
	 * @param patternsList
	 */
	public void setPatternsList(final List<AbstractPattern> patternsList) {
		this.patternsList = patternsList;
	}

	/**
	 * Returns "any" pattern
	 * 
	 * @return
	 */
	AbstractPattern getAnyPattern() {
		return anyPattern;
	}

	/**
	 * @return
	 */
	public List<AbstractPattern> getPatternsList() {
		return patternsList;
	}

	/**
	 * Return only patterns used for massive recognition
	 * 
	 * @return
	 */
	public List<AbstractPattern> getMassiveRecognitionPatterns() {
		List<AbstractPattern> result = new ArrayList<AbstractPattern>();
		for (AbstractPattern p : patternsList) {
			if (p.isMassiveRecognitionPattern()) {
				result.add(p);
			}
		}
		return result;
	}

	/**
	 * Return only patterns used for recognition (both massive or not)
	 * 
	 * @return
	 */
	public List<AbstractPattern> getRecognitionPatterns() {
		List<AbstractPattern> result = new ArrayList<AbstractPattern>();
		for (AbstractPattern p : patternsList) {
			if (p.isRecognitionPattern()) {
				result.add(p);
			}
		}
		return result;
	}

	/**
	 * Returns a String as result of applying all defined patterns over a
	 * pattern string and a AudioFile
	 * 
	 * @param pattern
	 * @param song
	 * @return
	 */
	public String applyPatternTransformations(final String pattern,
			final ILocalAudioObject song) {
		String result = pattern;
		for (AbstractPattern transform : patternsList) {
			result = applyPattern(transform, result, song);
		}
		return result;
	}

	/**
	 * Returns a string, result of apply this pattern to an audio file object
	 * 
	 * @param pattern
	 * @param sourceString
	 * @param audioFile
	 * @return
	 */
	private String applyPattern(final AbstractPattern pattern,
			final String sourceString, final ILocalAudioObject audioFile) {
		if (!pattern.getPattern().equals(getAnyPattern().getPattern())) {
			return sourceString.replace(pattern.getPattern(),
					pattern.getAudioFileStringValue(audioFile));
		}
		return sourceString;
	}

	/**
	 * Returns a map object from a map of matches. Every attribute is filled
	 * with the matched value if found in map
	 * 
	 * @param matches
	 * @return
	 */
	public Map<String, Object> getEditTagInfoFromMatches(
			final Map<String, String> matches) {
		Map<String, Object> tagInfo = new HashMap<String, Object>();
		for (Entry<String, String> entry : matches.entrySet()) {
			tagInfo.put(entry.getKey(), entry.getValue());
		}
		return tagInfo;
	}
}
