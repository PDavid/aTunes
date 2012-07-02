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

package net.sourceforge.atunes.kernel.modules.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Extracts patterns from strings
 * @author alex
 *
 */
public final class PatternMatcher {
	
	private PatternMatcher() {}

	/**
	 * Returns a map containing string values result of matching a pattern
	 * against a string
	 * 
	 * @param pattern
	 * @param value
	 * @param onlyMassiveRecognitionPatterns
	 * @return
	 */
	public static Map<String, String> getPatternMatches(String pattern, String value, boolean onlyMassiveRecognitionPatterns) {
		Map<String, String> matches = new HashMap<String, String>();

		if (pattern == null || value == null) {
			return matches;
		}

		String patternsString = pattern.trim();

		// Get all non-pattern sequences not blank
		List<String> nonPatternSequences = getNonPatternSequences(patternsString);

		int valueIndex = 0;
		int patternsStringIndex = 0;

		int i = 0;
		String matchedPattern = null;
		String matchedValue = null;

		// Start parsing patterns string
		while (patternsStringIndex < patternsString.length() && valueIndex < value.length()) {
			String nonPatternSequence = getNextNonPatternSequence(nonPatternSequences, i);

			matchedPattern = getMatchedPattern(patternsString, nonPatternSequence, patternsStringIndex);
			matchedValue = getMatchedValue(value, nonPatternSequence, valueIndex);

			patternsStringIndex = updateIndex(nonPatternSequence, patternsStringIndex, matchedPattern);
			valueIndex = updateIndex(nonPatternSequence, valueIndex, matchedValue);

			if (!checkAndAddMatch(matchedPattern, matchedValue, matches)) {
				return new HashMap<String, String>();
			}
			
			i++;
		}

		// Now return only necessary patterns
		return processPatternsFound(onlyMassiveRecognitionPatterns, matches);
	}

	/**
	 * @param patternsString
	 * @return
	 */
	private static List<String> getNonPatternSequences(String patternsString) {
		String[] nonPatternSequencesArray = patternsString.split(StringUtils.getString(AbstractPattern.PATTERN_NAME_FIRST_CHAR, '.'));
		List<String> nonPatternSequences = new ArrayList<String>();
		for (String nonPatternSequence : nonPatternSequencesArray) {
			if (!nonPatternSequence.isEmpty()) {
				nonPatternSequences.add(nonPatternSequence);
			}
		}
		return nonPatternSequences;
	}

	/**
	 * @param nonPatternSequences
	 * @param i
	 * @return
	 */
	private static String getNextNonPatternSequence(List<String> nonPatternSequences, int i) {
		String nonPatternSequence = null;
		if (i < nonPatternSequences.size()) {
			nonPatternSequence = nonPatternSequences.get(i);
		}
		return nonPatternSequence;
	}

	/**
	 * @param nonPatternSequence
	 * @param stringIndex
	 * @param matched
	 * @return
	 */
	private static int updateIndex(String nonPatternSequence, int stringIndex, String matched) {
		return stringIndex + matched.length() + (nonPatternSequence != null ? nonPatternSequence.length() : 0);
	}

	private static boolean checkAndAddMatch(String matchedPattern, String matchedValue, Map<String, String> matches) {
		// Force upper case patterns
		String match = matchedPattern.toUpperCase();

		// Duplicate patterns are not allowed (despite being ? pattern) so we return an empty map
		if (isDuplicatedPattern(matches, match)) {
			return false;
		}

		addMatch(matches, match, matchedValue);
		return true;
	}

	/**
	 * @param onlyMassiveRecognitionPatterns
	 * @param matches
	 * @return
	 */
	private static Map<String, String> processPatternsFound(boolean onlyMassiveRecognitionPatterns, Map<String, String> matches) {
		Map<String, String> result = new HashMap<String, String>();
		List<AbstractPattern> patternsToBeUsed = onlyMassiveRecognitionPatterns ? Patterns.getMassiveRecognitionPatterns() : Patterns.getPatterns();
		for (AbstractPattern p : patternsToBeUsed) {
			if (matches.containsKey(p.getPattern())) {
				result.put(p.getName(), matches.get(p.getPattern()));
			}
		}
		return result;
	}

	/**
	 * @param matches
	 * @param matchedPattern
	 * @param matchedValue
	 */
	private static void addMatch(Map<String, String> matches, String matchedPattern, String matchedValue) {
		// Ignore ? pattern
		if (!matchedPattern.equals(Patterns.getAnyPattern().getPattern())) {
			matches.put(matchedPattern, matchedValue.trim());
		}
	}

	/**
	 * @param matches
	 * @param matchedPattern
	 * @return
	 */
	private static boolean isDuplicatedPattern(Map<String, String> matches,
			String matchedPattern) {
		return !matchedPattern.equals(Patterns.getAnyPattern().getPattern()) && matches.containsKey(matchedPattern);
	}

	private static String getMatchedPattern(String patternsString, String nonPatternSequence, int patternsStringIndex) {
		if (nonPatternSequence != null) {
			// Get index of current non pattern sequence
			int indexAtPatternsString = patternsString.indexOf(nonPatternSequence, patternsStringIndex);
			// We found a matched pattern
			return patternsString.substring(patternsStringIndex, indexAtPatternsString);
		} else {
			return patternsString.substring(patternsStringIndex);
		}
	}

	private static String getMatchedValue(String value, String nonPatternSequence, int valueIndex) {
		if (nonPatternSequence != null) {
			int indexAtValueString = value.indexOf(nonPatternSequence, valueIndex);
			// If value string ended without finding non pattern sequence, then the next pattern is substring
			// from valueIndex to end of string
			// NOTE this is a less restricted pattern search, as means that the last patterns in a pattern string
			// are optional
			if (indexAtValueString == -1) {
				indexAtValueString = value.length();
			}
			return value.substring(valueIndex, indexAtValueString);
		} else {
			return value.substring(valueIndex);
		}
	}

	/**
	 * Creates a String using a pattern string and information about a cd track
	 * @param cdMetadata
	 * @param trackNumber
	 * @param patternString
	 * @return
	 */
	public static String translateToPattern(CDMetadata cdMetadata, int trackNumber, String patternString) {
		String result = patternString;
		for (AbstractPattern pattern : Patterns.getPatterns()) {
			result = pattern.applyPattern(result, cdMetadata, trackNumber);
		}
		return result;
	}
}
