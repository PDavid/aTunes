package net.sourceforge.atunes.kernel.modules.state;

import java.io.IOException;
import java.security.GeneralSecurityException;

import junit.framework.Assert;

import org.junit.Test;

public class PasswordPreferenceTest {

	private static final String PASSWORD = "jgJKGjh.jkgguu-o";
	
	@Test
	public void test() throws GeneralSecurityException, IOException {
		PasswordPreference p = new PasswordPreference();
		p.setPassword(PASSWORD);
		Assert.assertEquals(PASSWORD, p.getPassword());

		PasswordPreference p2 = new PasswordPreference(PASSWORD);
		Assert.assertEquals(PASSWORD, p2.getPassword());
	}
}
