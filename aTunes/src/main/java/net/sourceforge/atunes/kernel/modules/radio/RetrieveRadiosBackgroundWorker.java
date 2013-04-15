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
import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.XMLSerializerService;

/**
 * Updates radios
 * 
 * @author alex
 * 
 */
public final class RetrieveRadiosBackgroundWorker extends
		BackgroundWorker<List<IRadio>> {

	private RadioHandler radioHandler;

	private INavigationHandler navigationHandler;

	private INetworkHandler networkHandler;

	private INavigationView radioNavigationView;

	private XMLSerializerService xmlSerializerService;

	/**
	 * @param radioHandler
	 */
	public void setRadioHandler(final RadioHandler radioHandler) {
		this.radioHandler = radioHandler;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(final INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * @param radioNavigationView
	 */
	public void setRadioNavigationView(final INavigationView radioNavigationView) {
		this.radioNavigationView = radioNavigationView;
	}

	/**
	 * @param xmlSerializerService
	 */
	public void setXmlSerializerService(
			final XMLSerializerService xmlSerializerService) {
		this.xmlSerializerService = xmlSerializerService;
	}

	@Override
	protected void before() {
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<IRadio> doInBackground() {
		String xml;
		try {
			xml = this.networkHandler.readURL(this.networkHandler
					.getConnection(Constants.RADIO_LIST_DOWNLOAD));
			return (List<IRadio>) this.xmlSerializerService
					.readObjectFromString(xml);
		} catch (IOException e) {
			Logger.error(e);
		}
		return new ArrayList<IRadio>();
	}

	@Override
	protected void done(final List<IRadio> result) {
		this.radioHandler.getRetrievedPresetRadios().clear();
		this.radioHandler.getRetrievedPresetRadios().addAll(result);
		this.radioHandler.getRadioPresets();
		this.navigationHandler.refreshView(this.radioNavigationView);
	}
}