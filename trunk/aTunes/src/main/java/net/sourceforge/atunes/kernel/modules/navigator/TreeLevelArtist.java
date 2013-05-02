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

import java.util.Map;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.INavigationViewSorter;

/**
 * A level of a tree where artist objects are shown
 * 
 * @author alex
 * 
 */
public class TreeLevelArtist extends TreeLevel<IArtist> {

	private INavigationViewSorter artistSorter;

	/**
	 * Default constructor
	 */
	public TreeLevelArtist() {
		super();
		setMatch(TreeFilterMatch.ARTIST);
	}

	/**
	 * @param artistSorter
	 */
	public void setArtistSorter(final INavigationViewSorter artistSorter) {
		this.artistSorter = artistSorter;
	}

	@Override
	boolean matches(final IArtist object, final String filter) {
		return object.getName().toUpperCase().contains(filter.toUpperCase());
	}

	@Override
	Map<String, ?> getChildObjects(final IArtist object) {
		return object.getAlbums();
	}

	@Override
	INavigationViewSorter getSorter() {
		return this.artistSorter;
	}

}
