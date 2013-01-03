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

package net.sourceforge.atunes.model;

import java.io.Serializable;
import java.util.Locale;

/**
 * 
 * Stores information about a locale
 * @author alex
 *
 */
public interface ILocaleBean extends Serializable {

	/**
	 * Gets the country.
	 * 
	 * @return the country
	 */
	public String getCountry();

	/**
	 * Gets the language.
	 * 
	 * @return the language
	 */
	public String getLanguage();

	/**
	 * Get suitable java.util.Locale object
	 * 
	 * @return A suitable java.util.Locale object
	 */
	public Locale getLocale();

}