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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Session;

/**
 * Login into last.fm
 * 
 * @author alex
 * 
 */
public class LastFmLogin {

    private LastFmAPIKey lastFmAPIKey;

    private IStateContext stateContext;

    /**
     * @param stateContext
     */
    public void setStateContext(final IStateContext stateContext) {
	this.stateContext = stateContext;
    }

    /**
     * @param lastFmAPIKey
     */
    public void setLastFmAPIKey(final LastFmAPIKey lastFmAPIKey) {
	this.lastFmAPIKey = lastFmAPIKey;
    }

    /**
     * Test if given user and password are correct to login at last.fm
     * 
     * @param user
     * @param password
     */
    boolean testLogin(final String user, final String password) {
	return Authenticator.getMobileSession(user, password,
		lastFmAPIKey.getApiKey(), lastFmAPIKey.getApiSecret()) != null;
    }

    /**
     * Returns session
     * 
     * @return
     */
    Session getSession() {
	return Authenticator.getMobileSession(stateContext.getLastFmUser(),
		stateContext.getLastFmPassword(), lastFmAPIKey.getApiKey(),
		lastFmAPIKey.getApiSecret());
    }

    /**
     * Checks user
     * 
     * @return
     */
    boolean checkUser() {
	if (StringUtils.isEmpty(stateContext.getLastFmUser())) {
	    Logger.debug("Don't submit to Last.fm: Empty user");
	    return false;
	}
	return true;
    }

    /**
     * Check password
     * 
     * @return
     */
    boolean checkPassword() {
	if (StringUtils.isEmpty(stateContext.getLastFmPassword())) {
	    Logger.debug("Don't submit to Last.fm: Empty password");
	    return false;
	}
	return true;
    }

    /**
     * Check last.fm user and password
     * 
     * @return
     */
    boolean checkCredentials() {
	return checkUser() && checkPassword();
    }

}
