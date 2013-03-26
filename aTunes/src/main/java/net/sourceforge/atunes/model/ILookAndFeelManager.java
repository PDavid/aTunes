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
 * Responsible of managing look and feels
 * 
 * @author alex
 * 
 */
public interface ILookAndFeelManager {

    /**
     * Sets the look and feel.
     * 
     * @param lookAndFeelBean
     * @param stateCore
     * @param stateUI
     * @param osManager
     */
    public void setLookAndFeel(LookAndFeelBean lookAndFeelBean,
	    IStateCore stateCore, IStateUI stateUI, IOSManager osManager);

    /**
     * Returns available look and feels
     * 
     * @return
     */
    public List<String> getAvailableLookAndFeels();

    /**
     * Returns available skins for given look and feel
     * 
     * @param lookAndFeelName
     * @return
     */
    public List<String> getAvailableSkins(String lookAndFeelName);

    /**
     * Returns the name of the current look and feel
     * 
     * @return
     */
    public String getCurrentLookAndFeelName();

    /**
     * Updates the user interface to use a new skin
     * 
     * @param selectedSkin
     * @param stateCore
     * @param stateUI
     * @param osManager
     */
    public void applySkin(String selectedSkin, IStateCore stateCore,
	    IStateUI stateUI, IOSManager osManager);

    /**
     * @return the currentLookAndFeel
     */
    public ILookAndFeel getCurrentLookAndFeel();

    /**
     * Returns default skin for a given look and feel
     * 
     * @param lookAndFeelName
     * @return
     */
    public String getDefaultSkin(String lookAndFeelName);

    /**
     * @return the defaultLookAndFeel
     */
    public ILookAndFeel getDefaultLookAndFeel();

    /**
     * Adds a new look and feel change listener
     * 
     * @param listener
     */
    public void addLookAndFeelChangeListener(ILookAndFeelChangeListener listener);

    /**
     * Removes a look and feel change listener
     * 
     * @param listener
     */
    public void removeLookAndFeelChangeListener(
	    ILookAndFeelChangeListener listener);

}