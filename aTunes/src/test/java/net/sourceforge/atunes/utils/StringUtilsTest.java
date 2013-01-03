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

package net.sourceforge.atunes.utils;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testEqualsToStrings() {
		Assert.assertFalse(StringUtils.equalsToStrings(null));

		Assert.assertFalse(StringUtils.equalsToStrings("a", ""));

		Assert.assertFalse(StringUtils.equalsToStrings("a", "b", "c"));

		Assert.assertFalse(StringUtils.equalsToStrings("a", "b", "A", "c"));

		Assert.assertTrue(StringUtils.equalsToStrings("a", "b", "a", "c"));
	}

	@Test
	public void testGetCommonSuffix() {
		Assert.assertEquals(" (live)", StringUtils.getCommonSuffix(
				"Song 1 (live)", "Song 2 (live)", "One more song (live)",
				"One more song 2 (live)"));

		Assert.assertEquals("(live)", StringUtils.getCommonSuffix(
				"Song 1 (live)", "Song 2 (live)", "One more song (live)",
				"One more song 2(live)"));
	}

	@Test
	public void testGetCommonPrefix() {
		Assert.assertEquals("(live) ", StringUtils.getCommonPrefix(
				"(live) Song 1", "(live) Song 2", "(live) One more song",
				"(live) One more song 2"));

		Assert.assertEquals("(live)", StringUtils.getCommonPrefix(
				"(live) Song 1", "(live) Song 2", "(live) One more song",
				"(live)One more song 2"));
	}

}
