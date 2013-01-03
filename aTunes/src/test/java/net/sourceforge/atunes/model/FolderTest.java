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

import net.sourceforge.atunes.kernel.modules.repository.Folder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class FolderTest {

	private static final String FOLDER_1_NAME = "asdf";
	private static final String FOLDER_2_NAME = "asdfghjk";
	
	private Folder f1;
	private Folder f2;
	
	private ILocalAudioObject af1 = Mockito.mock(ILocalAudioObject.class);
	private ILocalAudioObject af2 = Mockito.mock(ILocalAudioObject.class);
	
	@Before
	public void init() {
		f1 = new Folder(FOLDER_1_NAME);
		f2 = new Folder(FOLDER_2_NAME);
	}
	
	@Test
	public void createFolder() {
		Assert.assertTrue(f1.getName().equals(FOLDER_1_NAME));
		Assert.assertTrue(f1.getAudioObjects().isEmpty());
		Assert.assertNull(f1.getFolder(FOLDER_2_NAME));
		Assert.assertTrue(f1.getFolderPath(Mockito.mock(IOSManager.class)).equals(new File(FOLDER_1_NAME)));
		Assert.assertTrue(f1.getFolders().isEmpty());
		Assert.assertNull(f1.getParentFolder());
	}
	
	@Test
	public void folderWithFiles() {
		f1.addAudioFile(af1);
		f1.addAudioFile(af2);
		Assert.assertEquals(2, f1.getAudioObjects().size());
		
		f1.removeAudioFile(af1);
		Assert.assertEquals(1, f1.getAudioObjects().size());
	}
	
	@Test
	public void folderWithFolders() {
		IOSManager osManager = Mockito.mock(IOSManager.class);
		Mockito.when(osManager.getFileSeparator()).thenReturn(System.getProperty("file.separator"));
		
		f1.addAudioFile(af1);
		f2.addAudioFile(af2);
		f1.addFolder(f2);
		
		Assert.assertEquals(f2, f1.getFolder(FOLDER_2_NAME));
		Assert.assertEquals(2, f1.getAudioObjects().size());
		Assert.assertTrue(f1.getFolders().containsValue(f2));
		Assert.assertEquals(f1, f2.getParentFolder());
		Assert.assertEquals(new File(FOLDER_1_NAME + System.getProperty("file.separator") + FOLDER_2_NAME), f2.getFolderPath(osManager));
	}	
}
