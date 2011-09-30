package net.sourceforge.atunes.model;

import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.radio.Radio;

/**
 * Responsible of managing radios
 * @author alex
 *
 */
public interface IRadioHandler extends IHandler {

	/**
	 * Add the radio station from the add radio dialog.
	 */
	public void addRadio();

	/**
	 * Add a radio station to the list.
	 * 
	 * @param radio
	 *            Station
	 */
	public void addRadio(IRadio radio);

	/**
	 * Gets the radios.
	 * 
	 * Radio cache is read on demand
	 * 
	 * @return the radios
	 */
	public List<IRadio> getRadios();

	/**
	 * Gets the radio presets.
	 * 
	 * @return the preset radios, minus user maintained radio stations.
	 */
	public List<IRadio> getRadioPresets();

	/**
	 * Gets the radios.
	 * 
	 * @param label
	 *            the label
	 * 
	 * @return the radios
	 */
	public List<IRadio> getRadios(String label);

	/**
	 * Sorts the labels alphabetically
	 * 
	 * @return Sorted label list
	 */
	public List<String> sortRadioLabels();

	/**
	 * Remove stations from the list. Preset stations are not really removed but
	 * are marked so they not show up in the navigator
	 * 
	 * @param radio
	 *            Radio to be removed
	 */
	public void removeRadios(List<IRadio> radios);

	/**
	 * Convenience method to remove a single radio
	 * 
	 * @param radio
	 */
	public void removeRadio(IRadio radio);

	/**
	 * Retrieve radios for browser.
	 * 
	 * @return the list< radio>
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public List<Radio> retrieveRadiosForBrowser() throws IOException;

	/**
	 * Retrieve radios.
	 */
	/*
	 * Get radios from the internet (update preset list)
	 */
	public void retrieveRadios();

	/**
	 * Change label of radio.
	 * 
	 * @param radioList
	 *            List of radios for which the label should be changed
	 * @param label
	 *            New label
	 */
	public void setLabel(List<IRadio> radioList, String label);

	/**
	 * Change radio attributes
	 * 
	 * @param radio
	 * @param newRadio
	 */
	public void replace(IRadio radio, IRadio newRadio);

	/**
	 * Returns a Radio object for the given url or null if a Radio object is not
	 * available for that url
	 * 
	 * @param url
	 * @return
	 */
	public IRadio getRadioIfLoaded(String url);

	/**
	 * Shows radio browser
	 */
	public void showRadioBrowser();

	public IRadio editRadio(IRadio radio);

}