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

package net.sourceforge.atunes.kernel.modules.radio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.BackgroundWorkerWithIndeterminateProgress;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.XMLSerializerService;

/**
 * Retrieves radios to show in browser
 * 
 * @author alex
 * 
 */
public final class RetrieveRadioBrowserDataBackgroundWorker extends
		BackgroundWorkerWithIndeterminateProgress<List<IRadio>, Void> {

	private INetworkHandler networkHandler;

	private XMLSerializerService xmlSerializerService;

	private RadioBrowserDialogController controller;

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(final INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * @param xmlSerializerService
	 */
	public void setXmlSerializerService(
			final XMLSerializerService xmlSerializerService) {
		this.xmlSerializerService = xmlSerializerService;
	}

	/**
	 * @param controller
	 */
	void retrieve(final RadioBrowserDialogController controller) {
		this.controller = controller;
		execute();
	}

	@Override
	protected String getDialogTitle() {
		return I18nUtils.getString("PLEASE_WAIT");
	}

	@Override
	protected void whileWorking(final List<Void> chunks) {
	}

	@Override
	protected List<IRadio> doInBackground() {
		try {
			return retrieveRadiosForBrowser();
		} catch (IOException e) {
			Logger.error(e);
		}
		return new ArrayList<IRadio>();
	}

	@SuppressWarnings("unchecked")
	private List<IRadio> retrieveRadiosForBrowser() throws IOException {
		try {
			String xml = this.networkHandler
					.readURL(this.networkHandler
							.getConnection(Constants.RADIO_LIST_DOWNLOAD_COMMON_JUKEBOX));
			return (List<IRadio>) this.xmlSerializerService
					.readObjectFromString(xml);
		} catch (IOException e) {
			String xml = this.networkHandler.readURL(this.networkHandler
					.getConnection(Constants.RADIO_LIST_DOWNLOAD));
			return (List<IRadio>) this.xmlSerializerService
					.readObjectFromString(xml);
		}
	}

	@Override
	protected void doneAndDialogClosed(List<IRadio> result) {
		this.controller.show(result);
	}
}