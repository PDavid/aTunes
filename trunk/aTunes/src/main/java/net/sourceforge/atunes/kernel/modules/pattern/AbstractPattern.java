/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.EditTagInfo;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Defines patterns based on AudioFile properties to get strings to be used in
 * file or folder names
 * 
 * @author fleax
 */
public abstract class AbstractPattern {

    private static final char PATTERN_NAME_FIRST_CHAR = '%';

    /**
     * All available patterns
     */
    private static List<AbstractPattern> patterns;

    /**
     * DUMMY PATTERN, USED TO MATCH ANYTHING
     */
    private static final AbstractPattern ANY_PATTERN = new AbstractPattern('?', "ANY", true, true) {
        @Override
        public String getAudioFileStringValue(AudioFile audioFile) {
            // This is a dummy pattern
            return null;
        }
    };

    /**
     * Returns all available patterns
     * 
     * @return
     */
    public static List<AbstractPattern> getPatterns() {
        if (patterns == null) {
            patterns = new ArrayList<AbstractPattern>();

            // DUMMY PATTERN, USED TO MATCH ANYTHING
            patterns.add(ANY_PATTERN);

            patterns.add(new AbstractPattern('T', "TITLE", true, false) {
                @Override
                public String getAudioFileStringValue(AudioFile audioFile) {
                    return audioFile.getTitleOrFileName();
                }
            });
            patterns.add(new AbstractPattern('A', "ARTIST", true, true) {
                @Override
                public String getAudioFileStringValue(AudioFile audioFile) {
                    return audioFile.getArtist();
                }
            });
            patterns.add(new AbstractPattern('L', "ALBUM", true, true) {
                @Override
                public String getAudioFileStringValue(AudioFile audioFile) {
                    return audioFile.getAlbum();
                }
            });
            patterns.add(new AbstractPattern('R', "ALBUM_ARTIST", true, true) {
                @Override
                public String getAudioFileStringValue(AudioFile audioFile) {
                    return audioFile.getAlbumArtist();
                }
            });
            patterns.add(new AbstractPattern('N', "TRACK", true, false) {
                @Override
                public String getAudioFileStringValue(AudioFile audioFile) {
                    String track = String.valueOf(audioFile.getTrackNumber());
                    return track.length() < 2 ? StringUtils.getString("0", track) : track;
                }
            });
            patterns.add(new AbstractPattern('G', "GENRE", true, true) {
                @Override
                public String getAudioFileStringValue(AudioFile audioFile) {
                    return audioFile.getGenre();
                }
            });
            patterns.add(new AbstractPattern('Y', "YEAR", true, true) {
                @Override
                public String getAudioFileStringValue(AudioFile audioFile) {
                    return audioFile.getYear();
                }
            });
            patterns.add(new AbstractPattern('C', "COMPOSER", true, true) {
                @Override
                public String getAudioFileStringValue(AudioFile audioFile) {
                    return audioFile.getComposer();
                }
            });
            patterns.add(new AbstractPattern('S', "ARTIST_FIRST_CHAR", false, false) {
                @Override
                public String getAudioFileStringValue(AudioFile audioFile) {
                    return audioFile.getArtist().substring(0, 1);
                }
            });
            patterns.add(new AbstractPattern('D', "DISC_NUMBER", true, true) {
                @Override
                public String getAudioFileStringValue(AudioFile audioFile) {
                    return String.valueOf(audioFile.getDiscNumber());
                }
            });
        }
        return patterns;
    }

    /**
     * Return only patterns used for massive recognition
     * 
     * @return
     */
    public static List<AbstractPattern> getMassiveRecognitionPatterns() {
        List<AbstractPattern> result = new ArrayList<AbstractPattern>();
        for (AbstractPattern p : getPatterns()) {
            if (p.massiveRecognitionPattern) {
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
        for (AbstractPattern p : getPatterns()) {
            if (p.recognitionPattern) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Chars representing this pattern
     */
    private String pattern;

    /**
     * Name of this pattern
     */
    private String name;

    /**
     * <code>true</code> if this pattern can be used for massive pattern
     * recognition
     * 
     * For example: song title or track number are not massive patterns as it
     * only apply to one song at a time
     * 
     */
    boolean massiveRecognitionPattern = false;

    /**
     * <code>true</code> if this pattern can be used for pattern recognition
     * 
     * For example: To be used for recognition application must be able to
     * assign a full attribute value from a pattern Fist artist char is not a
     * recognition pattern as it is not a complete attribute of a song
     * 
     */
    boolean recognitionPattern = false;

    /**
     * Private constructor
     * 
     * @param patternChar
     * @param name
     * @param recognitionPattern
     * @param massiveRecognitionPattern
     */
    protected AbstractPattern(char patternChar, String name, boolean recognitionPattern, boolean massiveRecognitionPattern) {
        // Force upper case
        this.pattern = StringUtils.getString(PATTERN_NAME_FIRST_CHAR, patternChar).toUpperCase();
        this.name = name;
        this.recognitionPattern = recognitionPattern;
        this.massiveRecognitionPattern = massiveRecognitionPattern;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description of this pattern
     */
    public String getDescription() {
        return I18nUtils.getString(name);
    }

    /**
     * Returns a string, result of apply this pattern to an audio file object
     * 
     * @param audioFile
     * @return
     */
    public String applyPattern(String sourceString, AudioFile audioFile) {
        if (!pattern.equals(ANY_PATTERN.pattern)) {
            return sourceString.replaceAll(pattern, getAudioFileStringValue(audioFile));
        }
        return sourceString;
    }

    /**
     * Returns string value of an AudioFile to do transformation
     * 
     * @param audioFile
     * @return
     */
    public abstract String getAudioFileStringValue(AudioFile audioFile);

    /**
     * Returns a String as result of applying all defined patterns over a
     * pattern string and a AudioFile
     * 
     * @param pattern
     * @param song
     * @return
     */
    public static String applyPatternTransformations(String pattern, AudioFile song) {
        String result = pattern;
        for (AbstractPattern transform : getPatterns()) {
            result = transform.applyPattern(result, song);
        }
        return result;
    }

    /**
     * Returns a EditTagInfo object from a map of matches. Every attribute is
     * filled with the matched value if found in map
     * 
     * @param matches
     * @return
     */
    public static EditTagInfo getEditTagInfoFromMatches(Map<String, String> matches) {
        EditTagInfo tagInfo = new EditTagInfo();
        for (Entry<String, String> entry : matches.entrySet()) {
            tagInfo.put(entry.getKey(), entry.getValue());
        }
        return tagInfo;
    }

    /**
     * Returns a map containing string values result of matching a pattern
     * against a string
     * 
     * @param patterns
     * @param value
     * @param onlyMassiveRecognitionPatterns
     * @return
     */
    public static Map<String, String> getPatternMatches(String patterns, String value, boolean onlyMassiveRecognitionPatterns) {
        Map<String, String> matches = new HashMap<String, String>();

        if (patterns == null || value == null) {
            return matches;
        }

        String patternsString = patterns.trim();

        // Get all non-pattern sequences
        String[] nonPatternSequences = patternsString.split(StringUtils.getString(PATTERN_NAME_FIRST_CHAR, '.'));

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
        List<AbstractPattern> patternsToBeUsed = onlyMassiveRecognitionPatterns ? getMassiveRecognitionPatterns() : getPatterns();
        for (AbstractPattern p : patternsToBeUsed) {
            if (matches.containsKey(p.getPattern())) {
                result.put(p.getName(), matches.get(p.getPattern()));
            }
        }

        return result;
    }
}
