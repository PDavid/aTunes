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

import java.util.Map;

/**
 * State of play list
 * 
 * @author alex
 * 
 */
public interface IStatePlaylist extends IState {

	/**
	 * Autoscroll playlist
	 * 
	 * @return
	 */
	public boolean isAutoScrollPlayListEnabled();

	/**
	 * Autoscroll playlist
	 * 
	 * @param autoScrollPlayListEnabled
	 */
	public void setAutoScrollPlayListEnabled(boolean autoScrollPlayListEnabled);

	/**
	 * Playlist columns
	 * 
	 * @return
	 */
	public Map<String, ColumnBean> getColumns();

	/**
	 * Playlist columns
	 * 
	 * @param columns
	 */
	public void setColumns(Map<String, ColumnBean> columns);

	/**
	 * Path for load playlists
	 * 
	 * @return
	 */
	public String getLoadPlaylistPath();

	/**
	 * Path for load playlists
	 * 
	 * @param loadPlaylistPath
	 */
	public void setLoadPlaylistPath(String loadPlaylistPath);

	/**
	 * Path for save playlists
	 * 
	 * @return
	 */
	public String getSavePlaylistPath();

	/**
	 * Path for save playlists
	 * 
	 * @param savePlaylistPath
	 */
	public void setSavePlaylistPath(String savePlaylistPath);

	/**
	 * Stop player when switching playlist
	 * 
	 * @return
	 */
	public boolean isStopPlayerOnPlayListSwitch();

	/**
	 * Stop player when switching playlist
	 * 
	 * @param stopPlayerOnPlayListSwitch
	 */
	public void setStopPlayerOnPlayListSwitch(boolean stopPlayerOnPlayListSwitch);

	/**
	 * Stop player when clearing playlist
	 * 
	 * @return
	 */
	public boolean isStopPlayerOnPlayListClear();

	/**
	 * Stop player when clearing playlist
	 * 
	 * @param stopPlayerOnPlayListClear
	 */
	public void setStopPlayerOnPlayListClear(boolean stopPlayerOnPlayListClear);

	/**
	 * If true shows a combo box with play lists instead of buttons
	 * 
	 * @param showPlayListSelectorComboBox
	 */
	public void setShowPlayListSelectorComboBox(
			boolean showPlayListSelectorComboBox);

	/**
	 * @return If true shows a combo box with play lists instead of buttons
	 */
	public boolean isShowPlayListSelectorComboBox();
}
