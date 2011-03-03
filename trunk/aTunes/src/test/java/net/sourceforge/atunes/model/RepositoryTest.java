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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


public class RepositoryTest {

	private static final String REP_FOLDER_1 = "folder_1";
	private static final String REP_FOLDER_2 = "folder_2";
	
	private static Repository rep;
	
	@Test
	public void createRepository() {
		File f1 = new File(REP_FOLDER_1);
		File f2 = new File(REP_FOLDER_2);
		List<File> folders = new ArrayList<File>();
		folders.add(f1);
		folders.add(f2);
		rep = new Repository(folders);

		Assert.assertTrue(rep.getAudioFiles().isEmpty());
		Assert.assertTrue(rep.getAudioFilesList().isEmpty());
		Assert.assertTrue(rep.getArtistStructure().isEmpty());
		Assert.assertTrue(rep.getFolderStructure().isEmpty());
		Assert.assertTrue(rep.getGenreStructure().isEmpty());
		Assert.assertTrue(rep.getYearStructure().isEmpty());
		Assert.assertTrue(rep.getFolders().contains(f1));
		Assert.assertTrue(rep.getFolders().contains(f2));
		
		Assert.assertEquals(0, rep.getTotalDurationInSeconds());
		Assert.assertEquals(0, rep.getTotalSizeInBytes());
		Assert.assertEquals(0, rep.countFiles());
	}
}
