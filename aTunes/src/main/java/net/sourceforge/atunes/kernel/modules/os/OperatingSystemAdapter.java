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
import java.util.Collection;
import java.util.Collections;

import net.sourceforge.atunes.gui.frame.Frame;
import net.sourceforge.atunes.kernel.OperatingSystem;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.AbstractCdToWavConverter;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.hotkeys.AbstractHotkeys;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.utils.GuiUtils;


public abstract class OperatingSystemAdapter {

	protected OperatingSystem systemType;
	
	public OperatingSystemAdapter(OperatingSystem systemType) {
		this.systemType = systemType;
	}
	
	/**
	 * OS-dependent line terminator
	 * @return
	 */
	public final String getSystemLineTerminator() {
		return System.getProperty("line.separator");
	}
	
	/**
	 * Path of user home
	 * @return
	 */
	public final String getUserHome() {
		return System.getProperty("user.home");
	}

	/**
	 * File separator
	 * @return
	 */
	public final String getFileSeparator() {
		return System.getProperty("file.separator");
	}
	
    /**
     * Returns <code>true</code> if the current operating system (actually
     * the VM) is 64 bit.
     * 
     * @return If the current operating system is 64 bit
     */
    public final boolean is64Bit() {
        return System.getProperty("os.arch").contains("64");
    }

	/**
	 * Returns path to folder where app stores its data
	 * @return
	 */
	public abstract String getAppDataFolder();
	
	/**
	 * Returns command used to launch app
	 * @return
	 */
	public abstract String getLaunchCommand();
	
	/**
	 * Setups specific configuration for a frame
	 * @param frame
	 */
	public void setUpFrame(Frame frame) {
		// Does nothing by default
	}

	/**
	 * Returns if shadow borders are supported
	 * @return
	 */
	public boolean areShadowBordersForToolTipsSupported() {
		return false; // false by default
	}

	/**
	 * Returns if OS uses short path names
	 * @return
	 */
	public boolean usesShortPathNames() {
		return false; // false by default
	}

	/**
	 * Sets full screen
	 * @param window
	 * @param fullscreen
	 */
	public void setFullScreen(Window window, boolean fullscreen) {
		// Default behaviour
        // Get in which screen is application and set full screen in that screen
        GraphicsDevice graphicsDevice = GuiUtils.getGraphicsDeviceForLocation(GuiHandler.getInstance().getFrame().getLocation());
        graphicsDevice.setFullScreenWindow(fullscreen ? window : null);
	}

	/**
	 * Returns OS-dependent converter
	 * @return
	 */
	public AbstractCdToWavConverter getCdToWavConverter() {
		return null; // By default there is no converter
	}

	/**
	 * Test OS-dependent converter
	 * @return
	 */
	public Boolean testCdToWavConverter() {
		return null; // None supported by default
	}

	/**
	 * Returns hotkeys listener
	 * @return
	 */
	public Class<? extends AbstractHotkeys> getHotkeysListener() {
		return null; // None by default
	}

	/**
	 * Returns if player is available
	 * @param engine
	 * @return
	 */
	public abstract boolean isPlayerEngineSupported(AbstractPlayerEngine engine);

	/**
	 * Returns command used (if any) to execute player engine
	 * @param engine
	 * @return
	 */
	public abstract String getPlayerEngineCommand(AbstractPlayerEngine engine);

	/**
	 * Returns specific player engine parameters
	 * @param engine
	 * @return
	 */
	public Collection<String> getPlayerEngineParameters(AbstractPlayerEngine engine) {
		return Collections.emptyList();
	}
	
	/**
	 * Returns path where external tools are (cdda2wav, mencoder, etc.)
	 * Leave "" when tools are in path
	 * @return
	 */
	public String getExternalToolsPath() {
		return "";
	}
}
