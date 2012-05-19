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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

class M3UPlayListReader {
	
    private static final String HTTP_PREFIX = "http://";
    
    /** The Constant M3U_START_COMMENT. */
    private static final String M3U_START_COMMENT = "#";

    /** The Constant M3U_UNIX_ABSOLUTE_PATH. */
    private static final String M3U_UNIX_ABSOLUTE_PATH = "/";

    /** The Constant M3U_WINDOWS_ABSOLUTE_PATH. */
    private static final String M3U_WINDOWS_ABSOLUTE_PATH = ":\\";
    
    /** The Constant UNC_ABSOLUTE_PATH. */
    private static final String M3U_UNC_ABSOLUTE_PATH = "\\\\";

	private IOSManager osManager;
	
	/**
	 * @param osManager
	 */
	M3UPlayListReader(IOSManager osManager) {
		this.osManager = osManager;
	}

    /**
     * This function reads the filenames from the playlist file (m3u). It will
     * return all filenames with absolute path. For this playlists with relative
     * pathname must be detected and the path must be added. Current problem of
     * this implementation is clearly the charset used. Java reads/writes in the
     * charset used by the OS! But for many *nixes this is UTF8, while Windows
     * will use CP1252 or similar. So, as long as we have the same charset
     * encoding or do not use any special character playlists will work
     * (absolute filenames with a pathname incompatible with the current OS are
     * not allowed), but as soon as we have say french accents in the filename a
     * playlist created under an application using CP1252 will not import
     * correctly on a UTF8 system (better: the files with accents in their
     * filename will not).
     * 
     * Only playlist with local files have been tested! Returns a list of file
     * names contained in a play list file
     * 
     * @param file
     *            The playlist file
     * 
     * @return Returns an List of files of the playlist as String.
     */
	List<String> read(File file) {

        BufferedReader br = null;
        try {
            List<String> result = new ArrayList<String>();
            br = new BufferedReader(new FileReader(file));
            String line;
            // Do look for the first uncommented line
            line = br.readLine();
            while (line != null && line.startsWith(M3U_START_COMMENT)) {
                // Go to next line
                line = br.readLine();
            }
            if (line == null) {
                return Collections.emptyList();
            }
            // First absolute path. Windows path detection is very rudimentary, but should work
            if (line.startsWith(M3U_WINDOWS_ABSOLUTE_PATH, 1) || 
            	line.startsWith(M3U_UNIX_ABSOLUTE_PATH) ||
            	line.startsWith(M3U_UNC_ABSOLUTE_PATH)) {
                // Let's check if we are at least using the right OS. Maybe a message should be returned, but for now it doesn't. UNC paths are allowed for all OS
                if (((osManager.isWindows()) && line.startsWith(M3U_UNIX_ABSOLUTE_PATH))
                        || (!(osManager.isWindows()) && line.startsWith(M3U_WINDOWS_ABSOLUTE_PATH, 1))) {
                    return Collections.emptyList();
                }
                result.add(line);
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(M3U_START_COMMENT) && !line.isEmpty()) {
                        result.add(line);
                    }
                }
            }
            // The path is relative! We must add it to the filename
            // But if entries are HTTP URLS then don't add any path
            else {
                String path = file.getParent() + osManager.getFileSeparator();
                result.add(line.startsWith(HTTP_PREFIX) ? line : StringUtils.getString(path, line));
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(M3U_START_COMMENT) && !line.isEmpty()) {
                        result.add(line.startsWith(HTTP_PREFIX) ? line : StringUtils.getString(path, line));
                    }
                }
            }
            // Return the filenames with their absolute path
            return result;
        } catch (IOException e) {
            return Collections.emptyList();
        } finally {
            ClosingUtils.close(br);
        }
    }

}
