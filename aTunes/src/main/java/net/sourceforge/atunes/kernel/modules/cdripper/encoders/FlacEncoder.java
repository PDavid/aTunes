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
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.TagModifier;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class FlacEncoder.
 */
public class FlacEncoder implements Encoder {

    /** The format name of this encoder */
    public static final String FORMAT_NAME = "FLAC";
    public static final String FLAC = "flac";
    public static final String OUTPUT = "-o";
    public static final String FORCE = "-f";
    public static final String ADD_TAG = "-t";
    public static final String APPEND = "-a";
    public static final String TITLE = "TITLE=";
    public static final String ARTIST = "ARTIST=";
    public static final String ALBUM = "ALBUM=";
    public static final String TRACK = "TRACKNUMBER=";
    public static final String QUALITY = "-q";
    public static final String VERSION = "--version";
    static final String[] FLAC_QUALITY = { "-0", "-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8" };
    static final String DEFAULT_FLAC_QUALITY = "-5";

    private Logger logger;
    private Process process;
    private ProgressListener listener;
    private String albumArtist;
    private String album;
    private int year;
    private String genre;
    private String quality;

    /**
     * Test the presence of the flac encoder flac.
     * 
     * @return Returns true if flac was found, false otherwise.
     */
    public static boolean testTool() {
        // Test for flac
        BufferedReader stdInput = null;
        try {
            //Test for Windows system checks in ...\aTunes\win_tools only!
            Process p;
            if (SystemProperties.OS == OperatingSystem.WINDOWS) {
                p = new ProcessBuilder(StringUtils.getString(Constants.WINDOWS_TOOLS_DIR, SystemProperties.FILE_SEPARATOR, FLAC), VERSION).start();
            } else {
                p = new ProcessBuilder(FLAC, VERSION).start();
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
     * @param title
     *            The title of the song (only title, not artist and album)
     * @param trackNumber
     *            The track number of the song
     * @param oggFile
     *            the ogg file
     * @param artist
     *            the artist
     * @param composer
     *            the composer
     * 
     * @return Returns true if encoding was successfull, false otherwise.
     */
    @Override
    public boolean encode(File wavFile, File oggFile, String title, int trackNumber, String artist, String composer) {
        getLogger().info(LogCategories.FLAC, StringUtils.getString("Flac encoding process started... ", wavFile.getName(), " -> ", oggFile.getName()));
        BufferedReader stdInput = null;
        try {
            // Encode the file using FLAC. We could pass the infos for the tag, but 
            // FLAC is very difficult with special characters so we don't use it.
            List<String> command = new ArrayList<String>();
            if (SystemProperties.OS == OperatingSystem.WINDOWS) {
                command.add(StringUtils.getString(Constants.WINDOWS_TOOLS_DIR, SystemProperties.FILE_SEPARATOR, FLAC));
            } else {
                command.add(FLAC);
            }
            command.add(quality);
            command.add(FORCE);
            command.add(wavFile.getAbsolutePath());
            command.add(OUTPUT);
            command.add(oggFile.getAbsolutePath());
            process = new ProcessBuilder(command).start();
            stdInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String s = null;
            int percent = -1;
            int aux = 0;
            // Read progress
            while ((s = stdInput.readLine()) != null) {
                int index = s.indexOf('%');
                if (listener != null) {
                    if (s.contains("% complete, ratio")) {
                        aux = Integer.parseInt(s.substring(index - 2, index).trim());
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
                    }
                }
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    listener.notifyProgress(100);
                }
            });

            int code = process.waitFor();
            if (code != 0) {
                getLogger().error(LogCategories.FLAC, StringUtils.getString("Process returned code ", code));
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
                tag.setTitle(title);
                tag.setComposer(composer);
                tag.setTrackNumber(trackNumber);

                TagModifier.setInfo(audiofile, tag);

            } catch (Exception e) {
                getLogger().error(LogCategories.FLAC, StringUtils.getString("Jaudiotagger: Process execution caused exception ", e));
                return false;
            }
            getLogger().info(LogCategories.FLAC, "Encoded ok!!");
            return true;

        } catch (Exception e) {
            getLogger().error(LogCategories.FLAC, StringUtils.getString("Process execution caused exception ", e));
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
        return "flac";
    }

    @Override
    public void setAlbum(String album) {
        this.album = album;
    }

    //AlbumArtist
    @Override
    public void setArtist(String albumArtist) {
        this.albumArtist = albumArtist;
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
        if (process != null) {
            process.destroy();
        }
    }

    @Override
    public String[] getAvailableQualities() {
        return FLAC_QUALITY;
    }

    @Override
    public String getDefaultQuality() {
        return DEFAULT_FLAC_QUALITY;
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
