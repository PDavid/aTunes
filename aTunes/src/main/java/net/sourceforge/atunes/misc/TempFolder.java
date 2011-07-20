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

package net.sourceforge.atunes.misc;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;
import org.commonjukebox.plugins.model.PluginApi;

/**
 * The Class TempFolder.
 */
@PluginApi
public class TempFolder {

    private static final TempFolder instance = new TempFolder();

    private TempFolder() {
    }

    /**
     * Returns singleton instance
     * 
     * @return
     */
    public static TempFolder getInstance() {
        return instance;
    }

    /**
     * Copies a file to temp folder.
     * 
     * @param srcFile
     *            the src file
     * 
     * @return File object to copied file in temp folder
     */
    public File copyToTempFolder(File srcFile) {
        File destFile = new File(StringUtils.getString(OsManager.getTempFolder(), OsManager.getFileSeparator(), srcFile.getName()));
        try {
            FileUtils.copyFile(srcFile, destFile);
        } catch (IOException e) {
            return null;
        }
        return destFile;
    }

    public File writeImageToTempFolder(RenderedImage image, String fileName) {
        try {
            File file = new File(OsManager.getTempFolder(), fileName);
            ImageIO.write(image, "png", file);
            return file;
        } catch (IOException e) {
            return null;
        }

    }

    /**
     * Removes a file from temp folder.
     * 
     * @param tempFile
     *            the temp file
     * 
     * @return true, if removes the file
     */
    public boolean removeFile(File tempFile) {
        return tempFile.delete();
    }

    /**
     * Removes all files from temp folder.
     */
    public void removeAllFiles() {
        File tempFolder = new File(OsManager.getTempFolder());
        File[] files = tempFolder.listFiles();
        for (File f : files) {
            if (f.delete()) {
            	Logger.info(f, " deleted");
            } else {
            	Logger.error(StringUtils.getString(f, " not deleted"));
            }
        }
    }
}
