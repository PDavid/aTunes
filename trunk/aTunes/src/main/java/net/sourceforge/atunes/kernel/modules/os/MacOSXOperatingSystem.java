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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.system.macos.MacOSXLookAndFeel;
import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.utils.StringUtils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MacOSXOperatingSystem extends OperatingSystemAdapter implements ApplicationContextAware {

	/**
     * Name of the MacOsX command
     */
    private static final String COMMAND_MACOSX = "/usr/bin/open";
    
    protected static final String MPLAYER_COMMAND = "mplayer.command";
    
    private ApplicationContext context;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	this.context = applicationContext;
    }
    
	@Override
	public String getAppDataFolder() {
		return StringUtils.getString(getUserHome(), "/Library/Preferences/aTunes");
	}

	@Override
	public String getLaunchCommand() {
		return COMMAND_MACOSX;
	}
	
	@Override
	public String getLaunchParameters() {
		return System.getProperty("atunes.package"); // This property is set in Info.plist
	}	
	
	@Override
	public void setUpFrame(IFrame frame) {
		context.getBean(MacOSXInitializer.class).initialize();
	}
	
	@Override
	public boolean isPlayerEngineSupported(IPlayerEngine engine) {
		return engine instanceof MPlayerEngine ? true : false; // Only MPLAYER
	}
	
	@Override
	public String getPlayerEngineCommand(IPlayerEngine engine) {
		return engine instanceof MPlayerEngine ? osManager.getOSProperty(MPLAYER_COMMAND) : null;
	}

	/**
	 * Returns list of supported look and feels
	 * @return
	 */
	@Override
	public Map<String, Class<? extends ILookAndFeel>> getSupportedLookAndFeels() {
	    Map<String, Class<? extends ILookAndFeel>> lookAndFeels = new HashMap<String, Class<? extends ILookAndFeel>>();
        lookAndFeels.put(SubstanceLookAndFeel.SUBSTANCE, SubstanceLookAndFeel.class);
        lookAndFeels.put(MacOSXLookAndFeel.SYSTEM, MacOSXLookAndFeel.class);
        return lookAndFeels;
	}

	/**
	 * Returns default look and feel class
	 * @return
	 */
	@Override
	public Class<? extends ILookAndFeel> getDefaultLookAndFeel() {
		return SubstanceLookAndFeel.class;
	}
	
	@Override
	public void manageNoPlayerEngine(IFrame frame) {
		MacOSXPlayerSelectionDialog dialog = new MacOSXPlayerSelectionDialog(frame, osManager, context.getBean(ILookAndFeelManager.class), context.getBean(IDesktop.class));
		dialog.setVisible(true);
	}
	
	@Override
	public boolean areTrayIconsSupported() {
		return true;
	}
	
	@Override
	public boolean areMenuEntriesDelegated() {
		return true;
	}
	
	@Override
	public boolean isClosingMainWindowClosesApplication() {
		return false;
	}
	
	@Override
	public boolean isRipSupported() {
		return false;
	}
	
	@Override
	public boolean isMultipleInstancesSupported() {
		return false;
	}

}
