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

package net.sourceforge.atunes.model;

import java.awt.Window;
import java.io.File;
import java.util.Collection;
import java.util.Map;

import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.AbstractCdToWavConverter;
import net.sourceforge.atunes.kernel.modules.hotkeys.AbstractHotkeys;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;

public class MockOSManager implements IOSManager {

	@Override
	public String getUserConfigFolder(boolean useWorkDir) {
		return null;
	}

	@Override
	public File getFileFromUserConfigFolder(String name, boolean useWorkDir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCustomConfigFolder(String folder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTempFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWorkingDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCustomRepositoryConfigFolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCustomRepositoryConfigFolder(String folder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLaunchCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLaunchParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setupFrame(IFrame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean areShadowBordersForToolTipsSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserHome() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileSeparator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean usesShortPathNames() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFullScreen(Window window, boolean fullscreen, IFrame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AbstractCdToWavConverter getCdToWavConverter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean testCdToWavConverter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<? extends AbstractHotkeys> getHotkeysListener() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getLineTerminator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean is64Bit() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPlayerEngineSupported(AbstractPlayerEngine engine) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPlayerEngineCommand(AbstractPlayerEngine engine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getPlayerEngineParameters(
			AbstractPlayerEngine engine) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getExternalToolsPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Class<? extends AbstractLookAndFeel>> getLookAndFeels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends AbstractLookAndFeel> getDefaultLookAndFeel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void manageNoPlayerEngine(IFrame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerEngineFound() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getOSProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOSProperty(String key, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean areTrayIconsSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean areMenuEntriesDelegated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClosingMainWindowClosesApplication() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRipSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWindowsVista() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWindows7() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOldWindows() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLinux() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMacOsX() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSolaris() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWindows() {
		// TODO Auto-generated method stub
		return false;
	}

}
