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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.navigator.RepositoryNavigationView;
import net.sourceforge.atunes.model.ArtistViewMode;
import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.TextTagAttribute;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateNavigation implements IStateNavigation {

	/**
	 * Component responsible of store state
	 */
	private IStateStore stateStore;

	/**
	 * Sets state store
	 * 
	 * @param store
	 */
	public void setStateStore(final IStateStore store) {
		this.stateStore = store;
	}

	@Override
	public boolean isShowNavigationTable() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.SHOW_NAVIGATION_TABLE, true);
	}

	@Override
	public void setShowNavigationTable(final boolean showNavigationTable) {
		this.stateStore.storePreference(Preferences.SHOW_NAVIGATION_TABLE,
				showNavigationTable);
	}

	@Override
	public String getNavigationView() {
		return (String) this.stateStore.retrievePreference(
				Preferences.NAVIGATION_VIEW,
				RepositoryNavigationView.class.getName());
	}

	@Override
	public void setNavigationView(final String navigationView) {
		this.stateStore.storePreference(Preferences.NAVIGATION_VIEW,
				navigationView);
	}

	@Override
	public ViewMode getViewMode() {
		return (ViewMode) this.stateStore.retrievePreference(
				Preferences.VIEW_MODE, ViewMode.ARTIST);
	}

	@Override
	public void setViewMode(final ViewMode viewMode) {
		this.stateStore.storePreference(Preferences.VIEW_MODE, viewMode);
	}

	@Override
	public boolean isShowFavoritesInNavigator() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.SHOW_FAVORITES_IN_NAVIGATOR, true);
	}

	@Override
	public void setShowFavoritesInNavigator(
			final boolean showFavoritesInNavigator) {
		this.stateStore.storePreference(
				Preferences.SHOW_FAVORITES_IN_NAVIGATOR,
				showFavoritesInNavigator);
	}

	@Override
	public boolean isUseSmartTagViewSorting() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.USE_SMART_TAG_VIEW_SORTING, false);
	}

	@Override
	public void setUseSmartTagViewSorting(final boolean useSmartTagViewSorting) {
		this.stateStore.storePreference(Preferences.USE_SMART_TAG_VIEW_SORTING,
				useSmartTagViewSorting);
	}

	@Override
	public boolean isUsePersonNamesArtistTagViewSorting() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.USE_PERSON_NAMES_ARTIST_TAG_SORTING, false);
	}

	@Override
	public void setUsePersonNamesArtistTagViewSorting(
			final boolean usePersonNamesArtistTagViewSorting) {
		this.stateStore.storePreference(
				Preferences.USE_PERSON_NAMES_ARTIST_TAG_SORTING,
				usePersonNamesArtistTagViewSorting);
	}

	@Override
	public boolean isShowExtendedTooltip() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.SHOW_EXTENDED_TOOLTIP, true);
	}

	@Override
	public void setShowExtendedTooltip(final boolean showExtendedTooltip) {
		this.stateStore.storePreference(Preferences.SHOW_EXTENDED_TOOLTIP,
				showExtendedTooltip);
	}

	@Override
	public int getExtendedTooltipDelay() {
		return (Integer) this.stateStore.retrievePreference(
				Preferences.EXTENDED_TOOLTIP_DELAY, 1);
	}

	@Override
	public void setExtendedTooltipDelay(final int extendedTooltipDelay) {
		this.stateStore.storePreference(Preferences.EXTENDED_TOOLTIP_DELAY,
				extendedTooltipDelay);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getNavigatorColumns() {
		Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore
				.retrievePreference(Preferences.NAVIGATOR_COLUMNS, null);
		return map != null ? Collections.unmodifiableMap(map) : null;
	}

	@Override
	public void setNavigatorColumns(
			final Map<String, ColumnBean> navigatorColumns) {
		if (getNavigatorColumns() == null
				|| !getNavigatorColumns().equals(navigatorColumns)) {
			this.stateStore.storePreference(Preferences.NAVIGATOR_COLUMNS,
					navigatorColumns);
		}
	}

	@Override
	public boolean isHighlightIncompleteTagElements() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS, true);
	}

	@Override
	public void setHighlightIncompleteTagElements(
			final boolean highlightIncompleteTagElements) {
		this.stateStore.storePreference(
				Preferences.HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS,
				highlightIncompleteTagElements);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TextTagAttribute> getHighlightIncompleteTagFoldersAttributes() {
		List<TextTagAttribute> defaultTagAttributes = new ArrayList<TextTagAttribute>();
		defaultTagAttributes.add(TextTagAttribute.ARTIST);
		defaultTagAttributes.add(TextTagAttribute.ALBUM);
		return (List<TextTagAttribute>) this.stateStore.retrievePreference(
				Preferences.HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES,
				defaultTagAttributes);
	}

	@Override
	public void setHighlightIncompleteTagFoldersAttributes(
			final List<TextTagAttribute> highlightIncompleteTagFoldersAttributes) {
		this.stateStore.storePreference(
				Preferences.HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES,
				highlightIncompleteTagFoldersAttributes);
	}

	@Override
	public boolean isShowNavigationTree() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.SHOW_NAVIGATION_TREE, true);
	}

	@Override
	public void setShowNavigationTree(final boolean showNavigationTree) {
		if (isShowNavigationTree() != showNavigationTree) {
			this.stateStore.storePreference(Preferences.SHOW_NAVIGATION_TREE,
					showNavigationTree);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, ColumnBean>> getCustomNavigatorColumns() {
		// This map is not unmodifiable
		return (Map<String, Map<String, ColumnBean>>) this.stateStore
				.retrievePreference(Preferences.CUSTOM_NAVIGATOR_COLUMNS, null);
	}

	@Override
	public void setCustomNavigatorColumns(
			final Map<String, Map<String, ColumnBean>> customNavigatorColumns) {
		this.stateStore.storePreference(Preferences.CUSTOM_NAVIGATOR_COLUMNS,
				customNavigatorColumns);
	}

	@Override
	public void setArtistViewMode(final ArtistViewMode artistViewMode) {
		this.stateStore.storePreference(Preferences.ARTIST_VIEW_MODE,
				artistViewMode);

	}

	@Override
	public ArtistViewMode getArtistViewMode() {
		return (ArtistViewMode) this.stateStore.retrievePreference(
				Preferences.ARTIST_VIEW_MODE, ArtistViewMode.BOTH);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}

}
