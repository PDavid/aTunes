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
