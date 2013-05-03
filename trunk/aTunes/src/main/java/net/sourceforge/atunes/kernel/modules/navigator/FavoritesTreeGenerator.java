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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Generator for favorites tree
 * 
 * @author alex
 * 
 */
public class FavoritesTreeGenerator implements ITreeGenerator {

	private static final String ALBUMS = "ALBUMS";

	private static final String ARTISTS = "ARTISTS";

	@SuppressWarnings("unchecked")
	@Override
	public void buildTree(INavigationTree tree, String rootTextKey,
			INavigationView view, Map<String, ?> data, String treeFilter,
			List<ITreeObject<? extends IAudioObject>> objectsSelected,
			List<ITreeObject<? extends IAudioObject>> objectsExpanded) {

		// Nodes to be selected after refresh
		List<ITreeNode> nodesToSelect = new ArrayList<ITreeNode>();
		// Nodes to be expanded after refresh
		List<ITreeNode> nodesToExpand = new ArrayList<ITreeNode>();

		view.getTree().setRoot(
				new NavigationTreeRoot(I18nUtils.getString(rootTextKey), view
						.getIcon()));

		ITreeNode artistsNode = view.getTree().createNode(
				I18nUtils.getString(ARTISTS));
		nodesToExpand.add(artistsNode);
		addArtistNodes(view, artistsNode, treeFilter,
				(Map<String, IArtist>) data.get(ARTISTS), objectsSelected,
				objectsExpanded, nodesToSelect, nodesToExpand);
		view.getTree().addNode(artistsNode);

		ITreeNode albumsNode = view.getTree().createNode(
				I18nUtils.getString(ALBUMS));
		nodesToExpand.add(albumsNode);
		addAlbumNodes(view, albumsNode, treeFilter,
				(Map<String, IAlbum>) data.get(ALBUMS), objectsSelected,
				objectsExpanded, nodesToSelect, nodesToExpand);
		view.getTree().addNode(albumsNode);

		ITreeNode songsNode = view.getTree().createNode(
				I18nUtils.getString("SONGS"));
		view.getTree().addNode(songsNode);

		view.getTree().reload();

		// Expand nodes
		view.getTree().expandNodes(nodesToExpand);

		// Once tree has been refreshed, select previously selected nodes
		view.getTree().selectNodes(nodesToSelect);
	}

	/**
	 * Adds the artist nodes.
	 * 
	 * @param view
	 * @param root
	 * @param currentFilter
	 * @param artists
	 * @param objectsSelected
	 * @param objectsExpanded
	 * @param nodesToSelect
	 * @param nodesToExpand
	 */
	private void addArtistNodes(INavigationView view, final ITreeNode root,
			final String currentFilter, final Map<String, IArtist> artists,
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			final List<ITreeNode> nodesToSelect,
			final List<ITreeNode> nodesToExpand) {
		List<String> artistNamesList = new ArrayList<String>(artists.keySet());
		Collections.sort(artistNamesList);

		for (int i = 0; i < artistNamesList.size(); i++) {
			IArtist artist = artists.get(artistNamesList.get(i));
			if (currentFilter == null
					|| artist.getName().toUpperCase()
							.contains(currentFilter.toUpperCase())) {
				ITreeNode artistNode = view.getTree().createNode(artist);

				// If node was selected before refreshing...
				if (objectsSelected.contains(artistNode.getUserObject())) {
					nodesToSelect.add(artistNode);
				}
				// If node was expanded before refreshing...
				if (objectsExpanded.contains(artistNode.getUserObject())) {
					nodesToExpand.add(artistNode);
				}

				// If an artist fits current filter we want to show all albums
				// so put filter to null
				addAlbumNodes(view, artistNode, null, artist.getAlbums(),
						objectsSelected, objectsExpanded, nodesToSelect,
						nodesToExpand);

				root.add(artistNode);
			}
		}
	}

	/**
	 * Adds the album nodes.
	 * 
	 * @param view
	 * @param root
	 * @param currentFilter
	 * @param albums
	 * @param objectsSelected
	 * @param objectsExpanded
	 * @param nodesToSelect
	 * @param nodesToExpand
	 */
	private void addAlbumNodes(final INavigationView view,
			final ITreeNode root, final String currentFilter,
			final Map<String, IAlbum> albums,
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			final List<ITreeNode> nodesToSelect,
			final List<ITreeNode> nodesToExpand) {
		List<String> albumsNamesList = new ArrayList<String>(albums.keySet());
		Collections.sort(albumsNamesList);

		for (int i = 0; i < albumsNamesList.size(); i++) {
			IAlbum album = albums.get(albumsNamesList.get(i));
			if (currentFilter == null
					|| album.getName().toUpperCase()
							.contains(currentFilter.toUpperCase())) {
				ITreeNode albumNode = view.getTree().createNode(album);

				// If node was selected before refreshing...
				if (objectsSelected.contains(albumNode.getUserObject())) {
					nodesToSelect.add(albumNode);
				}
				// If node was expanded before refreshing...
				if (objectsExpanded.contains(albumNode.getUserObject())) {
					nodesToExpand.add(albumNode);
				}

				root.add(albumNode);
			}
		}
	}

	@Override
	public void selectAudioObject(INavigationTree tree, IAudioObject audioObject) {
	}

	@Override
	public void selectArtist(INavigationTree tree, String artist) {
	}
}
