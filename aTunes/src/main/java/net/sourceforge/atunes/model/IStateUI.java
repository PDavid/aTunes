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

/**
 * @author alex
 *
 */
public interface IStateUI {
	
	/**
	 * Show navigation table
	 * @return
	 */
	public boolean isShowNavigationTable();

	/**
	 * Show navigation table
	 * @param showNavigationTable
	 */
	public void setShowNavigationTable(boolean showNavigationTable);
	
	/**
	 * Show status bar
	 * @return
	 */
	public boolean isShowStatusBar();

	/**
	 * Show status bar
	 * @param showStatusBar
	 */
	public void setShowStatusBar(boolean showStatusBar);

	/**
	 * Show OSD
	 * @return
	 */
	public boolean isShowOSD();

	/**
	 * Show OSD
	 * @param showOSD
	 */
	public void setShowOSD(boolean showOSD);
	
	/**
	 * Show system tray icon
	 * @return
	 */
	public boolean isShowSystemTray();

	/**
	 * Show system tray icon
	 * @param showSystemTray
	 */
	public void setShowSystemTray(boolean showSystemTray);

	/**
	 * Show player icons in tray
	 * @return
	 */
	public boolean isShowTrayPlayer();

	/**
	 * Show player icons in tray
	 * @param showTrayPlayer
	 */
	public void setShowTrayPlayer(boolean showTrayPlayer);
	
	/**
	 * Navigation view
	 * @return
	 */
	public String getNavigationView();

	/**
	 * Navigation view
	 * @param navigationView
	 */
	public void setNavigationView(String navigationView);

	/**
	 * View mode of navigator
	 * @return
	 */
	public ViewMode getViewMode();

	/**
	 * View mode of navigator
	 * @param viewMode
	 */
	public void setViewMode(ViewMode viewMode);

	/**
	 * Frame class used
	 * @return
	 */
	public Class<? extends IFrame> getFrameClass();

	/**
	 * Frame class used
	 * @param frameClass
	 */
	public void setFrameClass(Class<? extends IFrame> frameClass);

	/**
	 * Look And Feel
	 * @return
	 */
	public LookAndFeelBean getLookAndFeel();

	/**
	 * Look And Feel
	 * @param lookAndFeel
	 */
	public void setLookAndFeel(LookAndFeelBean lookAndFeel);

	/**
	 * Font settings
	 * @return
	 */
	public FontSettings getFontSettings();

	/**
	 * Font settings
	 * @param fontSettings
	 */
	public void setFontSettings(FontSettings fontSettings);
	
	/**
	 * Show advanced player controls
	 * @return
	 */
	public boolean isShowAdvancedPlayerControls();

	/**
	 * Show advanced player controls
	 * @param show
	 */
	public void setShowAdvancedPlayerControls(boolean show);

	/**
	 * OSD duration
	 * @return
	 */
	public int getOsdDuration();

	/**
	 * OSD duration
	 * @param osdDuration
	 */
	public void setOsdDuration(int osdDuration);


}
