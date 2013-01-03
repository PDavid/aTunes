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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class PatternMatcherTest {
	
	private PatternMatcher patternMatcher;
	
	@Before
	public void init() throws InstantiationException, IllegalAccessException {
		this.patternMatcher = new PatternMatcher();
		Patterns patterns = new Patterns();

		AbstractPattern anyPattern = getPattern(AnyPattern.class, '?', "ANY", true, true); 
		patterns.setAnyPattern(anyPattern);
		
		List<AbstractPattern> list = new ArrayList<AbstractPattern>();
		list.add(anyPattern);
		
		list.add(getPattern(AlbumArtistPattern.class, 'R', "ALBUM_ARTIST", true, true));
		list.add(getPattern(AlbumPattern.class, 'L', "ALBUM", true, true));
		list.add(getPattern(ArtistFirstCharPattern.class, 'S', "ARTIST_FIRST_CHAR", false, false));
		list.add(getPattern(ArtistPattern.class, 'A', "ARTIST", true, true));
		list.add(getPattern(ComposerPattern.class, 'C', "COMPOSER", true, true));
		list.add(getPattern(DiscNumberPattern.class, 'D', "DISC_NUMBER", true, true));
		list.add(getPattern(GenrePattern.class, 'G', "GENRE", true, true));
		list.add(getPattern(TitlePattern.class, 'T', "TITLE", true, false));
		list.add(getPattern(TrackPattern.class, 'N', "TRACK", true, false));
		list.add(getPattern(YearPattern.class, 'Y', "YEAR", true, true));
		
		patterns.setPatternsList(list);
		patternMatcher.setPatterns(patterns);
	}
	
	private AbstractPattern getPattern(Class<? extends AbstractPattern> patternClass, char patternChar, String name, boolean recognitionPattern, boolean massiveRecognitionPattern) throws InstantiationException, IllegalAccessException {
		AbstractPattern pattern = patternClass.newInstance();
		pattern.setPatternChar(patternChar);
		pattern.setName(name);
		pattern.setRecognitionPattern(recognitionPattern);
		pattern.setMassiveRecognitionPattern(massiveRecognitionPattern);
		return pattern;
	}

	@Test
	public void test1() {
		Map<String, String> result = patternMatcher.getPatternMatches("%N - %T", "01 - Title", false);
		assertEquals(2, result.size());
		assertEquals("01", result.get("TRACK"));
		assertEquals("Title", result.get("TITLE"));
	}

	@Test
	public void test2() {
		Map<String, String> result = patternMatcher.getPatternMatches("%? - %N - %T", "01 - Title", false);
		assertEquals(1, result.size());
		assertEquals("Title", result.get("TRACK"));
	}

	@Test
	public void test3() {
		Map<String, String> result = patternMatcher.getPatternMatches("%? - %N - %T", "01 - Track - Title", false);
		assertEquals(2, result.size());
		assertEquals("Track", result.get("TRACK"));
		assertEquals("Title", result.get("TITLE"));
	}

	@Test
	public void test4() {
		Map<String, String> result = patternMatcher.getPatternMatches("%?", "01 - Track - Title", false);
		assertEquals(0, result.size());
	}

	@Test
	public void test5() {
		Map<String, String> result = patternMatcher.getPatternMatches("%N-%?-%T", "01 - Track - Title", false);
		assertEquals(2, result.size());
		assertEquals("01", result.get("TRACK"));
		assertEquals("Title", result.get("TITLE"));
	}

	@Test
	public void test6() {
		Map<String, String> result = patternMatcher.getPatternMatches(null, "01 - Track - Title", false);
		assertEquals(0, result.size());
	}

	@Test
	public void test7() {
		Map<String, String> result = patternMatcher.getPatternMatches("%?", null, false);
		assertEquals(0, result.size());
	}
	
	@Test
	public void test8() {
		Map<String, String> result = patternMatcher.getPatternMatches("%N-%?-%T", "01 - Track - ", false);
		assertEquals(2, result.size());
		assertEquals("01", result.get("TRACK"));
		assertEquals("", result.get("TITLE"));
	}

	@Test
	public void test9() {
		Map<String, String> result = patternMatcher.getPatternMatches("%N%T", "01Title", false);
		assertEquals(0, result.size());
	}

	@Test
	public void test10() {
		Map<String, String> result = patternMatcher.getPatternMatches("%N %N", "01 Title", false);
		assertEquals(0, result.size());
	}

	@Test
	public void test11() {
		Map<String, String> result = patternMatcher.getPatternMatches("%N %T", "01 Title", true);
		assertEquals(0, result.size());
	}

	@Test
	public void test12() {
		Map<String, String> result = patternMatcher.getPatternMatches("%N %A", "01 Iron Maiden", true);
		assertEquals(1, result.size());
		assertEquals("Iron Maiden", result.get("ARTIST"));
	}


}
