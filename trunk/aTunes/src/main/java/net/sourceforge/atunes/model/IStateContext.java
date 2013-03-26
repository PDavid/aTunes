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

/**
 * @author alex
 * 
 */
public interface IStateContext extends IState {

	/**
	 * Use context information
	 * 
	 * @return
	 */
	public boolean isUseContext();

	/**
	 * Use context information
	 * 
	 * @param useContext
	 */
	public void setUseContext(boolean useContext);

	/**
	 * Selected context tab
	 * 
	 * @return
	 */
	public String getSelectedContextTab();

	/**
	 * Selected context tab
	 * 
	 * @param selectedContextTab
	 */
	public void setSelectedContextTab(String selectedContextTab);

	/**
	 * Save context picture
	 * 
	 * @return
	 */
	public boolean isSaveContextPicture();

	/**
	 * Save context picture
	 * 
	 * @param saveContextPicture
	 */
	public void setSaveContextPicture(boolean saveContextPicture);

	/**
	 * Use last.fm
	 * 
	 * @return
	 */
	public boolean isLastFmEnabled();

	/**
	 * Use last.fm
	 * 
	 * @param lastFmEnabled
	 */
	public void setLastFmEnabled(boolean lastFmEnabled);

	/**
	 * last.fm user
	 * 
	 * @return
	 */
	public String getLastFmUser();

	/**
	 * last.fm user
	 * 
	 * @param lastFmUser
	 */
	public void setLastFmUser(String lastFmUser);

	/**
	 * last.fm password
	 * 
	 * @return
	 */
	public String getLastFmPassword();

	/**
	 * last.fm password
	 * 
	 * @param lastFmPassword
	 */
	public void setLastFmPassword(String lastFmPassword);

	/**
	 * Automatically love songs in last.fm when marking as favorite
	 * 
	 * @return
	 */
	public boolean isAutoLoveFavoriteSong();

	/**
	 * Automatically love songs in last.fm when marking as favorite
	 * 
	 * @param autoLoveFavoriteSong
	 */
	public void setAutoLoveFavoriteSong(boolean autoLoveFavoriteSong);

	/**
	 * Lyrics engines
	 * 
	 * @return
	 */
	public List<ILyricsEngineInfo> getLyricsEnginesInfo();

	/**
	 * Lyrics engines
	 * 
	 * @param lyricsEnginesInfo
	 */
	public void setLyricsEnginesInfo(List<ILyricsEngineInfo> lyricsEnginesInfo);

	/**
	 * Show context albums in a grid
	 * 
	 * @return
	 */
	public boolean isShowContextAlbumsInGrid();

	/**
	 * Show context albums in a grid
	 * 
	 * @param showContextAlbumsInGrid
	 */
	public void setShowContextAlbumsInGrid(boolean showContextAlbumsInGrid);

	/**
	 * Query to launch in browser to search for a similar artist
	 * 
	 * @param query
	 */
	public void setSimilarArtistSearchQuery(String query);

	/**
	 * @return query to search for a similar artist
	 */
	public String getSimilarArtistSearchQuery();

}
