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

import java.awt.Window;
import java.io.File;
import java.util.Collection;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.AbstractCdToWavConverter;
import net.sourceforge.atunes.kernel.modules.hotkeys.AbstractHotkeys;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;

/**
 * Holds information about specific Operating System data
 * @author alex
 *
 */
public interface IOSManager {

	/**
	 * Gets folder where state is stored. If not exists, it's created
	 * 
	 * @param useWorkDir
	 *            If the current working directory should be used
	 * 
	 * @return The folder where the state is stored
	 */
	public String getUserConfigFolder(boolean useWorkDir);

	/**
	 * Returns file from the user config folder.
	 * 
	 * @param name
	 *            The name of the file (Example: aTunes.log or folder/file.abc)
	 * @param useWorkDir
	 *            If the current working directory should be used
	 * 
	 * @return The file from the user config folder
	 */
	public File getFileFromUserConfigFolder(String name, boolean useWorkDir);

	/**
	 * Return path to temporal folder, which is inside user's configuration folder.
	 * 
	 * @return the temporal folder
	 */
	public String getTempFolder();

	/**
	 * Returns the current working directory
	 * 
	 * @return
	 */
	public String getWorkingDirectory();

	/**
	 * @return the customRepositoryConfigFolder
	 */
	public String getCustomRepositoryConfigFolder();

	/**
	 * Returns a string with command to launch application This method is
	 * used when restarting app
	 * 
	 * @return
	 */
	public String getLaunchCommand();

	/**
	 * Returns launch parameters
	 * @return
	 */
	public String getLaunchParameters();

	/**
	 * Setup specific properties for frame
	 * @param frame
	 */
	public void setupFrame(IFrame frame);

	/**
	 * Returns if shadow borders are supported
	 * @return
	 */
	public boolean areShadowBordersForToolTipsSupported();

	/**
	 * Returns user home
	 * @return
	 */
	public String getUserHome();

	/**
	 * Returns path file separator
	 * @return
	 */
	public String getFileSeparator();

	/**
	 * Returns if OS uses short path names
	 * @return
	 */
	public boolean usesShortPathNames();

	/**
	 * Sets window in full screen
	 * @param window
	 * @param fullscreen
	 * @param frame
	 */
	public void setFullScreen(Window window, boolean fullscreen, IFrame frame);

	/**
	 * Return OS-dependent converter
	 * @return
	 */
	public AbstractCdToWavConverter getCdToWavConverter();

	/**
	 * Test OS-dependent converter
	 * @return
	 */
	public boolean testCdToWavConverter();

	/**
	 * Returns OS hotkey listener
	 * @return
	 */
	public Class<? extends AbstractHotkeys> getHotkeysListener();

	public Object getLineTerminator();

	/**
	 * Returns <code>true</code> if the current operating system (actually
	 * the VM) is 64 bit.
	 * 
	 * @return If the current operating system is 64 bit
	 */
	public boolean is64Bit();

	/**
	 * Returns if player engine is supported for current OS
	 * @param engine
	 * @return
	 */
	public boolean isPlayerEngineSupported(AbstractPlayerEngine engine);

	/**
	 * Returns command used (if any) to execute player engine
	 * @param engine
	 * @return
	 */
	public String getPlayerEngineCommand(AbstractPlayerEngine engine);

	/**
	 * Returns specific player engine parameters
	 * @param engine
	 * @return
	 */
	public Collection<String> getPlayerEngineParameters(
			AbstractPlayerEngine engine);

	/**
	 * Returns path where external tools are (cdda2wav, mencoder, etc.)
	 * Leave "" when tools are in path
	 * @return
	 */
	public Object getExternalToolsPath();

	/**
	 * Returns supported look and feels
	 * @return
	 */
	public Map<String, Class<? extends ILookAndFeel>> getLookAndFeels();

	/**
	 * Returns default look and feel class
	 * @return
	 */
	public Class<? extends ILookAndFeel> getDefaultLookAndFeel();

	/**
	 * Manages when no player engine is available
	 */
	public void manageNoPlayerEngine(IFrame frame);

	/**
	 * Called when player engine is found (after searching or entering manually)
	 */
	public void playerEngineFound();

	/**
	 * Returns os property
	 * @param key
	 * @return
	 */
	public String getOSProperty(String key);

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public void setOSProperty(String key, String value);

	/**
	 * Returns if OS supports tray icons
	 * @return
	 */
	public boolean areTrayIconsSupported();

	/**
	 * Returns if some menu entries (preferences, about) are delegated to OS
	 * @return
	 */
	public boolean areMenuEntriesDelegated();

	/**
	 * Returns if closing main window will terminate application
	 * 
	 * If not, OS will have to provide some method to make window visible again
	 * @return
	 */
	public boolean isClosingMainWindowClosesApplication();

	/**
	 * Returns true if rip CDs is supported in current system
	 * @return
	 */
	public boolean isRipSupported();

	/**
	 * @return
	 * @see net.sourceforge.atunes.model.OperatingSystem#isWindowsVista()
	 */
	public boolean isWindowsVista();

	/**
	 * @return
	 * @see net.sourceforge.atunes.model.OperatingSystem#isWindows7()
	 */
	public boolean isWindows7();

	/**
	 * @return
	 * @see net.sourceforge.atunes.model.OperatingSystem#isOldWindows()
	 */
	public boolean isOldWindows();

	/**
	 * @return
	 * @see net.sourceforge.atunes.model.OperatingSystem#isLinux()
	 */
	public boolean isLinux();

	/**
	 * @return
	 * @see net.sourceforge.atunes.model.OperatingSystem#isMacOsX()
	 */
	public boolean isMacOsX();

	/**
	 * @return
	 * @see net.sourceforge.atunes.model.OperatingSystem#isSolaris()
	 */
	public boolean isSolaris();

	/**
	 * @return
	 * @see net.sourceforge.atunes.model.OperatingSystem#isWindows()
	 */
	public boolean isWindows();

}