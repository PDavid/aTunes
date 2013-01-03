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

import java.io.IOException;
import java.security.GeneralSecurityException;

import net.sourceforge.atunes.utils.CryptoUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * aTunes API key
 * 
 * @author alex
 * 
 */
public class LastFmAPIKey {

    /*
     * DO NOT USE THESE KEYS FOR OTHER APPLICATIONS THAN aTunes!
     */
    private static final byte[] API_KEY = { 78, 119, -39, -5, -89, -107, -38,
	    41, -87, -107, 122, 98, -33, 46, 32, -47, -44, 54, 97, 67, 105,
	    122, 11, -26, -81, 90, 94, 55, 121, 11, 14, -104, -70, 123, -88,
	    -70, -108, 75, -77, 98 };
    private static final byte[] API_SECRET = { 38, -8, 33, 63, 10, 86, 29, -2,
	    87, -63, 67, 111, -5, -101, -87, 38, 2, 35, 86, -86, 19, 110, -81,
	    -115, 102, 54, -24, 27, 40, -124, -57, -62, -70, 123, -88, -70,
	    -108, 75, -77, 98 };
    private static final String CLIENT_ID = "atu";

    /**
     * Get Client ID
     * 
     * @return
     */
    String getClientId() {
	return CLIENT_ID;
    }

    /**
     * Get API key
     * 
     * @return
     */
    String getApiKey() {
	try {
	    return new String(CryptoUtils.decrypt(API_KEY));
	} catch (GeneralSecurityException e) {
	    Logger.error(e);
	} catch (IOException e) {
	    Logger.error(e);
	}
	return "";
    }

    /**
     * Get API secret
     * 
     * @return
     */
    String getApiSecret() {
	try {
	    return new String(CryptoUtils.decrypt(API_SECRET));
	} catch (GeneralSecurityException e) {
	    Logger.error(e);
	} catch (IOException e) {
	    Logger.error(e);
	}
	return "";
    }
}
