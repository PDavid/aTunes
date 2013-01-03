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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.IOException;

import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;


/**
 * Executes command before or after repository load / unload
 * @author alex
 *
 */
class LoadRepositoryCommandExecutor {
	
	/**
	 * Executes given command
	 * @param command
	 */
	public void execute(String command) {
        if (command != null && !command.trim().equals("")) {
            try {
                Process p = Runtime.getRuntime().exec(command);
                // Wait process to end
                p.waitFor();
                int rc = p.exitValue();
                Logger.info(StringUtils.getString("Command '", command, "' return code: ", rc));
            } catch (IOException e) {
                Logger.error(e);
            } catch (InterruptedException e) {
                Logger.error(e);
			}
        }
	}

}
