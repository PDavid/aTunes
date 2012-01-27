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

import java.io.File;

import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.OperatingSystem;
import net.sourceforge.atunes.utils.StringUtils;

public class LinuxOperatingSystem extends OperatingSystemAdapter {

	/**
     * Name of the Linux command
     */
    private static final String COMMAND_LINUX = "aTunes.sh";
    
    /**
     * Command to be executed on Linux systems to launch mplayer. Mplayer should
     * be in $PATH
     */
    private static final String MPLAYER_LINUX_COMMAND = "mplayer";
    
    /**
     * @param systemType
     * @param osManager
     * @param lookAndFeelManager
     */
    public LinuxOperatingSystem(OperatingSystem systemType, IOSManager osManager) {
		super(systemType, osManager);
	}

	@Override
	public String getAppDataFolder() {
		return StringUtils.getString(getUserHome(), "/.atunes");
	}

	@Override
	public String getLaunchCommand() {
		return new File(StringUtils.getString("./", COMMAND_LINUX)).getAbsolutePath();
	}
	
	@Override
	public String getLaunchParameters() {
		return null;
	}
	
	@Override
	public boolean isPlayerEngineSupported(IPlayerEngine engine) {
		return true; // all supported
	}
	
	@Override
	public String getPlayerEngineCommand(IPlayerEngine engine) {
		return engine instanceof MPlayerEngine ? MPLAYER_LINUX_COMMAND : null;
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
	
	@Override
	public boolean isMultipleInstancesSupported() {
		return true;
	}
}
