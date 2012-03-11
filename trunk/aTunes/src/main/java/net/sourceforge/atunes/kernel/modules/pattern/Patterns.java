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
import java.util.Map.Entry;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.StringUtils;

public final class Patterns {

    /**
     * All available patterns
     */
    private static List<AbstractPattern> patterns;

    /**
     * DUMMY PATTERN, USED TO MATCH ANYTHING
     */
    private static final AbstractPattern ANY_PATTERN = new AnyPattern();
    
    static {
        patterns = new ArrayList<AbstractPattern>();

        // DUMMY PATTERN, USED TO MATCH ANYTHING
        patterns.add(ANY_PATTERN);

        patterns.add(new TitlePattern());
        patterns.add(new ArtistPattern());
        patterns.add(new AlbumPattern());
        patterns.add(new AlbumArtistPattern());
        patterns.add(new TrackPattern());
        patterns.add(new GenrePattern());
        patterns.add(new YearPattern());
        patterns.add(new ComposerPattern());
        patterns.add(new ArtistFirstCharPattern());
        patterns.add(new DiscNumberPattern());
    }
    
    private Patterns() {}
    
    /**
     * Returns "any" pattern
     * @return
     */
    static AbstractPattern getAnyPattern() {
    	return ANY_PATTERN;
    }

    /**
     * Returns all available patterns
     * 
     * @return
     */
    public static List<AbstractPattern> getPatterns() {
        return patterns;
    }

    /**
     * Return only patterns used for massive recognition
     * 
     * @return
     */
    public static List<AbstractPattern> getMassiveRecognitionPatterns() {
        List<AbstractPattern> result = new ArrayList<AbstractPattern>();
        for (AbstractPattern p : patterns) {
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
    public static List<AbstractPattern> getRecognitionPatterns() {
        List<AbstractPattern> result = new ArrayList<AbstractPattern>();
        for (AbstractPattern p : patterns) {
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
    public static String applyPatternTransformations(String pattern, ILocalAudioObject song) {
        String result = pattern;
        for (AbstractPattern transform : patterns) {
            result = transform.applyPattern(result, song);
        }
        return result;
    }

    /**
     * Returns a map object from a map of matches. Every attribute is
     * filled with the matched value if found in map
     * 
     * @param matches
     * @return
     */
    public static Map<String, Object> getEditTagInfoFromMatches(Map<String, String> matches) {
        Map<String, Object> tagInfo = new HashMap<String, Object>();
        for (Entry<String, String> entry : matches.entrySet()) {
            tagInfo.put(entry.getKey(), entry.getValue());
        }
        return tagInfo;
    }

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

        // Get all non-pattern sequences
        String[] nonPatternSequences = patternsString.split(StringUtils.getString(AbstractPattern.PATTERN_NAME_FIRST_CHAR, '.'));

        int valueIndex = 0;
        int patternsStringIndex = 0;

        int i = 0;
        String matchedPattern = null;
        String matchedValue = null;

        // Start parsing patterns string
        while (patternsStringIndex < patternsString.length() && valueIndex < value.length()) {
            // Parsing before last non pattern sequence
            if (i < nonPatternSequences.length) {
                if (nonPatternSequences[i].isEmpty()) {
                    i++;
                    continue;
                }

                // Get index of current non pattern sequence
                int indexAtPatternsString = patternsString.indexOf(nonPatternSequences[i], patternsStringIndex);
                int indexAtValueString = value.indexOf(nonPatternSequences[i], valueIndex);

                // End of patterns string
                if (indexAtPatternsString == -1) {
                    break;
                }

                // If value string ended without finding non pattern sequence, then the next pattern is substring
                // from valueIndex to end of string
                // NOTE this is a less restricted pattern search, as means that the last patterns in a pattern string
                // are optional
                if (indexAtValueString == -1) {
                    indexAtValueString = value.length();
                }

                // We found a matched pattern
                matchedPattern = patternsString.substring(patternsStringIndex, indexAtPatternsString);
                matchedValue = value.substring(valueIndex, indexAtValueString);

                // Update indexes
                patternsStringIndex = patternsStringIndex + matchedPattern.length() + nonPatternSequences[i].length();
                valueIndex = valueIndex + matchedValue.length() + nonPatternSequences[i].length();

                i++;
            } else {
                // After last non pattern sequence we have a new matched pattern until end of string
                matchedPattern = patternsString.substring(patternsStringIndex);
                matchedValue = value.substring(valueIndex);

                patternsStringIndex = patternsStringIndex + matchedPattern.length();
            }

            // Force upper case patterns
            matchedPattern = matchedPattern.toUpperCase();

            // Duplicate patterns are not allowed (despite being ? pattern) so we return an empty map
            if (!matchedPattern.equals(ANY_PATTERN.getPattern()) && matches.containsKey(matchedPattern)) {
                return new HashMap<String, String>();
            }

            // Ignore ? pattern
            if (!matchedPattern.equals(ANY_PATTERN.getPattern())) {
                matches.put(matchedPattern, matchedValue.trim());
            }
        }

        // Now return only necessary patterns
        Map<String, String> result = new HashMap<String, String>();
        List<AbstractPattern> patternsToBeUsed = onlyMassiveRecognitionPatterns ? getMassiveRecognitionPatterns() : patterns;
        for (AbstractPattern p : patternsToBeUsed) {
            if (matches.containsKey(p.getPattern())) {
                result.put(p.getName(), matches.get(p.getPattern()));
            }
        }

        return result;
    }
}
