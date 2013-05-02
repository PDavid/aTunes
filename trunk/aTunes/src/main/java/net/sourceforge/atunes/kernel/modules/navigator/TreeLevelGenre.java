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

import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.INavigationViewSorter;

/**
 * A level of a tree where genre objects are shown
 * 
 * @author alex
 * 
 */
public class TreeLevelGenre extends TreeLevel<IGenre> {

	private INavigationViewSorter genreSorter;

	private ArtistStructureBuilder artistStructureBuilder;

	/**
	 * Default constructor
	 */
	public TreeLevelGenre() {
		super();
		setMatch(TreeFilterMatch.GENRE);
	}

	/**
	 * @param artistStructureBuilder
	 */
	public void setArtistStructureBuilder(
			final ArtistStructureBuilder artistStructureBuilder) {
		this.artistStructureBuilder = artistStructureBuilder;
	}

	/**
	 * @param genreSorter
	 */
	public void setGenreSorter(final INavigationViewSorter genreSorter) {
		this.genreSorter = genreSorter;
	}

	@Override
	boolean matches(final IGenre object, final String filter) {
		return object.getName().toUpperCase().contains(filter.toUpperCase());
	}

	@Override
	Map<String, ?> getChildObjects(final IGenre object) {
		return this.artistStructureBuilder.getArtistObjects(object
				.getAudioObjects());
	}

	@Override
	INavigationViewSorter getSorter() {
		return this.genreSorter;
	}

}
