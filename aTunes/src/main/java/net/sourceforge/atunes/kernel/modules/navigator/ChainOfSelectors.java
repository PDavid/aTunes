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

package net.sourceforge.atunes.kernel.modules.navigator;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ITreeObject;

/**
 * Used to locate audio objects using a list of selectors where each selector selects from previous selector result
 * @author alex
 *
 */
class ChainOfSelectors {

	private AudioObjectSelector<ITreeObject<?>, IAudioObject>[] selectors;
	
	public ChainOfSelectors(AudioObjectSelector<ITreeObject<?>, IAudioObject>...audioObjectSelectors) {
		this.selectors = audioObjectSelectors;
	}
	
	/**
	 * @param rootNode
	 * @param audioObject
	 * @return
	 */
	public DefaultMutableTreeNode selectAudioObject(DefaultMutableTreeNode rootNode, IAudioObject audioObject) {
		DefaultMutableTreeNode node = rootNode;
		for (int i = 0; i< selectors.length; i++) {
			node = selectors[i].getNodeRepresentingAudioObject(node, audioObject);
			if (node == null) {
				return null;
			}
		}
		return node;
	}
}
