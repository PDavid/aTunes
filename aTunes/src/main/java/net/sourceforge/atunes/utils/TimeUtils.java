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

/**
 * Utility class with methods to work with time
 * @author alex
 *
 */
public final class TimeUtils {

	/** Milliseconds in a seconds */
	private static final long MILLISECONDS_SECOND = 1000;

	/** Seconds in a minute. */
	private static final long SECONDS_MINUTE = 60;

	/** Seconds in an hour. */
	private static final long SECONDS_HOUR = 3600;

	/** Seconds in a day. */
	private static final long SECONDS_DAY = 86400;

	private TimeUtils() {}

	/**
	 * Return a string representation of a given amount of milliseconds.
	 * 
	 * @param millis
	 *            the milliseconds
	 * 
	 * @return the string with hours (optional), minutes and seconds
	 */
	public static String millisecondsToHoursMinutesSeconds(long millis) {
		long seconds = millis / MILLISECONDS_SECOND;
		return secondsToHoursMinutesSeconds(seconds);
	}

	/**
	 * Return a string representation of a given amount of seconds.
	 * 
	 * @param s
	 *            seconds
	 * 
	 * @return the string with hours (optional), minutes and seconds
	 */
	public static String secondsToHoursMinutesSeconds(long s) {
		long seconds = s;
		long hours = seconds / SECONDS_HOUR;
		seconds = seconds % SECONDS_HOUR;
		long minutes = seconds / SECONDS_MINUTE;
		seconds = seconds % SECONDS_MINUTE;

		String result = getTwoDigits(seconds);
		if (hours > 0) {
			result = StringUtils.getString(hours, ":", getTwoDigits(minutes), ":", result);
		} else {
			result = StringUtils.getString(minutes, ":", result);
		}
		return result;
	}

	/**
	 * Given an amount of seconds, returns a string representation in minutes,
	 * hours and days.
	 * 
	 * @param s
	 *            seconds
	 * 
	 * @return a string representation in minutes, hours and days
	 */
	public static String secondsToDaysHoursMinutesSeconds(long s) {
		long seconds = s;
		long days = seconds / SECONDS_DAY;
		seconds = seconds % SECONDS_DAY;
		long hours = seconds / SECONDS_HOUR;
		seconds = seconds % SECONDS_HOUR;
		long minutes = seconds / SECONDS_MINUTE;
		seconds = seconds % SECONDS_MINUTE;

		String result = getTwoDigits(seconds);
		if (days > 0 || hours > 0) {
			result = StringUtils.getString(getTwoDigits(minutes), ":", result);
			if (days > 0) {
				result = StringUtils.getString(hours, ":", result);
				if (days == 1) {
					return StringUtils.getString(days, " ", I18nUtils.getString("DAY"), " ", result);
				} else if (days > 1) {
					return StringUtils.getString(days, " ", I18nUtils.getString("DAYS"), " ", result);
				}
			} else {
				result = StringUtils.getString(hours, ":", result);
			}
		} else {
			result = StringUtils.getString(minutes, ":", result);
		}
		return result;
	}

	/**
	 * @param number
	 * @return number with two digits
	 */
	private static String getTwoDigits(long number) {
		if (number < 10) {
			return StringUtils.getString("0", number);
		} else {
			return Long.toString(number);
		}
	}
}
