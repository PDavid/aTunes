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
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handler for updates
 */
public final class UpdateHandler extends AbstractHandler {

    private final class CheckUpdatesSwingWorker extends
			SwingWorker<ApplicationVersion, Void> {
		private final boolean showNoNewVersion;
		private final ProxyBean p;
		private final boolean alwaysInDialog;

		private CheckUpdatesSwingWorker(boolean showNoNewVersion, ProxyBean p,
				boolean alwaysInDialog) {
			this.showNoNewVersion = showNoNewVersion;
			this.p = p;
			this.alwaysInDialog = alwaysInDialog;
		}

		@Override
		protected ApplicationVersion doInBackground() throws Exception {
		    return getLastVersion(p);
		}

		@Override
		protected void done() {
		    try {
		        ApplicationVersion version = get();
		        if (version != null && version.compareTo(Constants.VERSION) == 1) {
		            GuiHandler.getInstance().showNewVersionInfo(version, alwaysInDialog);
		        } else if (showNoNewVersion) {
		            GuiHandler.getInstance().showMessage(I18nUtils.getString("NOT_NEW_VERSION"));
		        }
		    } catch (InterruptedException e) {
		        Logger.error(e);
		    } catch (ExecutionException e) {
		        Logger.error(e);
		    }
		}
	}

	/** Url to look for new version. */
    private static final String updatesURL = "http://www.atunes.org/latest.xml";

    /** Instance */
    private static UpdateHandler instance = new UpdateHandler();

    private UpdateHandler() {
    }

    @Override
    public void applicationFinish() {
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
    }

    @Override
    protected void initHandler() {
    }

    public static UpdateHandler getInstance() {
        return instance;
    }

    @Override
    public void applicationStarted(List<AudioObject> playList) {
        if (!Kernel.isNoUpdate()) {
            checkUpdates(ApplicationState.getInstance().getProxy(), false, false);
        }
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
        new CheckUpdatesSwingWorker(showNoNewVersion, p, alwaysInDialog).execute();
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
                proxy = Proxy.getProxy(p);
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
            Logger.error("Could not connect to www.atunes.org");
        } catch (IOException e) {
            Logger.error("Could not connect to www.atunes.org");
        } catch (Exception e) {
            Logger.error(e);
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

	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(AudioObject audioObject) {}

}
