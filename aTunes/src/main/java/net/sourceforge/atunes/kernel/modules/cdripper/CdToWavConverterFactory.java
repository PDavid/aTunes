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

package net.sourceforge.atunes.kernel.modules.cdripper;

import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IOSManager;

/**
 * Creates a cd to wav converter
 * @author alex
 *
 */
public final class CdToWavConverterFactory {
	
	private IApplicationArguments applicationArguments;
	
	private IOSManager osManager;
	
	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(
			IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
    /**
     * creates a new CdToWavConverter-object. The implementation depends on the users OS.
     * @param applicationArguments
     * @param osManager
     * @return
     */
    public AbstractCdToWavConverter createNewConverterForOS() {
    	AbstractCdToWavConverter osConverter = null;
    	if (applicationArguments.isSimulateCD()) {
    		osConverter = new FakeCDToWavConverter();
    	} else if (osManager.isMacOsX()) {
    		osConverter = new Cdparanoia();
    	} else if (osManager.isWindows()) {
    		osConverter = new Cdda2wav(osManager);
    	}
    	
    	if (osConverter != null) {
    		return osConverter;
    	} else if (Cdda2wav.pTestTool(osManager)) {
            return new Cdda2wav(osManager);
        } else {
            return new Cdparanoia();
        }
    }
}
