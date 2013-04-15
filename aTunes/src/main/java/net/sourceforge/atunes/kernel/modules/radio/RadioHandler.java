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
	private List<IRadio> presetRadios = new ArrayList<IRadio>();
	private final List<IRadio> retrievedPresetRadios = new ArrayList<IRadio>();
	private boolean noNewStations = true;

	/**
	 * Flag indicating if radio information must be stored to disk
	 */
	private boolean radioListDirty = false;

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
	 * @return
	 */
	protected List<IRadio> getRetrievedPresetRadios() {
		return this.retrievedPresetRadios;
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
			this.radioListDirty = true;
		}
		Collections.sort(getRadios(), new RadioComparator());
		getBean(INavigationHandler.class).refreshView(this.radioNavigationView);
	}

	/**
	 * Write stations to xml files.
	 */
	@Override
	public void applicationFinish() {
		if (this.radioListDirty) {
			getBean(IStateService.class).persistRadioCache(getRadios());
			// Only write preset list if new stations were added
			if (!this.noNewStations) {
				getBean(IStateService.class).persistPresetRadioCache(
						this.presetRadios);
			}
		} else {
			Logger.info("Radio list is clean");
		}
	}

	@Override
	public List<IRadio> getRadios() {
		return this.radios;
	}

	@Override
	public List<IRadio> getRadioPresets() {
		// Check if new stations were added and set false if yes
		if (this.noNewStations) {
			this.noNewStations = this.presetRadios
					.containsAll(this.retrievedPresetRadios);
			this.retrievedPresetRadios.removeAll(this.presetRadios);
		} else {
			// New stations were already found, so leave noNewStations as false
			this.retrievedPresetRadios.removeAll(this.presetRadios);
		}
		this.presetRadios.addAll(this.retrievedPresetRadios);
		this.presetRadios.removeAll(getRadios());
		return new ArrayList<IRadio>(this.presetRadios);
	}

	@Override
	public List<IRadio> getRadios(final String label) {
		return new ArrayList<IRadio>(getRadios());
	}

	@Override
	public List<String> sortRadioLabels() {
		List<String> result = new ArrayList<String>();
		// Read labels from user radios
		for (IRadio radio : getRadios()) {
			String label = radio.getLabel();
			if (!result.contains(label)) {
				result.add(label);
			}
		}
		// Read labels from preset radios
		for (IRadio radio : this.presetRadios) {
			String label = radio.getLabel();
			if (!result.contains(label)) {
				result.add(label);
			}
		}

		Collections.sort(result);
		return result;
	}

	@Override
	public void removeRadios(final List<IRadio> radios) {
		Logger.info("Removing radios");
		for (IRadio radio : radios) {
			if (!this.presetRadios.contains(radio)) {
				getRadios().remove(radio);
			}
			// Preset radio station, we can not delete from preset file directly
			// but must mark it as removed.
			else {
				this.presetRadios.remove(radio);
				final IRadio newRadio = createRadio(radio.getName(),
						radio.getUrl(), radio.getLabel());
				newRadio.setRemoved(true);
				getRadios().add(newRadio);
			}
		}
		this.radioListDirty = true;
		getBean(INavigationHandler.class).refreshView(this.radioNavigationView);
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

	/*
	 * Get radios from the internet (update preset list)
	 */
	@Override
	public void retrieveRadios() {
		getBean(RetrieveRadiosBackgroundWorker.class).execute();
	}

	@Override
	public void setLabel(final List<IRadio> radioList, final String label) {
		for (IRadio r : radioList) {
			// Write preset stations to user list in order to modify label
			if (this.presetRadios.contains(r)) {
				addRadio(r);
			}
			r.setLabel(label);
		}
		this.radioListDirty = true;
	}

	@Override
	public void replace(final IRadio radio, final IRadio newRadio) {
		removeRadio(radio);
		addRadio(newRadio);
		this.radioListDirty = true;
	}

	@Override
	public IRadio getRadioIfLoaded(final String url) {
		// Check in user radios
		for (IRadio radio : getRadios()) {
			if (radio.getUrl().equalsIgnoreCase(url)) {
				return radio;
			}
		}
		// Check in preset radios
		for (IRadio radio : this.presetRadios) {
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

	/**
	 * @param presetRadios
	 */
	void setPresetRadios(final List<IRadio> presetRadios) {
		this.presetRadios = presetRadios;
	}
}
