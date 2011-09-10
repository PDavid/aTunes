/*
 * aTunes 2.1.0-SNAPSHOT
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

import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Year ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class YearTreeGenerator implements ITreeGenerator {

	/**
	 * Builds tree
	 * @param state
	 * @param rootTextKey
	 * @param view
	 * @param structure
	 * @param currentFilter
	 * @param root
	 * @param treeModel
	 * @param objectsSelected
	 * @param objectsExpanded
	 */
    public void buildTree(IState state, String rootTextKey, AbstractNavigationView view, Map<String, ?> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject<? extends IAudioObject>> objectsSelected, List<TreeObject<? extends IAudioObject>> objectsExpanded) {
        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        // Refresh nodes
        root.setUserObject(I18nUtils.getString(rootTextKey));
        root.removeAllChildren();
        List<String> yearNamesList = new ArrayList<String>(structure.keySet());
        Collections.sort(yearNamesList, view.getIntegerComparator());

        for (int i = 0; i < yearNamesList.size(); i++) {
            Year year = (Year) structure.get(yearNamesList.get(i));
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
                Map<String, Artist> yearArtists = year.getArtistObjects();
                for (int j = 0; j < artistNamesList.size(); j++) {
                    Artist artist = yearArtists.get(artistNamesList.get(j));
                    DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
                    List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
                    Collections.sort(albumNamesList);
                    for (int k = 0; k < albumNamesList.size(); k++) {
                        Album album = artist.getAlbum(albumNamesList.get(k));
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
                }
                root.add(yearNode);
            }
        }

        // Reload the tree to refresh content
        treeModel.reload();

        // Expand nodes
        AbstractNavigationView.expandNodes(view.getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        AbstractNavigationView.selectNodes(view.getTree(), nodesToSelect);
    }

    @Override
    public void selectAudioObject(JTree tree, IAudioObject audioObject) {

    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
    	TreePath treePath = null;

    	@SuppressWarnings("unchecked")
    	Enumeration<DefaultMutableTreeNode> years = rootNode.children();

    	while (years.hasMoreElements()){
    		DefaultMutableTreeNode yearNode = years.nextElement();
    		Year year = (Year) yearNode.getUserObject();
    		if (year.getName().equals(audioObject.getYear())){
    			@SuppressWarnings("unchecked")
    			Enumeration<DefaultMutableTreeNode> artists = yearNode.children();

    			while (artists.hasMoreElements()){
    				DefaultMutableTreeNode artistNode = artists.nextElement();
    				Artist artist = (Artist) artistNode.getUserObject();
    				if (artist.getName().equals(audioObject.getArtist())){
    					@SuppressWarnings("unchecked")
    					Enumeration<DefaultMutableTreeNode> albums = artistNode.children();
    					while (albums.hasMoreElements()){
    						DefaultMutableTreeNode albumNode = albums.nextElement();
    						Album album = (Album) albumNode.getUserObject();
    						if (album.getName().equals(audioObject.getAlbum())){
    							treePath = new TreePath(albumNode.getPath());
    							break;
    						}
    					}
    					break;
    				}
    			}
    			break;
    		}
    	}

    	tree.setSelectionPath(treePath);
    	tree.scrollPathToVisible(treePath);
    }

    @Override
    public void selectArtist(JTree tree, String artistName) {
    	DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
    	List<TreePath> treePathList = new ArrayList<TreePath>();
    	TreePath[] treePaths = null;

    	@SuppressWarnings("unchecked")
    	Enumeration<DefaultMutableTreeNode> years = rootNode.children();

    	while (years.hasMoreElements()){

    		DefaultMutableTreeNode yearNode = years.nextElement();

    		@SuppressWarnings("unchecked")
    		Enumeration<DefaultMutableTreeNode> artists = yearNode.children();

    		while (artists.hasMoreElements()){
    			DefaultMutableTreeNode artistNode = artists.nextElement();
    			Artist artist = (Artist) artistNode.getUserObject();
    			if (artist.getName().equals(artistName)){
    				TreePath treePath = new TreePath(artistNode.getPath()); 
    				treePathList.add(treePath);
    				tree.expandPath(treePath);
    				break;
    			}
    		}
    	}

    	treePaths = new TreePath[treePathList.size()];
    	treePaths = (TreePath[]) treePathList.toArray(treePaths);

    	tree.setSelectionPaths(treePaths);
    	tree.scrollPathToVisible(treePaths[0]);
    }


}
