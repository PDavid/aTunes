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
import java.util.Map;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Builds an Artist ViewMode for a view. Several views can use this code
 * (Repository and Device)
 * 
 * @author fleax
 * 
 */
public class ArtistTreeGenerator implements ITreeGenerator {

	private INavigationViewSorter artistSorter;

	private INavigationViewSorter albumSorter;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param albumSorter
	 */
	public void setAlbumSorter(final INavigationViewSorter albumSorter) {
		this.albumSorter = albumSorter;
	}

	/**
	 * @param artistSorter
	 */
	public void setArtistSorter(final INavigationViewSorter artistSorter) {
		this.artistSorter = artistSorter;
	}

	@Override
	public void buildTree(final INavigationTree tree, final String rootTextKey,
			final INavigationView view, final Map<String, ?> structure,
			final String currentFilter,
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<ITreeObject<? extends IAudioObject>> objectsExpanded) {
		// Set root
		tree.setRoot(I18nUtils.getString(rootTextKey));

		List<String> artistNamesList = new ArrayList<String>(structure.keySet());
		this.artistSorter.sort(artistNamesList);

		// Nodes to be selected after refresh
		List<ITreeNode> nodesToSelect = new ArrayList<ITreeNode>();
		// Nodes to be expanded after refresh
		List<ITreeNode> nodesToExpand = new ArrayList<ITreeNode>();

		for (String artistName : artistNamesList) {
			buildArtistNode(tree, structure, currentFilter, objectsSelected,
					objectsExpanded, nodesToSelect, nodesToExpand, artistName);
		}

		// Reload the tree to refresh content
		tree.reload();

		// Expand nodes
		tree.expandNodes(nodesToExpand);

		// Once tree has been refreshed, select previously selected nodes
		tree.selectNodes(nodesToSelect);
	}

	/**
	 * @param tree
	 * @param structure
	 * @param currentFilter
	 * @param objectsSelected
	 * @param objectsExpanded
	 * @param nodesToSelect
	 * @param nodesToExpand
	 * @param artistName
	 */
	private void buildArtistNode(final INavigationTree tree,
			final Map<String, ?> structure, final String currentFilter,
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			final List<ITreeNode> nodesToSelect,
			final List<ITreeNode> nodesToExpand, final String artistName) {

		IArtist artist = (IArtist) structure.get(artistName);
		if (artistOrAlbumMatchesFilter(currentFilter, artist)) {
			ITreeNode artistNode = tree.createNode(artist);
			List<String> albums = filterAndSortAlbums(currentFilter, artist);
			for (String albumName : albums) {
				ITreeNode albumNode = tree.createNode(artist
						.getAlbum(albumName));
				artistNode.add(albumNode);
				// If node was selected before refreshing...
				if (objectsSelected.contains(albumNode.getUserObject())) {
					nodesToSelect.add(albumNode);
				}
			}
			tree.addNode(artistNode);
			// Reload causes very important lag on large collections and if it
			// is not used
			// selection does not work.

			// If node was selected before refreshing...
			if (objectsSelected.contains(artistNode.getUserObject())) {
				nodesToSelect.add(artistNode);
			}
			// If node was expanded before refreshing...
			if (objectsExpanded.contains(artistNode.getUserObject())) {
				nodesToExpand.add(artistNode);
			}
		}
	}

	private List<String> filterAndSortAlbums(final String filter,
			final IArtist artist) {
		List<String> result = null;
		if (StringUtils.isEmpty(filter)) {
			result = new ArrayList<String>(artist.getAlbums().keySet());
		} else {
			result = new ArrayList<String>();
			for (String album : artist.getAlbums().keySet()) {
				if (albumMatchesFilter(filter, album)) {
					result.add(album);
				}
			}
		}
		this.albumSorter.sort(result);
		return result;
	}

	private boolean artistOrAlbumMatchesFilter(final String filter,
			final IArtist artist) {
		if (StringUtils.isEmpty(filter)) {
			return true;
		}

		// Artist name matches
		if (artist.getName().toUpperCase().contains(filter.toUpperCase())) {
			return true;
		} else {
			// Find in albums
			for (String album : artist.getAlbums().keySet()) {
				if (albumMatchesFilter(filter, album)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param filter
	 * @param album
	 */
	private boolean albumMatchesFilter(final String filter, final String album) {
		if (filter == null
				|| album.toUpperCase().contains(filter.toUpperCase())) {
			return true;
		}
		return false;
	}

	@Override
	public void selectAudioObject(final INavigationTree tree,
			final IAudioObject audioObject) {
		ITreeNode artistNode = new ArtistAudioObjectSelector(
				this.unknownObjectChecker).getNodeRepresentingAudioObject(tree,
				audioObject);
		if (artistNode != null) {
			ITreeNode albumNode = new AlbumAudioObjectSelector(
					this.unknownObjectChecker).getNodeRepresentingAudioObject(
					tree, artistNode, audioObject);
			if (albumNode != null) {
				tree.selectNode(albumNode);
				tree.scrollToNode(albumNode);
			}
		}
	}

	@Override
	public void selectArtist(final INavigationTree tree, final String artistName) {
		ITreeNode artistNode = new ArtistByNameAudioObjectSelector()
				.getNodeRepresentingAudioObject(tree, artistName);
		if (artistNode != null) {
			tree.selectNode(artistNode);
			tree.scrollToNode(artistNode);
			tree.expandNode(artistNode);
		}
	}
}
