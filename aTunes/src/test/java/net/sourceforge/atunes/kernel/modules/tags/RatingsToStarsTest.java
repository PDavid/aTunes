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

package net.sourceforge.atunes.kernel.modules.tags;

import org.junit.Assert;
import org.junit.Test;

public class RatingsToStarsTest {

	@Test
	public void testRatingToStars() {
		RatingsToStars sut = new RatingsToStars();
		sut.setMaxRatingValue(255);
		sut.setMinRatingValue(0);
		sut.setNumberOfStars(5);

		Assert.assertEquals(0, sut.ratingToStars(null));
		Assert.assertEquals(0, sut.ratingToStars(""));
		Assert.assertEquals(0, sut.ratingToStars("0"));
		Assert.assertEquals(0, sut.ratingToStars("10"));
		Assert.assertEquals(1, sut.ratingToStars("51"));
		Assert.assertEquals(1, sut.ratingToStars("52"));
		Assert.assertEquals(5, sut.ratingToStars("255"));
	}

	@Test
	public void testStarsToRating() {
		RatingsToStars sut = new RatingsToStars();
		sut.setMaxRatingValue(255);
		sut.setMinRatingValue(0);
		sut.setNumberOfStars(5);

		Assert.assertEquals("0", sut.starsToRating(0));
		Assert.assertEquals("51", sut.starsToRating(1));
		Assert.assertEquals("102", sut.starsToRating(2));
		Assert.assertEquals("255", sut.starsToRating(5));
	}
}
