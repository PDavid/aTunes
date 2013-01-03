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

package net.sourceforge.atunes.kernel.modules.navigator;

import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.utils.TimeUtils;

/**
 * Shows duration of podcast
 * 
 * @author alex
 * 
 */
public final class PodcastDurationColumn extends AbstractColumn<String> {

    private static final long serialVersionUID = -5577224920500040774L;

    PodcastDurationColumn() {
	super("DURATION");
	setVisible(true);
	setWidth(60);
	setUsedForFilter(true);
    }

    @Override
    public String getValueFor(final IAudioObject audioObject, final int row) {
	return TimeUtils
		.secondsToHoursMinutesSeconds(audioObject.getDuration());
    }

    @Override
    protected int ascendingCompare(final IAudioObject o1, final IAudioObject o2) {
	return Integer.valueOf(o1.getDuration()).compareTo(
		Integer.valueOf(o2.getDuration()));
    }

    @Override
    protected int descendingCompare(final IAudioObject ao1,
	    final IAudioObject ao2) {
	return -ascendingCompare(ao1, ao2);
    }
}