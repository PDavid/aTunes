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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Utility methods for Strings.
 */

public final class StringUtils {

	private StringUtils() {
	}

	/**
	 * Given an amount of bytes, return a string representation in Bytes,
	 * Kilobytes, Megabytes or Gigabytes Examples: Given 1024 bytes -> "1KB"
	 * Given 1536 bytes -> 1.5KB"
	 * 
	 * @param size
	 *            amount of bytes
	 * 
	 * @return String representation in Bytes, Kilobytes, Megabytes or Gigabytes
	 */
	public static String fromByteToMegaOrGiga(final long size) {
		if (size < FileUtils.KILOBYTE) {
			return StringUtils.getString(String.valueOf(size), " Bytes");
		} else if (size < FileUtils.MEGABYTE) {
			return StringUtils.getString(
					toString((double) size / FileUtils.KILOBYTE, 2), " KB");
		} else if (size < FileUtils.GIGABYTE) {
			return StringUtils.getString(
					toString((double) size / FileUtils.MEGABYTE, 2), " MB");
		} else {
			return StringUtils.getString(
					toString((double) size / FileUtils.GIGABYTE, 2), " GB");
		}
	}

	/**
	 * <p>
	 * Returns a List containing strings of the array. Text between " chars, are
	 * returned on a string
	 * </p>
	 * <p>
	 * Example: {"This", "is\"", "a ", "test\""} will return: "This" "is" "a
	 * test"
	 * </p>
	 * 
	 * @param str
	 *            String array
	 * 
	 * @return List containing strings of the array
	 */
	public static List<String> fromStringArrayToList(final String... str) {
		List<String> result = new ArrayList<String>();
		boolean openedQuotes = false;
		String auxStr = "";
		for (String s : str) {
			if (s.startsWith("\"") && s.endsWith("\"")) {
				result.add(s.replaceAll("\"", ""));
			} else if (s.endsWith("\"")) {
				openedQuotes = false;
				auxStr = StringUtils.getString(auxStr, " ",
						s.replaceAll("\"", ""));
				result.add(auxStr);
			} else if (s.startsWith("\"")) {
				openedQuotes = true;
				auxStr = s.replaceFirst("\"", "");
			} else if (openedQuotes) {
				auxStr = StringUtils.getString(auxStr, " ", s);
			} else {
				result.add(s);
			}
		}
		return result;
	}

	/**
	 * Returns a string with concatenation of argument array.
	 * 
	 * @param strings
	 *            strings
	 * 
	 * @return concatenation of argument array
	 */
	public static String getString(final Object... strings) {
		StringBuilder objStringBuilder = new StringBuilder();

		for (Object element : strings) {
			objStringBuilder.append(element);
		}

		return objStringBuilder.toString();
	}

	/**
	 * Returns a double value as a string with a given number of decimal digits.
	 * 
	 * @param value
	 *            double value
	 * @param numberOfDecimals
	 *            number of decimal digits
	 * 
	 * @return string with a given number of decimal digits
	 */
	public static String toString(final double value, final int numberOfDecimals) {
		DecimalFormat df = new DecimalFormat("#.#");
		df.setMinimumFractionDigits(numberOfDecimals);
		return df.format(value);
	}

	/**
	 * Converts the first character of a String to uppercase.
	 * 
	 * @param s
	 *            A String that should be converted
	 * 
	 * @return The String with the first character converted to uppercase
	 */
	public static String convertFirstCharacterToUppercase(final String s) {
		if (s != null && !s.isEmpty()) {
			String result;
			result = String.valueOf(Character.toUpperCase(s.charAt(0)));
			if (s.length() > 1) {
				result += s.substring(1);
			}
			return result;
		}
		return s;
	}

	/**
	 * Checks if a String is empty.
	 * 
	 * @param s
	 *            a String
	 * 
	 * @return If the specified String is empty
	 */
	public static boolean isEmpty(final String s) {
		return s == null || s.trim().isEmpty();
	}

	/**
	 * Unescapes a HTML string
	 * 
	 * @param source
	 *            the HTML string
	 * @param start
	 *            the start position
	 * @return the unescaped HTML string
	 */
	public static String unescapeHTML(final String source, final int start) {
		return StringEscapeUtils.unescapeHtml(source.substring(start));
	}

	/**
	 * Returns int represented by string argument or 0
	 * 
	 * @param number
	 * @return
	 */
	public static int getNumberOrZero(final String number) {
		if (isEmpty(number)) {
			return 0;
		}
		try {
			return Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Returns list of text between specified chars. Both chars are included in
	 * result elements. Returns empty list if chars are not found in string in
	 * given order For example given string "ab cd (ef) gh (ij)" and chars '('
	 * and ')' will return a list with two strings: "(ef)" and "(ij)"
	 * 
	 * @param string
	 * @param beginChar
	 * @param endChar
	 * @return
	 */
	public static List<String> getTextBetweenChars(final String string,
			final char beginChar, final char endChar) {
		List<String> result = new ArrayList<String>();

		if (string == null || string.indexOf(beginChar) == -1
				|| string.indexOf(endChar) == -1) {
			return result;
		}

		String auxStr = string;
		int beginIndex = auxStr.indexOf(beginChar);
		int endIndex = auxStr.indexOf(endChar);
		while (beginIndex != -1 && endIndex != -1) {
			if (beginIndex < endIndex) {
				result.add(auxStr.substring(beginIndex, endIndex + 1));
			}
			auxStr = auxStr.substring(endIndex + 1);
			beginIndex = auxStr.indexOf(beginChar);
			endIndex = auxStr.indexOf(endChar);
		}

		return result;
	}

	/**
	 * Returns true if first string is equals to any of others, false if null
	 * 
	 * @param string
	 * @param strings
	 * @return
	 */
	public static boolean equalsToStrings(final String string,
			final String... strings) {
		if (string == null) {
			return false;
		}
		for (String s : strings) {
			if (string.equals(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns common suffix for all strings (ignoring case)
	 * 
	 * @param strings
	 * @return
	 */
	public static String getCommonSuffix(final String... strings) {
		if (strings.length == 0) {
			return null;
		}
		if (strings.length == 1) {
			return strings[0];
		}

		String[] reverse = new String[strings.length];
		int i = 0;
		for (String str : strings) {
			reverse[i++] = org.apache.commons.lang.StringUtils.reverse(str);
		}

		int indexOfDifference = org.apache.commons.lang.StringUtils
				.indexOfDifference(reverse);

		return strings[0].substring(strings[0].length() - indexOfDifference);
	}

	/**
	 * Returns common prefix for all strings (ignoring case)
	 * 
	 * @param strings
	 * @return
	 */
	public static String getCommonPrefix(final String... strings) {
		if (strings.length == 0) {
			return null;
		}
		if (strings.length == 1) {
			return strings[0];
		}

		return org.apache.commons.lang.StringUtils.getCommonPrefix(strings);
	}

	/**
	 * @param string
	 * @return true if string represents an integer number
	 */
	public static boolean isNumber(String string) {
		if (!isEmpty(string)) {
			try {
				Integer.parseInt(string);
				return true;
			} catch (NumberFormatException e) {
			}
		}
		return false;
	}

}
