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
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds an Artist ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class ArtistTreeGenerator implements ITreeGenerator {

	private INavigationViewSorter artistSorter;
	
	private INavigationViewSorter albumSorter;
	
	/**
	 * @param albumSorter
	 */
	public void setAlbumSorter(INavigationViewSorter albumSorter) {
		this.albumSorter = albumSorter;
	}
	
	/**
	 * @param artistSorter
	 */
	public void setArtistSorter(INavigationViewSorter artistSorter) {
		this.artistSorter = artistSorter;
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
    public void buildTree(String rootTextKey, INavigationView view, Map<String, ?> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<ITreeObject<? extends IAudioObject>> objectsSelected, List<ITreeObject<? extends IAudioObject>> objectsExpanded) {
        // Set root
        root.setUserObject(I18nUtils.getString(rootTextKey));
        root.removeAllChildren();

        List<String> artistNamesList = new ArrayList<String>(structure.keySet());
        artistSorter.sort(artistNamesList);
        
        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        for (String artistName : artistNamesList) {
            buildArtistNode(structure, currentFilter, root, objectsSelected, objectsExpanded, nodesToSelect, nodesToExpand, artistName);
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
	 * @param artistName
	 */
	private void buildArtistNode(Map<String, ?> structure,
			String currentFilter, DefaultMutableTreeNode root,
			List<ITreeObject<? extends IAudioObject>> objectsSelected,
			List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			List<DefaultMutableTreeNode> nodesToSelect,
			List<DefaultMutableTreeNode> nodesToExpand, String artistName) {
		
		IArtist artist = (IArtist) structure.get(artistName);
		DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
		List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
		albumSorter.sort(albumNamesList);
		if (currentFilter == null || artist.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
		    for (String albumName : albumNamesList) {
		        DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(artist.getAlbum(albumName));
		        artistNode.add(albumNode);
		        // If node was selected before refreshing...
		        if (objectsSelected.contains(albumNode.getUserObject())) {
		            nodesToSelect.add(albumNode);
		        }
		    }
		    root.add(artistNode);
		    // Reload causes very important lag on large collections and if it is not used
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

	@Override
	public void selectAudioObject(JTree tree, IAudioObject audioObject) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
		
		DefaultMutableTreeNode artistNode = new ArtistAudioObjectSelector().getNodeRepresentingAudioObject(rootNode, audioObject);
		if (artistNode != null) {
			DefaultMutableTreeNode albumNode = new AlbumAudioObjectSelector().getNodeRepresentingAudioObject(artistNode, audioObject);
			if (albumNode != null) {
				TreePath treePath = new TreePath(albumNode.getPath());
				tree.setSelectionPath(treePath);
				tree.scrollPathToVisible(treePath);
			}
		}
	}

    @Override
	public void selectArtist(JTree tree, String artistName) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();

		DefaultMutableTreeNode artistNode = new ArtistByNameAudioObjectSelector().getNodeRepresentingAudioObject(rootNode, artistName);
		if (artistNode != null) {
			TreePath treePath = new TreePath(artistNode.getPath());
			tree.setSelectionPath(treePath);
			tree.scrollPathToVisible(treePath);
			tree.expandPath(treePath);
		}
	}
}
