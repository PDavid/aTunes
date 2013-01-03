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

package net.sourceforge.atunes.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;

import junit.framework.Assert;

import org.junit.Test;

public class CryptoUtilsTest {

    private static final String TEST_STRING_1 = "aTunes";
    private static final String EMPTY_TEST_STRING = "";
    private static final String TEST_STRING_2 = "*1bkk49fkjkpp)U=(Zhfh98";

    @Test
    public void encryptAndDecryptTest() throws IOException, GeneralSecurityException {
        Assert.assertEquals(TEST_STRING_1, new String(CryptoUtils.decrypt(CryptoUtils.encrypt(TEST_STRING_1.getBytes()))));
        Assert.assertEquals(EMPTY_TEST_STRING, new String(CryptoUtils.decrypt(CryptoUtils.encrypt(EMPTY_TEST_STRING.getBytes()))));
        Assert.assertEquals(EMPTY_TEST_STRING, new String(CryptoUtils.decrypt(CryptoUtils.encrypt((byte[]) null))));
        Assert.assertEquals(TEST_STRING_2, new String(CryptoUtils.decrypt(CryptoUtils.encrypt(TEST_STRING_2.getBytes()))));
    }

    @Test
    public void encryptTest() throws IOException, GeneralSecurityException {
        Assert.assertFalse(TEST_STRING_1.equalsIgnoreCase(new String(CryptoUtils.encrypt(TEST_STRING_1.getBytes()))));
        Assert.assertFalse(TEST_STRING_2.equalsIgnoreCase(new String(CryptoUtils.encrypt(TEST_STRING_2.getBytes()))));
    }

}
