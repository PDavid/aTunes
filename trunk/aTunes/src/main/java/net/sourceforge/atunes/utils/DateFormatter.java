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

import net.sourceforge.atunes.model.IDateFormatter;
import net.sourceforge.atunes.model.IStateCore;

import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Formats Joda dates
 * 
 * @author alex
 * 
 */
public class DateFormatter implements IDateFormatter {

	private DateTimeFormatter dateFormat;

	private IStateCore stateCore;

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	@Override
	public String toString(final BaseDateTime date) {
		return date != null ? getDateFormat().print(date) : null;
	}

	/**
	 * @return date formatter to use
	 */
	private DateTimeFormatter getDateFormat() {
		if (this.dateFormat == null) {
			this.dateFormat = DateTimeFormat.mediumDate().withLocale(
					this.stateCore.getLocale().getLocale());
		}
		return this.dateFormat;
	}
}
