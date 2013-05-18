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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
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
		return this.favoriteIcon.getColorMutableIcon();
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
		if (this.favoritesTree == null) {
			this.favoritesTree = new NavigationTree(
					I18nUtils.getString("FAVORITES"), getTreeRenderer());
		}
		return this.favoritesTree;
	}

	@Override
	public JPopupMenu getTreePopupMenu() {
		return this.favoritesNavigationViewTreePopupMenu;
	}

	@Override
	public JPopupMenu getTablePopupMenu() {
		return this.favoritesNavigationViewTablePopupMenu;
	}

	@Override
	public Map<String, ?> getViewData(final ViewMode viewMode) {
		Map<String, Map<?, ?>> data = new HashMap<String, Map<?, ?>>();
		Map<String, IArtist> artistMap = new HashMap<String, IArtist>();
		for (IArtist artist : this.favoritesHandler.getFavoriteArtists()) {
			artistMap.put(artist.getName(), artist);
		}
		Map<String, IAlbum> albumMap = new HashMap<String, IAlbum>();
		for (IAlbum album : this.favoritesHandler.getFavoriteAlbums()) {
			albumMap.put(album.getName(), album);
		}
		data.put(ARTISTS, artistMap);
		data.put(ALBUMS, albumMap);
		return data;
	}

	@Override
	protected void refreshTree(final ViewMode viewMode, final String treeFilter) {
		// Get objects selected before refreshing tree
		List<ITreeObject<? extends IAudioObject>> objectsSelected = getSelectedTreeObjects();
		// Get objects expanded before refreshing tree
		List<ITreeObject<? extends IAudioObject>> objectsExpanded = getTreeObjectsExpanded(getTree());

		getBeanFactory().getBean(FavoritesTreeGenerator.class).buildTree(
				getTree(), "FAVORITES", this, getViewData(viewMode),
				treeFilter, objectsSelected, objectsExpanded);

		getTree().expandRow(0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IAudioObject> getAudioObjectForTreeNode(final ITreeNode node,
			final ViewMode viewMode, final String treeFilter,
			final String tableFilter) {
		Set<IAudioObject> songs = new HashSet<IAudioObject>();

		if (node.isRoot()) {
			addArtistAudioObjects(songs);
			addAlbumAudioObjects(songs);
			addAudioObjects(songs);
		} else {
			if (node.getUserObject() instanceof ITreeObject) {
				songs.addAll(((ITreeObject<ILocalAudioObject>) node
						.getUserObject()).getAudioObjects());
			} else {
				if (node.getUserObject().toString()
						.equals(I18nUtils.getString(ARTISTS))) {
					addArtistAudioObjects(songs);
				} else if (node.getUserObject().toString()
						.equals(I18nUtils.getString(ALBUMS))) {
					addAlbumAudioObjects(songs);
				} else {
					addAudioObjects(songs);
				}
			}
		}
		return new ArrayList<IAudioObject>(songs);
	}

	private void addArtistAudioObjects(final Set<IAudioObject> songs) {
		songs.addAll(this.repositoryHandler
				.getAudioFilesForArtists(this.favoritesHandler
						.getFavoriteArtists()));
	}

	private void addAlbumAudioObjects(final Set<IAudioObject> songs) {
		songs.addAll(this.repositoryHandler
				.getAudioFilesForAlbums(this.favoritesHandler
						.getFavoriteAlbums()));
	}

	private void addAudioObjects(final Set<IAudioObject> songs) {
		songs.addAll(this.favoritesHandler.getFavoriteSongs());
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

	@Override
	public boolean overlayNeedsToBeVisible() {
		return this.favoritesHandler.getAllFavoriteSongs().isEmpty();
	}

	@Override
	public Action getOverlayAction() {
		return null;
	}

	@Override
	public String getOverlayText() {
		return I18nUtils.getString("NO_FAVORITES_INFORMATION");
	}
}
