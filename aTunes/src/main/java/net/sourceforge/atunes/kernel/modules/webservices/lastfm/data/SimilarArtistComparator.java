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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm.data;

import java.util.Comparator;

import net.sourceforge.atunes.model.IArtistInfo;

/**
 * Sorts similar artists by match value
 * 
 * @author alex
 * 
 */
public class SimilarArtistComparator implements Comparator<IArtistInfo> {

	@Override
	public int compare(final IArtistInfo o1, final IArtistInfo o2) {
		return -Float.valueOf(o1.getMatch()).compareTo(
				Float.valueOf(o2.getMatch()));
	}

}
