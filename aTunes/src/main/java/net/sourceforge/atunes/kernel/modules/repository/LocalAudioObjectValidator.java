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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.io.FileFilter;

import net.sourceforge.atunes.kernel.modules.repository.data.Format;

public class LocalAudioObjectValidator {

    /**
     * Checks if is valid audio file.
     * 
     * @param file
     *            the file
     * 
     * @return true, if is valid audio file
     */
    public static boolean isValidAudioFile(File file) {
        return !file.isDirectory()
                && isValidAudioFile(file.getName(), Format.MP3, Format.OGG, Format.MP4_1, Format.MP4_2, Format.WAV, Format.WMA, Format.FLAC, Format.REAL_1, Format.REAL_2, Format.APE,
                        Format.MPC);
    }
    
    public static FileFilter validAudioFileFilter() {
    	return new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return isValidAudioFile(pathname);
			}
		};
    }
    
    /**
     * Checks if a file is a valid audio file given its name
     * 
     * @param fileName
     * @param formats
     * @return if the file is a valid audio file
     */
    public static boolean isValidAudioFile(String fileName, Format... formats) {
        String path = fileName.toLowerCase();
        for (Format format : formats) {
            if (path.endsWith(format.getExtension())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is valid audio file.
     * 
     * @param file
     *            the file
     * 
     * @return true, if is valid audio file
     */
    public static boolean isValidAudioFile(String file) {
        File f = new File(file);
        return f.exists() && isValidAudioFile(f);
    }



}
