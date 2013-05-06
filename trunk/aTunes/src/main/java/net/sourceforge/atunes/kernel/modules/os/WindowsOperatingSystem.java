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

import java.awt.Dimension;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.kernel.modules.tray.CommonPlayerTrayIconsHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.IPlayerTrayIconsHandler;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.WString;

/**
 * Windows operating system adapter
 * 
 * @author alex
 * 
 */
public class WindowsOperatingSystem extends OperatingSystemAdapter {

	/**
	 * Name of the Windows command
	 */
	private static final String COMMAND_WINDOWS = "aTunes.exe";

	/** Directory where Windows binaries are found (i.e. mplayer, lame, etc) */
	public static final String WINDOWS_TOOLS_DIR = "win_tools";

	/**
	 * Command to be executed on Windows systems to launch mplayer. Mplayer is
	 * in "win_tools" dir, inside aTunes package
	 */
	private static final String MPLAYER_WIN_COMMAND = WINDOWS_TOOLS_DIR
			+ "/mplayer.exe";

	private static final String WINOPTPRIORITY = "-priority";

	private static final String WINOPTPRIORITY_DEFAULT = "abovenormal";

	private Dimension screenSize;

	private static final int CHAR_BYTE_WIDTH = 2;

	private Kernel32 kernel32;

	private boolean kernel32error;

	/**
	 * @param screenSize
	 */
	public void setScreenSize(final Dimension screenSize) {
		this.screenSize = screenSize;
	}

	@Override
	public String getAppDataFolder() {
		return StringUtils.getString(System.getenv("APPDATA"), "/atunes");
	}

	@Override
	public String getLaunchCommand() {
		return FileUtils.getPath(new File(StringUtils.getString("./",
				COMMAND_WINDOWS)));
	}

	@Override
	public String getLaunchParameters() {
		return null;
	}

	@Override
	public boolean areShadowBordersForToolTipsSupported() {
		return true;
	}

	@Override
	public void setFullScreen(final Window window, final boolean fullscreen,
			final IFrame frame) {
		if (getBeanFactory().getBean(IOSManager.class).isOldWindows()
				&& fullscreen) {
			window.setSize(this.screenSize.width, this.screenSize.height);
		}
	}

	@Override
	public boolean isPlayerEngineSupported(final IPlayerEngine engine) {
		return true;
	}

	@Override
	public String getPlayerEngineCommand(final IPlayerEngine engine) {
		return engine instanceof MPlayerEngine ? MPLAYER_WIN_COMMAND : null;
	}

	@Override
	public Collection<String> getPlayerEngineParameters(
			final IPlayerEngine engine) {
		if (engine instanceof MPlayerEngine) {
			List<String> parameters = new ArrayList<String>(2);
			parameters.add(WINOPTPRIORITY);
			parameters.add(WINOPTPRIORITY_DEFAULT);
			return parameters;
		} else {
			return super.getPlayerEngineParameters(engine);
		}
	}

	@Override
	public String getExternalToolsPath() {
		return StringUtils.getString(WINDOWS_TOOLS_DIR, getFileSeparator());
	}

	@Override
	public boolean areTrayIconsSupported() {
		return true;
	}

	@Override
	public void setupFrame(final IFrame frame) {
	}

	@Override
	public boolean areMenuEntriesDelegated() {
		return false;
	}

	@Override
	public boolean isClosingMainWindowClosesApplication() {
		return true;
	}

	@Override
	public boolean isRipSupported() {
		return true;
	}

	@Override
	public boolean isMultipleInstancesSupported() {
		return true;
	}

	@Override
	public IPlayerTrayIconsHandler getPlayerTrayIcons() {
		return getBeanFactory().getBean(CommonPlayerTrayIconsHandler.class);
	}

	@Override
	public boolean usesShortPathNames() {
		// Only 32-bit Windows support short path names
		return !is64Bit();
	}

	@Override
	public String getShortPathName(String filePathAndName) {
		if (initializeKernel32()) {
			try {
				return getShortPathNameW(filePathAndName);
			} catch (NoClassDefFoundError error) {
				// Avoid errors when using JNA
				Logger.error(error);
				kernel32error = true;
				return filePathAndName;
			}
		} else {
			// If there is a problem using Kernel32 return the same file path
			// and name
			return filePathAndName;
		}
	}

	/*
	 * Thanks to Paul Loy from the JNA mailing list ->
	 * https://jna.dev.java.net/servlets/ReadMsg?list=users&msgNo=928
	 * 
	 * Requires: JNA https://jna.dev.java.net/#getting_started
	 */
	/**
	 * Returns the 8.3 (DOS) file-/pathname for a given file. Only available for
	 * 32-bit Windows. The filename must include the path as whole and be passed
	 * as String.
	 * 
	 * @param longPathName
	 *            the long path name
	 * @param osManager
	 * 
	 * @return File/Path in 8.3 format
	 */
	private String getShortPathNameW(final String longPathName) {
		WString pathname = new WString(longPathName);
		int bufferSize = (pathname.length() * CHAR_BYTE_WIDTH)
				+ CHAR_BYTE_WIDTH;
		Memory buffer = new Memory(bufferSize);

		if (kernel32.GetShortPathNameW(pathname, buffer, bufferSize) == 0) {
			return "";
		}
		return buffer.getString(0, true);
	}

	/**
	 * Tries to initialize Kernel32
	 * 
	 * @return if initialization has been successful and kernel32 can be used
	 */
	private boolean initializeKernel32() {
		if (kernel32error) {
			return false;
		}
		if (kernel32 == null) {
			try {
				Native.setProtected(true);
				kernel32 = (Kernel32) Native.loadLibrary("Kernel32",
						Kernel32.class);
			} catch (UnsatisfiedLinkError e) {
				Logger.debug("kernel32 not found");
				kernel32error = true;
			}
		}
		return kernel32 != null && !kernel32error;
	}
}
