/*
 * aTunes 3.1.0
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
	 * Show navigation table
	 * 
	 * @return
	 */
	public boolean isShowNavigationTable();

	/**
	 * Show navigation table
	 * 
	 * @param showNavigationTable
	 */
	public void setShowNavigationTable(boolean showNavigationTable);

	/**
	 * Navigation view
	 * 
	 * @return
	 */
	public String getNavigationView();

	/**
	 * Navigation view
	 * 
	 * @param navigationView
	 */
	public void setNavigationView(String navigationView);

	/**
	 * View mode of navigator
	 * 
	 * @return
	 */
	public ViewMode getViewMode();

	/**
	 * View mode of navigator
	 * 
	 * @param viewMode
	 */
	public void setViewMode(ViewMode viewMode);

	/**
	 * Show favorites in navigator
	 * 
	 * @return
	 */
	public boolean isShowFavoritesInNavigator();

	/**
	 * Show favorites in navigator
	 * 
	 * @param showFavoritesInNavigator
	 */
	public void setShowFavoritesInNavigator(boolean showFavoritesInNavigator);

	/**
	 * Use smart tag view sorting
	 * 
	 * @return
	 */
	public boolean isUseSmartTagViewSorting();

	/**
	 * Use smart tag view sorting
	 * 
	 * @param useSmartTagViewSorting
	 */
	public void setUseSmartTagViewSorting(boolean useSmartTagViewSorting);

	/**
	 * Use person name artist sorting
	 * 
	 * @return
	 */
	public boolean isUsePersonNamesArtistTagViewSorting();

	/**
	 * Use person name artist sorting
	 * 
	 * @param usePersonNamesArtistTagViewSorting
	 */
	public void setUsePersonNamesArtistTagViewSorting(
			boolean usePersonNamesArtistTagViewSorting);

	/**
	 * Show extended tooltip
	 * 
	 * @return
	 */
	public boolean isShowExtendedTooltip();

	/**
	 * Show extended tooltip
	 * 
	 * @param showExtendedTooltip
	 */
	public void setShowExtendedTooltip(boolean showExtendedTooltip);

	/**
	 * Extended tooltip delay
	 * 
	 * @return
	 */
	public int getExtendedTooltipDelay();

	/**
	 * Extended tooltip delay
	 * 
	 * @param extendedTooltipDelay
	 */
	public void setExtendedTooltipDelay(int extendedTooltipDelay);

	/**
	 * Navigator columns
	 * 
	 * @return
	 */
	public Map<String, ColumnBean> getNavigatorColumns();

	/**
	 * Navigator columns
	 * 
	 * @param navigatorColumns
	 */
	public void setNavigatorColumns(Map<String, ColumnBean> navigatorColumns);

	/**
	 * Highlight incomplete tag elements
	 * 
	 * @return
	 */
	public boolean isHighlightIncompleteTagElements();

	/**
	 * Highlight incomplete tag elements
	 * 
	 * @param highlightIncompleteTagElements
	 */
	public void setHighlightIncompleteTagElements(
			boolean highlightIncompleteTagElements);

	/**
	 * Attributes used to highlight incomplete elements
	 * 
	 * @return
	 */
	public List<TextTagAttribute> getHighlightIncompleteTagFoldersAttributes();

	/**
	 * Attributes used to highlight incomplete elements
	 * 
	 * @param highlightIncompleteTagFoldersAttributes
	 */
	public void setHighlightIncompleteTagFoldersAttributes(
			List<TextTagAttribute> highlightIncompleteTagFoldersAttributes);

	/**
	 * Show navigation tree
	 * 
	 * @return
	 */
	public boolean isShowNavigationTree();

	/**
	 * Show navigation tree
	 * 
	 * @param showNavigationTree
	 */
	public void setShowNavigationTree(boolean showNavigationTree);

	/**
	 * Custom navigator columns
	 * 
	 * @return
	 */
	public Map<String, Map<String, ColumnBean>> getCustomNavigatorColumns();

	/**
	 * Custom navigator columns
	 * 
	 * @param customNavigatorColumns
	 */
	public void setCustomNavigatorColumns(
			Map<String, Map<String, ColumnBean>> customNavigatorColumns);

	/**
	 * Artist view mode
	 * 
	 * @return
	 */
	public ArtistViewMode getArtistViewMode();

	/**
	 * Artist view mode
	 * 
	 * @param artistViewMode
	 */
	void setArtistViewMode(ArtistViewMode artistViewMode);

}
