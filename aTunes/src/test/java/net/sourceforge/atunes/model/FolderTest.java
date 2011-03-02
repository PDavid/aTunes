package net.sourceforge.atunes.model;

import java.io.File;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class FolderTest {

	private static final String FOLDER_1_NAME = "asdf";
	private static final String FOLDER_2_NAME = "asdfghjk";
	
	private static final String FILE_1_NAME = "cqwercqwc";
	private static final String FILE_2_NAME = "jglgffydff";
	
	private static Folder f1;
	private static Folder f2;
	
	private AudioFile af1;
	private AudioFile af2;
	
	@Before
	public void init() {
		f1 = new Folder(FOLDER_1_NAME);
		f2 = new Folder(FOLDER_2_NAME);
		af1 = new AudioFile(FILE_1_NAME);
		af2 = new AudioFile(FILE_2_NAME);
	}
	
	@Test
	public void createFolder() {
		Assert.assertTrue(f1.getName().equals(FOLDER_1_NAME));
		Assert.assertTrue(f1.getAudioObjects().isEmpty());
		Assert.assertNull(f1.getFolder(FOLDER_2_NAME));
		Assert.assertTrue(f1.getFolderPath().equals(new File(FOLDER_1_NAME)));
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
		f1.addAudioFile(af1);
		f2.addAudioFile(af2);
		f1.addFolder(f2);
		
		Assert.assertEquals(f2, f1.getFolder(FOLDER_2_NAME));
		Assert.assertEquals(2, f1.getAudioObjects().size());
		Assert.assertTrue(f1.getFolders().containsValue(f2));
		Assert.assertEquals(f1, f2.getParentFolder());
		Assert.assertEquals(new File(FOLDER_1_NAME + System.getProperty("file.separator") + FOLDER_2_NAME), f2.getFolderPath());
	}
	
}
