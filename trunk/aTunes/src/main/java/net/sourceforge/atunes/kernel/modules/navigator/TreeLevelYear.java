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

import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IYear;

/**
 * A level of a tree where year objects are shown
 * 
 * @author alex
 * 
 */
public class TreeLevelYear extends TreeLevel<IYear> {

	private INavigationViewSorter yearSorter;

	private ArtistStructureBuilder artistStructureBuilder;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * Default constructor
	 */
	public TreeLevelYear() {
		super();
		setMatch(TreeFilterMatch.YEAR);
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param artistStructureBuilder
	 */
	public void setArtistStructureBuilder(
			final ArtistStructureBuilder artistStructureBuilder) {
		this.artistStructureBuilder = artistStructureBuilder;
	}

	/**
	 * @param yearSorter
	 */
	public void setYearSorter(final INavigationViewSorter yearSorter) {
		this.yearSorter = yearSorter;
	}

	@Override
	boolean matches(final IYear object, final String filter) {
		return object.getName(this.unknownObjectChecker).toUpperCase()
				.contains(filter.toUpperCase());
	}

	@Override
	Map<String, ?> getChildObjects(final IYear object) {
		return this.artistStructureBuilder.getArtistObjects(object
				.getAudioObjects());
	}

	@Override
	INavigationViewSorter getSorter() {
		return this.yearSorter;
	}
}
