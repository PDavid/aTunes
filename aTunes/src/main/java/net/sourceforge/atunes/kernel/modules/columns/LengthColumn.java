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
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.TimeUtils;

/**
 * Column to show length
 * 
 * @author alex
 * 
 */
public class LengthColumn extends AbstractColumn<String> {

	private static final long serialVersionUID = -4428276519530619107L;

	/**
	 * Default constructor
	 */
	public LengthColumn() {
		super("DURATION");
		setWidth(100);
		setVisible(true);
		setAlignment(SwingConstants.RIGHT);
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return Long.valueOf(ao1.getDuration()).compareTo(
				Long.valueOf(ao2.getDuration()));
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return -ascendingCompare(ao1, ao2);
	}

	@Override
	public String getValueFor(final IAudioObject audioObject, final int row) {
		// Return length
		if (audioObject instanceof IRadio) {
			return "";
		}
		if (audioObject instanceof IPodcastFeedEntry
				&& ((IPodcastFeedEntry) audioObject).getDuration() <= 0) {
			return "-";
		}
		return TimeUtils
				.secondsToHoursMinutesSeconds(audioObject.getDuration());
	}

}
