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
 * @author alex
 * 
 */
public interface IStateUI extends IState {

	/**
	 * Show status bar
	 * 
	 * @return
	 */
	public boolean isShowStatusBar();

	/**
	 * Show status bar
	 * 
	 * @param showStatusBar
	 */
	public void setShowStatusBar(boolean showStatusBar);

	/**
	 * Show OSD
	 * 
	 * @return
	 */
	public boolean isShowOSD();

	/**
	 * Show OSD
	 * 
	 * @param showOSD
	 */
	public void setShowOSD(boolean showOSD);

	/**
	 * Show system tray icon
	 * 
	 * @return
	 */
	public boolean isShowSystemTray();

	/**
	 * Show system tray icon
	 * 
	 * @param showSystemTray
	 */
	public void setShowSystemTray(boolean showSystemTray);

	/**
	 * Show player icons in tray
	 * 
	 * @return
	 */
	public boolean isShowTrayPlayer();

	/**
	 * Show player icons in tray
	 * 
	 * @param showTrayPlayer
	 */
	public void setShowTrayPlayer(boolean showTrayPlayer);

	/**
	 * Frame class used
	 * 
	 * @return
	 */
	public Class<? extends IFrame> getFrameClass();

	/**
	 * Frame class used
	 * 
	 * @param frameClass
	 */
	public void setFrameClass(Class<? extends IFrame> frameClass);

	/**
	 * Look And Feel
	 * 
	 * @return
	 */
	public LookAndFeelBean getLookAndFeel();

	/**
	 * Look And Feel
	 * 
	 * @param lookAndFeel
	 */
	public void setLookAndFeel(LookAndFeelBean lookAndFeel);

	/**
	 * Font settings
	 * 
	 * @return
	 */
	public FontSettings getFontSettings();

	/**
	 * Font settings
	 * 
	 * @param fontSettings
	 */
	public void setFontSettings(FontSettings fontSettings);

	/**
	 * Show advanced player controls
	 * 
	 * @return
	 */
	public boolean isShowAdvancedPlayerControls();

	/**
	 * Show advanced player controls
	 * 
	 * @param show
	 */
	public void setShowAdvancedPlayerControls(boolean show);

	/**
	 * OSD duration
	 * 
	 * @return
	 */
	public int getOsdDuration();

	/**
	 * OSD duration
	 * 
	 * @param osdDuration
	 */
	public void setOsdDuration(int osdDuration);

	/**
	 * Full screen background
	 * 
	 * @return
	 */
	public String getFullScreenBackground();

	/**
	 * Full screen background
	 * 
	 * @param fullScreenBackground
	 */
	public void setFullScreenBackground(String fullScreenBackground);

	/**
	 * Search results columns
	 * 
	 * @return
	 */
	public Map<String, ColumnBean> getSearchResultsColumns();

	/**
	 * Search results columns
	 * 
	 * @param searchResultsColumns
	 */
	public void setSearchResultsColumns(
			Map<String, ColumnBean> searchResultsColumns);

	/**
	 * Frame state
	 * 
	 * @param frame
	 * @return
	 */
	public IFrameState getFrameState(Class<? extends IFrame> frame);

	/**
	 * Frame state
	 * 
	 * @param frame
	 * @param fs
	 */
	public void setFrameState(Class<? extends IFrame> frame, IFrameState fs);

	/**
	 * @param frameSize
	 */
	public void setFrameSize(IFrameSize frameSize);

	/**
	 * @return frame size
	 */
	public IFrameSize getFrameSize();

	/**
	 * OSD width
	 * 
	 * @return
	 */
	public int getOsdWidth();

	/**
	 * OSD width
	 * 
	 * @param osdWidth
	 */
	public void setOsdWidth(int osdWidth);

	/**
	 * OSD alignment
	 * 
	 * @return
	 */
	public int getOsdHorizontalAlignment();

	/**
	 * OSD alignment
	 * 
	 * @param osdHorizontalAlignment
	 */
	public void setOsdHorizontalAlignment(int osdHorizontalAlignment);

	/**
	 * OSD alignment
	 * 
	 * @return
	 */
	public int getOsdVerticalAlignment();

	/**
	 * OSD alignment
	 * 
	 * @param osdVerticalAlignment
	 */
	public void setOsdVerticalAlignment(int osdVerticalAlignment);

	/**
	 * Color of tray player icons
	 * 
	 * @return
	 */
	public IColorBean getTrayPlayerIconsColor();

	/**
	 * Color of tray player icons
	 * 
	 * @param color
	 */
	public void setTrayPlayerIconsColor(IColorBean color);

	/**
	 * Show player controls on top
	 * 
	 * @return
	 */
	public boolean isShowPlayerControlsOnTop();

	/**
	 * Show player controls on top
	 * 
	 * @param onTop
	 */
	public void setShowPlayerControlsOnTop(boolean onTop);

	/**
	 * Saves frame position
	 * 
	 * @param framePosition
	 */
	public void setFramePosition(IFramePosition framePosition);

	/**
	 * @return frame position
	 */
	public IFramePosition getFramePosition();

}
