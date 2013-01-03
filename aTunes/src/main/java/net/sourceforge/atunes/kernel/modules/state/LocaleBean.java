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

package net.sourceforge.atunes.kernel.modules.state;

import java.beans.ConstructorProperties;
import java.util.Locale;

import net.sourceforge.atunes.model.ILocaleBean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Bean for java.util.Locale
 */
public final class LocaleBean implements ILocaleBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7198874125338846198L;

	private final String language;
	private final String country;

	/**
	 * Constructs LocaleBean from a given java.util.Locale
	 * 
	 * @param locale
	 *            locale
	 */
	public LocaleBean(final Locale locale) {
		this.language = locale.getLanguage();
		this.country = locale.getCountry();
	}

	/**
	 * Instantiates a new locale bean.
	 * 
	 * @param language
	 *            the language
	 * @param country
	 *            the country
	 */
	@ConstructorProperties({ "language", "country" })
	public LocaleBean(final String language, final String country) {
		this.language = language;
		this.country = country;
	}

	/**
	 * Gets the country.
	 * 
	 * @return the country
	 */
	@Override
	public String getCountry() {
		return this.country;
	}

	/**
	 * Gets the language.
	 * 
	 * @return the language
	 */
	@Override
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Get suitable java.util.Locale object
	 * 
	 * @return A suitable java.util.Locale object
	 */
	@Override
	public Locale getLocale() {
		return new Locale(this.language, this.country);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof LocaleBean) {
			return this.language.equals(((ILocaleBean) obj).getLanguage())
					&& this.country.equals(((ILocaleBean) obj).getCountry());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (this.language + this.country).hashCode();
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
