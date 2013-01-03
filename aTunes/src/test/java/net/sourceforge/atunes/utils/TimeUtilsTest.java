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

public class TimeUtilsTest {
	
	@Test
	public void millisecondsToHoursMinutesSecondsTest() {
		Assert.assertEquals("0:00", TimeUtils.millisecondsToHoursMinutesSeconds(0));
		Assert.assertEquals("0:00", TimeUtils.millisecondsToHoursMinutesSeconds(1));
		Assert.assertEquals("1:01", TimeUtils.millisecondsToHoursMinutesSeconds(61000));
		Assert.assertEquals("10:10", TimeUtils.millisecondsToHoursMinutesSeconds(610000));
		Assert.assertEquals("1:41:40", TimeUtils.millisecondsToHoursMinutesSeconds(6100000));
	}
	
	@Test
	public void secondsToHoursMinutesSecondsTest() {
		Assert.assertEquals("0:00", TimeUtils.secondsToHoursMinutesSeconds(0));
		Assert.assertEquals("0:01", TimeUtils.secondsToHoursMinutesSeconds(1));
		Assert.assertEquals("1:01", TimeUtils.secondsToHoursMinutesSeconds(61));
		Assert.assertEquals("10:10", TimeUtils.secondsToHoursMinutesSeconds(610));
		Assert.assertEquals("1:06:01", TimeUtils.secondsToHoursMinutesSeconds(3961));
		Assert.assertEquals("1:41:40", TimeUtils.secondsToHoursMinutesSeconds(6100));
	}
	
	@Test
	public void secondsToDaysHoursMinutesSecondsTest() {
		Assert.assertEquals("0:00", TimeUtils.secondsToDaysHoursMinutesSeconds(0));
		Assert.assertEquals("0:01", TimeUtils.secondsToDaysHoursMinutesSeconds(1));
		Assert.assertEquals("1:01", TimeUtils.secondsToDaysHoursMinutesSeconds(61));
		Assert.assertEquals("10:10", TimeUtils.secondsToDaysHoursMinutesSeconds(610));
		Assert.assertEquals("1:41:40", TimeUtils.secondsToDaysHoursMinutesSeconds(6100));
		Assert.assertEquals("16:56:40", TimeUtils.secondsToDaysHoursMinutesSeconds(61000));
		Assert.assertEquals("1 DAY 0:00:01", TimeUtils.secondsToDaysHoursMinutesSeconds(86401));
		Assert.assertEquals("7 DAYS 1:26:40", TimeUtils.secondsToDaysHoursMinutesSeconds(610000));
	}
}
