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

package net.sourceforge.atunes.kernel.modules.os;

import java.awt.Window;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.IPlayerTrayIconsHandler;
import net.sourceforge.atunes.model.ITrayIcon;
import net.sourceforge.atunes.model.OperatingSystem;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Holds information about specific Operating System data
 * 
 * @author alex
 * 
 */
public class OsManager implements IOSManager {

	private OperatingSystemAdapter adapter;

	private IApplicationArguments applicationArguments;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param adapter
	 */
	public void setAdapter(final OperatingSystemAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(
			final IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	@Override
	public final String getUserConfigFolder() {
		// Get path depending on parameters
		String userConfigFolder = getConfigFolder();

		// Test if it's valid
		if (!isValidConfigFolder(userConfigFolder)) {
			// As workaround if can't get a valid folder, use temporal folder,
			// for example: /tmp/atunes
			userConfigFolder = StringUtils.getString(
					System.getProperty("java.io.tmpdir"), getFileSeparator(),
					"atunes");
			Logger.error("Using ", userConfigFolder, " as config folder");
		}

		return userConfigFolder;
	}

	/**
	 * Test if path is valid
	 * 
	 * @param path
	 * @return
	 */
	private boolean isValidConfigFolder(final String path) {
		File folder = new File(path);

		// If folder does not exist, try to create
		if (!folder.exists() && !folder.mkdirs()) {
			Logger.error("Can't create folder ", path);
			return false;
		}

		// Folder exists, check if can write
		if (!folder.canWrite()) {
			Logger.error("Can't write folder ", path);
			return false;
		}

		// Also check if it's a directory
		if (!folder.isDirectory()) {
			Logger.error(path, " is not a directory");
			return false;
		}

		return true;
	}

	/**
	 * Returns folder path where application stores its configuration
	 * 
	 * @param useWorkDir
	 * @return
	 */
	private String getConfigFolder() {
		if (this.applicationArguments.isDebug()) {
			return "./debug";
		} else if (this.applicationArguments.getUserConfigFolder() != null) {
			return this.applicationArguments.getUserConfigFolder();
		} else {
			return this.adapter.getAppDataFolder();
		}
	}

	@Override
	public File getFileFromUserConfigFolder(final String name) {
		String userConfigFolder = getUserConfigFolder();
		if (userConfigFolder.equals(".")) {
			return new File(name);
		}
		return new File(StringUtils.getString(userConfigFolder, "/", name));
	}

	@Override
	public String getTempFolder() {
		String userConfigFolder = getUserConfigFolder();
		String tempFolder = StringUtils.getString(userConfigFolder,
				this.adapter.getFileSeparator(), Constants.TEMP_DIR);
		File tempFile = new File(tempFolder);
		if (!tempFile.exists() && !tempFile.mkdir()) {
			return userConfigFolder;
		}
		return tempFolder;
	}

	@Override
	public String getWorkingDirectory() {
		return System.getProperty("user.dir");
	}

	@Override
	public String getCustomRepositoryConfigFolder() {
		return this.applicationArguments.getRepositoryConfigFolder();
	}

	@Override
	public String getLaunchCommand() {
		return this.adapter.getLaunchCommand();
	}

	@Override
	public String getLaunchParameters() {
		return this.adapter.getLaunchParameters();
	}

	@Override
	public void setupFrame(final IFrame frame) {
		this.adapter.setupFrame(frame);
	}

	@Override
	public boolean areShadowBordersForToolTipsSupported() {
		return this.adapter.areShadowBordersForToolTipsSupported();
	}

	@Override
	public String getUserHome() {
		return this.adapter.getUserHome();
	}

	@Override
	public String getFileSeparator() {
		return this.adapter.getFileSeparator();
	}

	@Override
	public boolean usesShortPathNames() {
		return this.adapter.usesShortPathNames();
	}

	@Override
	public void setFullScreen(final Window window, final boolean fullscreen,
			final IFrame frame) {
		this.adapter.setFullScreen(window, fullscreen, frame);
	}

	@Override
	public String getLineTerminator() {
		return this.adapter.getSystemLineTerminator();
	}

	@Override
	public boolean is64Bit() {
		return this.adapter.is64Bit();
	}

	@Override
	public boolean isPlayerEngineSupported(final IPlayerEngine engine) {
		return this.adapter.isPlayerEngineSupported(engine);
	}

	@Override
	public String getPlayerEngineCommand(final IPlayerEngine engine) {
		return this.adapter.getPlayerEngineCommand(engine);
	}

	@Override
	public Collection<String> getPlayerEngineParameters(
			final IPlayerEngine engine) {
		return this.adapter.getPlayerEngineParameters(engine);
	}

	@Override
	public Object getExternalToolsPath() {
		return this.adapter.getExternalToolsPath();
	}

	@Override
	public Map<String, Class<? extends ILookAndFeel>> getLookAndFeels() {
		return this.adapter.getSupportedLookAndFeels();
	}

	@Override
	public Class<? extends ILookAndFeel> getDefaultLookAndFeel() {
		return this.adapter.getDefaultLookAndFeel();
	}

	@Override
	public void manageNoPlayerEngine(final IFrame frame) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				OsManager.this.adapter.manageNoPlayerEngine(frame);
			}
		});
	}

	@Override
	public void playerEngineFound() {
		this.adapter.playerEngineFound();
	}

	@Override
	public void setOSProperty(final String key, final String value) {
		Properties p = this.adapter.getOsProperties();
		p.setProperty(key, value);
		this.adapter.setOsProperties(p);
	}

	@Override
	public boolean areTrayIconsSupported() {
		return this.adapter.areTrayIconsSupported();
	}

	@Override
	public boolean areMenuEntriesDelegated() {
		return this.adapter.areMenuEntriesDelegated();
	}

	@Override
	public boolean isClosingMainWindowClosesApplication() {
		return this.adapter.isClosingMainWindowClosesApplication();
	}

	@Override
	public boolean isRipSupported() {
		return this.adapter.isRipSupported();
	}

	/**
	 * @param os
	 * @param name
	 * @return if OS and name (optional) match
	 */
	private boolean checkOS(final OperatingSystem os, final String name) {
		return this.beanFactory.getBean("osType", OperatingSystem.class)
				.equals(os)
				&& (name == null || System.getProperty("os.name").toLowerCase()
						.contains(name.toLowerCase()));
	}

	private boolean isWindowsVista() {
		return checkOS(OperatingSystem.WINDOWS, "Vista");
	}

	private boolean isWindows7() {
		return checkOS(OperatingSystem.WINDOWS, "Windows 7");
	}

	private boolean isWindows8() {
		return checkOS(OperatingSystem.WINDOWS, "Windows 8");
	}

	@Override
	public boolean isOldWindows() {
		return !isNewWindows();
	}

	@Override
	public boolean isNewWindows() {
		return isWindowsVista() || isWindows7() || !isWindows8();
	}

	@Override
	public boolean isLinux() {
		return checkOS(OperatingSystem.LINUX, null);
	}

	@Override
	public boolean isMacOsX() {
		return checkOS(OperatingSystem.MACOSX, null);
	}

	@Override
	public boolean isSolaris() {
		return checkOS(OperatingSystem.SOLARIS, null);
	}

	@Override
	public boolean isWindows() {
		return checkOS(OperatingSystem.WINDOWS, null);
	}

	@Override
	public boolean isMultipleInstancesSupported() {
		return this.adapter.isMultipleInstancesSupported();
	}

	@Override
	public ITrayIcon getTrayIcon() {
		return this.beanFactory.getBean("trayIcon", ITrayIcon.class);
	}

	@Override
	public IPlayerTrayIconsHandler getPlayerTrayIcons() {
		return this.adapter.getPlayerTrayIcons();
	}

	@Override
	public boolean areTrayIconsColorsSupported() {
		return this.adapter.areTrayIconsColorsSupported();
	}

	@Override
	public File getFile(final String folder, final String name) {
		return new File(getFilePath(folder, name));
	}

	@Override
	public String getFilePath(final String folder, final String name) {
		return StringUtils.getString(folder, getFileSeparator(), name);
	}

	@Override
	public String getFileNormalized(final String filePathAndName) {
		if (this.adapter.usesShortPathNames()) {
			return this.adapter.getShortPathName(filePathAndName);
		}
		return filePathAndName;
	}

	@Override
	public File getFileNormalized(final File file) {
		return new File(getFileNormalized(FileUtils.getPath(file)));
	}
}
