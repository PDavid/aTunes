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
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioDialog;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.XMLSerializerService;

/**
 * The Class RadioHandler.
 * 
 * @author sylvain
 */
public final class RadioHandler extends AbstractHandler implements
		IRadioHandler {

	private List<IRadio> radios = new ArrayList<IRadio>();

	private INetworkHandler networkHandler;

	private INavigationView radioNavigationView;

	private XMLSerializerService xmlSerializerService;

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param xmlSerializerService
	 */
	public void setXmlSerializerService(
			final XMLSerializerService xmlSerializerService) {
		this.xmlSerializerService = xmlSerializerService;
	}

	/**
	 * @param radioNavigationView
	 */
	public void setRadioNavigationView(final INavigationView radioNavigationView) {
		this.radioNavigationView = radioNavigationView;
	}

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(final INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	@Override
	public IRadio createRadio(final String name, final String url,
			final String label) {
		return new Radio(name, url, label);
	}

	@Override
	public void addRadio() {
		IRadioDialog dialog = this.dialogFactory.newDialog(IRadioDialog.class);
		dialog.showDialog();
		IRadio radio = dialog.getRadio();
		if (radio != null) {
			addRadio(radio);
		}
	}

	@Override
	public void addRadio(final IRadio radio) {
		Logger.info("Adding radio");
		if (radio != null && !getRadios().contains(radio)) {
			getRadios().add(radio);
		}
		Collections.sort(getRadios(), new RadioComparator());
		getBean(INavigationHandler.class).refreshView(this.radioNavigationView);
		persistRadios();
	}

	private void persistRadios() {
		getBean(IStateService.class).persistRadioCache(getRadios());
	}

	@Override
	public List<IRadio> getRadios() {
		return this.radios;
	}

	@Override
	public List<IRadio> getRadios(final String label) {
		return new ArrayList<IRadio>(getRadios());
	}

	@Override
	public List<String> getRadioLabels() {
		List<String> result = new ArrayList<String>();
		// Read labels from user radios
		for (IRadio radio : getRadios()) {
			String label = radio.getLabel();
			if (!result.contains(label)) {
				result.add(label);
			}
		}
		return result;
	}

	@Override
	public void removeRadios(final List<IRadio> radios) {
		Logger.info("Removing radios");
		for (IRadio radio : radios) {
			getRadios().remove(radio);
		}
		getBean(INavigationHandler.class).refreshView(this.radioNavigationView);
		persistRadios();
	}

	@Override
	public void removeRadio(final IRadio radio) {
		removeRadios(Collections.singletonList(radio));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IRadio> retrieveRadiosForBrowser() throws IOException {
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
	public void setLabel(final List<IRadio> radioList, final String label) {
		for (IRadio r : radioList) {
			r.setLabel(label);
		}
		persistRadios();
	}

	@Override
	public void replace(final IRadio radio, final IRadio newRadio) {
		getRadios().remove(radio);
		addRadio(newRadio);
	}

	@Override
	public IRadio getRadioIfLoaded(final String url) {
		// Check in user radios
		for (IRadio radio : getRadios()) {
			if (radio.getUrl().equalsIgnoreCase(url)) {
				return radio;
			}
		}
		return null;
	}

	@Override
	public void showRadioBrowser() {
		new RadioBrowserDialogController(
				this.dialogFactory.newDialog(RadioBrowserDialog.class), this,
				getBeanFactory()).showRadioBrowser();
	}

	@Override
	public IRadio editRadio(final IRadio radio) {
		IRadioDialog dialog = this.dialogFactory.newDialog(IRadioDialog.class);
		dialog.setRadio(radio);
		dialog.showDialog();
		return dialog.getRadio();
	}

	/**
	 * @param radios
	 */
	void setRadios(final List<IRadio> radios) {
		this.radios = radios;
	}
}
