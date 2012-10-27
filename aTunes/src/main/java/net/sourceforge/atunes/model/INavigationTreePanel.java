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

package net.sourceforge.atunes.model;

import javax.swing.TransferHandler;

/**
 * Panel holding navigator
 * 
 * @author alex
 * 
 */
public interface INavigationTreePanel extends IPanel {

    /**
     * Updates trees
     */
    public void updateTrees();

    /**
     * Shows tree view
     * 
     * @param view
     */
    public void showNavigationView(INavigationView view);

    /**
     * Prepares component for drag and drop
     * 
     * @param transferHandler
     */
    public void enableDragAndDrop(TransferHandler transferHandler);

}