/*
 * aTunes 2.1.0-SNAPSHOT
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

import net.sourceforge.atunes.gui.frame.Frame;
import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.system.SystemLookAndFeel;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.OperatingSystem;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.AbstractCdToWavConverter;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.hotkeys.AbstractHotkeys;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngineManager;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public abstract class OperatingSystemAdapter {

	protected OperatingSystem systemType;

	public OperatingSystemAdapter(OperatingSystem systemType) {
		this.systemType = systemType;
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
	 * Setups specific configuration for a frame
	 * 
	 * @param frame
	 */
	public abstract void setUpFrame(Frame frame);

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
	 * Sets full screen
	 * 
	 * @param window
	 * @param fullscreen
	 */
	public void setFullScreen(Window window, boolean fullscreen) {
		// Default behaviour
		// Get in which screen is application and set full screen in that screen
		GraphicsDevice graphicsDevice = GuiUtils
				.getGraphicsDeviceForLocation(GuiHandler.getInstance()
						.getFrame().getLocation());
		graphicsDevice.setFullScreenWindow(fullscreen ? window : null);
	}

	/**
	 * Returns OS-dependent converter
	 * 
	 * @return
	 */
	public AbstractCdToWavConverter getCdToWavConverter() {
		return null; // By default there is no converter
	}

	/**
	 * Test OS-dependent converter
	 * 
	 * @return
	 */
	public boolean testCdToWavConverter() {
		return false; // None supported by default
	}

	/**
	 * Returns hotkeys listener
	 * 
	 * @return
	 */
	public Class<? extends AbstractHotkeys> getHotkeysListener() {
		return null; // None by default
	}

	/**
	 * Returns if player is available
	 * 
	 * @param engine
	 * @return
	 */
	public abstract boolean isPlayerEngineSupported(AbstractPlayerEngine engine);

	/**
	 * Returns command used (if any) to execute player engine
	 * 
	 * @param engine
	 * @return
	 */
	public abstract String getPlayerEngineCommand(AbstractPlayerEngine engine);

	/**
	 * Returns specific player engine parameters
	 * 
	 * @param engine
	 * @return
	 */
	public Collection<String> getPlayerEngineParameters(
			AbstractPlayerEngine engine) {
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
	public Map<String, Class<? extends AbstractLookAndFeel>> getSupportedLookAndFeels() {
		Map<String, Class<? extends AbstractLookAndFeel>> lookAndFeels = new HashMap<String, Class<? extends AbstractLookAndFeel>>();
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
	public Class<? extends AbstractLookAndFeel> getDefaultLookAndFeel() {
		return SubstanceLookAndFeel.class;
	}

	/**
	 * Manages when no player engine is available
	 */
	public void manageNoPlayerEngine() {
		// By default no management is done, only an error message
        GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("NO_PLAYER_ENGINE"));
	}

	/**
	 * Returns os properties
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
	 * @param p
	 */
	public final void setOsProperties(Properties p) {
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
	 * @return
	 */
	private final String getOsPropertiesFileName() {
		return StringUtils.getString(OsManager.getUserConfigFolder(Kernel.isDebug()), getFileSeparator(), "os.properties");
	}

	/**
	 * Called when player engine is found (after searching or entering manually)
	 */
	public void playerEngineFound() {
		PlayerEngineManager.playerEngineFound();
	}

	/**
	 * Returns if OS supports tray icons
	 * @return
	 */
	public abstract boolean areTrayIconsSupported();	
}
