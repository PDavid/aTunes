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
		this.rep = new Repository(folders, null);
	}

	@Test
	public void createRepository() {
		Assert.assertTrue(this.rep.getFiles().isEmpty());
		Assert.assertTrue(this.rep.getArtists().isEmpty());
		Assert.assertTrue(this.rep.getFolders().isEmpty());
		Assert.assertTrue(this.rep.getGenres().isEmpty());
		Assert.assertTrue(this.rep.getYears().isEmpty());
		List<String> paths = new ArrayList<String>();
		for (File folder : this.rep.getRepositoryFolders()) {
			paths.add(folder.getAbsolutePath());
		}
		Assert.assertTrue(paths.contains(f1.getAbsolutePath()));
		Assert.assertTrue(paths.contains(f2.getAbsolutePath()));

		Assert.assertEquals(0, this.rep.getTotalDurationInSeconds());
		Assert.assertEquals(0, this.rep.getTotalSizeInBytes());
		Assert.assertEquals(0, this.rep.countFiles());
	}

	@Test
	public void testDuration() {
		this.rep.addDurationInSeconds(2);
		this.rep.addDurationInSeconds(3);
		Assert.assertEquals(5, this.rep.getTotalDurationInSeconds());
		this.rep.removeDurationInSeconds(1);
		Assert.assertEquals(4, this.rep.getTotalDurationInSeconds());
	}

	@Test
	public void testSize() {
		this.rep.addSizeInBytes(2);
		this.rep.addSizeInBytes(3);
		Assert.assertEquals(5, this.rep.getTotalSizeInBytes());
		this.rep.removeSizeInBytes(1);
		Assert.assertEquals(4, this.rep.getTotalSizeInBytes());
	}

}
