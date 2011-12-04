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
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class Mp4Encoder.
 */
public class Mp4Encoder extends AbstractEncoder {

    /** The format name of this encoder */
    public static final String FORMAT_NAME = "FAAC_MP4";
    public static final String OGGENC = "faac";
    public static final String OUTPUT = "-o";
    public static final String WRAP = "-w";
    public static final String TITLE = "--title";
    public static final String ARTIST = "--artist";
    public static final String ALBUM = "--album";
    public static final String QUALITY = "-q";
    public static final String VERSION = "--help";
    static final String[] MP4_QUALITY = { "50", "100", "150", "200", "250", "300", "350", "400", "450", "500" };

    static final String DEFAULT_MP4_QUALITY = "200";

    private Process p;

    /**
     * Creates a new mp4 encoder
     */
    public Mp4Encoder() {
    	super("m4a", MP4_QUALITY, DEFAULT_MP4_QUALITY, FORMAT_NAME, Context.getBean(ILocalAudioObjectValidator.class));
	}

    @Override
    public boolean testEncoder() {
        // Test for faac
        BufferedReader stdInput = null;
        try {
            Process p = new ProcessBuilder(StringUtils.getString(getOsManager().getExternalToolsPath(), OGGENC)).start();
            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line = null;
            while ((line = stdInput.readLine()) != null) {
            	Logger.debug(line);
            }

            int code = p.waitFor();
            if (code != 1) {
                return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
        	return false;
		} finally {
            ClosingUtils.close(stdInput);
        }
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
            command.add(StringUtils.getString(getOsManager().getExternalToolsPath(), OGGENC));
            command.add(OUTPUT);
            command.add(mp4File.getAbsolutePath());
            command.add(QUALITY);
            command.add(getQuality());
            command.add(WRAP);
            command.add(wavFile.getAbsolutePath());
            p = new ProcessBuilder(command).start();
            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s = null;
            int percent = -1;

            // Read progress
            while ((s = stdInput.readLine()) != null) {
                if (getListener() != null) {
                    if (s.matches(".*(...%).*")) {
                        // Percent values can be for example 0.3% or 0,3%, so be careful with "." and ","
                        int decimalPointPosition = s.indexOf('%');
                        int aux = Integer.parseInt((s.substring(s.indexOf('(') + 1, decimalPointPosition).trim()));
                        if (aux != percent) {
                            percent = aux;
                            final int percentHelp = percent;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                	getListener().notifyProgress(percentHelp);
                                }
                            });
                        }
                    } else if (s.startsWith("Done")) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                            	getListener().notifyProgress(100);
                            }
                        });
                    }
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

        } catch (InterruptedException e) {
            Logger.error(StringUtils.getString("Process execution caused exception ", e));
            return false;
        } catch (IOException e) {
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
