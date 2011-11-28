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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.File;

import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class WavEncoder.
 */
public class WavEncoder extends AbstractEncoder {

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
        Logger.info(StringUtils.getString("Wav encoding started... ", wavFile.getName(), " -> ", wavFile2.getName()));
        try {
            if (wavFile.renameTo(wavFile2)) {
                Logger.info("Renamed ok!!");
            } else {
                Logger.error(StringUtils.getString(wavFile, " Renamed failed!!"));
            }
            return true;

        } catch (Exception e) {
            Logger.error(StringUtils.getString("Exception ", e));
            return false;
        }
    }

    /**
     * @param localAudioObjectFactory
     */
    public WavEncoder(ILocalAudioObjectFactory localAudioObjectFactory) {
    	super("wav", new String[0], "", FORMAT_NAME, localAudioObjectFactory);
    }
 
    @Override
    public void stop() {
        // do nothing
    }

    public static boolean testTool() {
        // This encoder is always available
        return true;
    }
}
