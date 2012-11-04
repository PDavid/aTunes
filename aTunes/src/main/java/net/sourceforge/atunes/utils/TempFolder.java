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

package net.sourceforge.atunes.utils;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITemporalDiskStorage;

import org.apache.commons.io.FileUtils;

/**
 * A temporal folder where store files removed when application finishes
 * 
 * @author alex
 * 
 */
public class TempFolder implements ITemporalDiskStorage {

    private IOSManager osManager;

    @Override
    public File addFile(final File srcFile) {
	File destFile = new File(StringUtils.getString(
		osManager.getTempFolder(), osManager.getFileSeparator(),
		srcFile.getName()));
	try {
	    FileUtils.copyFile(srcFile, destFile);
	} catch (IOException e) {
	    return null;
	}
	return destFile;
    }

    @Override
    public File addImage(final RenderedImage image, final String fileName) {
	try {
	    File file = new File(osManager.getTempFolder(), fileName);
	    ImageIO.write(image, "png", file);
	    return file;
	} catch (IOException e) {
	    return null;
	}
    }

    @Override
    public boolean removeFile(final File tempFile) {
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

    /**
     * @param osManager
     */
    public void setOsManager(final IOSManager osManager) {
	this.osManager = osManager;
    }
}
