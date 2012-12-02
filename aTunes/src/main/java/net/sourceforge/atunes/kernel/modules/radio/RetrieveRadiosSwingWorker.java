/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.radio;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.XMLSerializerService;

final class RetrieveRadiosSwingWorker extends SwingWorker<List<IRadio>, Void> {

	private RadioHandler radioHandler;
	
	private INavigationHandler navigationHandler;
	
	private INetworkHandler networkHandler;
	
	private INavigationView radioNavigationView;
	
	private XMLSerializerService xmlSerializerService;
	
	/**
	 * @param radioHandler
	 * @param navigationHandler
	 * @param networkHandler
	 * @param radioNavigationView
	 * @param xmlSerializerService
	 */
	public RetrieveRadiosSwingWorker(RadioHandler radioHandler, INavigationHandler navigationHandler, INetworkHandler networkHandler, INavigationView radioNavigationView, XMLSerializerService xmlSerializerService) {
		this.radioHandler = radioHandler;
		this.navigationHandler = navigationHandler;
		this.networkHandler = networkHandler;
		this.radioNavigationView = radioNavigationView;
		this.xmlSerializerService = xmlSerializerService;
	}
	
    @SuppressWarnings("unchecked")
    @Override
    protected List<IRadio> doInBackground() throws IOException {
        String xml = networkHandler.readURL(networkHandler.getConnection(Constants.RADIO_LIST_DOWNLOAD));
        return (List<IRadio>) xmlSerializerService.readObjectFromString(xml);
    }

    @Override
    protected void done() {
        try {
            radioHandler.getRetrievedPresetRadios().clear();
            radioHandler.getRetrievedPresetRadios().addAll(get());
            radioHandler.getRadioPresets();
            navigationHandler.refreshView(radioNavigationView);
        } catch (InterruptedException e) {
            Logger.error(e);
        } catch (ExecutionException e) {
            Logger.error(e);
        }

    }
}