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
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.XMLSerializerService;

/**
 * The Class RadioHandler.
 * 
 * @author sylvain
 */
public final class RadioHandler extends AbstractHandler implements IRadioHandler {

    private List<IRadio> radios = new ArrayList<IRadio>();
    private List<IRadio> presetRadios = new ArrayList<IRadio>();
    private List<IRadio> retrievedPresetRadios = new ArrayList<IRadio>();
    private boolean noNewStations = true;

    /**
     * Flag indicating if radio information must be stored to disk
     */
    private boolean radioListDirty = false;
    
    private INetworkHandler networkHandler;
    
    private INavigationView radioNavigationView;
    
    private INavigationHandler navigationHandler;
    
    private XMLSerializerService xmlSerializerService;
    
    private IDialogFactory dialogFactory;
    
    /**
     * @param dialogFactory
     */
    public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
    
    /**
     * @param xmlSerializerService
     */
    public void setXmlSerializerService(XMLSerializerService xmlSerializerService) {
		this.xmlSerializerService = xmlSerializerService;
	}
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @return
     */
    protected List<IRadio> getRetrievedPresetRadios() {
		return retrievedPresetRadios;
	}
    
    /**
     * @param radioNavigationView
     */
    public void setRadioNavigationView(INavigationView radioNavigationView) {
		this.radioNavigationView = radioNavigationView;
	}
    
    /**
     * @param networkHandler
     */
    public void setNetworkHandler(INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

    @Override
    public IRadio createRadio(String name, String url, String label) {
    	return new Radio(name, url, label);
    }
    
    @Override
	public void addRadio() {
        IRadioDialog dialog = dialogFactory.newDialog(IRadioDialog.class);
        dialog.showDialog();
        IRadio radio = dialog.getRadio();
        if (radio != null) {
            addRadio(radio);
        }
    }

    @Override
	public void addRadio(IRadio radio) {
        Logger.info("Adding radio");
        if (radio != null && !getRadios().contains(radio)) {
            getRadios().add(radio);
            radioListDirty = true;
        }
        Collections.sort(getRadios(), new RadioComparator());
        getBean(INavigationHandler.class).refreshView(radioNavigationView);
    }

    /**
     * Write stations to xml files.
     */
    @Override
    public void applicationFinish() {
        if (radioListDirty) {
        	getBean(IStateHandler.class).persistRadioCache(getRadios());
            // Only write preset list if new stations were added
            if (!noNewStations) {
            	getBean(IStateHandler.class).persistPresetRadioCache(presetRadios);
            }
        } else {
            Logger.info("Radio list is clean");
        }
    }

    @Override
	public List<IRadio> getRadios() {
        return radios;
    }

    @Override
	public List<IRadio> getRadioPresets() {
        // Check if new stations were added and set false if yes
        if (noNewStations) {
            noNewStations = presetRadios.containsAll(retrievedPresetRadios);
            retrievedPresetRadios.removeAll(presetRadios);
        } else {
            // New stations were already found, so leave noNewStations as false
            retrievedPresetRadios.removeAll(presetRadios);
        }
        presetRadios.addAll(retrievedPresetRadios);
        presetRadios.removeAll(getRadios());
        return new ArrayList<IRadio>(presetRadios);
    }

    @Override
	public List<IRadio> getRadios(String label) {
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
        for (IRadio radio : presetRadios) {
            String label = radio.getLabel();
            if (!result.contains(label)) {
                result.add(label);
            }
        }
        
        Collections.sort(result);
        return result;
    }

    @Override
	public void removeRadios(List<IRadio> radios) {
        Logger.info("Removing radios");
        for (IRadio radio : radios) {
            if (!presetRadios.contains(radio)) {
                getRadios().remove(radio);
            }
            // Preset radio station, we can not delete from preset file directly but must mark it as removed.
            else {
                presetRadios.remove(radio);
                final IRadio newRadio = createRadio(radio.getName(), radio.getUrl(), radio.getLabel());
                newRadio.setRemoved(true);
                getRadios().add(newRadio);
            }
        }
        radioListDirty = true;
        getBean(INavigationHandler.class).refreshView(radioNavigationView);
    }

    @Override
	public void removeRadio(IRadio radio) {
        removeRadios(Collections.singletonList(radio));
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<IRadio> retrieveRadiosForBrowser() throws IOException {
        try {
            String xml = networkHandler.readURL(networkHandler.getConnection(Constants.RADIO_LIST_DOWNLOAD_COMMON_JUKEBOX));
            return (List<IRadio>) xmlSerializerService.readObjectFromString(xml);
        } catch (IOException e) {
            String xml = networkHandler.readURL(networkHandler.getConnection(Constants.RADIO_LIST_DOWNLOAD));
            return (List<IRadio>) xmlSerializerService.readObjectFromString(xml);
        }
    }

    /*
     * Get radios from the internet (update preset list)
     */
    @Override
	public void retrieveRadios() {
        new RetrieveRadiosSwingWorker(this, navigationHandler, networkHandler, radioNavigationView, xmlSerializerService).execute();
    }

    @Override
	public void setLabel(List<IRadio> radioList, String label) {
        for (IRadio r : radioList) {
            // Write preset stations to user list in order to modify label
            if (presetRadios.contains(r)) {
                addRadio(r);
            }
            r.setLabel(label);
        }
        radioListDirty = true;
    }

    @Override
	public void replace(IRadio radio, IRadio newRadio) {
        removeRadio(radio);
        addRadio(newRadio);
        radioListDirty = true;
    }

    @Override
	public IRadio getRadioIfLoaded(String url) {
        // Check in user radios
        for (IRadio radio : getRadios()) {
            if (radio.getUrl().equalsIgnoreCase(url)) {
                return radio;
            }
        }
        // Check in preset radios
        for (IRadio radio : presetRadios) {
            if (radio.getUrl().equalsIgnoreCase(url)) {
                return radio;
            }
        }
        return null;
    }
    
	@Override
	public void showRadioBrowser() {
		new RadioBrowserDialogController(dialogFactory.newDialog(RadioBrowserDialog.class), this).showRadioBrowser();
	}

	@Override
	public IRadio editRadio(IRadio radio) {
		IRadioDialog dialog = dialogFactory.newDialog(IRadioDialog.class);
		dialog.setRadio(radio);
		dialog.showDialog();
		return dialog.getRadio();
	}
	
	/**
	 * @param radios
	 */
	void setRadios(List<IRadio> radios) {
		this.radios = radios;
	}
	
	/**
	 * @param presetRadios
	 */
	void setPresetRadios(List<IRadio> presetRadios) {
		this.presetRadios = presetRadios;
	}
}
