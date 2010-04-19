/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

/**
 * Utility class to unzip a file
 * 
 * Based on code from Piotr Gabryanczyk
 * http://piotrga.wordpress.com/2008/05/07/how-to-unzip-archive-in-java/
 * 
 * @author fleax
 * 
 */
public final class ZipUtils {

    private ZipUtils() {
    }

    /**
     * Unzips a file in a directory
     * 
     * @param archive
     * @param outputDir
     * @throws IOException
     */
    public static void unzipArchive(File archive, File outputDir) throws IOException {
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(archive);
            for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e.hasMoreElements();) {
                ZipEntry entry = e.nextElement();
                unzipEntry(zipfile, entry, outputDir);
            }
        } finally {
            ClosingUtils.close(zipfile);
        }
    }

    /**
     * Unzips a zip entry in a directory
     * 
     * @param zipfile
     * @param entry
     * @param outputDir
     * @throws IOException
     */
    private static void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException {
        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }
        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            createDir(outputFile.getParentFile());
        }
        BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        try {
            IOUtils.copy(inputStream, outputStream);
        } finally {
            ClosingUtils.close(outputStream);
            ClosingUtils.close(inputStream);
        }
    }

    /**
     * Utility method to create directory structure
     * 
     * @param dir
     */
    private static void createDir(File dir) throws IOException {
        if (!dir.mkdirs()) {
            throw new IOException("Can not create dir " + dir);
        }
    }
}
