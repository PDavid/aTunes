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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.cdripper.ProgressListener;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.DefaultTag;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.AbstractTag;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.TagModifier;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class NeroAacEncoder.
 */
public class NeroAacEncoder implements Encoder {

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

    private Logger logger;
    private ProgressListener listener;
    private Process p;
    private String albumArtist;
    private String album;
    private int year;
    private String genre;
    private String quality;

    /**
     * Test the presence of the ogg encoder oggenc.
     * 
     * @return Returns true if oggenc was found, false otherwise.
     */
    public static boolean testTool() {
        // Test for Nero Aac encoder
        BufferedReader stdInput = null;
        try {
            Process p;
            // Test for Windows system checks in ...\aTunes\win_tools only!
            if (SystemProperties.OS == OperatingSystem.WINDOWS) {
                p = new ProcessBuilder(StringUtils.getString(Constants.WINDOWS_TOOLS_DIR, SystemProperties.FILE_SEPARATOR, NERO_AAC), VERSION).start();
            } else {
                p = new ProcessBuilder(NERO_AAC, VERSION).start();
            }
            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while (stdInput.readLine() != null) {
                // Nothing to do
            }

            int code = p.waitFor();
            if (code != 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
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
        getLogger().info(LogCategories.NERO_AAC, StringUtils.getString("Mp4 encoding process started... ", wavFile.getName(), " -> ", mp4File.getName()));
        BufferedReader stdInput = null;
        try {
            List<String> command = new ArrayList<String>();
            if (SystemProperties.OS == OperatingSystem.WINDOWS) {
                command.add(StringUtils.getString(Constants.WINDOWS_TOOLS_DIR, SystemProperties.FILE_SEPARATOR, NERO_AAC));
            } else {
                command.add(NERO_AAC);
            }
            command.add(QUALITY);
            command.add(quality);
            //command.add(IGNORE_LENGTH);
            command.add(INPUT);
            command.add(wavFile.getAbsolutePath());
            command.add(OUTPUT);
            command.add(mp4File.getAbsolutePath());
            p = new ProcessBuilder(command).start();
            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // Required to avoid deadlook under Windows
            while (stdInput.readLine() != null) {
                // Enable indeterminate progress bar
                if (listener != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            listener.notifyProgress(-1);
                        }
                    });
                }
            }
            int code = p.waitFor();
            if (code != 0) {
                getLogger().error(LogCategories.FAAC, StringUtils.getString("Process returned code ", code));
                return false;
            }

            // Gather the info and write the tag
            try {
                AudioFile audiofile = new AudioFile(mp4File.getAbsolutePath());
                AbstractTag tag = new DefaultTag();

                tag.setAlbum(album);
                tag.setAlbumArtist(albumArtist);
                tag.setArtist(artist);
                tag.setComposer(composer);
                tag.setYear(year);
                tag.setGenre(genre);
                tag.setTitle(title);
                tag.setTrackNumber(trackNumber);

                TagModifier.setInfo(audiofile, tag);

            } catch (Exception e) {
                getLogger().error(LogCategories.NERO_AAC, StringUtils.getString("Jaudiotagger: Process execution caused exception ", e));
                return false;
            }

            getLogger().info(LogCategories.NERO_AAC, "Encoded ok!!");
            return true;

        } catch (Exception e) {
            getLogger().error(LogCategories.NERO_AAC, StringUtils.getString("Process execution caused exception ", e));
            return false;
        } finally {
            ClosingUtils.close(stdInput);
        }
    }

    /**
     * Gets the extension of encoded files.
     * 
     * @return Returns the extension of the encoded file
     */
    @Override
    public String getExtensionOfEncodedFiles() {
        return "m4a";
    }

    @Override
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * Sets the album artist.
     * 
     * @param albumArtist
     *            the new album artist
     */
    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    @Override
    public void setArtist(String artist) {
        this.albumArtist = artist;
    }

    @Override
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public void setListener(ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public void setQuality(String quality) {
        this.quality = quality;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public void stop() {
        if (p != null) {
            p.destroy();
        }
    }

    @Override
    public String[] getAvailableQualities() {
        return NERO_AAC_QUALITY;
    }

    @Override
    public String getDefaultQuality() {
        return DEFAULT_NERO_AAC_QUALITY;
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME;
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
