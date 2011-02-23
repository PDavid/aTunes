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

package net.sourceforge.atunes.kernel.modules.state;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.CryptoUtils;

public class PasswordPreference extends Preference {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6072746075893564924L;

	public PasswordPreference() {		
	}
	
	public PasswordPreference(String value) {
		super();
		setPassword(value);
	}
	
	@Override
	public void setValue(Object value) {
		throw new IllegalArgumentException("Use setPassword");
	}

	@Override
	public Object getValue() {
		throw new IllegalArgumentException("Use getPassword");
	}

	public void setPassword(String password) {
		try {
			byte[] encrypted = CryptoUtils.encrypt(password.getBytes());
			new Logger().debug(LogCategories.PREFERENCES, "Encrypted password: ", Arrays.toString(encrypted));
			super.setValue(encrypted);
		} catch (GeneralSecurityException e) {
			new Logger().error(LogCategories.PREFERENCES, e);
		} catch (IOException e) {
			new Logger().error(LogCategories.PREFERENCES, e);
		}
	}

	public String getPassword() {
		try {
			String decrypted = new String(CryptoUtils.decrypt((byte[])super.getValue()));
			new Logger().debug(LogCategories.PREFERENCES, "Decrypted password: ", decrypted);
			return decrypted;
		} catch (GeneralSecurityException e) {
			new Logger().error(LogCategories.PREFERENCES, e);
		} catch (IOException e) {
			new Logger().error(LogCategories.PREFERENCES, e);
		}
		return null;
	}
}
