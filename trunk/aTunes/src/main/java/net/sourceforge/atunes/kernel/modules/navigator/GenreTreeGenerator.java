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
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.Collator;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Genre ViewMode for a view. Several views can use this code
 * (Repository and Device)
 * 
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

    @Override
    public void buildTree(final INavigationTree tree, final String rootTextKey,
	    final INavigationView view, final Map<String, ?> structure,
	    final String currentFilter,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded) {
	// Nodes to be selected after refresh
	List<ITreeNode> nodesToSelect = new ArrayList<ITreeNode>();
	// Nodes to be expanded after refresh
	List<ITreeNode> nodesToExpand = new ArrayList<ITreeNode>();

	// Refresh nodes
	tree.setRoot(I18nUtils.getString(rootTextKey));

	List<String> genreNamesList = new ArrayList<String>(structure.keySet());
	Collections.sort(genreNamesList,
		new DefaultComparator(new Collator().getCollator()));

	for (String genreName : genreNamesList) {
	    buildGenreNode(tree, structure, currentFilter, objectsSelected,
		    objectsExpanded, nodesToSelect, nodesToExpand, genreName);
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
     * @param root
     * @param objectsSelected
     * @param objectsExpanded
     * @param nodesToSelect
     * @param nodesToExpand
     * @param genreName
     */
    private void buildGenreNode(final INavigationTree tree,
	    final Map<String, ?> structure, final String currentFilter,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
	    final List<ITreeNode> nodesToSelect,
	    final List<ITreeNode> nodesToExpand, final String genreName) {

	IGenre genre = (IGenre) structure.get(genreName);
	if (currentFilter == null
		|| genre.getName().toUpperCase()
			.contains(currentFilter.toUpperCase())) {
	    ITreeNode genreNode = tree.createNode(genre);
	    // If node was selected before refreshing...
	    if (objectsSelected.contains(genreNode.getUserObject())) {
		nodesToSelect.add(genreNode);
	    }
	    // If node was expanded before refreshing...
	    if (objectsExpanded.contains(genreNode.getUserObject())) {
		nodesToExpand.add(genreNode);
	    }
	    ArtistStructureBuilder builder = new ArtistStructureBuilder(
		    unknownObjectChecker);
	    List<ILocalAudioObject> audioObjects = genre.getAudioObjects();
	    // Returns all artists of this genre
	    List<String> artistNamesList = builder.getArtistList(audioObjects);
	    artistSorter.sort(artistNamesList);
	    // Returns an structure of artists and albums containing songs of
	    // this genre
	    Map<String, IArtist> genreArtists = builder
		    .getArtistObjects(audioObjects);
	    for (String artistName : artistNamesList) {
		buildArtistNode(tree, objectsSelected, objectsExpanded,
			nodesToSelect, nodesToExpand, genreNode, genreArtists,
			artistName);
	    }
	    tree.addNode(genreNode);
	}
    }

    /**
     * @param tree
     * @param objectsSelected
     * @param objectsExpanded
     * @param nodesToSelect
     * @param nodesToExpand
     * @param genreNode
     * @param genreArtists
     * @param artistName
     */
    private void buildArtistNode(final INavigationTree tree,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
	    final List<ITreeNode> nodesToSelect,
	    final List<ITreeNode> nodesToExpand, final ITreeNode genreNode,
	    final Map<String, IArtist> genreArtists, final String artistName) {
	IArtist artist = genreArtists.get(artistName);
	ITreeNode artistNode = tree.createNode(artist);
	List<String> albumNamesList = new ArrayList<String>(artist.getAlbums()
		.keySet());
	albumSorter.sort(albumNamesList);
	for (String albumName : albumNamesList) {
	    buildAlbumNode(tree, objectsSelected, objectsExpanded,
		    nodesToSelect, nodesToExpand, genreNode, artist,
		    artistNode, albumName);
	}
    }

    /**
     * @param tree
     * @param objectsSelected
     * @param objectsExpanded
     * @param nodesToSelect
     * @param nodesToExpand
     * @param genreNode
     * @param artist
     * @param artistNode
     * @param albumName
     */
    private void buildAlbumNode(final INavigationTree tree,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
	    final List<ITreeNode> nodesToSelect,
	    final List<ITreeNode> nodesToExpand, final ITreeNode genreNode,
	    final IArtist artist, final ITreeNode artistNode,
	    final String albumName) {
	IAlbum album = artist.getAlbum(albumName);
	ITreeNode albumNode = tree.createNode(album);
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
	if (objectsExpanded.contains(artistNode.getUserObject())
		&& objectsExpanded.contains(artistNode.getParent()
			.getUserObject())) {
	    nodesToExpand.add(artistNode);
	}
    }

    @Override
    public void selectAudioObject(final INavigationTree tree,
	    final IAudioObject audioObject) {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	ChainOfSelectors chain = new ChainOfSelectors(
		(AudioObjectSelector) new GenreAudioObjectSelector(
			unknownObjectChecker),
		(AudioObjectSelector) new ArtistAudioObjectSelector(
			unknownObjectChecker),
		(AudioObjectSelector) new AlbumAudioObjectSelector(
			unknownObjectChecker));
	ITreeNode albumNode = chain.selectAudioObject(tree, audioObject);

	if (albumNode != null) {
	    tree.selectNode(albumNode);
	    tree.scrollToNode(albumNode);
	}
    }

    @Override
    public void selectArtist(final INavigationTree tree, final String artistName) {
	List<ITreeNode> artistNodesList = new ArrayList<ITreeNode>();
	List<ITreeNode> genres = tree.getRootChildsNodes();
	ArtistByNameAudioObjectSelector selector = new ArtistByNameAudioObjectSelector();
	for (ITreeNode genre : genres) {
	    ITreeNode artistNode = selector.getNodeRepresentingAudioObject(
		    tree, genre, artistName);
	    if (artistNode != null) {
		artistNodesList.add(artistNode);
	    }
	}
	if (!artistNodesList.isEmpty()) {
	    tree.expandNodes(artistNodesList);
	    tree.selectNodes(artistNodesList);
	    tree.scrollToNode(artistNodesList.get(0));
	}
    }
}
