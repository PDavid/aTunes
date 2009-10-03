/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class PlayListIO.
 */
public class PlayListIO {

    // The different Strings used
    /** The Constant M3U_HEADER. */
    private static final String M3U_HEADER = "#EXTM3U";

    /** The Constant M3U_START_COMMENT. */
    private static final String M3U_START_COMMENT = "#";

    /** The Constant M3U_UNIX_ABSOLUTE_PATH. */
    private static final String M3U_UNIX_ABSOLUTE_PATH = "/";

    /** The Constant M3U_WINDOWS_ABSOLUTE_PATH. */
    private static final String M3U_WINDOWS_ABSOLUTE_PATH = ":\\";

    /** The Constant M3U_FILE_EXTENSION. */
    public static final String M3U_FILE_EXTENSION = "m3u";

    /** The Constant M3U_HTTP_PREFIX. */
    private static final String M3U_HTTP_PREFIX = "http://";

    /**
     * Returns a list of files contained in a list of file names.
     * 
     * @param fileNames
     *            the file names
     * 
     * @return the audio objects from file names list
     */
    public static List<AudioObject> getAudioObjectsFromFileNamesList(List<String> fileNames) {
        List<AudioObject> result = new ArrayList<AudioObject>();
        for (String fileName : fileNames) {
            result.add(getAudioFileOrCreate(fileName));
        }
        return result;
    }

    /**
     * Returns an AudioObject given a resource name or instantiates it if does
     * not exist. A resource can be a file or URL at this moment
     * 
     * @param resourceName
     * @return
     */
    static AudioObject getAudioFileOrCreate(String resourceName) {
        AudioObject ao = null;

        // It's an online radio
        if (resourceName.startsWith(M3U_HTTP_PREFIX)) {
            ao = RadioHandler.getInstance().getRadioIfLoaded(resourceName);
            if (ao == null) {
                // If radio is not previously loaded in application then create a new Radio object with resource as name and url and leave label empty
                ao = new Radio(resourceName, resourceName, null);
            }
            return ao;
        }

        // It's not an online radio, then it must be an AudioFile
        ao = RepositoryHandler.getInstance().getFileIfLoaded(resourceName);
        if (ao == null) {
            // If AudioFile is not previously loaded in application then create a new AudioFile
            ao = new AudioFile(resourceName);
        }
        return ao;
    }

    /**
     * Returns a list of files contained in a play list file.
     * 
     * @param file
     *            the file
     * 
     * @return the files from list
     */
    public static List<AudioObject> getFilesFromList(File file) {
        List<String> list = read(file);
        return getAudioObjectsFromFileNamesList(list);
    }

    /**
     * FileFilter to be used when loading and saving a play list file.
     * 
     * @return the playlist file filter
     */
    public static final FileFilter getPlaylistFileFilter() {
        return new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(M3U_FILE_EXTENSION);
            }

            @Override
            public String getDescription() {
                return I18nUtils.getString("PLAYLIST");
            }
        };
    }

    /**
     * Checks if is valid play list.
     * 
     * @param playListFile
     *            the play list file
     * 
     * @return true, if is valid play list
     */
    public static boolean isValidPlayList(String playListFile) {
        File f = new File(playListFile);
        return playListFile.endsWith(M3U_FILE_EXTENSION) && f.exists();
    }

    /**
     * This function reads the filenames from the playlist file (m3u). It will
     * return all filenames with absolute path. For this playlists with relative
     * pathname must be detected and the path must be added. Current problem of
     * this implementation is clearly the charset used. Java reads/writes in the
     * charset used by the OS! But for many *nixes this is UTF8, while Windows
     * will use CP1252 or similar. So, as long as we have the same charset
     * encoding or do not use any special character playlists will work
     * (absolute filenames with a pathname incompatible with the current OS are
     * not allowed), but as soon as we have say french accents in the filename a
     * playlist created under an application using CP1252 will not import
     * correctly on a UTF8 system (better: the files with accents in their
     * filename will not).
     * 
     * Only playlist with local files have been tested! Returns a list of file
     * names contained in a play list file
     * 
     * @param file
     *            The playlist file
     * 
     * @return Returns an List of files of the playlist as String.
     */
    public static List<String> read(File file) {

        BufferedReader br = null;
        try {
            List<String> result = new ArrayList<String>();
            br = new BufferedReader(new FileReader(file));
            String line;
            //line = br.readLine();
            // Do look for the first uncommented line
            while ((line = br.readLine()) != null && line.startsWith(M3U_START_COMMENT)) {
                // Go to next line
            }
            if (line == null) {
                return Collections.emptyList();
            }
            // First absolute path. Windows path detection is very rudimentary, but should work
            if (line.startsWith(M3U_WINDOWS_ABSOLUTE_PATH, 1) || line.startsWith(M3U_UNIX_ABSOLUTE_PATH)) {
                // Let's check if we are at least using the right OS. Maybe a message should be returned, but for now it doesn't
                if (((SystemProperties.OS == OperatingSystem.WINDOWS) && line.startsWith(M3U_UNIX_ABSOLUTE_PATH))
                        || (!(SystemProperties.OS == OperatingSystem.WINDOWS) && line.startsWith(M3U_WINDOWS_ABSOLUTE_PATH, 1))) {
                    return Collections.emptyList();
                }
                result.add(line);
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(M3U_START_COMMENT) && !line.isEmpty()) {
                        result.add(line);
                    }
                }
            }
            // The path is relative! We must add it to the filename
            // But if entries are HTTP URLS then don't add any path
            else {
                String path = file.getParent() + SystemProperties.FILE_SEPARATOR;
                result.add(line.startsWith(M3U_HTTP_PREFIX) ? line : StringUtils.getString(path, line));
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith(M3U_START_COMMENT) && !line.isEmpty()) {
                        result.add(line.startsWith(M3U_HTTP_PREFIX) ? line : StringUtils.getString(path, line));
                    }
                }
            }
            // Return the filenames with their absolute path
            return result;
        } catch (IOException e) {
            return Collections.emptyList();
        } finally {
            ClosingUtils.close(br);
        }
    }

    /**
     * Writes a play list to a file.
     * 
     * @param playlist
     *            the playlist
     * @param file
     *            the file
     * 
     * @return true, if write
     */
    public static boolean write(PlayList playlist, File file) {
        FileWriter writer = null;
        try {
            if (file.exists()) {
                file.delete();
            }
            writer = new FileWriter(file);
            writer.append(StringUtils.getString(M3U_HEADER, SystemProperties.LINE_TERMINATOR));
            for (AudioObject f : playlist.getAudioObjects()) {
                writer.append(StringUtils.getString(f.getUrl(), SystemProperties.LINE_TERMINATOR));
            }
            writer.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            ClosingUtils.close(writer);
        }
    }
}
