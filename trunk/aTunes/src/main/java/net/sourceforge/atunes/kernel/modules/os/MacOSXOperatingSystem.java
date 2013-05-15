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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.system.macos.MacOSXLookAndFeel;
import net.sourceforge.atunes.kernel.modules.player.mplayer.MPlayerEngine;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.IPlayerTrayIconsHandler;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Adapter for Mac OS X
 * 
 * @author alex
 * 
 */
public class MacOSXOperatingSystem extends OperatingSystemAdapter {

	protected static final String MPLAYER_COMMAND = "mplayer.command";

	private IDialogFactory dialogFactory;

	private IPlayerTrayIconsHandler macPlayerTrayIconsHandler;

	/**
	 * @param macPlayerTrayIconsHandler
	 */
	public void setMacPlayerTrayIconsHandler(
			final IPlayerTrayIconsHandler macPlayerTrayIconsHandler) {
		this.macPlayerTrayIconsHandler = macPlayerTrayIconsHandler;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	@Override
	public String getAppDataFolder() {
		return StringUtils.getString(getUserHome(),
				"/Library/Preferences/aTunes");
	}

	@Override
	public String getLaunchCommand() {
		// -n to allow start another instance
		return "/usr/bin/open -n";
	}

	@Override
	public String getLaunchParameters() {
		// This property is set in Info.plist
		return System.getProperty("atunes.package");
	}

	@Override
	public void setupFrame(final IFrame frame) {
		// Can't be created when creating this object
		getBeanFactory().getBean(MacOSXInitializer.class).initialize(frame);
	}

	@Override
	public boolean isPlayerEngineSupported(final IPlayerEngine engine) {
		return engine instanceof MPlayerEngine ? true : false; // Only MPLAYER
	}

	@Override
	public String getPlayerEngineCommand(final IPlayerEngine engine) {
		return engine instanceof MPlayerEngine ? getOsProperties().getProperty(
				MPLAYER_COMMAND) : null;
	}

	/**
	 * Returns list of supported look and feels
	 * 
	 * @return
	 */
	@Override
	public Map<String, Class<? extends ILookAndFeel>> getSupportedLookAndFeels() {
		Map<String, Class<? extends ILookAndFeel>> lookAndFeels = new HashMap<String, Class<? extends ILookAndFeel>>();
		lookAndFeels.put(SubstanceLookAndFeel.SUBSTANCE,
				SubstanceLookAndFeel.class);
		lookAndFeels.put(MacOSXLookAndFeel.SYSTEM, MacOSXLookAndFeel.class);
		return lookAndFeels;
	}

	/**
	 * Returns default look and feel class
	 * 
	 * @return
	 */
	@Override
	public Class<? extends ILookAndFeel> getDefaultLookAndFeel() {
		return SubstanceLookAndFeel.class;
	}

	@Override
	public void manageNoPlayerEngine(final IFrame frame) {
		this.dialogFactory.newDialog(MacOSXPlayerSelectionDialog.class)
				.showDialog();
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
		return true;
	}

	@Override
	public IPlayerTrayIconsHandler getPlayerTrayIcons() {
		return this.macPlayerTrayIconsHandler;
	}

	@Override
	public boolean areTrayIconsColorsSupported() {
		return false;
	}
}
