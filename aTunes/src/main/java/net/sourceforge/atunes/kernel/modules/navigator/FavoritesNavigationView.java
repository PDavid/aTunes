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

import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.gui.images.FavoriteImageIcon;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.CopyToDeviceAction;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.ExtractPictureAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromFavoritesAction;
import net.sourceforge.atunes.kernel.actions.RenameAudioFileInNavigationTableAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAtAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

public final class FavoritesNavigationView extends AbstractNavigationView {

    private JTree favoritesTree;

    /** The favorite tree menu. */
    private JPopupMenu favoriteTreeMenu;

    /** The favorite table menu. */
    private JPopupMenu favoriteTableMenu;

    public FavoritesNavigationView(IState state) {
    	super(state);
	}
    
    @Override
    public ColorMutableImageIcon getIcon() {
        return new ColorMutableImageIcon() {
			
			@Override
			public ImageIcon getIcon(Paint paint) {
				return FavoriteImageIcon.getIcon(paint);
			}
		};
    }

    @Override
    public String getTitle() {
        return I18nUtils.getString("FAVORITES");
    }

    @Override
    public String getTooltip() {
        return I18nUtils.getString("FAVORITES_TAB_TOOLTIP");
    }

    @Override
    public JTree getTree() {
        if (favoritesTree == null) {
            favoritesTree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("FAVORITES"))));
            favoritesTree.setToggleClickCount(0);
            favoritesTree.setCellRenderer(getTreeRenderer());
        }
        return favoritesTree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (favoriteTreeMenu == null) {
            favoriteTreeMenu = new JPopupMenu();
            favoriteTreeMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            favoriteTreeMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(getMenuItemForAction(OpenFolderFromNavigatorAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(new EditTagMenu(false, this));
            favoriteTreeMenu.add(Actions.getAction(EditTitlesAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(Actions.getAction(RemoveFromDiskAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(getMenuItemForAction(CopyToDeviceAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(Actions.getAction(RemoveFromFavoritesAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(Actions.getAction(SearchArtistAction.class));
            favoriteTreeMenu.add(Actions.getAction(SearchArtistAtAction.class));
        }
        return favoriteTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (favoriteTableMenu == null) {
            favoriteTableMenu = new JPopupMenu();
            favoriteTableMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            favoriteTableMenu.add(getMenuItemForAction(AddToPlayListAfterCurrentAudioObjectAction.class));
            favoriteTableMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            favoriteTableMenu.add(Actions.getAction(PlayNowAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(Actions.getAction(ShowNavigatorTableItemInfoAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(getMenuItemForAction(OpenFolderFromNavigatorAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(new EditTagMenu(false, this));
            favoriteTableMenu.add(getMenuItemForAction(ExtractPictureAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(Actions.getAction(RemoveFromDiskAction.class));
            favoriteTableMenu.add(Actions.getAction(RenameAudioFileInNavigationTableAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(getMenuItemForAction(CopyToDeviceAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(Actions.getAction(RemoveFromFavoritesAction.class));
        }
        return favoriteTableMenu;
    }

    @Override
    public Map<String, ?> getViewData(ViewMode viewMode) {
        Map<String, Map<?, ?>> data = new HashMap<String, Map<?, ?>>();
        data.put("ARTISTS", FavoritesHandler.getInstance().getFavoriteArtistsInfo());
        data.put("ALBUMS", FavoritesHandler.getInstance().getFavoriteAlbumsInfo());
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing ", this.getClass().getName());

        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Get objects selected before refreshing tree
        List<TreeObject<? extends AudioObject>> objectsSelected = getTreeObjectsSelected(getTree());
        // Get objects expanded before refreshing tree
        List<TreeObject<? extends AudioObject>> objectsExpanded = getTreeObjectsExpanded(getTree(), root);

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        Map<String, ?> data = getViewData(viewMode);

        root.setUserObject(I18nUtils.getString("FAVORITES"));
        root.removeAllChildren();

        DefaultMutableTreeNode artistsNode = new DefaultMutableTreeNode();
        artistsNode.setUserObject(I18nUtils.getString("ARTISTS"));
        nodesToExpand.add(artistsNode);
        addArtistNodes(artistsNode, treeFilter, (Map<String, Artist>) data.get("ARTISTS"), objectsSelected, objectsExpanded, nodesToSelect, nodesToExpand);
        root.add(artistsNode);

        DefaultMutableTreeNode albumsNode = new DefaultMutableTreeNode();
        albumsNode.setUserObject(I18nUtils.getString("ALBUMS"));
        nodesToExpand.add(albumsNode);
        addAlbumNodes(albumsNode, treeFilter, (Map<String, Album>) data.get("ALBUMS"), objectsSelected, objectsExpanded, nodesToSelect, nodesToExpand);
        root.add(albumsNode);

        DefaultMutableTreeNode songsNode = new DefaultMutableTreeNode();
        songsNode.setUserObject(I18nUtils.getString("SONGS"));
        root.add(songsNode);

        treeModel.reload();

        // Expand nodes
        expandNodes(getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        selectNodes(getTree(), nodesToSelect);
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<LocalAudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<LocalAudioObject> songs = null;

        if (node.isRoot()) {
            songs = new ArrayList<LocalAudioObject>();
            songs.addAll(RepositoryHandler.getInstance().getAudioFilesForArtists(FavoritesHandler.getInstance().getFavoriteArtistsInfo()));
            songs.addAll(RepositoryHandler.getInstance().getAudioFilesForAlbums(FavoritesHandler.getInstance().getFavoriteAlbumsInfo()));
            songs.addAll(FavoritesHandler.getInstance().getFavoriteSongsInfo().values());
        } else {
            if (node.getUserObject() instanceof TreeObject) {
                songs = ((TreeObject<LocalAudioObject>) node.getUserObject()).getAudioObjects();
            } else {
                songs = new ArrayList<LocalAudioObject>();
                if (node.getUserObject().toString().equals(I18nUtils.getString("ARTISTS"))) {
                    songs.addAll(RepositoryHandler.getInstance().getAudioFilesForArtists(FavoritesHandler.getInstance().getFavoriteArtistsInfo()));
                } else if (node.getUserObject().toString().equals(I18nUtils.getString("ALBUMS"))) {
                    songs.addAll(RepositoryHandler.getInstance().getAudioFilesForAlbums(FavoritesHandler.getInstance().getFavoriteAlbumsInfo()));
                } else {
                    songs.addAll(new ArrayList<LocalAudioObject>(FavoritesHandler.getInstance().getFavoriteSongsInfo().values()));
                }
            }
        }
        return songs;
    }

    /**
     * Adds the album nodes.
     * 
     * @param root
     *            the root
     * @param albums
     *            the albums
     */
    private static void addAlbumNodes(DefaultMutableTreeNode root, String currentFilter, Map<String, Album> albums, List<TreeObject<? extends AudioObject>> objectsSelected, List<TreeObject<? extends AudioObject>> objectsExpanded, List<DefaultMutableTreeNode> nodesToSelect, List<DefaultMutableTreeNode> nodesToExpand) {
        List<String> albumsNamesList = new ArrayList<String>(albums.keySet());
        Collections.sort(albumsNamesList);

        for (int i = 0; i < albumsNamesList.size(); i++) {
            Album album = albums.get(albumsNamesList.get(i));
            if (currentFilter == null || album.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);

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

    /**
     * Adds the artist nodes.
     * 
     * @param root
     *            the root
     * @param artists
     *            the artists
     */
    private static void addArtistNodes(DefaultMutableTreeNode root, String currentFilter, Map<String, Artist> artists, List<TreeObject<? extends AudioObject>> objectsSelected, List<TreeObject<? extends AudioObject>> objectsExpanded, List<DefaultMutableTreeNode> nodesToSelect, List<DefaultMutableTreeNode> nodesToExpand) {
        List<String> artistNamesList = new ArrayList<String>(artists.keySet());
        Collections.sort(artistNamesList);

        for (int i = 0; i < artistNamesList.size(); i++) {
            Artist artist = artists.get(artistNamesList.get(i));
            if (currentFilter == null || artist.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);

                // If node was selected before refreshing...
                if (objectsSelected.contains(artistNode.getUserObject())) {
                    nodesToSelect.add(artistNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(artistNode.getUserObject())) {
                    nodesToExpand.add(artistNode);
                }

                // If an artist fits current filter we want to show all albums so put filter to null
                addAlbumNodes(artistNode, null, artist.getAlbums(), objectsSelected, objectsExpanded, nodesToSelect, nodesToExpand);

                root.add(artistNode);
            }
        }
    }

    @Override
    public boolean isUseDefaultNavigatorColumnSet() {
        return true;
    }

    @Override
    public AbstractColumnSet getCustomColumnSet() {
        // Returns null since uses default column set
        return null;
    }

    @Override
    public boolean isViewModeSupported() {
        return false;
    }
}
