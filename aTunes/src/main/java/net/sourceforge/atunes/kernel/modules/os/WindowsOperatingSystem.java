/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.kernel.OperatingSystem;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.AbstractCdToWavConverter;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.Cdda2wav;
import net.sourceforge.atunes.kernel.modules.hotkeys.AbstractHotkeys;
import net.sourceforge.atunes.kernel.modules.hotkeys.WindowsHotkeys;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class WindowsOperatingSystem extends OperatingSystemAdapter {

	/**
     * Name of the Windows command
     */
    private static final String COMMAND_WINDOWS = "aTunes.exe";

    public WindowsOperatingSystem(OperatingSystem systemType) {
		super(systemType);
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
	public boolean areShadowBordersForToolTipsSupported() {
		return true;
	}
	
	@Override
	public boolean allowsShortPathNames() {
		return true;
	}
	
	@Override
	public void setFullScreen(Window window, boolean fullscreen) {
		if (systemType.isOldWindows()) {
			if (fullscreen) {
				window.setSize(GuiUtils.getDeviceWidth(), GuiUtils.getDeviceHeight());
			}
		}
	}
	
	@Override
	public AbstractCdToWavConverter getCdToWavConverter() {
		return new Cdda2wav();
	}
	
	@Override
	public Boolean testCdToWavConverter() {
		return Cdda2wav.pTestTool();
	}
	
	@Override
	public Class<? extends AbstractHotkeys> getHotkeysListener() {
		return WindowsHotkeys.isSupported() ? WindowsHotkeys.class : null;
	}
}
