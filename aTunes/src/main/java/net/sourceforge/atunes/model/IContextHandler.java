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


/**
 * Responsible of show context information
 * @author alex
 *
 */
public interface IContextHandler extends IHandler {

	/**
	 * Updates panel with audio object information.
	 * 
	 * @param ao
	 *            the audio object
	 */
	public void retrieveInfoAndShowInPanel(IAudioObject ao);

	/**
	 * @return the lastAudioObject
	 */
	public IAudioObject getCurrentAudioObject();

	/**
	 * Show context information panel.
	 * 
	 * @param show
	 *            the show
	 */
	public void showContextPanel(boolean show);
	
    /**
     * Called when user changes context tab
     */
    public void contextPanelChanged();
    
	/**
	 * Selects context tab
	 * @param selectedContextTab
	 */
    public void setContextTab(String selectedContextTab);

	/**
	 * Called when a context panel is updated
	 */
	public void finishedContextPanelUpdate();

}