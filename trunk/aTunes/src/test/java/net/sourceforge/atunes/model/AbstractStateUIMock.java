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

import java.util.Map;

public class AbstractStateUIMock implements IStateUI {

	@Override
	public FontSettings getFontSettings() {
		return null;
	}
	
	@Override
	public Class<? extends IFrame> getFrameClass() {
		return null;
	}
	
	@Override
	public IFrameState getFrameState(Class<? extends IFrame> frame) {
		return null;
	}
	
	@Override
	public String getFullScreenBackground() {
		return null;
	}
	
	@Override
	public LookAndFeelBean getLookAndFeel() {
		return null;
	}
	
	@Override
	public int getOsdDuration() {
		return 0;
	}
	
	@Override
	public int getOsdHorizontalAlignment() {
		return 0;
	}
	
	@Override
	public int getOsdVerticalAlignment() {
		return 0;
	}
	
	@Override
	public int getOsdWidth() {
		return 0;
	}
	
	@Override
	public Map<String, ColumnBean> getSearchResultsColumns() {
		return null;
	}
	
	@Override
	public IColorBean getTrayPlayerIconsColor() {
		return null;
	}
	
	@Override
	public boolean isShowAdvancedPlayerControls() {
		return false;
	}
	
	@Override
	public boolean isShowOSD() {
		return false;
	}
	
	@Override
	public boolean isShowPlayerControlsOnTop() {
		return false;
	}
	
	@Override
	public boolean isShowStatusBar() {
		return false;
	}
	
	@Override
	public boolean isShowSystemTray() {
		return false;
	}
	
	@Override
	public boolean isShowTrayPlayer() {
		return false;
	}
	
	@Override
	public void setFontSettings(FontSettings fontSettings) {
	}
	
	@Override
	public void setFrameClass(Class<? extends IFrame> frameClass) {
	}
	
	@Override
	public void setFrameState(Class<? extends IFrame> frame, IFrameState fs) {
	}
	
	@Override
	public void setFullScreenBackground(String fullScreenBackground) {
	}
	
	@Override
	public void setLookAndFeel(LookAndFeelBean lookAndFeel) {
	}
	
	@Override
	public void setOsdDuration(int osdDuration) {
	}
	
	@Override
	public void setOsdHorizontalAlignment(int osdHorizontalAlignment) {
	}
	
	@Override
	public void setOsdVerticalAlignment(int osdVerticalAlignment) {
	}
	
	@Override
	public void setOsdWidth(int osdWidth) {
	}
	
	@Override
	public void setSearchResultsColumns(
			Map<String, ColumnBean> searchResultsColumns) {
	}
	
	@Override
	public void setShowAdvancedPlayerControls(boolean show) {
	}
	
	@Override
	public void setShowOSD(boolean showOSD) {
	}
	
	@Override
	public void setShowPlayerControlsOnTop(boolean onTop) {
	}
	
	@Override
	public void setShowStatusBar(boolean showStatusBar) {
	}
	
	@Override
	public void setShowSystemTray(boolean showSystemTray) {
	}
	
	@Override
	public void setShowTrayPlayer(boolean showTrayPlayer) {
	}
	
	@Override
	public void setTrayPlayerIconsColor(IColorBean color) {
	}
	
	@Override
	public IFrameSize getFrameSize() {
		return null;
	}
	
	@Override
	public void setFrameSize(IFrameSize frameSize) {
	}

	@Override
	public IFramePosition getFramePosition() {
		return null;
	}
	
	@Override
	public void setFramePosition(IFramePosition framePosition) {
	}
}
