package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.util.Collections;

import net.sourceforge.atunes.model.Repository;

import org.junit.Before;
import org.junit.Test;


public class RepositoryFillerTest {

	private static RepositoryFiller filler;
	
	@Before
	public void init() {
		filler = new RepositoryFiller(new Repository(Collections.singletonList(new File(""))));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createRepositoryFillerNullRepository() {
		RepositoryFiller f = new RepositoryFiller(null);		
	}
	
	@Test
	public void addAudioFile() {
//		filler.addAudioFile(audioFile, repositoryFolderRoot, relativePathToRepositoryFolderRoot)
		
	}
}
