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

package net.sourceforge.atunes.kernel.modules.radio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.dialogs.RadioBrowserDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.RadioNavigationView;
import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.XMLUtils;

/**
 * The Class RadioHandler.
 * 
 * @author sylvain
 */
public final class RadioHandler extends AbstractHandler {

    private final class RetrieveRadiosSwingWorker extends SwingWorker<List<Radio>, Void> {
        private final ProxyBean proxy = RadioHandler.this.getState().getProxy();

        @SuppressWarnings("unchecked")
        @Override
        protected List<Radio> doInBackground() throws Exception {
            String xml = NetworkUtils.readURL(NetworkUtils.getConnection(Constants.RADIO_LIST_DOWNLOAD, ExtendedProxy.getProxy(proxy)));
            return (List<Radio>) XMLUtils.readObjectFromString(xml);
        }

        @Override
        protected void done() {
            try {
                retrievedPresetRadios.clear();
                retrievedPresetRadios.addAll(get());
                getRadioPresets();
                getBean(INavigationHandler.class).refreshView(RadioNavigationView.class);
            } catch (InterruptedException e) {
                Logger.error(e);
            } catch (ExecutionException e) {
                Logger.error(e);
            }

        }
    }
    
    private static final Comparator<IRadio> COMPARATOR = new Comparator<IRadio>() {
        @Override
        public int compare(IRadio o1, IRadio o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };


    private static RadioHandler instance = new RadioHandler();

    private List<IRadio> radios;
    private List<IRadio> presetRadios;
    private List<IRadio> retrievedPresetRadios = new ArrayList<IRadio>();
    private boolean noNewStations = true;

    /**
     * Flag indicating if radio information must be stored to disk
     */
    private boolean radioListDirty = false;

    /**
     * Instantiates a new radio handler.
     */
    private RadioHandler() {
    }

    @Override
    public void applicationStateChanged(IState newState) {
    }

    @Override
    protected void initHandler() {
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
    }

    /**
     * Gets the single instance of RadioHandler.
     * 
     * @return single instance of RadioHandler
     */
    public static RadioHandler getInstance() {
        return instance;
    }

    /**
     * Add the radio station from the add radio dialog.
     */
    public void addRadio() {
        IRadioDialog dialog = getBean(IRadioDialog.class);
        dialog.showDialog();
        IRadio radio = dialog.getRadio();
        if (radio != null) {
            addRadio(radio);
        }
    }

    /**
     * Add a radio station to the list.
     * 
     * @param radio
     *            Station
     */
    public void addRadio(IRadio radio) {
        Logger.info("Adding radio");
        if (radio != null && !getRadios().contains(radio)) {
            getRadios().add(radio);
            radioListDirty = true;
        }
        Collections.sort(getRadios(), COMPARATOR);
        getBean(INavigationHandler.class).refreshView(RadioNavigationView.class);
    }

    /**
     * Write stations to xml files.
     */
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
    protected Runnable getPreviousInitializationTask() {
        return new Runnable() {
            /**
             * Read radio stations lists. We use different files, one for
             * presets which is not modified by the user and a second one for
             * all the user modifications.
             */
            @Override
            public void run() {
                radios = getBean(IStateHandler.class).retrieveRadioCache();
                presetRadios = getBean(IStateHandler.class).retrieveRadioPreset();
            }
        };
    }

    /**
     * Gets the radios.
     * 
     * Radio cache is read on demand
     * 
     * @return the radios
     */
    public List<IRadio> getRadios() {
        return radios;
    }

    /**
     * Gets the radio presets.
     * 
     * @return the preset radios, minus user maintained radio stations.
     */
    public List<IRadio> getRadioPresets() {
        // Check if new stations were added and set false if yes
        if (noNewStations == true) {
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

    /**
     * Gets the radios.
     * 
     * @param label
     *            the label
     * 
     * @return the radios
     */
    public List<IRadio> getRadios(String label) {
        return new ArrayList<IRadio>(getRadios());
    }

    /**
     * Sorts the labels alphabetically
     * 
     * @return Sorted label list
     */
    public List<String> sortRadioLabels() {
        List<String> result = new ArrayList<String>();
        List<String> newResult = new ArrayList<String>();
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

        newResult.add(result.get(0));
        // Sort labels, currently in reversed order
        for (String label : result) {
            int i = 0;
            while (i < newResult.size()) {
                int n = label.compareTo(newResult.get(i));
                if (n < 0) {
                    newResult.add(i, label);
                    i = result.size();
                } else if (i == newResult.size() - 1) {
                    if (newResult.get(i) != label) {
                        newResult.add(label);
                    }
                    i = result.size();
                } else {
                    i = i + 1;
                }
            }
        }
        return newResult;
    }

    /**
     * Remove stations from the list. Preset stations are not really removed but
     * are marked so they not show up in the navigator
     * 
     * @param radio
     *            Radio to be removed
     */
    public void removeRadios(List<IRadio> radios) {
        Logger.info("Removing radios");
        for (IRadio radio : radios) {
            if (!presetRadios.contains(radio)) {
                getRadios().remove(radio);
            }
            // Preset radio station, we can not delete from preset file directly but must mark it as removed.
            else {
                presetRadios.remove(radio);
                final Radio newRadio = new Radio(radio.getName(), radio.getUrl(), radio.getLabel());
                newRadio.setRemoved(true);
                getRadios().add(newRadio);
            }
        }
        radioListDirty = true;
        getBean(INavigationHandler.class).refreshView(RadioNavigationView.class);
    }

    /**
     * Convenience method to remove a single radio
     * 
     * @param radio
     */
    public void removeRadio(IRadio radio) {
        removeRadios(Collections.singletonList(radio));
    }

    /**
     * Retrieve radios for browser.
     * 
     * @return the list< radio>
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("unchecked")
    public List<Radio> retrieveRadiosForBrowser() throws IOException {
        try {
            String xml = NetworkUtils.readURL(NetworkUtils.getConnection(Constants.RADIO_LIST_DOWNLOAD_COMMON_JUKEBOX, ExtendedProxy.getProxy(getState().getProxy())));
            return (List<Radio>) XMLUtils.readObjectFromString(xml);
        } catch (Exception e) {
            String xml = NetworkUtils.readURL(NetworkUtils.getConnection(Constants.RADIO_LIST_DOWNLOAD, ExtendedProxy.getProxy(getState().getProxy())));
            return (List<Radio>) XMLUtils.readObjectFromString(xml);
        }
    }

    /**
     * Retrieve radios.
     */
    /*
     * Get radios from the internet (update preset list)
     */
    public void retrieveRadios() {
        new RetrieveRadiosSwingWorker().execute();
    }

    /**
     * Change label of radio.
     * 
     * @param radioList
     *            List of radios for which the label should be changed
     * @param label
     *            New label
     */
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

    /**
     * Change radio attributes
     * 
     * @param radio
     * @param newRadio
     */
    public void replace(IRadio radio, IRadio newRadio) {
        removeRadio(radio);
        addRadio(newRadio);
        radioListDirty = true;
    }

    /**
     * Returns a Radio object for the given url or null if a Radio object is not
     * available for that url
     * 
     * @param url
     * @return
     */
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
    
	/**
	 * Shows radio browser
	 */
	public void showRadioBrowser() {
		new RadioBrowserDialogController(new RadioBrowserDialog(getFrame().getFrame()), getState()).showRadioBrowser();
	}

	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

	public IRadio editRadio(IRadio radio) {
		IRadioDialog dialog = getBean(IRadioDialog.class);
		dialog.setRadio(radio);
		dialog.showDialog();
		return dialog.getRadio();
	}

}
