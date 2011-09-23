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

package net.sourceforge.atunes.utils;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITemporalDiskStorage;

import org.apache.commons.io.FileUtils;

public class TempFolder implements ITemporalDiskStorage {

    private IOSManager osManager;
    
    @Override
	public File addFile(File srcFile) {
        File destFile = new File(StringUtils.getString(osManager.getTempFolder(), osManager.getFileSeparator(), srcFile.getName()));
        try {
            FileUtils.copyFile(srcFile, destFile);
        } catch (IOException e) {
            return null;
        }
        return destFile;
    }

    @Override
	public File addImage(RenderedImage image, String fileName) {
        try {
            File file = new File(osManager.getTempFolder(), fileName);
            ImageIO.write(image, "png", file);
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
	public boolean removeFile(File tempFile) {
        return tempFile.delete();
    }

    @Override
	public void removeAll() {
        File tempFolder = new File(osManager.getTempFolder());
        File[] files = tempFolder.listFiles();
        for (File f : files) {
            if (f.delete()) {
            	Logger.info(f, " deleted");
            } else {
            	Logger.error(StringUtils.getString(f, " not deleted"));
            }
        }
    }
    
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
}
