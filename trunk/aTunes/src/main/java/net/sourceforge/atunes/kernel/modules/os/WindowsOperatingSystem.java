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

package net.sourceforge.atunes.kernel.modules.os;

import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.xine.XineEngine;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.OperatingSystem;
import net.sourceforge.atunes.utils.StringUtils;

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
    private static final String MPLAYER_WIN_COMMAND = WINDOWS_TOOLS_DIR + "/mplayer.exe";

    private static final String WINOPTPRIORITY = "-priority";
    
    private static final String WINOPTPRIORITY_DEFAULT = "abovenormal";

    /**
     * @param systemType
     * @param osManager
     */
    public WindowsOperatingSystem(OperatingSystem systemType, IOSManager osManager) {
		super(systemType, osManager);
	}

	@Override
	public String getAppDataFolder() {
		return StringUtils.getString(System.getenv("APPDATA"), "/atunes");
	}

	@Override
	public String getLaunchCommand() {
		return new File(StringUtils.getString("./", COMMAND_WINDOWS)).getAbsolutePath();
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
	public boolean usesShortPathNames() {
		return true;
	}
	
	@Override
	public void setFullScreen(Window window, boolean fullscreen, IFrame frame) {
		if (systemType.isOldWindows() && fullscreen) {
			window.setSize(GuiUtils.getDeviceWidth(), GuiUtils.getDeviceHeight());
		}
	}
	
	@Override
	public boolean isPlayerEngineSupported(IPlayerEngine engine) {
		return engine instanceof XineEngine ? false : true; // Xine is not supported
	}
	
	@Override
	public String getPlayerEngineCommand(IPlayerEngine engine) {		
		return engine instanceof MPlayerEngine ? MPLAYER_WIN_COMMAND : null;
	}
	
	@Override
	public Collection<String> getPlayerEngineParameters(IPlayerEngine engine) {
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
	public void setUpFrame(IFrame frame) {
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

}
