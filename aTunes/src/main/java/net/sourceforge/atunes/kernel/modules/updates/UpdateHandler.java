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

package net.sourceforge.atunes.kernel.modules.updates;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.ApplicationVersion;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IApplicationUpdatedListener;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IUpdateHandler;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.PropertiesUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;

/**
 * Handler for updates
 */
public final class UpdateHandler extends AbstractHandler implements
		IUpdateHandler {

	/**
	 * Url to look for new version.
	 */
	private String updatesURL;

	private IApplicationArguments applicationArguments;

	private ITaskService taskService;

	private INetworkHandler networkHandler;

	private String versionFile;

	private String versionProperty;

	/**
	 * @param versionProperty
	 */
	public void setVersionProperty(final String versionProperty) {
		this.versionProperty = versionProperty;
	}

	/**
	 * @param versionFile
	 */
	public void setVersionFile(final String versionFile) {
		this.versionFile = versionFile;
	}

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(final INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(
			final IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	/**
	 * @param updatesURL
	 */
	public final void setUpdatesURL(final String updatesURL) {
		this.updatesURL = updatesURL;
	}

	@Override
	public void deferredInitialization() {
		if (!this.applicationArguments.isNoUpdate()) {
			this.taskService.submitNow("Check updates", new Runnable() {
				@Override
				public void run() {
					checkUpdates(true, false);
				}
			});
		}
		checkIfVersionChanged();
	}

	private void checkIfVersionChanged() {
		Properties versionProperties = PropertiesUtils
				.readProperties(getVersionFilePath());
		String oldVersion = versionProperties.getProperty(this.versionProperty);
		oldVersion = oldVersion != null ? oldVersion : "";
		String newVersion = Constants.VERSION.toString();
		if (!newVersion.equals(oldVersion)) {
			for (IApplicationUpdatedListener listener : getBeanFactory()
					.getBeans(IApplicationUpdatedListener.class)) {
				listener.versionChanged(oldVersion, newVersion);
			}
			versionProperties.put(this.versionProperty, newVersion);
			try {
				PropertiesUtils.writeProperties(getVersionFilePath(),
						versionProperties);
			} catch (IOException e) {
				Logger.error(e);
			}
		}
	}

	private String getVersionFilePath() {
		return StringUtils.getString(getOsManager().getUserConfigFolder(),
				getOsManager().getFileSeparator(), this.versionFile);
	}

	/**
	 * Used to check for new version.
	 * 
	 * @param alwaysInDialog
	 * @param showNoNewVersion
	 */
	@Override
	public void checkUpdates(final boolean alwaysInDialog,
			final boolean showNoNewVersion) {
		CheckUpdatesBackgroundWorker worker = getBean(CheckUpdatesBackgroundWorker.class);
		worker.setShowNoNewVersion(showNoNewVersion);
		worker.setAlwaysInDialog(alwaysInDialog);
		worker.execute();
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

			// Check if xml is not null: if URL is not found response is a HTTP
			// 200 code, as current hosting system
			// returns a valid html page reporting the real HTTP 404 code
			if (xml == null) {
				throw new IOException(StringUtils.getString(
						"Failled to retrieve xml from ", this.updatesURL));
			}

			// Parse xml document
			result = getBean(VersionXmlParser.class)
					.getApplicationVersionFromXml(xml);

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
		return XMLUtils.getXMLDocument(this.networkHandler
				.readURL(this.networkHandler.getConnection(this.updatesURL)));
	}
}
