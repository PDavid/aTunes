/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import org.commonjukebox.plugins.PluginApi;

/**
 * Utility methods for Strings.
 */
@PluginApi
public final class StringUtils {

    private StringUtils() {
    }

    /** Kilobyte 1024 bytes. */
    private static final long KILOBYTE = 1024;

    /** Megabyte 1024 Kilobytes. */
    private static final long MEGABYTE = KILOBYTE * 1024;

    /** Gigabyte 1024 Megabytes. */
    private static final long GIGABYTE = MEGABYTE * 1024;

    /** Seconds in a minute. */
    private static final long SECONDS_MINUTE = 60;

    /** Seconds in an hour. */
    private static final long SECONDS_HOUR = 3600;

    /** Seconds in a day. */
    private static final long SECONDS_DAY = 86400;

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
    public static String fromByteToMegaOrGiga(long size) {
        if (size < KILOBYTE) {
            return StringUtils.getString(String.valueOf(size), " Bytes");
        } else if (size < MEGABYTE) {
            return StringUtils.getString(toString((double) size / KILOBYTE, 2), " KB");
        } else if (size < GIGABYTE) {
            return StringUtils.getString(toString((double) size / MEGABYTE, 2), " MB");
        } else {
            return StringUtils.getString(toString((double) size / GIGABYTE, 2), " GB");
        }
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

    public static String fromSecondsToHoursAndDays(long s) {
        long seconds = s;

        long days = seconds / SECONDS_DAY;
        seconds = seconds % SECONDS_DAY;
        long hours = seconds / SECONDS_HOUR;
        seconds = seconds % SECONDS_HOUR;
        long minutes = seconds / SECONDS_MINUTE;
        seconds = seconds % SECONDS_MINUTE;

        String hoursMinutesSeconds = StringUtils.getString(hours, ":", (minutes < 10 ? "0" : ""), minutes, ":", (seconds < 10 ? "0" : ""), seconds);
        if (days == 1) {
            return StringUtils.getString(days, " ", I18nUtils.getString("DAY"), " ", hoursMinutesSeconds);
        } else if (days > 1) {
            return StringUtils.getString(days, " ", I18nUtils.getString("DAYS"), " ", hoursMinutesSeconds);
        } else {
            return hoursMinutesSeconds;
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
    public static List<String> fromStringArrayToList(String... str) {
        List<String> result = new ArrayList<String>();
        boolean openedQuotes = false;
        String auxStr = "";
        for (String s : str) {
            if (s.startsWith("\"") && s.endsWith("\"")) {
                result.add(s.replaceAll("\"", ""));
            } else if (s.endsWith("\"")) {
                openedQuotes = false;
                auxStr = StringUtils.getString(auxStr, " ", s.replaceAll("\"", ""));
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
    public static String getString(Object... strings) {
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
    public static String toString(double value, int numberOfDecimals) {
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
    public static String convertFirstCharacterToUppercase(String s) {
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
    public static boolean isEmpty(String s) {
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
    public static String unescapeHTML(String source, int start) {
        return StringEscapeUtils.unescapeHtml(source.substring(start));
    }

    /**
     * Returns int represented by string argument or 0
     * 
     * @param number
     * @return
     */
    public static int getNumberOrZero(String number) {
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
    public static List<String> getTextBetweenChars(String string, char beginChar, char endChar) {
        List<String> result = new ArrayList<String>();

        if (string == null || string.indexOf(beginChar) == -1 || string.indexOf(endChar) == -1) {
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
     * Return a string representation of a given amount of microseconds.
     * 
     * @param micros
     *            the microseconds
     * 
     * @return the string
     */
    public static String microseconds2String(long micros) {
        long aux = micros / 1000000;
        int minutes = (int) aux / 60;
        aux = aux % 60;
        return getString(minutes, ":", (aux < 10 ? "0" : ""), aux);
    }

    /**
     * Return a string representation of a given amount of milliseconds.
     * 
     * @param millis
     *            the milliseconds
     * 
     * @return the string
     */
    public static String milliseconds2String(long millis) {
        long aux = millis / 1000;
        int minutes = (int) aux / 60;
        aux = aux % 60;
        return getString(minutes, ":", (aux < 10 ? "0" : ""), aux);
    }

    /**
     * Return a string representation of a given amount of seconds.
     * 
     * @param s
     *            seconds
     * 
     * @return the string
     */
    public static String seconds2String(long s) {
        long seconds = s;
        int minutes = (int) seconds / 60;
        seconds = seconds % 60;
        return getString(minutes, ":", (seconds < 10 ? "0" : ""), seconds);
    }
}
