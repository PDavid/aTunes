/*
 * aTunes 3.1.0
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.sourceforge.atunes.kernel.modules.repository.Repository;

import org.junit.Before;
import org.junit.Test;


public class RepositoryTest {

	private static final String REP_FOLDER_1 = "folder_1";
	private static final String REP_FOLDER_2 = "folder_2";
	
	private IRepository rep;
	
	private static File f1 = new File(REP_FOLDER_1);
	private static File f2 = new File(REP_FOLDER_2);

	@Before
	public void init() {
		List<File> folders = new ArrayList<File>();
		folders.add(f1);
		folders.add(f2);
		rep = new Repository(folders, null);
	}
	
	@Test
	public void createRepository() {
		Assert.assertTrue(rep.getFiles().isEmpty());
		Assert.assertTrue(rep.getArtists().isEmpty());
		Assert.assertTrue(rep.getFolders().isEmpty());
		Assert.assertTrue(rep.getGenres().isEmpty());
		Assert.assertTrue(rep.getYears().isEmpty());
		Assert.assertTrue(rep.getRepositoryFolders().contains(f1));
		Assert.assertTrue(rep.getRepositoryFolders().contains(f2));
		
		Assert.assertEquals(0, rep.getTotalDurationInSeconds());
		Assert.assertEquals(0, rep.getTotalSizeInBytes());
		Assert.assertEquals(0, rep.countFiles());
	}
	
	@Test
	public void testDuration() {
		rep.addDurationInSeconds(2);
		rep.addDurationInSeconds(3);
		Assert.assertEquals(5, rep.getTotalDurationInSeconds());
		rep.removeDurationInSeconds(1);
		Assert.assertEquals(4, rep.getTotalDurationInSeconds());
	}
	
	@Test
	public void testSize() {
		rep.addSizeInBytes(2);
		rep.addSizeInBytes(3);
		Assert.assertEquals(5, rep.getTotalSizeInBytes());
		rep.removeSizeInBytes(1);
		Assert.assertEquals(4, rep.getTotalSizeInBytes());
	}

}
