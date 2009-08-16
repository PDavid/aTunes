/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.kernel.ApplicationFinishListener;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;

/**
 * The Class TempFolder.
 */
public class TempFolder implements ApplicationFinishListener {

    private static final TempFolder instance = new TempFolder();

    TempFolder() {
        Kernel.getInstance().addFinishListener(this);
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
        File destFile = new File(StringUtils.getString(SystemProperties.getTempFolder(), SystemProperties.FILE_SEPARATOR, srcFile.getName()));
        try {
            FileUtils.copyFile(srcFile, destFile);
        } catch (IOException e) {
            return null;
        }
        return destFile;
    }

    public File writeImageToTempFolder(RenderedImage image, String fileName) {
        try {
            File file = new File(SystemProperties.getTempFolder(), fileName);
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
        File tempFolder = new File(SystemProperties.getTempFolder());
        File[] files = tempFolder.listFiles();
        for (File f : files) {
            f.delete();
        }
    }

    @Override
    public void applicationFinish() {
        removeAllFiles();
    }
}
