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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPopupMenu;

import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Navigation view to show favorites
 * 
 * @author alex
 * 
 */
public final class FavoritesNavigationView extends AbstractNavigationView {

	private static final String ALBUMS = "ALBUMS";

	private static final String ARTISTS = "ARTISTS";

	private NavigationTree favoritesTree;

	private IFavoritesHandler favoritesHandler;

	private IRepositoryHandler repositoryHandler;

	private IIconFactory favoriteIcon;

	private JPopupMenu favoritesNavigationViewTreePopupMenu;

	private JPopupMenu favoritesNavigationViewTablePopupMenu;

	/**
	 * @param favoritesNavigationViewTablePopupMenu
	 */
	public void setFavoritesNavigationViewTablePopupMenu(
			final JPopupMenu favoritesNavigationViewTablePopupMenu) {
		this.favoritesNavigationViewTablePopupMenu = favoritesNavigationViewTablePopupMenu;
	}

	/**
	 * @param favoritesNavigationViewTreePopupMenu
	 */
	public void setFavoritesNavigationViewTreePopupMenu(
			final JPopupMenu favoritesNavigationViewTreePopupMenu) {
		this.favoritesNavigationViewTreePopupMenu = favoritesNavigationViewTreePopupMenu;
	}

	/**
	 * @param favoriteIcon
	 */
	public void setFavoriteIcon(final IIconFactory favoriteIcon) {
		this.favoriteIcon = favoriteIcon;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	public IColorMutableImageIcon getIcon() {
		return favoriteIcon.getColorMutableIcon();
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
	public NavigationTree getTree() {
		if (favoritesTree == null) {
			favoritesTree = new NavigationTree(
					I18nUtils.getString("FAVORITES"), getTreeRenderer());
		}
		return favoritesTree;
	}

	@Override
	public JPopupMenu getTreePopupMenu() {
		return favoritesNavigationViewTreePopupMenu;
	}

	@Override
	public JPopupMenu getTablePopupMenu() {
		return favoritesNavigationViewTablePopupMenu;
	}

	@Override
	public Map<String, ?> getViewData(final ViewMode viewMode) {
		Map<String, Map<?, ?>> data = new HashMap<String, Map<?, ?>>();
		data.put(ARTISTS, favoritesHandler.getFavoriteArtistsInfo());
		data.put(ALBUMS, favoritesHandler.getFavoriteAlbumsInfo());
		return data;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void refreshTree(final ViewMode viewMode, final String treeFilter) {
		// Get objects selected before refreshing tree
		List<ITreeObject<? extends IAudioObject>> objectsSelected = getSelectedTreeObjects();
		// Get objects expanded before refreshing tree
		List<ITreeObject<? extends IAudioObject>> objectsExpanded = getTreeObjectsExpanded(getTree());

		// Nodes to be selected after refresh
		List<ITreeNode> nodesToSelect = new ArrayList<ITreeNode>();
		// Nodes to be expanded after refresh
		List<ITreeNode> nodesToExpand = new ArrayList<ITreeNode>();

		Map<String, ?> data = getViewData(viewMode);

		getTree().setRoot(I18nUtils.getString("FAVORITES"));

		ITreeNode artistsNode = getTree().createNode(
				I18nUtils.getString(ARTISTS));
		nodesToExpand.add(artistsNode);
		addArtistNodes(artistsNode, treeFilter,
				(Map<String, IArtist>) data.get(ARTISTS), objectsSelected,
				objectsExpanded, nodesToSelect, nodesToExpand);
		getTree().addNode(artistsNode);

		ITreeNode albumsNode = getTree()
				.createNode(I18nUtils.getString(ALBUMS));
		nodesToExpand.add(albumsNode);
		addAlbumNodes(albumsNode, treeFilter,
				(Map<String, IAlbum>) data.get(ALBUMS), objectsSelected,
				objectsExpanded, nodesToSelect, nodesToExpand);
		getTree().addNode(albumsNode);

		ITreeNode songsNode = getTree()
				.createNode(I18nUtils.getString("SONGS"));
		getTree().addNode(songsNode);

		getTree().reload();

		// Expand nodes
		getTree().expandNodes(nodesToExpand);

		// Once tree has been refreshed, select previously selected nodes
		getTree().selectNodes(nodesToSelect);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ILocalAudioObject> getAudioObjectForTreeNode(
			final ITreeNode node, final ViewMode viewMode,
			final String treeFilter, String tableFilter) {
		List<ILocalAudioObject> songs = null;

		if (node.isRoot()) {
			songs = new ArrayList<ILocalAudioObject>();
			songs.addAll(repositoryHandler
					.getAudioFilesForArtists(favoritesHandler
							.getFavoriteArtistsInfo()));
			songs.addAll(repositoryHandler
					.getAudioFilesForAlbums(favoritesHandler
							.getFavoriteAlbumsInfo()));
			songs.addAll(favoritesHandler.getFavoriteSongsInfo().values());
		} else {
			if (node.getUserObject() instanceof ITreeObject) {
				songs = ((ITreeObject<ILocalAudioObject>) node.getUserObject())
						.getAudioObjects();
			} else {
				songs = new ArrayList<ILocalAudioObject>();
				if (node.getUserObject().toString()
						.equals(I18nUtils.getString(ARTISTS))) {
					songs.addAll(repositoryHandler
							.getAudioFilesForArtists(favoritesHandler
									.getFavoriteArtistsInfo()));
				} else if (node.getUserObject().toString()
						.equals(I18nUtils.getString(ALBUMS))) {
					songs.addAll(repositoryHandler
							.getAudioFilesForAlbums(favoritesHandler
									.getFavoriteAlbumsInfo()));
				} else {
					songs.addAll(new ArrayList<ILocalAudioObject>(
							favoritesHandler.getFavoriteSongsInfo().values()));
				}
			}
		}
		return songs;
	}

	/**
	 * Adds the album nodes.
	 * 
	 * @param root
	 * @param currentFilter
	 * @param albums
	 * @param objectsSelected
	 * @param objectsExpanded
	 * @param nodesToSelect
	 * @param nodesToExpand
	 */
	private void addAlbumNodes(final ITreeNode root,
			final String currentFilter, final Map<String, IAlbum> albums,
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
				ITreeNode albumNode = getTree().createNode(album);

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
	 * @param currentFilter
	 * @param artists
	 * @param objectsSelected
	 * @param objectsExpanded
	 * @param nodesToSelect
	 * @param nodesToExpand
	 */
	private void addArtistNodes(final ITreeNode root,
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
				ITreeNode artistNode = getTree().createNode(artist);

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
				addAlbumNodes(artistNode, null, artist.getAlbums(),
						objectsSelected, objectsExpanded, nodesToSelect,
						nodesToExpand);

				root.add(artistNode);
			}
		}
	}

	@Override
	public boolean isUseDefaultNavigatorColumnSet() {
		return true;
	}

	@Override
	public IColumnSet getCustomColumnSet() {
		// Returns null since uses default column set
		return null;
	}

	@Override
	public boolean isViewModeSupported() {
		return false;
	}

	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(final IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}
}
