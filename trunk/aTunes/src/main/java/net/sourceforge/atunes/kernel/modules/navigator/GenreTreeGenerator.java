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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.Collator;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Genre ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class GenreTreeGenerator implements ITreeGenerator {

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
	 * @param artistSorter
	 */
	public void setArtistSorter(final INavigationViewSorter artistSorter) {
		this.artistSorter = artistSorter;
	}

	/**
	 * @param albumSorter
	 */
	public void setAlbumSorter(final INavigationViewSorter albumSorter) {
		this.albumSorter = albumSorter;
	}

	/**
	 * Builds tree
	 * @param rootTextKey
	 * @param view
	 * @param structure
	 * @param currentFilter
	 * @param root
	 * @param treeModel
	 * @param objectsSelected
	 * @param objectsExpanded
	 */
	@Override
	public void buildTree(final String rootTextKey, final INavigationView view, final Map<String, ?> structure, final String currentFilter, final DefaultMutableTreeNode root, final DefaultTreeModel treeModel, final List<ITreeObject<? extends IAudioObject>> objectsSelected, final List<ITreeObject<? extends IAudioObject>> objectsExpanded) {
		// Nodes to be selected after refresh
		List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
		// Nodes to be expanded after refresh
		List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

		// Refresh nodes
		root.setUserObject(I18nUtils.getString(rootTextKey));
		root.removeAllChildren();
		List<String> genreNamesList = new ArrayList<String>(structure.keySet());
		Collections.sort(genreNamesList, new DefaultComparator(new Collator().getCollator()));

		for (String genreName : genreNamesList) {
			buildGenreNode(structure, currentFilter, root, objectsSelected,
					objectsExpanded, nodesToSelect, nodesToExpand, genreName);
		}

		// Reload the tree to refresh content
		treeModel.reload();

		// Expand nodes
		NavigationViewHelper.expandNodes(view.getTree(), nodesToExpand);

		// Once tree has been refreshed, select previously selected nodes
		NavigationViewHelper.selectNodes(view.getTree(), nodesToSelect);
	}

	/**
	 * @param structure
	 * @param currentFilter
	 * @param root
	 * @param objectsSelected
	 * @param objectsExpanded
	 * @param nodesToSelect
	 * @param nodesToExpand
	 * @param genreName
	 */
	private void buildGenreNode(final Map<String, ?> structure, final String currentFilter,
			final DefaultMutableTreeNode root,
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			final List<DefaultMutableTreeNode> nodesToSelect,
			final List<DefaultMutableTreeNode> nodesToExpand, final String genreName) {

		IGenre genre = (IGenre) structure.get(genreName);
		if (currentFilter == null || genre.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
			DefaultMutableTreeNode genreNode = new DefaultMutableTreeNode(genre);
			// If node was selected before refreshing...
			if (objectsSelected.contains(genreNode.getUserObject())) {
				nodesToSelect.add(genreNode);
			}
			// If node was expanded before refreshing...
			if (objectsExpanded.contains(genreNode.getUserObject())) {
				nodesToExpand.add(genreNode);
			}
			ArtistStructureBuilder builder = new ArtistStructureBuilder(unknownObjectChecker);
			List<ILocalAudioObject> audioObjects = genre.getAudioObjects();
			// Returns all artists of this genre
			List<String> artistNamesList = builder.getArtistList(audioObjects);
			artistSorter.sort(artistNamesList);
			// Returns an structure of artists and albums containing songs of this genre
			Map<String, IArtist> genreArtists = builder.getArtistObjects(audioObjects);
			for (String artistName : artistNamesList) {
				buildArtistNode(objectsSelected, objectsExpanded,
						nodesToSelect, nodesToExpand, genreNode, genreArtists,
						artistName);
			}
			root.add(genreNode);
		}
	}

	/**
	 * @param objectsSelected
	 * @param objectsExpanded
	 * @param nodesToSelect
	 * @param nodesToExpand
	 * @param genreNode
	 * @param genreArtists
	 * @param artistName
	 */
	private void buildArtistNode(
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			final List<DefaultMutableTreeNode> nodesToSelect,
			final List<DefaultMutableTreeNode> nodesToExpand,
			final DefaultMutableTreeNode genreNode, final Map<String, IArtist> genreArtists,
			final String artistName) {
		IArtist artist = genreArtists.get(artistName);
		DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
		List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
		albumSorter.sort(albumNamesList);
		for (String albumName : albumNamesList) {
			buildAlbumNode(objectsSelected, objectsExpanded, nodesToSelect,
					nodesToExpand, genreNode, artist, artistNode, albumName);
		}
	}

	/**
	 * @param objectsSelected
	 * @param objectsExpanded
	 * @param nodesToSelect
	 * @param nodesToExpand
	 * @param genreNode
	 * @param artist
	 * @param artistNode
	 * @param albumName
	 */
	private void buildAlbumNode(
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			final List<DefaultMutableTreeNode> nodesToSelect,
			final List<DefaultMutableTreeNode> nodesToExpand,
			final DefaultMutableTreeNode genreNode, final IArtist artist,
			final DefaultMutableTreeNode artistNode, final String albumName) {
		IAlbum album = artist.getAlbum(albumName);
		DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
		artistNode.add(albumNode);
		genreNode.add(artistNode);
		// If node was selected before refreshing...
		if (objectsSelected.contains(artistNode.getUserObject())) {
			nodesToSelect.add(artistNode);
		}
		// If node was selected before refreshing...
		if (objectsSelected.contains(albumNode.getUserObject())) {
			nodesToSelect.add(albumNode);
		}
		// If node was expanded before refreshing...
		if (objectsExpanded.contains(artistNode.getUserObject()) && objectsExpanded.contains(((DefaultMutableTreeNode) artistNode.getParent()).getUserObject())) {
			nodesToExpand.add(artistNode);
		}
	}

	@Override
	public void selectAudioObject(final JTree tree, final IAudioObject audioObject) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		ChainOfSelectors chain = new ChainOfSelectors((AudioObjectSelector) new GenreAudioObjectSelector(unknownObjectChecker),
				(AudioObjectSelector) new ArtistAudioObjectSelector(unknownObjectChecker),
				(AudioObjectSelector) new AlbumAudioObjectSelector(unknownObjectChecker));
		DefaultMutableTreeNode albumNode = chain.selectAudioObject(rootNode, audioObject);

		if (albumNode != null) {
			TreePath treePath = new TreePath(albumNode.getPath());
			tree.setSelectionPath(treePath);
			tree.scrollPathToVisible(treePath);
		}
	}

	@Override
	public void selectArtist(final JTree tree, final String artistName) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
		List<TreePath> treePathList = new ArrayList<TreePath>();
		TreePath[] treePaths = null;
		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> genres = rootNode.children();
		ArtistByNameAudioObjectSelector selector = new ArtistByNameAudioObjectSelector();
		while (genres.hasMoreElements()) {
			DefaultMutableTreeNode artistNode = selector.getNodeRepresentingAudioObject(genres.nextElement(), artistName);
			if (artistNode != null) {
				TreePath treePath = new TreePath(artistNode.getPath());
				treePathList.add(treePath);
				tree.expandPath(treePath);
			}
		}

		if (!treePathList.isEmpty()) {
			treePaths = new TreePath[treePathList.size()];
			treePaths = treePathList.toArray(treePaths);
			tree.setSelectionPaths(treePaths);
			tree.scrollPathToVisible(treePaths[0]);
		}
	}


}
