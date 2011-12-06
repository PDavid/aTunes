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
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class PlayListIO.
 */
public final class PlayListIO {

    // The different Strings used
    /** The Constant M3U_HEADER. */
    private static final String M3U_HEADER = "#EXTM3U";

    /** The Constant M3U_START_COMMENT. */
    private static final String M3U_START_COMMENT = "#";

    /** The Constant M3U_UNIX_ABSOLUTE_PATH. */
    private static final String M3U_UNIX_ABSOLUTE_PATH = "/";

    /** The Constant M3U_WINDOWS_ABSOLUTE_PATH. */
    private static final String M3U_WINDOWS_ABSOLUTE_PATH = ":\\";
    
    /** The Constant UNC_ABSOLUTE_PATH. */
    private static final String M3U_UNC_ABSOLUTE_PATH = "\\\\";

    /** The Constant M3U_FILE_EXTENSION. */
    public static final String M3U_FILE_EXTENSION = "m3u";

    /** The Constant M3U_HTTP_PREFIX. */
    private static final String M3U_HTTP_PREFIX = "http://";

    private PlayListIO() {

    }

    /**
     * Returns a list of files contained in a list of file names.
     * 
     * @param repositoryHandler
     * @param fileNames
     * @param radioHandler
     * @param localAudioObjectFactory
     * @return the audio objects from file names list
     */
    public static List<IAudioObject> getAudioObjectsFromFileNamesList(IRepositoryHandler repositoryHandler, List<String> fileNames, IRadioHandler radioHandler, ILocalAudioObjectFactory localAudioObjectFactory) {
        List<IAudioObject> result = new ArrayList<IAudioObject>();
        for (String fileName : fileNames) {
            result.add(getAudioFileOrCreate(repositoryHandler, fileName, radioHandler, localAudioObjectFactory));
        }
        return result;
    }

    /**
     * Returns an AudioObject given a resource name or instantiates it if does
     * not exist. A resource can be a file or URL at this moment
     * 
     * @param repositoryHandler
     * @param resourceName
     * @param radioHandler
     * @param localAudioObjectFactory
     * @return
     */
    public static IAudioObject getAudioFileOrCreate(IRepositoryHandler repositoryHandler, String resourceName, IRadioHandler radioHandler, ILocalAudioObjectFactory localAudioObjectFactory) {
        IAudioObject ao = null;

        // It's an online radio
        if (resourceName.startsWith(M3U_HTTP_PREFIX)) {
            ao = radioHandler.getRadioIfLoaded(resourceName);
            if (ao == null) {
                // If radio is not previously loaded in application then create a new Radio object with resource as name and url and leave label empty
                ao = new Radio(resourceName, resourceName, null);
            }
            return ao;
        }

        // It's not an online radio, then it must be an AudioFile
        ao = repositoryHandler.getFileIfLoaded(resourceName);
        if (ao == null) {
            // If LocalAudioObject is not previously loaded in application then create a new AudioFile
            ao = localAudioObjectFactory.getLocalAudioObject(new File(resourceName));
        }
        return ao;
    }

    /**
     * Returns a list of files contained in a play list file.
     * 
     * @param file
     * @param repositoryHandler
     * @param osManager
     * @param radioHandler
     * @param localAudioObjectFactory
     * @return
     */
    public static List<IAudioObject> getFilesFromList(File file, IRepositoryHandler repositoryHandler, IOSManager osManager, IRadioHandler radioHandler, ILocalAudioObjectFactory localAudioObjectFactory) {
        List<String> list = read(file, osManager);
        return getAudioObjectsFromFileNamesList(repositoryHandler, list, radioHandler, localAudioObjectFactory);
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
     * Checks if is valid play list.
     * 
     * @param playListFile
     *            the play list file
     * 
     * @return true, if is valid play list
     */
    public static boolean isValidPlayList(File playListFile) {
        return playListFile.getName().endsWith(M3U_FILE_EXTENSION) && playListFile.exists();
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
    public static List<String> read(File file, IOSManager osManager) {

        BufferedReader br = null;
        try {
            List<String> result = new ArrayList<String>();
            br = new BufferedReader(new FileReader(file));
            String line;
            // Do look for the first uncommented line
            line = br.readLine();
            while (line != null && line.startsWith(M3U_START_COMMENT)) {
                // Go to next line
                line = br.readLine();
            }
            if (line == null) {
                return Collections.emptyList();
            }
            // First absolute path. Windows path detection is very rudimentary, but should work
            if (line.startsWith(M3U_WINDOWS_ABSOLUTE_PATH, 1) || 
            	line.startsWith(M3U_UNIX_ABSOLUTE_PATH) ||
            	line.startsWith(M3U_UNC_ABSOLUTE_PATH)) {
                // Let's check if we are at least using the right OS. Maybe a message should be returned, but for now it doesn't. UNC paths are allowed for all OS
                if (((osManager.isWindows()) && line.startsWith(M3U_UNIX_ABSOLUTE_PATH))
                        || (!(osManager.isWindows()) && line.startsWith(M3U_WINDOWS_ABSOLUTE_PATH, 1))) {
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
                String path = file.getParent() + osManager.getFileSeparator();
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
     * @param file
     * @param osManager
     * @return
     */
    public static boolean write(IPlayList playlist, File file, IOSManager osManager) {
        FileWriter writer = null;
        try {
            if (file.exists()) {
                if (!file.delete()) {
                	Logger.error(StringUtils.getString(file, " not deleted"));
                }
            }
            writer = new FileWriter(file);
            writer.append(StringUtils.getString(M3U_HEADER, osManager.getLineTerminator()));
            for (int i = 0; i < playlist.size(); i++) {
            	IAudioObject f = playlist.get(i);
                writer.append(StringUtils.getString(f.getUrl(), osManager.getLineTerminator()));
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
