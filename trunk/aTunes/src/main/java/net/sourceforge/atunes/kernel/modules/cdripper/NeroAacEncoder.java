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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class NeroAacEncoder.
 */
public class NeroAacEncoder extends AbstractEncoder {

    /** The format name of this encoder */
    public static final String FORMAT_NAME = "Nero_AAC";
    public static final String NERO_AAC = "neroAacEnc";
    public static final String IGNORE_LENGTH = "-ignorelength";
    public static final String INPUT = "-if";
    public static final String OUTPUT = "-of";
    //public static final String WRAP = "-w";
    public static final String QUALITY = "-q";
    public static final String VERSION = "-help";
    static final String[] NERO_AAC_QUALITY = { "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0" };
    static final String DEFAULT_NERO_AAC_QUALITY = "0.4";

    private Process p;

    @Override
    public boolean testEncoder() {
        // Test for Nero Aac encoder
        BufferedReader stdInput = null;
        try {
            Process p = new ProcessBuilder(StringUtils.getString(getOsManager().getExternalToolsPath(), NERO_AAC), VERSION).start();
            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line = null;
            while ((line = stdInput.readLine()) != null) {
            	Logger.debug(line);
            }

            int code = p.waitFor();
            if (code != 0) {
                return false;
            }
            return true;
        } catch (IOException e) {
        	Logger.error(e);
            return false;
        } catch (InterruptedException e) {
        	Logger.error(e);
        	return false;
		} finally {
            ClosingUtils.close(stdInput);
        }
    }
    
    /**
     * Creates a new nero aac encoder
     */
    public NeroAacEncoder() {
    	super("m4a", NERO_AAC_QUALITY, DEFAULT_NERO_AAC_QUALITY, FORMAT_NAME, Context.getBean(ITagHandler.class));
	}

    /**
     * Encode the wav file and tags it using entagged.
     * 
     * @param wavFile
     *            The filename and path of the wav file that should be encoded
     * @param mp4File
     *            The name of the new file to be created
     * @param title
     *            The title of the song (only title, not artist and album)
     * @param trackNumber
     *            The track number of the song
     * @param artist
     *            the artist
     * @param composer
     *            the composer
     * 
     * @return Returns true if encoding was successfull, false otherwise.
     */
    @Override
    public boolean encode(File wavFile, File mp4File, String title, int trackNumber, String artist, String composer) {
        Logger.info(StringUtils.getString("Mp4 encoding process started... ", wavFile.getName(), " -> ", mp4File.getName()));
        BufferedReader stdInput = null;
        try {
            List<String> command = new ArrayList<String>();
            command.add(StringUtils.getString(getOsManager().getExternalToolsPath(), NERO_AAC));
            command.add(QUALITY);
            command.add(getQuality());
            //command.add(IGNORE_LENGTH);
            command.add(INPUT);
            command.add(wavFile.getAbsolutePath());
            command.add(OUTPUT);
            command.add(mp4File.getAbsolutePath());
            p = new ProcessBuilder(command).start();
            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // Required to avoid deadlook under Windows
            String line = null;
            while ((line = stdInput.readLine()) != null) {
            	Logger.debug(line);
                // Enable indeterminate progress bar
                if (getListener() != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                        	getListener().notifyProgress(-1);
                        }
                    });
                }
            }
            int code = p.waitFor();
            if (code != 0) {
                Logger.error(StringUtils.getString("Process returned code ", code));
                return false;
            }

            // Gather the info and write the tag
            boolean tagOk = setTag(mp4File, title, trackNumber, artist, composer);
            
            if (!tagOk) {
            	return false;
            }

            Logger.info("Encoded ok!!");
            return true;

        } catch (Exception e) {
            Logger.error(StringUtils.getString("Process execution caused exception ", e));
            return false;
        } finally {
            ClosingUtils.close(stdInput);
        }
    }

    @Override
    public void stop() {
        if (p != null) {
            p.destroy();
        }
    }
}
