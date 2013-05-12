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

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * The Class DateUtils.
 */
public final class DateUtils {

	/*
	 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
	 * 
	 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
	 * 
	 * The contents of this file are subject to the terms of either the GNU
	 * General Public License Version 2 only ("GPL") or the Common Development
	 * and Distribution License("CDDL") (collectively, the "License"). You may
	 * not use this file except in compliance with the License. You can obtain a
	 * copy of the License at
	 * https://glassfish.dev.java.net/public/CDDL+GPL.html or
	 * glassfish/bootstrap/legal/LICENSE.txt. See the License for the specific
	 * language governing permissions and limitations under the License.
	 * 
	 * When distributing the software, include this License Header Notice in
	 * each file and include the License file at
	 * glassfish/bootstrap/legal/LICENSE.txt. Sun designates this particular
	 * file as subject to the "Classpath" exception as provided by Sun in the
	 * GPL Version 2 section of the License file that accompanied this code. If
	 * applicable, add the following below the License Header, with the fields
	 * enclosed by brackets [] replaced by your own identifying information:
	 * "Portions Copyrighted [year] [name of copyright owner]"
	 * 
	 * Contributor(s):
	 * 
	 * If you wish your version of this file to be governed by only the CDDL or
	 * only the GPL Version 2, indicate your decision by adding "[Contributor]
	 * elects to include this software in this distribution under the [CDDL or
	 * GPL Version 2] license." If you don't indicate a single choice of
	 * license, a recipient has the option to distribute your version of this
	 * file under either the CDDL, the GPL Version 2 or to extend the choice of
	 * license to its licensees as provided above. However, if you add GPL
	 * Version 2 code and therefore, elected the GPL Version 2 license, then the
	 * option applies only if the new code is made subject to such option by the
	 * copyright holder.
	 */

	private DateUtils() {
	}

	/**
	 * Returns the current year.
	 * 
	 * @return the current year
	 */
	public static int getCurrentYear() {
		return new DateTime().getYear();
	}

	private static final DateTimeFormatter RFC3339_1 = DateTimeFormat
			.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withLocale(Locale.US);
	private static final DateTimeFormatter RFC3339_2 = DateTimeFormat
			.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
			.withLocale(Locale.US);
	private static final DateTimeFormatter RFC3339_3 = DateTimeFormat
			.forPattern("yyyy-MM-dd'T'HH:mm:ssZ").withLocale(Locale.US);
	private static final DateTimeFormatter RFC3339_4 = DateTimeFormat
			.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ").withLocale(Locale.US);

	/*
	 * -> http://hatori42.com/RFC3339Date.txt
	 * 
	 * Feel free to use this in your code, but I'd appreciate it if you keep
	 * this note in the code if you distribute it. Thanks!
	 * 
	 * For people who might be googling: The date format parsed by this goes by:
	 * atomDateConstruct, xsd:dateTime, RFC3339 and is compatable with:
	 * ISO.8601.1988, W3C.NOTE-datetime-19980827 and
	 * W3C.REC-xmlschema-2-20041028 (that I know of)
	 * 
	 * Copyright 2007, Chad Okere (ceothrow1 at gmail dotcom) OMG NO WARRENTY
	 * EXPRESSED OR IMPLIED!!!
	 * 
	 * 
	 * 
	 * 
	 * 2011/09/13: Alex Aranda: Code refactored
	 */
	/**
	 * Parses a RFC 3339 date string.
	 * 
	 * @param dateStr
	 *            A RFC 3339 date as string
	 * 
	 * @return The accordant Date object or <code>null</code> if the date
	 *         couldn't be parsed
	 */
	public static DateTime parseRFC3339Date(final String dateStr) {
		if (!StringUtils.isEmpty(dateStr)) {
			// if there is no time zone, we don't need to do any special
			// parsing.
			if (dateStr.endsWith("Z")) {
				try {
					return RFC3339_1.parseDateTime(dateStr);
				} catch (IllegalArgumentException e) {
					try {
						return RFC3339_2.parseDateTime(dateStr);
					} catch (IllegalArgumentException e2) {
						return null;
					}
				}
			} else if (dateStr.indexOf('-') != -1) {
				// step one, split off the timezone.
				String firstpart = dateStr.substring(0,
						dateStr.lastIndexOf('-'));
				String secondpart = dateStr.substring(dateStr.lastIndexOf('-'));

				if (secondpart.indexOf(':') != -1) {
					// step two, remove the colon from the timezone offset
					secondpart = StringUtils.getString(
							secondpart.substring(0, secondpart.indexOf(':')),
							secondpart.substring(secondpart.indexOf(':') + 1));
					String dateString = StringUtils.getString(firstpart,
							secondpart);

					try {
						return RFC3339_3.parseDateTime(dateString);
					} catch (IllegalArgumentException e) {
						try {
							return RFC3339_4.parseDateTime(dateString);
						} catch (IllegalArgumentException e2) {
							return null;
						}
					}
				}

			}
		}
		return null;
	}

	/**
	 * Parses a RFC 822 date string.
	 * 
	 * @param dateString
	 *            A RFC 822 date as string
	 * 
	 * @return The accordant Date object or <code>null</code> if the date
	 *         couldn't be parsed
	 */
	public static DateTime parseRFC822Date(final String dateString) {
		try {
			return DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss ZZ")
					.withLocale(Locale.US).parseDateTime(dateString);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * Returns a String with locale representation of Date, replacing / for -.
	 * 
	 * @param objDate
	 *            the obj date
	 * 
	 * @return the string
	 */
	public static String toPathString(final DateTime objDate) {
		return DateTimeFormat.shortDateTime().print(objDate).replace("/", "-");
	}
}
