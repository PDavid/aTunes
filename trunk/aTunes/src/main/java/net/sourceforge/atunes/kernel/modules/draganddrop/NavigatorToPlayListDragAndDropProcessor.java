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

package net.sourceforge.atunes.kernel.modules.draganddrop;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.ITreeNode;

/**
 * Processes drag from navigator
 * 
 * @author alex
 * 
 */
public class NavigatorToPlayListDragAndDropProcessor {

	private INavigationHandler navigationHandler;

	private IPlayListTable playListTable;

	private IPlayListHandler playListHandler;

	/**
	 * @param playListTable
	 */
	public void setPlayListTable(IPlayListTable playListTable) {
		this.playListTable = playListTable;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * adds objects from a drag started in navigator tree
	 * 
	 * @param dropRow
	 * @param listOfObjectsDragged
	 */
	boolean dragFromNavigatorTree(int dropRow,
			final List<ITreeNode> listOfObjectsDragged) {
		List<IAudioObject> objectsToAdd = getObjectsFromNavigatorTree(listOfObjectsDragged);
		if (!objectsToAdd.isEmpty()) {
			addObjects(dropRow, objectsToAdd);
			return true;
		}
		return false;
	}

	/**
	 * adds objects from a drag started in navigator table
	 * 
	 * @param dropRow
	 * @param listOfObjectsDragged
	 */
	boolean dragFromNavigatorTable(int dropRow,
			final List<Integer> listOfObjectsDragged) {
		List<IAudioObject> objectsToAdd = getObjectsFromNavigatorTable(listOfObjectsDragged);
		if (!objectsToAdd.isEmpty()) {
			addObjects(dropRow, objectsToAdd);
			return true;
		}
		return false;
	}

	private void addObjects(int dropRow, List<IAudioObject> audioObjects) {
		playListHandler.addToVisiblePlayList(dropRow, audioObjects);
		// Keep selected rows: if drop row is the bottom of play list (-1)
		// then select last row
		if (dropRow == -1) {
			dropRow = playListHandler.getVisiblePlayList().size()
					- audioObjects.size();
		}
		playListTable.getSelectionModel().addSelectionInterval(dropRow,
				dropRow + audioObjects.size() - 1);
	}

	/**
	 * @param nodes
	 * @return audio objects dragged
	 */
	private List<IAudioObject> getObjectsFromNavigatorTree(
			final List<ITreeNode> nodes) {
		List<IAudioObject> audioObjectsToAdd = new ArrayList<IAudioObject>();
		for (ITreeNode node : nodes) {
			List<? extends IAudioObject> objectsToImport = navigationHandler
					.getAudioObjectsForTreeNode(node);
			if (objectsToImport != null) {
				audioObjectsToAdd.addAll(objectsToImport);
			}
		}
		return audioObjectsToAdd;
	}

	/**
	 * @param rows
	 * @return audio objects dragged
	 */
	private List<IAudioObject> getObjectsFromNavigatorTable(
			final List<Integer> rows) {
		List<IAudioObject> audioObjectsToAdd = new ArrayList<IAudioObject>();
		for (Integer row : rows) {
			audioObjectsToAdd.add(navigationHandler
					.getAudioObjectInNavigationTable(row));
		}
		return audioObjectsToAdd;
	}
}
