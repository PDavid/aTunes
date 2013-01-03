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

package net.sourceforge.atunes.kernel.modules.statistics;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class AudioObjectStatsTest {

	@Test
	public void testInit() {
		AudioObjectStats sut = new AudioObjectStats();
		Assert.assertNull(sut.getLastPlayed());
		Assert.assertEquals(0, sut.getTimesPlayed());
	}
	
	@Test
	public void testIncrease() {
		AudioObjectStats sut = new AudioObjectStats();
		sut.increaseStatistics();
		Assert.assertNotNull(sut.getLastPlayed());
		Assert.assertEquals(1, sut.getTimesPlayed());
		DateTime dt = sut.getLastPlayed();
		sut.increaseStatistics();
		Assert.assertNotNull(sut.getLastPlayed());
		Assert.assertEquals(2, sut.getTimesPlayed());
		Assert.assertFalse(sut.getLastPlayed().isBefore(dt));
	}

	@Test
	public void testReset() {
		AudioObjectStats sut = new AudioObjectStats();
		sut.increaseStatistics();
		sut.resetStatistics();
		Assert.assertNull(sut.getLastPlayed());
		Assert.assertEquals(0, sut.getTimesPlayed());
	}

}
