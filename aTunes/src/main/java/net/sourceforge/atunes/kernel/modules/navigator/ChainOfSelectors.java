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

package net.sourceforge.atunes.kernel.modules.navigator;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;

/**
 * Used to locate audio objects using a list of selectors where each selector
 * selects from previous selector result
 * 
 * @author alex
 * 
 */
class ChainOfSelectors {

    private final AudioObjectSelector<ITreeObject<?>, IAudioObject>[] selectors;

    /**
     * @param audioObjectSelectors
     */
    public ChainOfSelectors(
	    final AudioObjectSelector<ITreeObject<?>, IAudioObject>... audioObjectSelectors) {
	this.selectors = audioObjectSelectors;
    }

    /**
     * @param tree
     * @param audioObject
     * @return
     */
    public ITreeNode selectAudioObject(final INavigationTree tree,
	    final IAudioObject audioObject) {
	ITreeNode node = null;
	for (int i = 0; i < selectors.length; i++) {
	    if (node == null) {
		node = selectors[i].getNodeRepresentingAudioObject(tree,
			audioObject);
	    } else {
		node = selectors[i].getNodeRepresentingAudioObject(tree, node,
			audioObject);
	    }
	    if (node == null) {
		return null;
	    }
	}
	return node;
    }
}
