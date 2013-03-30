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

package net.sourceforge.atunes.model;

import java.util.List;
import java.util.Map;

/**
 * State of navigator
 * 
 * @author alex
 * 
 */
public interface IStateNavigation extends IState {

	/**
	 * Show navigation tree
	 * 
	 * @return
	 */
	boolean isShowNavigationTree();

	/**
	 * Show navigation ree
	 * 
	 * @param showNavigationTree
	 */
	void setShowNavigationTree(boolean showNavigationTree);

	/**
	 * Show navigation table
	 * 
	 * @return
	 */
	boolean isShowNavigationTable();

	/**
	 * Show navigation table
	 * 
	 * @param showNavigationTable
	 */
	void setShowNavigationTable(boolean showNavigationTable);

	/**
	 * Show navigation table filter
	 * 
	 * @return
	 */
	boolean isShowNavigationTableFilter();

	/**
	 * @param showNavigationTableFilter
	 */
	void setShowNavigationTableFilter(boolean showNavigationTableFilter);

	/**
	 * Navigation view
	 * 
	 * @return
	 */
	String getNavigationView();

	/**
	 * Navigation view
	 * 
	 * @param navigationView
	 */
	void setNavigationView(String navigationView);

	/**
	 * View mode of navigator
	 * 
	 * @return
	 */
	ViewMode getViewMode();

	/**
	 * View mode of navigator
	 * 
	 * @param viewMode
	 */
	void setViewMode(ViewMode viewMode);

	/**
	 * Show favorites in navigator
	 * 
	 * @return
	 */
	boolean isShowFavoritesInNavigator();

	/**
	 * Show favorites in navigator
	 * 
	 * @param showFavoritesInNavigator
	 */
	void setShowFavoritesInNavigator(boolean showFavoritesInNavigator);

	/**
	 * Use smart tag view sorting
	 * 
	 * @return
	 */
	boolean isUseSmartTagViewSorting();

	/**
	 * Use smart tag view sorting
	 * 
	 * @param useSmartTagViewSorting
	 */
	void setUseSmartTagViewSorting(boolean useSmartTagViewSorting);

	/**
	 * Use person name artist sorting
	 * 
	 * @return
	 */
	boolean isUsePersonNamesArtistTagViewSorting();

	/**
	 * Use person name artist sorting
	 * 
	 * @param usePersonNamesArtistTagViewSorting
	 */
	void setUsePersonNamesArtistTagViewSorting(
			boolean usePersonNamesArtistTagViewSorting);

	/**
	 * Show extended tooltip
	 * 
	 * @return
	 */
	boolean isShowExtendedTooltip();

	/**
	 * Show extended tooltip
	 * 
	 * @param showExtendedTooltip
	 */
	void setShowExtendedTooltip(boolean showExtendedTooltip);

	/**
	 * Extended tooltip delay
	 * 
	 * @return
	 */
	int getExtendedTooltipDelay();

	/**
	 * Extended tooltip delay
	 * 
	 * @param extendedTooltipDelay
	 */
	void setExtendedTooltipDelay(int extendedTooltipDelay);

	/**
	 * Navigator columns
	 * 
	 * @return
	 */
	Map<String, ColumnBean> getNavigatorColumns();

	/**
	 * Navigator columns
	 * 
	 * @param navigatorColumns
	 */
	void setNavigatorColumns(Map<String, ColumnBean> navigatorColumns);

	/**
	 * Highlight incomplete tag elements
	 * 
	 * @return
	 */
	boolean isHighlightIncompleteTagElements();

	/**
	 * Highlight incomplete tag elements
	 * 
	 * @param highlightIncompleteTagElements
	 */
	void setHighlightIncompleteTagElements(
			boolean highlightIncompleteTagElements);

	/**
	 * Attributes used to highlight incomplete elements
	 * 
	 * @return
	 */
	List<TextTagAttribute> getHighlightIncompleteTagFoldersAttributes();

	/**
	 * Attributes used to highlight incomplete elements
	 * 
	 * @param highlightIncompleteTagFoldersAttributes
	 */
	void setHighlightIncompleteTagFoldersAttributes(
			List<TextTagAttribute> highlightIncompleteTagFoldersAttributes);

	/**
	 * Show navigator
	 * 
	 * @return
	 */
	boolean isShowNavigator();

	/**
	 * Show navigator
	 * 
	 * @param showNavigator
	 */
	void setShowNavigator(boolean showNavigator);

	/**
	 * Custom navigator columns
	 * 
	 * @return
	 */
	Map<String, Map<String, ColumnBean>> getCustomNavigatorColumns();

	/**
	 * Custom navigator columns
	 * 
	 * @param customNavigatorColumns
	 */
	void setCustomNavigatorColumns(
			Map<String, Map<String, ColumnBean>> customNavigatorColumns);

	/**
	 * Artist view mode
	 * 
	 * @return
	 */
	ArtistViewMode getArtistViewMode();

	/**
	 * Artist view mode
	 * 
	 * @param artistViewMode
	 */
	void setArtistViewMode(ArtistViewMode artistViewMode);

}
