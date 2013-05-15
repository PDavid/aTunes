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

import java.awt.GraphicsDevice;
import java.awt.Window;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.system.SystemLookAndFeel;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPlayerTrayIconsHandler;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Adapter for a specific operating system
 * 
 * @author alex
 * 
 */
public abstract class OperatingSystemAdapter {

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return
	 */
	protected IBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * OS-dependent line terminator
	 * 
	 * @return
	 */
	public final String getSystemLineTerminator() {
		return System.getProperty("line.separator");
	}

	/**
	 * Path of user home
	 * 
	 * @return
	 */
	public final String getUserHome() {
		return System.getProperty("user.home");
	}

	/**
	 * File separator
	 * 
	 * @return
	 */
	public final String getFileSeparator() {
		return System.getProperty("file.separator");
	}

	/**
	 * Returns <code>true</code> if the current operating system (actually the
	 * VM) is 64 bit.
	 * 
	 * @return If the current operating system is 64 bit
	 */
	public final boolean is64Bit() {
		return System.getProperty("os.arch").contains("64");
	}

	/**
	 * Returns path to folder where app stores its data
	 * 
	 * @return
	 */
	public abstract String getAppDataFolder();

	/**
	 * Returns command used to launch app
	 * 
	 * @return
	 */
	public abstract String getLaunchCommand();

	/**
	 * Returns command parameters used to launch app
	 * 
	 * @return
	 */
	public abstract String getLaunchParameters();

	/**
	 * Setups specific configuration for a frame
	 * 
	 * @param frame
	 */
	public abstract void setupFrame(IFrame frame);

	/**
	 * Returns if shadow borders are supported
	 * 
	 * @return
	 */
	public boolean areShadowBordersForToolTipsSupported() {
		return false; // false by default
	}

	/**
	 * Returns if OS uses short path names
	 * 
	 * @return
	 */
	public boolean usesShortPathNames() {
		return false; // false by default
	}

	/**
	 * Returns short path name file if OS supports it. Default implementation
	 * returns the same file
	 * 
	 * @param filePathAndName
	 * @return
	 */
	public String getShortPathName(final String filePathAndName) {
		return filePathAndName; // default implementation returns the same file
								// path and name
	}

	/**
	 * Sets full screen
	 * 
	 * @param window
	 * @param fullscreen
	 * @param frame
	 */
	public void setFullScreen(final Window window, final boolean fullscreen,
			final IFrame frame) {
		// Default behaviour
		// Get in which screen is application and set full screen in that screen
		GraphicsDevice graphicsDevice = GuiUtils
				.getGraphicsDeviceForLocation(frame.getLocation());
		graphicsDevice.setFullScreenWindow(fullscreen ? window : null);
	}

	/**
	 * Returns if player is available
	 * 
	 * @param engine
	 * @return
	 */
	public abstract boolean isPlayerEngineSupported(IPlayerEngine engine);

	/**
	 * Returns command used (if any) to execute player engine
	 * 
	 * @param engine
	 * @return
	 */
	public abstract String getPlayerEngineCommand(IPlayerEngine engine);

	/**
	 * Returns specific player engine parameters
	 * 
	 * @param engine
	 * @return
	 */
	public Collection<String> getPlayerEngineParameters(
			final IPlayerEngine engine) {
		return Collections.emptyList();
	}

	/**
	 * Returns path where external tools are (cdda2wav, mencoder, etc.) Leave ""
	 * when tools are in path
	 * 
	 * @return
	 */
	public String getExternalToolsPath() {
		return "";
	}

	/**
	 * Returns list of supported look and feels
	 * 
	 * @return
	 */
	public Map<String, Class<? extends ILookAndFeel>> getSupportedLookAndFeels() {
		Map<String, Class<? extends ILookAndFeel>> lookAndFeels = new HashMap<String, Class<? extends ILookAndFeel>>();
		lookAndFeels.put(SubstanceLookAndFeel.SUBSTANCE,
				SubstanceLookAndFeel.class);
		lookAndFeels.put(SystemLookAndFeel.SYSTEM, SystemLookAndFeel.class);
		return lookAndFeels;
	}

	/**
	 * Returns default look and feel class
	 * 
	 * @return
	 */
	public Class<? extends ILookAndFeel> getDefaultLookAndFeel() {
		return SubstanceLookAndFeel.class;
	}

	/**
	 * Manages when no player engine is available
	 * 
	 * @param frame
	 */
	public void manageNoPlayerEngine(final IFrame frame) {
		// By default no management is done, only an error message
		this.beanFactory.getBean(IDialogFactory.class)
				.newDialog(IErrorDialog.class)
				.showErrorDialog(I18nUtils.getString("NO_PLAYER_ENGINE"));
	}

	/**
	 * Returns os properties
	 * 
	 * @return
	 */
	public final Properties getOsProperties() {
		Properties p = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(getOsPropertiesFileName());
			p.load(fis);
		} catch (FileNotFoundException e) {
			Logger.error(e.getMessage());
		} catch (IOException e) {
			Logger.error(e);
		} finally {
			ClosingUtils.close(fis);
		}
		return p;
	}

	/**
	 * Stores os properties
	 * 
	 * @param p
	 */
	public final void setOsProperties(final Properties p) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(getOsPropertiesFileName());
			p.store(fos, null);
		} catch (FileNotFoundException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		} finally {
			ClosingUtils.close(fos);
		}
	}

	/**
	 * Returns name of file where os-dependent properties are stored
	 * 
	 * @return
	 */
	private final String getOsPropertiesFileName() {
		return StringUtils.getString(this.beanFactory.getBean(IOSManager.class)
				.getUserConfigFolder(), getFileSeparator(), "os.properties");
	}

	/**
	 * Called when player engine is found (after searching or entering manually)
	 */
	public final void playerEngineFound() {
		this.beanFactory.getBean(IPlayerHandler.class).initializeAndCheck();
	}

	/**
	 * Returns if OS supports tray icons
	 * 
	 * @return
	 */
	public abstract boolean areTrayIconsSupported();

	/**
	 * Returns if some menu entries are delegated to OS
	 * 
	 * @return
	 */
	public abstract boolean areMenuEntriesDelegated();

	/**
	 * Returns if closing main window will terminate application
	 * 
	 * @return
	 */
	public abstract boolean isClosingMainWindowClosesApplication();

	/**
	 * Returns true if rip CDs is supported in current system
	 * 
	 * @return
	 */
	public abstract boolean isRipSupported();

	/**
	 * Returns true if OS allows to run more than one instance of application
	 * 
	 * @return
	 */
	public abstract boolean isMultipleInstancesSupported();

	/**
	 * Returns implementor of player tray icons for this OS
	 * 
	 * @return
	 */
	public abstract IPlayerTrayIconsHandler getPlayerTrayIcons();

	/**
	 * Returns if icons in system tray support change their colors
	 * 
	 * @return
	 */
	public boolean areTrayIconsColorsSupported() {
		return true;
	}
}
