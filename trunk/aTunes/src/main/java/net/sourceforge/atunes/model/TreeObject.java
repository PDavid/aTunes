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

package net.sourceforge.atunes.model;

import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * All objects that can be shown in Navigator tree must implement this
 * interface.
 */
@PluginApi
public interface TreeObject<T extends IAudioObject> {

    /**
     * Returns the audio objects of this tree object
     * 
     * @return The audio objects of this object
     */
    public List<T> getAudioObjects();

    /**
     * Returns a string to be shown as tool tip when object is used
     * 
     * @return ToolTip
     */
    public String getToolTip();

    /**
     * Returns <code>true</code> if this object supports extended tool tip
     * 
     * @return
     */
    public boolean isExtendedToolTipSupported();

    /**
     * Fills tool tip with object data
     * 
     * @param toolTip
     */
    public void setExtendedToolTip(ExtendedToolTip toolTip);

    /**
     * Returns <code>true</code> if this object supports image in extended tool
     * tip
     * 
     * @return
     */
    public boolean isExtendedToolTipImageSupported();

    /**
     * Returns image to be shown in extended tool tip
     * 
     * @param osManager
     * @return
     */
    public ImageIcon getExtendedToolTipImage(IOSManager osManager);
    
	/**
	 * Returns number of audio objects
	 * @return
	 */
	public int size();

}
