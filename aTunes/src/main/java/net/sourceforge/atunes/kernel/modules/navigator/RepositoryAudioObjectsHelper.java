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
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;

class RepositoryAudioObjectsHelper {

	/**
	 * Returns objects from node with view and filter or all objects if
	 * selection is root and no filter
	 * 
	 * @param allObjects
	 * @param node
	 * @param viewMode
	 * @param treeFilter
	 * @param tableFilter
	 * @param navigationTableColumnSet
	 * @return
	 */
	List<IAudioObject> getAudioObjectForTreeNode(
			final Collection<ILocalAudioObject> allObjects,
			final ITreeNode node, final ViewMode viewMode,
			final String treeFilter, String tableFilter, IColumnSet columnSet) {
		List<IAudioObject> songs = new ArrayList<IAudioObject>();
		if (node.isRoot()) {
			if (treeFilter == null) {
				songs.addAll(allObjects);
			} else {
				for (int i = 0; i < node.getChildCount(); i++) {
					@SuppressWarnings("unchecked")
					ITreeObject<ILocalAudioObject> obj = (ITreeObject<ILocalAudioObject>) node
							.getChildAt(i).getUserObject();
					songs.addAll(obj.getAudioObjects());
				}
			}
		} else {
			@SuppressWarnings("unchecked")
			ITreeObject<ILocalAudioObject> obj = (ITreeObject<ILocalAudioObject>) node
					.getUserObject();
			songs.addAll(obj.getAudioObjects());
		}

		if (tableFilter != null) {
			songs = columnSet.filterAudioObjects(songs, tableFilter);
		}

		return songs;
	}
}
