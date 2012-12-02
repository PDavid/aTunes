/*
 * aTunes 3.0.0
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
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Year ViewMode for a view. Several views can use this code
 * (Repository and Device)
 * 
 * @author fleax
 * 
 */
public class YearTreeGenerator implements ITreeGenerator {

    private INavigationViewSorter yearSorter;

    private IUnknownObjectChecker unknownObjectChecker;

    private ArtistStructureBuilder artistStructureBuilder;

    /**
     * @param artistStructureBuilder
     */
    public void setArtistStructureBuilder(
	    final ArtistStructureBuilder artistStructureBuilder) {
	this.artistStructureBuilder = artistStructureBuilder;
    }

    /**
     * @param unknownObjectChecker
     */
    public void setUnknownObjectChecker(
	    final IUnknownObjectChecker unknownObjectChecker) {
	this.unknownObjectChecker = unknownObjectChecker;
    }

    /**
     * @param yearSorter
     */
    public void setYearSorter(final INavigationViewSorter yearSorter) {
	this.yearSorter = yearSorter;
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

	List<String> yearNamesList = new ArrayList<String>(structure.keySet());
	yearSorter.sort(yearNamesList);

	for (String yearName : yearNamesList) {
	    buildYearNode(tree, structure, currentFilter, objectsSelected,
		    objectsExpanded, nodesToSelect, nodesToExpand, yearName);
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
     * @param yearName
     */
    private void buildYearNode(final INavigationTree tree,
	    final Map<String, ?> structure, final String currentFilter,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
	    final List<ITreeNode> nodesToSelect,
	    final List<ITreeNode> nodesToExpand, final String yearName) {
	IYear year = (IYear) structure.get(yearName);
	if (currentFilter == null
		|| year.getName(unknownObjectChecker).toUpperCase()
			.contains(currentFilter.toUpperCase())) {
	    ITreeNode yearNode = tree.createNode(year);
	    // If node was selected before refreshing...
	    if (objectsSelected.contains(yearNode.getUserObject())) {
		nodesToSelect.add(yearNode);
	    }
	    // If node was expanded before refreshing...
	    if (objectsExpanded.contains(yearNode.getUserObject())) {
		nodesToExpand.add(yearNode);
	    }
	    List<ILocalAudioObject> audioObjects = year.getAudioObjects();
	    // Returns all artists of this year
	    List<String> artistNamesList = new ArrayList<String>(
		    artistStructureBuilder.getArtistList(audioObjects));
	    Collections.sort(artistNamesList);
	    // Returns an structure of artists and albums containing songs of
	    // this year
	    Map<String, IArtist> yearArtists = artistStructureBuilder
		    .getArtistObjects(audioObjects);
	    for (int j = 0; j < artistNamesList.size(); j++) {
		buildArtistNode(tree, objectsSelected, objectsExpanded,
			nodesToSelect, nodesToExpand, yearNode,
			artistNamesList, yearArtists, j);
	    }
	    tree.addNode(yearNode);
	}
    }

    /**
     * @param tree
     * @param objectsSelected
     * @param objectsExpanded
     * @param nodesToSelect
     * @param nodesToExpand
     * @param yearNode
     * @param artistNamesList
     * @param yearArtists
     * @param j
     */
    private void buildArtistNode(final INavigationTree tree,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
	    final List<ITreeNode> nodesToSelect,
	    final List<ITreeNode> nodesToExpand, final ITreeNode yearNode,
	    final List<String> artistNamesList,
	    final Map<String, IArtist> yearArtists, final int j) {
	IArtist artist = yearArtists.get(artistNamesList.get(j));
	ITreeNode artistNode = tree.createNode(artist);
	List<String> albumNamesList = new ArrayList<String>(artist.getAlbums()
		.keySet());
	Collections.sort(albumNamesList);
	for (int k = 0; k < albumNamesList.size(); k++) {
	    buildAlbumNode(tree, objectsSelected, objectsExpanded,
		    nodesToSelect, nodesToExpand, yearNode, artist, artistNode,
		    albumNamesList, k);
	}
    }

    /**
     * @param tree
     * @param objectsSelected
     * @param objectsExpanded
     * @param nodesToSelect
     * @param nodesToExpand
     * @param yearNode
     * @param artist
     * @param artistNode
     * @param albumNamesList
     * @param k
     */
    private void buildAlbumNode(final INavigationTree tree,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
	    final List<ITreeNode> nodesToSelect,
	    final List<ITreeNode> nodesToExpand, final ITreeNode yearNode,
	    final IArtist artist, final ITreeNode artistNode,
	    final List<String> albumNamesList, final int k) {
	IAlbum album = artist.getAlbum(albumNamesList.get(k));
	ITreeNode albumNode = tree.createNode(album);
	artistNode.add(albumNode);
	yearNode.add(artistNode);
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
		(AudioObjectSelector) new YearAudioObjectSelector(
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
	List<ITreeNode> yearNodeList = new ArrayList<ITreeNode>();
	List<ITreeNode> years = tree.getRootChildsNodes();
	ArtistByNameAudioObjectSelector selector = new ArtistByNameAudioObjectSelector();
	for (ITreeNode year : years) {
	    ITreeNode artistNode = selector.getNodeRepresentingAudioObject(
		    tree, year, artistName);
	    if (artistNode != null) {
		yearNodeList.add(artistNode);
	    }
	}

	if (!yearNodeList.isEmpty()) {
	    tree.expandNodes(yearNodeList);
	    tree.selectNodes(yearNodeList);
	    tree.scrollToNode(yearNodeList.get(0));
	}
    }
}
