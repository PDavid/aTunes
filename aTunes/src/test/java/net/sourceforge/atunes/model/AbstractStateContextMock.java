/*
 * aTunes 2.2.0-SNAPSHOT
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

public abstract class AbstractStateContextMock implements IStateContext {

	@Override
	public String getLastFmPassword() {
		return null;
	}
	
	@Override
	public String getLastFmUser() {
		return null;
	}
	
	@Override
	public List<ILyricsEngineInfo> getLyricsEnginesInfo() {
		return null;
	}
	
	@Override
	public String getSelectedContextTab() {
		return null;
	}
	
	@Override
	public boolean isAutoLoveFavoriteSong() {
		return false;
	}
	
	@Override
	public boolean isLastFmEnabled() {
		return false;
	}
	
	@Override
	public boolean isSaveContextPicture() {
		return false;
	}
	
	@Override
	public boolean isShowContextAlbumsInGrid() {
		return false;
	}
	
	@Override
	public boolean isUseContext() {
		return false;
	}
	
	@Override
	public void setAutoLoveFavoriteSong(boolean autoLoveFavoriteSong) {
	}
	
	@Override
	public void setLastFmEnabled(boolean lastFmEnabled) {
	}
	
	@Override
	public void setLastFmPassword(String lastFmPassword) {
	}
	
	@Override
	public void setLastFmUser(String lastFmUser) {
	}
	
	@Override
	public void setLyricsEnginesInfo(List<ILyricsEngineInfo> lyricsEnginesInfo) {
	}
	
	@Override
	public void setSaveContextPicture(boolean saveContextPicture) {
	}
	
	@Override
	public void setSelectedContextTab(String selectedContextTab) {
	}
	
	@Override
	public void setShowContextAlbumsInGrid(boolean showContextAlbumsInGrid) {
	}
	
	@Override
	public void setUseContext(boolean useContext) {
	}
}