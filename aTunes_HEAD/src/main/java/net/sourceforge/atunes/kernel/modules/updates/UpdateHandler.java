/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.net.Proxy.Type;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handler for updates
 */
public final class UpdateHandler {

    /** Url to look for new version. */
    private static final String updatesURL = "http://www.atunes.org/latest.xml";

    /** Instance */
    private static UpdateHandler instance = new UpdateHandler();

    /** Logger. */
    Logger logger = new Logger();

    private UpdateHandler() {
    }

    public static UpdateHandler getInstance() {
        return instance;
    }

    /**
     * Used to check for new version.
     * 
     * @param p
     *            the p
     * @param alwaysInDialog
     *            if the new version info shouls always be shown in a dialog
     * @param showNoNewVersion
     *            the show no new version
     */
    public void checkUpdates(final ProxyBean p, final boolean alwaysInDialog, final boolean showNoNewVersion) {
        new SwingWorker<ApplicationVersion, Void>() {
            @Override
            protected ApplicationVersion doInBackground() throws Exception {
                return getLastVersion(p);
            }

            @Override
            protected void done() {
                try {
                    ApplicationVersion version = get();
                    if (version != null && version.compareTo(Constants.VERSION) == 1) {
                        VisualHandler.getInstance().showNewVersionInfo(version, alwaysInDialog);
                    } else if (showNoNewVersion) {
                        VisualHandler.getInstance().showMessage(LanguageTool.getString("NOT_NEW_VERSION"));
                    }
                } catch (InterruptedException e) {
                    logger.internalError(e);
                } catch (ExecutionException e) {
                    logger.internalError(e);
                }
            }
        }.execute();
    }

    /**
     * Tries to find a new release by connecting to update URL.
     * 
     * @param p
     *            the p
     * 
     * @return the last version
     */
    ApplicationVersion getLastVersion(ProxyBean p) {
        ApplicationVersion result = null;
        try {
            Proxy proxy;
            if (p != null) {
                proxy = new Proxy(p.getType().equals(ProxyBean.HTTP_PROXY) ? Type.HTTP : Type.SOCKS, p.getUrl(), p.getPort(), p.getUser(), p.getPassword());
            } else {
                proxy = null;
            }
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
            logger.error(LogCategories.NETWORK, "Could not connect to www.atunes.org");
        } catch (IOException e) {
            logger.error(LogCategories.NETWORK, "Could not connect to www.atunes.org");
        } catch (Exception e) {
            logger.internalError(e);
        }
        return result;
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        instance.checkUpdates(null, true, true);
    }

}
