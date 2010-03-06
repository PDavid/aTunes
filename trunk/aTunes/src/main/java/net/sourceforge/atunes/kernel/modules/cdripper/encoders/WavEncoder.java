/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.cdripper.encoders;

import java.io.File;

import net.sourceforge.atunes.kernel.modules.cdripper.ProgressListener;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class WavEncoder.
 */
public class WavEncoder implements Encoder {

    private Logger logger;
    /** The format name of this encoder */
    public static final String FORMAT_NAME = "WAV";

    /**
     * Encode the wav file and tags it using entagged.
     * 
     * @param wavFile
     *            The filename and path of the wav file that should be encoded
     * @param wavFile2
     *            The name of the new file to be created
     * @param title
     *            The title of the song (only title, not artist and album)
     * @param trackNumber
     *            The track number of the song
     * @param artists
     *            the artists
     * @param composer
     *            the composer
     * 
     * @return Returns true if encoding was successfull, false otherwise.
     */
    @Override
    public boolean encode(File wavFile, File wavFile2, String title, int trackNumber, String artists, String composer) {
        getLogger().info(LogCategories.WAV, StringUtils.getString("Wav encoding started... ", wavFile.getName(), " -> ", wavFile2.getName()));
        try {
            wavFile.renameTo(wavFile2);
            getLogger().info(LogCategories.WAV, "Renamed ok!!");
            return true;

        } catch (Exception e) {
            getLogger().error(LogCategories.WAV, StringUtils.getString("Exception ", e));
            return false;
        }
    }

    /**
     * Gets the extension of encoded files.
     * 
     * @return Returns the extension of the encoded file
     */
    @Override
    public String getExtensionOfEncodedFiles() {
        return "wav";
    }

    @Override
    public void setAlbum(String album) {
        // do nothing
    }

    @Override
    public void setArtist(String artist) {
        // do nothing
    }

    @Override
    public void setGenre(String genre) {
        // do nothing
    }

    @Override
    public void setListener(ProgressListener listener) {
        // do nothing
    }

    @Override
    public void setQuality(String quality) {
        // do nothing
    }

    @Override
    public void setYear(int year) {
        // do nothing
    }

    @Override
    public void stop() {
        // do nothing
    }

    @Override
    public String[] getAvailableQualities() {
        return new String[0];
    }

    @Override
    public String getDefaultQuality() {
        return "";
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME;
    }

    public static boolean testTool() {
        // This encoder is always available
        return true;
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
