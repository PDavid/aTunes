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
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.dialogs.RadioBrowserDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.RadioNavigationView;
import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IState;
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
                NavigationHandler.getInstance().refreshView(RadioNavigationView.class);
            } catch (InterruptedException e) {
                Logger.error(e);
            } catch (ExecutionException e) {
                Logger.error(e);
            }

        }
    }

    private static RadioHandler instance = new RadioHandler();

    private List<Radio> radios;
    private List<Radio> presetRadios;
    private List<Radio> retrievedPresetRadios = new ArrayList<Radio>();
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
        Radio radio = GuiHandler.getInstance().showAddRadioDialog();
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
    public void addRadio(Radio radio) {
        Logger.info("Adding radio");
        if (radio != null && !getRadios().contains(radio)) {
            getRadios().add(radio);
            radioListDirty = true;
        }
        Collections.sort(getRadios(), Radio.getComparator());
        NavigationHandler.getInstance().refreshView(RadioNavigationView.class);
    }

    /**
     * Write stations to xml files.
     */
    public void applicationFinish() {
        if (radioListDirty) {
            ApplicationStateHandler.getInstance().persistRadioCache(getRadios());
            // Only write preset list if new stations were added
            if (!noNewStations) {
                ApplicationStateHandler.getInstance().persistPresetRadioCache(presetRadios);
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
                radios = ApplicationStateHandler.getInstance().retrieveRadioCache();
                presetRadios = ApplicationStateHandler.getInstance().retrieveRadioPreset();
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
    public List<Radio> getRadios() {
        return radios;
    }

    /**
     * Gets the radio presets.
     * 
     * @return the preset radios, minus user maintained radio stations.
     */
    public List<Radio> getRadioPresets() {
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
        return new ArrayList<Radio>(presetRadios);
    }

    /**
     * Gets the radios.
     * 
     * @param label
     *            the label
     * 
     * @return the radios
     */
    public List<Radio> getRadios(String label) {
        return new ArrayList<Radio>(getRadios());
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
        for (Radio radio : getRadios()) {
            String label = radio.getLabel();
            if (!result.contains(label)) {
                result.add(label);
            }
        }
        // Read labels from preset radios
        for (Radio radio : presetRadios) {
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
    public void removeRadios(List<Radio> radios) {
        Logger.info("Removing radios");
        for (Radio radio : radios) {
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
        NavigationHandler.getInstance().refreshView(RadioNavigationView.class);
    }

    /**
     * Convenience method to remove a single radio
     * 
     * @param radio
     */
    public void removeRadio(Radio radio) {
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
    public void setLabel(List<Radio> radioList, String label) {
        for (Radio r : radioList) {
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
    public void replace(Radio radio, Radio newRadio) {
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
    public Radio getRadioIfLoaded(String url) {
        // Check in user radios
        for (Radio radio : getRadios()) {
            if (radio.getUrl().equalsIgnoreCase(url)) {
                return radio;
            }
        }
        // Check in preset radios
        for (Radio radio : presetRadios) {
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

	public Radio editRadio(Radio radio) {
		return GuiHandler.getInstance().showEditRadioDialog(radio);
	}

}
