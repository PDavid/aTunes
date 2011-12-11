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
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Year ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class YearTreeGenerator implements ITreeGenerator {

	private INavigationViewSorter yearSorter;
	
	/**
	 * @param yearSorter
	 */
	public void setYearSorter(INavigationViewSorter yearSorter) {
		this.yearSorter = yearSorter;
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
    public void buildTree(String rootTextKey, INavigationView view, Map<String, ?> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<ITreeObject<? extends IAudioObject>> objectsSelected, List<ITreeObject<? extends IAudioObject>> objectsExpanded) {
        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        // Refresh nodes
        root.setUserObject(I18nUtils.getString(rootTextKey));
        root.removeAllChildren();
        List<String> yearNamesList = new ArrayList<String>(structure.keySet());
        yearSorter.sort(yearNamesList);

        for (String yearName : yearNamesList) {
            buildYearNode(structure, currentFilter, root, objectsSelected,
					objectsExpanded, nodesToSelect, nodesToExpand, yearName);
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
	 * @param yearName
	 */
	private void buildYearNode(Map<String, ?> structure, String currentFilter,
			DefaultMutableTreeNode root,
			List<ITreeObject<? extends IAudioObject>> objectsSelected,
			List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			List<DefaultMutableTreeNode> nodesToSelect,
			List<DefaultMutableTreeNode> nodesToExpand, String yearName) {
		IYear year = (IYear) structure.get(yearName);
		if (currentFilter == null || year.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
		    DefaultMutableTreeNode yearNode = new DefaultMutableTreeNode(year);
		    // If node was selected before refreshing...
		    if (objectsSelected.contains(yearNode.getUserObject())) {
		        nodesToSelect.add(yearNode);
		    }
		    // If node was expanded before refreshing...
		    if (objectsExpanded.contains(yearNode.getUserObject())) {
		        nodesToExpand.add(yearNode);
		    }
		    List<String> artistNamesList = new ArrayList<String>(year.getArtistSet());
		    Collections.sort(artistNamesList);
		    Map<String, IArtist> yearArtists = year.getArtistObjects();
		    for (int j = 0; j < artistNamesList.size(); j++) {
		        buildArtistNode(objectsSelected, objectsExpanded,
						nodesToSelect, nodesToExpand, yearNode,
						artistNamesList, yearArtists, j);
		    }
		    root.add(yearNode);
		}
	}

	/**
	 * @param objectsSelected
	 * @param objectsExpanded
	 * @param nodesToSelect
	 * @param nodesToExpand
	 * @param yearNode
	 * @param artistNamesList
	 * @param yearArtists
	 * @param j
	 */
	private void buildArtistNode(
			List<ITreeObject<? extends IAudioObject>> objectsSelected,
			List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			List<DefaultMutableTreeNode> nodesToSelect,
			List<DefaultMutableTreeNode> nodesToExpand,
			DefaultMutableTreeNode yearNode, List<String> artistNamesList,
			Map<String, IArtist> yearArtists, int j) {
		IArtist artist = yearArtists.get(artistNamesList.get(j));
		DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
		List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
		Collections.sort(albumNamesList);
		for (int k = 0; k < albumNamesList.size(); k++) {
		    buildAlbumNode(objectsSelected, objectsExpanded, nodesToSelect,
					nodesToExpand, yearNode, artist, artistNode,
					albumNamesList, k);
		}
	}

	/**
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
	private void buildAlbumNode(
			List<ITreeObject<? extends IAudioObject>> objectsSelected,
			List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			List<DefaultMutableTreeNode> nodesToSelect,
			List<DefaultMutableTreeNode> nodesToExpand,
			DefaultMutableTreeNode yearNode, IArtist artist,
			DefaultMutableTreeNode artistNode, List<String> albumNamesList,
			int k) {
		IAlbum album = artist.getAlbum(albumNamesList.get(k));
		DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
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
		if (objectsExpanded.contains(artistNode.getUserObject()) && objectsExpanded.contains(((DefaultMutableTreeNode) artistNode.getParent()).getUserObject())) {
		    nodesToExpand.add(artistNode);
		}
	}

    @Override
    public void selectAudioObject(JTree tree, IAudioObject audioObject) {
    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
    	
    	@SuppressWarnings({ "unchecked", "rawtypes" })
		ChainOfSelectors chain = new ChainOfSelectors((AudioObjectSelector) new YearAudioObjectSelector(), 
    												  (AudioObjectSelector) new ArtistAudioObjectSelector(), 
    												  (AudioObjectSelector) new AlbumAudioObjectSelector());
    	DefaultMutableTreeNode albumNode = chain.selectAudioObject(rootNode, audioObject);
    	
    	if (albumNode != null) {
    		TreePath treePath = new TreePath(albumNode.getPath());
    		tree.setSelectionPath(treePath);
    		tree.scrollPathToVisible(treePath);
    	}
    }

    @Override
    public void selectArtist(JTree tree, String artistName) {
    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
    	List<TreePath> treePathList = new ArrayList<TreePath>();
    	TreePath[] treePaths = null;
    	@SuppressWarnings("unchecked")
    	Enumeration<DefaultMutableTreeNode> years = rootNode.children();
    	ArtistByNameAudioObjectSelector selector = new ArtistByNameAudioObjectSelector();
    	while (years.hasMoreElements()){
    		DefaultMutableTreeNode yearNode = years.nextElement();
    		DefaultMutableTreeNode artistNode = selector.getNodeRepresentingAudioObject(yearNode, artistName);
    		if (artistNode != null) {
				TreePath treePath = new TreePath(artistNode.getPath()); 
				treePathList.add(treePath);
				tree.expandPath(treePath);
    		}
    	}

    	if (!treePathList.isEmpty()) {
    		treePaths = new TreePath[treePathList.size()];
    		treePaths = (TreePath[]) treePathList.toArray(treePaths);
    		tree.setSelectionPaths(treePaths);
    		tree.scrollPathToVisible(treePaths[0]);
    	}
    }
}
