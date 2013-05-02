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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * Builds an Album ViewMode for a view. Several views can use this code
 * (Repository and Device)
 * 
 * @author fleax
 * 
 */
public class AlbumTreeGenerator extends AbstractTreeGenerator {

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@SuppressWarnings("unchecked")
	@Override
	List<Class<? extends TreeLevel<?>>> getTreeLevels() {
		return CollectionUtils.fillCollectionWithElements(
				new ArrayList<Class<? extends TreeLevel<?>>>(),
				TreeLevelAlbum.class);
	}

	@Override
	public void selectAudioObject(final INavigationTree tree,
			final IAudioObject audioObject) {
		ITreeNode albumNode = new AlbumAudioObjectSelector(
				this.unknownObjectChecker).getNodeRepresentingAudioObject(tree,
				audioObject);
		if (albumNode != null) {
			tree.selectNode(albumNode);
			tree.scrollToNode(albumNode);
		}
	}

	@Override
	public void selectArtist(final INavigationTree tree, final String artist) {
		List<ITreeNode> albumsNodes = new AlbumWithArtistAudioObjectSelector()
				.getNodesRepresentingAudioObject(tree, artist);
		if (!albumsNodes.isEmpty()) {
			tree.expandNodes(albumsNodes);
			tree.selectNodes(albumsNodes);
			tree.scrollToNode(albumsNodes.get(0));
		}
	}
}
