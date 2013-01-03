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

import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Converts from ID3 ratings to stars
 * 
 * @author alex
 * 
 */
public class RatingsToStars {

	private int minRatingValue;

	private int maxRatingValue;

	private int numberOfStars;

	/**
	 * @param numberOfStars
	 */
	public void setNumberOfStars(final int numberOfStars) {
		this.numberOfStars = numberOfStars;
	}

	/**
	 * @param minRatingValue
	 */
	public void setMinRatingValue(final int minRatingValue) {
		this.minRatingValue = minRatingValue;
	}

	/**
	 * @param maxRatingValue
	 */
	public void setMaxRatingValue(final int maxRatingValue) {
		this.maxRatingValue = maxRatingValue;
	}

	/**
	 * @param rating
	 * @return stars
	 */
	int ratingToStars(final String rating) {
		if (!StringUtils.isEmpty(rating)) {
			try {
				Integer stars = Integer.parseInt(rating);
				if (stars >= this.minRatingValue
						&& stars <= this.maxRatingValue) {
					return stars / (this.maxRatingValue / this.numberOfStars);
				}
			} catch (NumberFormatException e) {
				Logger.error("Wrong rating: ", rating);
			}
		}
		return this.minRatingValue;
	}

	/**
	 * @param value
	 * @return rating string to store in tag
	 */
	public String starsToRating(final Integer value) {
		return Integer.toString(value
				* (this.maxRatingValue / this.numberOfStars));
	}

}
