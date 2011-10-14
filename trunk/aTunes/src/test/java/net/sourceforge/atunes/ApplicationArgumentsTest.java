/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import net.sourceforge.atunes.model.ICommandHandler;

import org.junit.Before;
import org.junit.Test;

public class ApplicationArgumentsTest {

	private ApplicationArguments sut;
	
    private List<String> args1;
    private List<String> args2;
    private List<String> args3;

    @Before
    public void init() {
    	sut = new ApplicationArguments();
    	
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
        Assert.assertEquals(null, sut.getUserConfigFolder(null));
        Assert.assertEquals(null, sut.getUserConfigFolder(args1));
        Assert.assertEquals("", sut.getUserConfigFolder(args2));
        Assert.assertEquals("/home/user/.aTunes", sut.getUserConfigFolder(args3));
    }

    @Test
    public void testRepositoryConfigFolder() {
        Assert.assertEquals(null, sut.getRepositoryConfigFolder(null));
        Assert.assertEquals(null, sut.getRepositoryConfigFolder(args1));
        Assert.assertEquals("", sut.getRepositoryConfigFolder(args2));
        Assert.assertEquals("/home/user/repository", sut.getRepositoryConfigFolder(args3));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSaveArgumentsError() {
    	sut.saveArguments(null);
    }

    @Test
    public void testSaveArguments() {
    	sut.saveArguments(new ArrayList<String>());
        ICommandHandler commandHandler = mock(ICommandHandler.class);
        when(commandHandler.isValidCommand(anyString())).thenReturn(false);
        Assert.assertEquals("", sut.getSavedArguments(commandHandler));

        sut.saveArguments(args3);
        String savedArguments = sut.getSavedArguments(commandHandler);
        Assert.assertTrue(savedArguments.contains("debug"));
        Assert.assertTrue(savedArguments.contains("uSe-CoNfIg-FoLdEr=/home/user/.aTunes"));
        Assert.assertTrue(savedArguments.contains("UsE-rEpOsItOrY-cOnFiG-fOlDeR=/home/user/repository"));
        Assert.assertFalse(savedArguments.contains("argument"));
    }
}
