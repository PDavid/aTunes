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
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Column to show bitrate
 * @author alex
 *
 */
public class BitrateColumn extends AbstractColumn<String> {


	private static final long serialVersionUID = 7541146903350085592L;

	/**
	 * Default constructor
	 */
	public BitrateColumn() {
		super("BITRATE");
		setWidth(100);
		setVisible(false);
		setAlignment(SwingConstants.CENTER);
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		return Long.valueOf(ao1.getBitrate()).compareTo(Long.valueOf(ao2.getBitrate()));
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		return - ascendingCompare(ao1, ao2);
	}

	@Override
	public String getValueFor(final IAudioObject audioObject, final int row) {
		// Return bitrate
		if (audioObject.getBitrate() > 0) {
			return StringUtils.getString(Long.toString(audioObject.getBitrate()), " Kbps");
		}
		return "";
	}
}
