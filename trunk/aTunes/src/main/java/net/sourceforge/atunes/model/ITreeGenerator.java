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
import java.util.Map;

/**
 * Interface for methods that generate a tree for a particular view mode
 * 
 * @author fleax
 * 
 */
public interface ITreeGenerator {

    /**
     * Builds a tree
     * 
     * @param tree
     * @param rootTextKey
     * @param view
     * @param structure
     * @param currentFilter
     * @param objectsSelected
     * @param objectsExpanded
     */
    public void buildTree(INavigationTree tree, String rootTextKey,
	    INavigationView view, Map<String, ?> structure,
	    String currentFilter,
	    List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    List<ITreeObject<? extends IAudioObject>> objectsExpanded);

    /**
     * Request generator to select given audio object
     * 
     * @param tree
     * @param audioObject
     */
    public void selectAudioObject(INavigationTree tree, IAudioObject audioObject);

    /**
     * Request generator to select given artist
     * 
     * @param tree
     * @param artist
     */
    public void selectArtist(INavigationTree tree, String artist);
}
