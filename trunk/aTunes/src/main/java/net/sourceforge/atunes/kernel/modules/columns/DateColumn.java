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

package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDateFormatter;

/**
 * Column to show date
 * 
 * @author alex
 * 
 */
public class DateColumn extends AbstractColumn<String> {

	private static final long serialVersionUID = 6832826017182272636L;

	private IDateFormatter dateFormatter;

	/**
	 * @param dateFormatter
	 */
	public void setDateFormatter(final IDateFormatter dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

	/**
	 * Default constructor
	 */
	public DateColumn() {
		super("DATE");
		setAlignment(SwingConstants.CENTER);
		setVisible(false);
		setUsedForFilter(true);
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		if (ao1.getDate() == null) {
			return 1;
		} else if (ao2.getDate() == null) {
			return -1;
		} else {
			return ao1.getDate().compareTo(ao2.getDate());
		}
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return -ascendingCompare(ao1, ao2);
	}

	@Override
	public String getValueFor(final IAudioObject audioObject, final int row) {
		if (audioObject.getDate() != null) {
			return this.dateFormatter.toString(audioObject.getDate());
		} else {
			return "";
		}
	}

	@Override
	public String getValueForFilter(final IAudioObject audioObject,
			final int row) {
		if (audioObject != null) {
			return this.dateFormatter.toString(audioObject.getDate());
		} else {
			return "";
		}
	}
}
