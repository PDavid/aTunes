/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.updates;

import java.io.IOException;
import java.net.URLConnection;
import java.net.UnknownHostException;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.model.ApplicationVersion;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IUpdateHandler;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handler for updates
 */
public final class UpdateHandler extends AbstractHandler implements IUpdateHandler {

    /** 
     * Url to look for new version. 
     */
    private String updatesURL;

    public final void setUpdatesURL(String updatesURL) {
		this.updatesURL = updatesURL;
	}
    
    @Override
    public void allHandlersInitialized() {
        if (!Kernel.isNoUpdate()) {
            checkUpdates(false, false);
        }
    }

    /**
     * Used to check for new version.
     * @param alwaysInDialog
     * @param showNoNewVersion
     */
    @Override
    public void checkUpdates(boolean alwaysInDialog, boolean showNoNewVersion) {
        new CheckUpdatesSwingWorker(this, showNoNewVersion, alwaysInDialog, getState(), getFrame(), getBean(ILookAndFeelManager.class)).execute();
    }

    /**
     * Tries to find a new release by connecting to update URL.
     * 
     * @param p
     *            the p
     * 
     * @return the last version
     */
    @Override
    public ApplicationVersion getLastVersion() {
        ApplicationVersion result = null;
        try {
            ExtendedProxy proxy = ExtendedProxy.getProxy(getState().getProxy());
            URLConnection connection = NetworkUtils.getConnection(updatesURL, proxy);
            Document xml = XMLUtils.getXMLDocument(NetworkUtils.readURL(connection));

            // Check if xml is not null: if URL is not found response is a HTTP 200 code, as current hosting system
            // returns a valid html page reporting the real HTTP 404 code
            if (xml == null) {
                throw new IOException(StringUtils.getString("Could not connect to ", updatesURL));
            }

            // Parse xml document
            Element element = (Element) xml.getElementsByTagName("latest").item(0);
            String date = XMLUtils.getChildElementContent(element, "date");
            int major = Integer.parseInt(XMLUtils.getChildElementContent(element, "majorNumber"));
            int minor = Integer.parseInt(XMLUtils.getChildElementContent(element, "minorNumber"));
            int revision = Integer.parseInt(XMLUtils.getChildElementContent(element, "revisionNumber"));
            String url = element.getAttribute("url");

            result = new ApplicationVersion(date, major, minor, revision, VersionType.FINAL, "", url);

        } catch (UnknownHostException uhe) {
            Logger.error("Could not connect to www.atunes.org");
        } catch (IOException e) {
            Logger.error("Could not connect to www.atunes.org");
        } catch (Exception e) {
            Logger.error(e);
        }
        return result;
    }
}
