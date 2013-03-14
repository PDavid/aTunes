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

/**
 * Column to show track number
 * 
 * @author alex
 * 
 */
public class TrackColumn extends AbstractColumn<Integer> {

	private static final long serialVersionUID = 6114834986452693757L;

	/**
	 * Constructor
	 */
	public TrackColumn() {
		super("TRACK");
		setWidth(40);
		setVisible(true);
		setAlignment(SwingConstants.CENTER);
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return Integer.valueOf(ao1.getTrackNumber()).compareTo(
				ao2.getTrackNumber());
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return -ascendingCompare(ao1, ao2);
	}

	@Override
	public Integer getValueFor(final IAudioObject audioObject, final int row) {
		// Return track number or null, otherwise problems while comparing arise
		int track = audioObject.getTrackNumber();
		return track > 0 ? track : null;
	}
}
