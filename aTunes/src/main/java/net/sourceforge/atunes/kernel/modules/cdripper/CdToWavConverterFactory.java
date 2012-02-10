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

package net.sourceforge.atunes.kernel.modules.cdripper;

import net.sourceforge.atunes.model.IOSManager;

final class CdToWavConverterFactory {
	
	private CdToWavConverterFactory() {}
	
    /**
     * creates a new CdToWavConverter-object. The implementation depends on the
     * users OS.
     * @param osManager
     * @return
     */
    public static AbstractCdToWavConverter createNewConverterForOS(IOSManager osManager) {
    	AbstractCdToWavConverter osConverter = null;
    	if (osManager.isMacOsX()) {
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
