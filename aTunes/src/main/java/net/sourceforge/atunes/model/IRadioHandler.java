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

package net.sourceforge.atunes.model;

import java.util.List;

/**
 * Responsible of managing radios
 * 
 * @author alex
 * 
 */
public interface IRadioHandler extends IHandler {

	/**
	 * Creates a new IRadio object
	 * 
	 * @param name
	 * @param url
	 * @param label
	 * @return
	 */
	public IRadio createRadio(String name, String url, String label);

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
	public List<String> getRadioLabels();

	/**
	 * Remove stations from the list. Preset stations are not really removed but
	 * are marked so they not show up in the navigator
	 * 
	 * @param radios
	 */
	public void removeRadios(List<IRadio> radios);

	/**
	 * Convenience method to remove a single radio
	 * 
	 * @param radio
	 */
	public void removeRadio(IRadio radio);

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

	/**
	 * Changes attributes of radio
	 * 
	 * @param radio
	 * @return
	 */
	public IRadio editRadio(IRadio radio);

}