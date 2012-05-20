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
}
