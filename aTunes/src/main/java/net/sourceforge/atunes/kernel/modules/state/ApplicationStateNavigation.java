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

	private PreferenceHelper preferenceHelper;

	/**
	 * @param preferenceHelper
	 */
	public void setPreferenceHelper(final PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	@Override
	public boolean isShowNavigationTable() {
		return this.preferenceHelper.getPreference(
				Preferences.SHOW_NAVIGATION_TABLE, Boolean.class, true);
	}

	@Override
	public void setShowNavigationTable(final boolean showNavigationTable) {
		this.preferenceHelper.setPreference(Preferences.SHOW_NAVIGATION_TABLE,
				showNavigationTable);
	}

	@Override
	public boolean isShowNavigationTree() {
		return this.preferenceHelper.getPreference(
				Preferences.SHOW_NAVIGATION_TREE, Boolean.class, true);
	}

	@Override
	public void setShowNavigationTree(final boolean showNavigationTree) {
		this.preferenceHelper.setPreference(Preferences.SHOW_NAVIGATION_TREE,
				showNavigationTree);
	}

	@Override
	public boolean isShowNavigationTableFilter() {
		return this.preferenceHelper.getPreference(
				Preferences.SHOW_NAVIGATION_TABLE_FILTER, Boolean.class, false);
	}

	@Override
	public void setShowNavigationTableFilter(
			final boolean showNavigationTableFilter) {
		this.preferenceHelper.setPreference(
				Preferences.SHOW_NAVIGATION_TABLE_FILTER,
				showNavigationTableFilter);
	}

	@Override
	public String getNavigationView() {
		return this.preferenceHelper.getPreference(Preferences.NAVIGATION_VIEW,
				String.class, RepositoryNavigationView.class.getName());
	}

	@Override
	public void setNavigationView(final String navigationView) {
		this.preferenceHelper.setPreference(Preferences.NAVIGATION_VIEW,
				navigationView);
	}

	@Override
	public ViewMode getViewMode() {
		return this.preferenceHelper.getPreference(Preferences.VIEW_MODE,
				ViewMode.class, ViewMode.ARTIST);
	}

	@Override
	public void setViewMode(final ViewMode viewMode) {
		this.preferenceHelper.setPreference(Preferences.VIEW_MODE, viewMode);
	}

	@Override
	public boolean isShowFavoritesInNavigator() {
		return this.preferenceHelper.getPreference(
				Preferences.SHOW_FAVORITES_IN_NAVIGATOR, Boolean.class, true);
	}

	@Override
	public void setShowFavoritesInNavigator(
			final boolean showFavoritesInNavigator) {
		this.preferenceHelper.setPreference(
				Preferences.SHOW_FAVORITES_IN_NAVIGATOR,
				showFavoritesInNavigator);
	}

	@Override
	public boolean isUseSmartTagViewSorting() {
		return this.preferenceHelper.getPreference(
				Preferences.USE_SMART_TAG_VIEW_SORTING, Boolean.class, false);
	}

	@Override
	public void setUseSmartTagViewSorting(final boolean useSmartTagViewSorting) {
		this.preferenceHelper.setPreference(
				Preferences.USE_SMART_TAG_VIEW_SORTING, useSmartTagViewSorting);
	}

	@Override
	public boolean isUsePersonNamesArtistTagViewSorting() {
		return this.preferenceHelper.getPreference(
				Preferences.USE_PERSON_NAMES_ARTIST_TAG_SORTING, Boolean.class,
				false);
	}

	@Override
	public void setUsePersonNamesArtistTagViewSorting(
			final boolean usePersonNamesArtistTagViewSorting) {
		this.preferenceHelper.setPreference(
				Preferences.USE_PERSON_NAMES_ARTIST_TAG_SORTING,
				usePersonNamesArtistTagViewSorting);
	}

	@Override
	public boolean isShowExtendedTooltip() {
		return this.preferenceHelper.getPreference(
				Preferences.SHOW_EXTENDED_TOOLTIP, Boolean.class, true);
	}

	@Override
	public void setShowExtendedTooltip(final boolean showExtendedTooltip) {
		this.preferenceHelper.setPreference(Preferences.SHOW_EXTENDED_TOOLTIP,
				showExtendedTooltip);
	}

	@Override
	public int getExtendedTooltipDelay() {
		return this.preferenceHelper.getPreference(
				Preferences.EXTENDED_TOOLTIP_DELAY, Integer.class, 1);
	}

	@Override
	public void setExtendedTooltipDelay(final int extendedTooltipDelay) {
		this.preferenceHelper.setPreference(Preferences.EXTENDED_TOOLTIP_DELAY,
				extendedTooltipDelay);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getNavigatorColumns() {
		Map<String, ColumnBean> map = this.preferenceHelper.getPreference(
				Preferences.NAVIGATOR_COLUMNS, Map.class, null);
		return map != null ? Collections.unmodifiableMap(map) : null;
	}

	@Override
	public void setNavigatorColumns(
			final Map<String, ColumnBean> navigatorColumns) {
		if (getNavigatorColumns() == null
				|| !getNavigatorColumns().equals(navigatorColumns)) {
			this.preferenceHelper.setPreference(Preferences.NAVIGATOR_COLUMNS,
					navigatorColumns);
		}
	}

	@Override
	public boolean isHighlightIncompleteTagElements() {
		return this.preferenceHelper.getPreference(
				Preferences.HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS, Boolean.class,
				true);
	}

	@Override
	public void setHighlightIncompleteTagElements(
			final boolean highlightIncompleteTagElements) {
		this.preferenceHelper.setPreference(
				Preferences.HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS,
				highlightIncompleteTagElements);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TextTagAttribute> getHighlightIncompleteTagFoldersAttributes() {
		List<TextTagAttribute> defaultTagAttributes = new ArrayList<TextTagAttribute>();
		defaultTagAttributes.add(TextTagAttribute.ARTIST);
		defaultTagAttributes.add(TextTagAttribute.ALBUM);
		return this.preferenceHelper.getPreference(
				Preferences.HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES,
				List.class, defaultTagAttributes);
	}

	@Override
	public void setHighlightIncompleteTagFoldersAttributes(
			final List<TextTagAttribute> highlightIncompleteTagFoldersAttributes) {
		this.preferenceHelper.setPreference(
				Preferences.HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES,
				highlightIncompleteTagFoldersAttributes);
	}

	@Override
	public boolean isShowNavigator() {
		return this.preferenceHelper.getPreference(Preferences.SHOW_NAVIGATOR,
				Boolean.class, true);
	}

	@Override
	public void setShowNavigator(final boolean showNavigatorTree) {
		if (isShowNavigator() != showNavigatorTree) {
			this.preferenceHelper.setPreference(Preferences.SHOW_NAVIGATOR,
					showNavigatorTree);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, ColumnBean>> getCustomNavigatorColumns() {
		// This map is not unmodifiable
		return this.preferenceHelper.getPreference(
				Preferences.CUSTOM_NAVIGATOR_COLUMNS, Map.class, null);
	}

	@Override
	public void setCustomNavigatorColumns(
			final Map<String, Map<String, ColumnBean>> customNavigatorColumns) {
		this.preferenceHelper.setPreference(
				Preferences.CUSTOM_NAVIGATOR_COLUMNS, customNavigatorColumns);
	}

	@Override
	public void setArtistViewMode(final ArtistViewMode artistViewMode) {
		this.preferenceHelper.setPreference(Preferences.ARTIST_VIEW_MODE,
				artistViewMode);

	}

	@Override
	public ArtistViewMode getArtistViewMode() {
		return this.preferenceHelper.getPreference(
				Preferences.ARTIST_VIEW_MODE, ArtistViewMode.class,
				ArtistViewMode.BOTH);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}

}
