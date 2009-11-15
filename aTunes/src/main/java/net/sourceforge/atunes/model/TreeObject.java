/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import net.sourceforge.atunes.kernel.modules.repository.Repository;

/**
 * All objects that can be shown in Navigator tree must implement this
 * interface.
 */
public interface TreeObject {

    /**
     * Returns the audio objects of this tree object from the given repository
     * 
     * @return The audio objects of this object
     */
    public List<AudioObject> getAudioObjects(Repository repository);

    /**
     * Returns a string to be shown as tool tip when object is used for the given repository
     * 
     * @return ToolTip
     */
    public String getToolTip(Repository repository);

    /**
     * Returns <code>true</code> if this object supports extended tool tip
     * 
     * @return
     */
    public boolean isExtendedToolTipSupported();

    /**
     * Fills tool tip with object data for the given repository
     * 
     * @param toolTip
     */
    public void setExtendedToolTip(ExtendedToolTip toolTip, Repository repository);

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
     * @return
     */
    public ImageIcon getExtendedToolTipImage();
}
