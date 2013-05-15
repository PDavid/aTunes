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

import java.awt.Window;
import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * Holds information about specific Operating System data
 * 
 * @author alex
 * 
 */
public interface IOSManager {

	/**
	 * Gets folder where state is stored. If not exists, it's created
	 * 
	 * @return The folder where the state is stored
	 */
	String getUserConfigFolder();

	/**
	 * Returns file from the user config folder.
	 * 
	 * @param name
	 *            The name of the file (Example: aTunes.log or folder/file.abc)
	 * 
	 * @return The file from the user config folder
	 */
	File getFileFromUserConfigFolder(String name);

	/**
	 * Return path to temporal folder, which is inside user's configuration
	 * folder.
	 * 
	 * @return the temporal folder
	 */
	String getTempFolder();

	/**
	 * Returns the current working directory
	 * 
	 * @return
	 */
	String getWorkingDirectory();

	/**
	 * @return the customRepositoryConfigFolder
	 */
	String getCustomRepositoryConfigFolder();

	/**
	 * Returns a string with command to launch application This method is used
	 * when restarting app
	 * 
	 * @return
	 */
	String getLaunchCommand();

	/**
	 * Returns launch parameters
	 * 
	 * @return
	 */
	String getLaunchParameters();

	/**
	 * Setup specific properties for frame
	 * 
	 * @param frame
	 */
	void setupFrame(IFrame frame);

	/**
	 * Returns if shadow borders are supported
	 * 
	 * @return
	 */
	boolean areShadowBordersForToolTipsSupported();

	/**
	 * Returns user home
	 * 
	 * @return
	 */
	String getUserHome();

	/**
	 * Returns path file separator
	 * 
	 * @return
	 */
	String getFileSeparator();

	/**
	 * Returns if OS uses short path names
	 * 
	 * @return
	 */
	boolean usesShortPathNames();

	/**
	 * Sets window in full screen
	 * 
	 * @param window
	 * @param fullscreen
	 * @param frame
	 */
	void setFullScreen(Window window, boolean fullscreen, IFrame frame);

	/**
	 * Returns line terminator for current OS
	 * 
	 * @return
	 */
	String getLineTerminator();

	/**
	 * Returns <code>true</code> if the current operating system (actually the
	 * VM) is 64 bit.
	 * 
	 * @return If the current operating system is 64 bit
	 */
	boolean is64Bit();

	/**
	 * Returns if player engine is supported for current OS
	 * 
	 * @param engine
	 * @return
	 */
	boolean isPlayerEngineSupported(IPlayerEngine engine);

	/**
	 * Returns command used (if any) to execute player engine
	 * 
	 * @param engine
	 * @return
	 */
	String getPlayerEngineCommand(IPlayerEngine engine);

	/**
	 * Returns specific player engine parameters
	 * 
	 * @param engine
	 * @return
	 */
	Collection<String> getPlayerEngineParameters(IPlayerEngine engine);

	/**
	 * Returns path where external tools are (cdda2wav, mencoder, etc.) Leave ""
	 * when tools are in path
	 * 
	 * @return
	 */
	Object getExternalToolsPath();

	/**
	 * Returns supported look and feels
	 * 
	 * @return
	 */
	Map<String, Class<? extends ILookAndFeel>> getLookAndFeels();

	/**
	 * Returns default look and feel class
	 * 
	 * @return
	 */
	Class<? extends ILookAndFeel> getDefaultLookAndFeel();

	/**
	 * Manages when no player engine is available
	 * 
	 * @param frame
	 */
	void manageNoPlayerEngine(IFrame frame);

	/**
	 * Called when player engine is found (after searching or entering manually)
	 */
	void playerEngineFound();

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	void setOSProperty(String key, String value);

	/**
	 * Returns if OS supports tray icons
	 * 
	 * @return
	 */
	boolean areTrayIconsSupported();

	/**
	 * Returns if some menu entries (preferences, about) are delegated to OS
	 * 
	 * @return
	 */
	boolean areMenuEntriesDelegated();

	/**
	 * Returns if closing main window will terminate application
	 * 
	 * If not, OS will have to provide some method to make window visible again
	 * 
	 * @return
	 */
	boolean isClosingMainWindowClosesApplication();

	/**
	 * Returns true if rip CDs is supported in current system
	 * 
	 * @return
	 */
	boolean isRipSupported();

	/**
	 * Returns if OS allows to run more than one instance of application
	 * 
	 * @return
	 */
	boolean isMultipleInstancesSupported();

	/**
	 * @return if Windows XP or older
	 */
	boolean isOldWindows();

	/**
	 * @return if Windows vista, 7 or 8
	 */
	boolean isNewWindows();

	/**
	 * @return if linux
	 */
	boolean isLinux();

	/**
	 * @return if OS X
	 */
	boolean isMacOsX();

	/**
	 * @return if solaris
	 */
	boolean isSolaris();

	/**
	 * @return if any Windows
	 */
	boolean isWindows();

	/**
	 * Returns tray icon implementor for current OS
	 * 
	 * @return
	 */
	ITrayIcon getTrayIcon();

	/**
	 * Returns responsible of player tray icons
	 * 
	 * @return
	 */
	IPlayerTrayIconsHandler getPlayerTrayIcons();

	/**
	 * Returns if icons in system tray support change their colors
	 * 
	 * @return
	 */
	boolean areTrayIconsColorsSupported();

	/**
	 * @param folder
	 * @param name
	 * @return file object
	 */
	File getFile(String folder, String name);

	/**
	 * @param folder
	 * @param name
	 * @return file path
	 */
	String getFilePath(String folder, String name);

	/**
	 * Normalizes file path and name if necessary
	 * 
	 * Needed for UNC filenames with spaces ->
	 * http://bugs.sun.com/view_bug.do?bug_id=6550588
	 * 
	 * @param filePathAndName
	 * @return
	 */
	String getFileNormalized(String filePathAndName);

	/**
	 * Normalizes file path and name if necessary
	 * 
	 * Needed for UNC filenames with spaces ->
	 * http://bugs.sun.com/view_bug.do?bug_id=6550588
	 * 
	 * @param file
	 * @return
	 */
	File getFileNormalized(File file);
}