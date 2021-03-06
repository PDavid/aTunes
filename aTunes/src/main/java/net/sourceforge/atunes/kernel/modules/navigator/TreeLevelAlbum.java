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

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.INavigationViewSorter;

/**
 * A level of a tree where album objects are shown
 * 
 * @author alex
 * 
 */
public class TreeLevelAlbum extends TreeLevel<IAlbum> {

	private INavigationViewSorter albumSorter;

	/**
	 * Default constructor
	 */
	public TreeLevelAlbum() {
		super();
		setMatch(TreeFilterMatch.ALBUM);
	}

	/**
	 * @param albumSorter
	 */
	public void setAlbumSorter(final INavigationViewSorter albumSorter) {
		this.albumSorter = albumSorter;
	}

	@Override
	boolean matches(final IAlbum object, final String filter) {
		return object.getName().toUpperCase().contains(filter.toUpperCase());
	}

	@Override
	Map<String, ?> getChildObjects(final IAlbum object) {
		return null;
	}

	@Override
	INavigationViewSorter getSorter() {
		return this.albumSorter;
	}

}
