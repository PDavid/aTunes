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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

final class MPlayerConstants {

	/** Argument to not display more information than needed. */
	static final String QUIET = "-quiet";
	/** Argument to control mplayer through commands. */
	static final String SLAVE = "-slave";
	/** Argument to pass mplayer a play list. */
	static final String PLAYLIST = "-playlist";
	/** Arguments to filter audio output. */
	static final String AUDIO_FILTER = "-af";
	static final String VOLUME_NORM = "volnorm";
	static final String EQUALIZER = "equalizer=";
	static final String CACHE = "-cache";
	static final String CACHE_SIZE = "500";
	static final String CACHE_MIN = "-cache-min";
	static final String CACHE_FILL_SIZE_IN_PERCENT = "7.0";
	static final String PREFER_IPV4 = "-prefer-ipv4";
	static final String VOLUME = "-volume";

	// Use -vo null instead of -novideo, seek does not work with -novideo
	public static final String NOVIDEO1 = "-vo";
	public static final String NOVIDEO2 = "null";

	private MPlayerConstants() {
	}

}
