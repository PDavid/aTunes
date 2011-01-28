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
