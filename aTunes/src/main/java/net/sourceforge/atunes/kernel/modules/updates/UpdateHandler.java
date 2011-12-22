/*
 * aTunes 2.2.0-SNAPSHOT
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
import java.net.UnknownHostException;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.ApplicationVersion;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IUpdateHandler;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;

/**
 * Handler for updates
 */
public final class UpdateHandler extends AbstractHandler implements IUpdateHandler {

    /** 
     * Url to look for new version. 
     */
    private String updatesURL;
    
    private IApplicationArguments applicationArguments;
    
    private ITaskService taskService;
    
    private INetworkHandler networkHandler;
    
    /**
     * @param networkHandler
     */
    public void setNetworkHandler(INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}
    
    /**
     * @param taskService
     */
    public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}
    
    /**
     * @param applicationArguments
     */
    public void setApplicationArguments(IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

    /**
     * @param updatesURL
     */
    public final void setUpdatesURL(String updatesURL) {
		this.updatesURL = updatesURL;
	}
    
    @Override
    public void allHandlersInitialized() {
        if (!applicationArguments.isNoUpdate()) {
        	taskService.submitOnce("Check updates", 5, new Runnable() {
        		@Override
        		public void run() {
        			checkUpdates(false, false);
        		}
        	});
        }
    }

    /**
     * Used to check for new version.
     * @param alwaysInDialog
     * @param showNoNewVersion
     */
    @Override
    public void checkUpdates(boolean alwaysInDialog, boolean showNoNewVersion) {
        new CheckUpdatesSwingWorker(this, showNoNewVersion, alwaysInDialog, getState(), getFrame()).execute();
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
            Document xml = getVersionXml();

            // Check if xml is not null: if URL is not found response is a HTTP 200 code, as current hosting system
            // returns a valid html page reporting the real HTTP 404 code
            if (xml == null) {
                throw new IOException(StringUtils.getString("Failled to retrieve xml from ", updatesURL));
            }

            // Parse xml document
            result = new VersionXmlParser().getApplicationVersionFromXml(xml);

        } catch (UnknownHostException uhe) {
            Logger.error("Could not connect to www.atunes.org");
        } catch (IOException e) {
            Logger.error("Could not connect to www.atunes.org");
        }
        return result;
    }

	/**
	 * @return xml document retrieved from update url
	 * @throws IOException
	 */
	private Document getVersionXml() throws IOException {
		return  XMLUtils.getXMLDocument(networkHandler.readURL(networkHandler.getConnection(updatesURL)));
	}
}
