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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.kernel.OperatingSystem;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.Cdda2wav;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.Cdparanoia;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.StringUtils;

public class SolarisOperatingSystem extends OperatingSystemAdapter {

	/**
     * Name of the Solaris command
     */
    private static final String COMMAND_SOLARIS = "aTunes.sh";
    
    /**
     * Command to be executed on Solaris systems to launch mplayer. Note the
     * workaround with the options - Java6 on Solaris Express appears to require
     * these options added separately.
     */
    private static final String MPLAYER_SOLARIS_COMMAND = "mplayer";
    
    private static final String MPLAYER_SOLARISOPTAO = "-ao";
    
    private static final String MPLAYER_SOLARISOPTTYPE = "sun"; 

    public SolarisOperatingSystem(OperatingSystem systemType) {
		super(systemType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getAppDataFolder() {
		return StringUtils.getString(getUserHome(), "/.atunes");
	}

	@Override
	public String getLaunchCommand() {
		return new File(StringUtils.getString("./", COMMAND_SOLARIS)).getAbsolutePath();
	}
	
	@Override
	public String getLaunchParameters() {
		return null;
	}
	
	@Override
	public boolean isPlayerEngineSupported(AbstractPlayerEngine engine) {
		return true; // all supported
	}
	
	@Override
	public String getPlayerEngineCommand(AbstractPlayerEngine engine) {
		return engine instanceof MPlayerEngine ? MPLAYER_SOLARIS_COMMAND : null;
	}
	
	@Override
	public Collection<String> getPlayerEngineParameters(AbstractPlayerEngine engine) {
		if (engine instanceof MPlayerEngine) {
			List<String> parameters = new ArrayList<String>(2);
			parameters.add(MPLAYER_SOLARISOPTAO);
			parameters.add(MPLAYER_SOLARISOPTTYPE);
			return parameters;
		} else {
			return super.getPlayerEngineParameters(engine);
		}
	}
	
	@Override
	public boolean areTrayIconsSupported() {
		return true;
	}

	@Override
	public boolean testCdToWavConverter() {
		if (Cdda2wav.pTestTool()) {
			return true;
		}
		return Cdparanoia.pTestTool();
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
