/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.model;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class RepositoryStructureTest {

	private static RepositoryStructure structure;
	
	@Before
	public void init() {
		structure = new RepositoryStructure();
	}

	@Test
	public void testGetFolderStructure() {
		assertTrue(structure.getFolderStructure().isEmpty());
	}

	@Test
	public void testGetGenreStructure() {
		assertTrue(structure.getGenreStructure().isEmpty());
	}

	@Test
	public void testGetArtistStructure() {
		assertTrue(structure.getArtistStructure().isEmpty());
	}

	@Test
	public void testGetYearStructure() {
		assertTrue(structure.getYearStructure().isEmpty());
	}

	@Test
	public void testGetFilesStructure() {
		assertTrue(structure.getFilesStructure().isEmpty());
	}

}
