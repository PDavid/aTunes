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

package net.sourceforge.atunes.kernel.modules.state;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import net.sourceforge.atunes.utils.CryptoUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * A preference storing a password
 * 
 * @author alex
 * 
 */
public class PasswordPreference extends Preference {

    /**
	 * 
	 */
    private static final long serialVersionUID = -6072746075893564924L;

    /**
     * Default constructor
     */
    public PasswordPreference() {
    }

    /**
     * @param value
     */
    public PasswordPreference(final String value) {
	super();
	setPassword(value);
    }

    @Override
    public void setValue(final Object value) {
	throw new IllegalArgumentException("Use setPassword");
    }

    @Override
    public Object getValue() {
	throw new IllegalArgumentException("Use getPassword");
    }

    /**
     * @param password
     */
    public final void setPassword(final String password) {
	try {
	    byte[] encrypted = CryptoUtils.encrypt(password.getBytes());
	    Logger.debug("Encrypted password: ", Arrays.toString(encrypted));
	    super.setValue(encrypted);
	} catch (GeneralSecurityException e) {
	    Logger.error(e);
	} catch (IOException e) {
	    Logger.error(e);
	}
    }

    /**
     * @return password
     */
    public String getPassword() {
	try {
	    return new String(CryptoUtils.decrypt((byte[]) super.getValue()));
	} catch (GeneralSecurityException e) {
	    Logger.error(e);
	} catch (IOException e) {
	    Logger.error(e);
	}
	return null;
    }
}
