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

package net.sourceforge.atunes.kernel.modules.cdripper.encoders;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.cdripper.EncoderProgressListener;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.DefaultTag;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.TagModifier;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class OggEncoder.
 */
public class OggEncoder implements Encoder {

    /** The format name of this encoder */
    public static final String FORMAT_NAME = "OGG";
    public static final String OGGENC = "oggenc";
    public static final String OUTPUT = "-o";
    public static final String ADD_TAG = "-t";
    public static final String APPEND = "-a";
    public static final String TITLE = "TITLE=";
    public static final String ARTIST = "ARTIST=";
    public static final String ALBUM = "ALBUM=";
    public static final String TRACK = "TRACKNUMBER=";
    public static final String QUALITY = "-q";
    public static final String VERSION = "--version";
    static final String[] OGG_QUALITIES = { "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
    static final String DEFAULT_OGG_QUALITY = "5";

    private Logger logger = new Logger();
    private Process p;
    EncoderProgressListener listener;
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
        if (SystemProperties.OS == OperatingSystem.WINDOWS) {
            return true;
        }

        BufferedReader stdInput = null;
        // Test for oggenc
        try {
            Process p;
            // Test for Windows system checks in ...\aTunes\win_tools only!
            if (SystemProperties.OS == OperatingSystem.WINDOWS) {
                p = new ProcessBuilder(StringUtils.getString(Constants.WINDOWS_TOOLS_DIR, SystemProperties.FILE_SEPARATOR, OGGENC), VERSION).start();
            } else {
                p = new ProcessBuilder(OGGENC, VERSION).start();
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
     * @param oggFile
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
    public boolean encode(File wavFile, File oggFile, String title, int trackNumber, String artist, String composer) {
        logger.info(LogCategories.OGGENC, StringUtils.getString("Ogg encoding process started... ", wavFile.getName(), " -> ", oggFile.getName()));
        BufferedReader stdInput = null;
        try {
            // Encode the file using oggenc. We could pass the infos for the tag, but 
            // oggenc is very difficult with special characters so we don't use it.
            List<String> command = new ArrayList<String>();
            if (SystemProperties.OS == OperatingSystem.WINDOWS) {
                command.add(StringUtils.getString(Constants.WINDOWS_TOOLS_DIR, SystemProperties.FILE_SEPARATOR, OGGENC));
            } else {
                command.add(OGGENC);
            }
            command.add(wavFile.getAbsolutePath());
            command.add(OUTPUT);
            command.add(oggFile.getAbsolutePath());
            command.add(QUALITY);
            command.add(quality);
            p = new ProcessBuilder(command).start();
            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s = null;
            int percent = -1;

            // Read progress
            while ((s = stdInput.readLine()) != null) {
                if (listener != null) {
                    if (s.matches("\t\\[.....%.*")) {
                        // Percent values can be for example 0.3% or 0,3%, so be careful with "." and ","
                        int decimalPointPosition = s.indexOf('.');
                        if (decimalPointPosition == -1) {
                            decimalPointPosition = s.indexOf(',');
                        }
                        int aux = Integer.parseInt((s.substring(s.indexOf('[') + 1, decimalPointPosition).trim()));
                        if (aux != percent) {
                            percent = aux;
                            final int percentHelp = percent;
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    listener.notifyProgress(percentHelp);
                                }
                            });
                        }
                    } else if (s.startsWith("Done")) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                listener.notifyProgress(100);
                            }
                        });
                    }
                }
            }

            int code = p.waitFor();
            if (code != 0) {
                logger.error(LogCategories.OGGENC, StringUtils.getString("Process returned code ", code));
                return false;
            }

            // Gather the info and write the tag
            try {
                AudioFile audiofile = new AudioFile(oggFile.getAbsolutePath());
                Tag tag = new DefaultTag();

                tag.setAlbum(album);
                tag.setAlbumArtist(albumArtist);
                tag.setArtist(artist);
                tag.setYear(year);
                tag.setGenre(genre);
                tag.setComposer(composer);
                tag.setTitle(title);
                tag.setTrackNumber(trackNumber);

                TagModifier.setInfo(audiofile, tag);

            } catch (Exception e) {
                logger.error(LogCategories.OGGENC, StringUtils.getString("Jaudiotagger: Process execution caused exception ", e));
                return false;
            }
            logger.info(LogCategories.OGGENC, "Encoded ok!!");
            return true;

        } catch (Exception e) {
            logger.error(LogCategories.OGGENC, StringUtils.getString("Process execution caused exception ", e));
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
        return "ogg";
    }

    @Override
    public void setAlbum(String album) {
        this.album = album;
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
    public void setListener(EncoderProgressListener listener) {
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
        return OGG_QUALITIES;
    }

    @Override
    public String getDefaultQuality() {
        return DEFAULT_OGG_QUALITY;
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME;
    }
}
