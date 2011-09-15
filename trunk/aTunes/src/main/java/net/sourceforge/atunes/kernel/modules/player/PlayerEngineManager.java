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

package net.sourceforge.atunes.kernel.modules.player;

import net.sourceforge.atunes.model.IOSManager;

/**
 * Manages several issues related to player engines
 * @author alex
 *
 */
public class PlayerEngineManager {

	/**
	 * Called when no player engine is available
	 * @param osManager
	 */
	static void manageNoPlayerEngine(IOSManager osManager) {
		// Delegate to specific OS code
		osManager.manageNoPlayerEngine();
	}
	
	/**
	 * Called when player engine is found (after searching or entering manually)
	 */
	public static void playerEngineFound() {
		PlayerHandler.getInstance().initialize();
	}
}
