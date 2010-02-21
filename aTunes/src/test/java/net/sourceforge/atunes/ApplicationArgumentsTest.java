package net.sourceforge.atunes;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ApplicationArgumentsTest {

	private List<String> args1;
	private List<String> args2;
	private List<String> args3;
	
	@Before
	public void init() {
		args1 = new ArrayList<String>();
		
		args2 = new ArrayList<String>();
		args2.add("uSe-CoNfIg-FoLdEr=");
		args2.add("UsE-rEpOsItOrY-cOnFiG-fOlDeR=");
		
		args3 = new ArrayList<String>();
		args3.add("debug");
		args3.add("uSe-CoNfIg-FoLdEr=/home/user/.aTunes");
		args3.add("UsE-rEpOsItOrY-cOnFiG-fOlDeR=/home/user/repository");
		args3.add("argument");
	}
	
	@Test
	public void testUserConfigFolder() {
		Assert.assertEquals(null, ApplicationArguments.getUserConfigFolder(null));
		Assert.assertEquals(null, ApplicationArguments.getUserConfigFolder(args1));
		Assert.assertEquals("", ApplicationArguments.getUserConfigFolder(args2));
		Assert.assertEquals("/home/user/.aTunes", ApplicationArguments.getUserConfigFolder(args3));
	}
	
	@Test
	public void testRepositoryConfigFolder() {
		Assert.assertEquals(null, ApplicationArguments.getRepositoryConfigFolder(null));
		Assert.assertEquals(null, ApplicationArguments.getRepositoryConfigFolder(args1));
		Assert.assertEquals("", ApplicationArguments.getRepositoryConfigFolder(args2));
		Assert.assertEquals("/home/user/repository", ApplicationArguments.getRepositoryConfigFolder(args3));		
	}
	
	@Test
	public void testSaveArguments() {
		ApplicationArguments.saveArguments(null);
		Assert.assertEquals("", ApplicationArguments.getSavedArguments());
		
		ApplicationArguments.saveArguments(args3);
		String savedArguments = ApplicationArguments.getSavedArguments();
		Assert.assertTrue(savedArguments.contains("debug"));
		Assert.assertTrue(savedArguments.contains("uSe-CoNfIg-FoLdEr=/home/user/.aTunes"));
		Assert.assertTrue(savedArguments.contains("UsE-rEpOsItOrY-cOnFiG-fOlDeR=/home/user/repository"));
		Assert.assertFalse(savedArguments.contains("argument"));
		
		
	}
}
