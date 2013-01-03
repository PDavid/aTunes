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

package net.sourceforge.atunes.kernel.modules.player;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class EqualizerPresetsReaderTest {

	@Test
	public void test() {
		EqualizerPresetsReader sut = new EqualizerPresetsReader();
		sut.setPresetsFile("/settings/presets.properties"); // If presets file changes this test will break
		Map<String, Integer[]> presets = sut.getPresetsFromBundle();
		Assert.assertEquals(18, presets.size());
		
		// Don't test all presets, just some of them
		Assert.assertTrue(Arrays.equals(presets.get("none"), new Integer[] {31,31,31,31,31,31,31,31,31,31}));
		Assert.assertTrue(Arrays.equals(presets.get("laptop speakers or headphones"), new Integer[] {24,14,23,38,36,29,24,16,11,8}));
		
	}
}
