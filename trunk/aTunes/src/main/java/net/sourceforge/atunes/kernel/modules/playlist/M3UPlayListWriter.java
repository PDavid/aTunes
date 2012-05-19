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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

class M3UPlayListWriter {
	
    /** The Constant M3U_HEADER. */
    private static final String M3U_HEADER = "#EXTM3U";

	private IOSManager osManager;
	
	/**
	 * @param osManager
	 */
	public M3UPlayListWriter(IOSManager osManager) {
		this.osManager = osManager;
	}

    /**
     * Writes a play list to a file in M3U format
     * 
     * @param playlist
     * @param file
     * @return
     */
	boolean writeM3U(IPlayList playlist, File file) {
        FileWriter writer = null;
        try {
            if (file.exists() && !file.delete()) {
            	Logger.error(StringUtils.getString(file, " not deleted"));
            }
            writer = new FileWriter(file);
            writer.append(StringUtils.getString(M3U_HEADER, osManager.getLineTerminator()));
            for (int i = 0; i < playlist.size(); i++) {
            	IAudioObject f = playlist.get(i);
                writer.append(StringUtils.getString(f.getUrl(), osManager.getLineTerminator()));
            }
            writer.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            ClosingUtils.close(writer);
        }
    }

}
