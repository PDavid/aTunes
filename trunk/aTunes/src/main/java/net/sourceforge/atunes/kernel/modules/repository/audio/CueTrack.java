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

package net.sourceforge.atunes.kernel.modules.repository.audio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.DefaultTag;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.ClosingUtils;

/**
 * CueTack class defines track described in cue-sheets, and extract those. This
 * means that an audio file can contain several tracks described in a cue sheet.
 * Therefore the actual position of the cue track in the audio file is
 * important. We will describe the position by the start position of the track
 * and its duration (not to be confused with the duration of the audio file
 * which can be longer). The start position is described in seconds from the
 * beginning of the matching audio file, so if the track starts on position
 * 01:01:01, this will be its start position. It is considered as the null
 * point.
 * 
 * A track is considered as a special audio file, so this class extends
 * AudioFile, and it inherits the behaviors and properties of AudioFile.
 * 
 * Before doing anything, the cue file must be read (extracted). Then its
 * properties are set.
 * 
 * @author mingsky<crazy4004@gmail.com>
 * 
 */
public class CueTrack extends AudioFile {

    private static final long serialVersionUID = -6204956020578468629L;

    private static final Logger logger = new Logger();

    String album;
    String trackNumber;
    String title;
    String artist;
    String albumArtist;
    String genre;
    int year;

    // Base/Start time of the track, begins at 00:00:00 (second)
    String trackStartPosition;
    String trackEndPosition;

    /**
     * The audiofile with the music
     */
    String file;

    // The cuesheet (.cue file)
    String cuesheet;

    AudioFile cueAudioFile;
    long totalDuration;

    int trackStartPositionAsInt;

    /**
     * Creates an empty cue track
     * 
     * @param fileName
     *            The .cue file name
     */
    public CueTrack(String fileName) {
        super(fileName);
        album = null;
        trackNumber = null;
        title = null;
        artist = null;
        albumArtist = null;
        genre = null;
        trackStartPosition = null;
        trackEndPosition = null;
        file = null;
        cuesheet = null;
        cueAudioFile = null;
        bitrate = 0;
        frequency = 0;
        year = 0;
    }

    /**
     * Computes the end position of the cue tracks. The cue file must have been
     * read first.
     * 
     * Use start time of Track i+1 to compute the end time of Track i, and
     * calculate duration of the Track i. The last track need some special
     * treatment. We dont know end time of the last track here, so we dont know
     * its duration. We left it to deal with at playing time. see protected void
     * read(String line) in AudioFileMplayerOutputReader.java
     * 
     * @param tracks
     *            The cue tracks from the cue sheet.
     */
    private static void computeEndTimePosition(List<CueTrack> tracks) {
        if (tracks.size() == 0) {
            return; //if no tracks, then return
        }
        int i;
        for (i = 0; i < tracks.size() - 1; i++) {
            String mm1 = null;
            String ss1 = null;
            String mm2 = null;
            String ss2 = null;
            String trackEndPosition = tracks.get(i + 1).trackStartPosition;
            Pattern p = Pattern.compile("\\d+\\:(\\d+)\\:(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(String.valueOf(tracks.get(i).trackStartPosition));
            if (m.find()) {
                mm1 = m.group(1);
                ss1 = m.group(2);
            }
            m = p.matcher(trackEndPosition);
            if (m.find()) {
                mm2 = m.group(1);
                ss2 = m.group(2);
                StringBuilder sb = new StringBuilder();
                sb.append("00");
                sb.append(":");
                int nss = Integer.valueOf(ss2);
                ss2 = String.format("%02d", nss - 1); //maybe -1s is better?
                sb.append(mm2);
                sb.append(":");
                sb.append(ss2);
                trackEndPosition = sb.toString();
            }
            tracks.get(i).duration = (Integer.parseInt(mm2) - Integer.parseInt(mm1)) * 60 + (Integer.parseInt(ss2) - Integer.parseInt(ss1));
            tracks.get(i).trackEndPosition = Integer.valueOf((int) tracks.get(i).duration).toString();
            tracks.get(i).setCueTrackProperties();
        }
        // Dont forget the last track
        tracks.get(i).setCueTrackProperties();
        tracks.get(i).duration = tracks.get(i).totalDuration - tracks.get(i).trackStartPositionAsInt;
    }

    /**
     * Reads tracks from a cue sheet and get their positions. Also gets the
     * following properties: - Track number - Audio file name (file containing
     * the music) - Album Artist (of the album) - Artist (of a title) - Title
     * name
     * 
     * @param filename
     *            the name of the cue sheet
     * 
     * @return the list of CueTracks
     */
    public static List<CueTrack> read(String filename) {
        List<CueTrack> tracks = new ArrayList<CueTrack>();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(filename);
            /**
             * i use GBK here to read some cuesheets correctly which have some
             * simple chinese characters. otherwise i will get some Unreadable
             * code :( This is only a temporary method here. We still need find
             * some graceful way to deal with chinese and japanese charset.
             */
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
        } catch (FileNotFoundException e) {
            logger.error(LogCategories.CUE_SHEET, e);
            return Collections.emptyList();
        }
        // Extract tracks from cue sheet
        try {
            String line = null;
            String album = null;
            String albumArtist = null;
            String file = null;
            String genre = null;
            String year = null;
            CueTrack track = null;
            long duration = 0;
            long bitrate = 0;
            int frequency = 0;

            while ((line = br.readLine()) != null) {
                if (track == null && album == null) {
                    album = fetchTitle(line);
                    if (album != null)
                        continue;
                }
                if (track == null && albumArtist == null) {
                    albumArtist = fetchArtist(line);
                    if (albumArtist != null)
                        continue;
                }
                if (file == null) {
                    if ((file = fetchFile(line)) != null) {
                        String buildFileName = new File(filename).getParent();
                        // The String must be build depending on OS (file separator!), otherwise the audio file is not found
                        if (buildFileName.endsWith("/") || buildFileName.endsWith("\\")) {
                            buildFileName = buildFileName.substring(0, buildFileName.length() - 2);
                        }
                        file = buildFileName + SystemProperties.FILE_SEPARATOR + file;
                        // Check if the audio file exists and is in repository. If it is not, do not add the
                        // cue track to the repository
                        if (new File(file).exists()) {
                            AudioFile audioFile = null;
                            audioFile = RepositoryHandler.getInstance().getFileIfLoaded(file);
                            if (audioFile != null) {
                                bitrate = audioFile.getBitrate();
                                duration = audioFile.getDuration();
                                frequency = audioFile.getFrequency();
                                genre = audioFile.getGenre();
                                year = audioFile.getYear();
                                continue;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                String trackNumber = null;
                if ((trackNumber = fetchTrackNumber(line)) != null) {
                    if (track != null) {
                        tracks.add(track);
                    }
                    // Add properties
                    track = new CueTrack(filename);
                    track.album = album;
                    track.albumArtist = albumArtist;
                    track.artist = albumArtist;
                    track.bitrate = bitrate;
                    track.genre = genre;
                    try {
                        track.year = Integer.parseInt(year);
                    } catch (Exception e) {
                        // Do nothing
                    }
                    track.cuesheet = filename;
                    track.file = file;
                    track.frequency = frequency;
                    track.trackNumber = trackNumber;
                    track.totalDuration = duration;
                    continue;
                }
                if (track == null) {
                    continue;
                }
                String title = null;
                if ((title = fetchTitle(line)) != null) {
                    track.title = title;
                    continue;
                }
                String artist = null;
                if ((artist = fetchArtist(line)) != null) {
                    track.artist = artist;
                    continue;
                }
                String trackStartPosition = null;
                if ((trackStartPosition = (fetchTrackStartPosition(line))) != null) {
                    if (track.trackStartPosition == null) {
                        track.trackStartPosition = trackStartPosition;
                        Pattern p = Pattern.compile("\\d+\\:(\\d+)\\:(\\d+)", Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(String.valueOf(trackStartPosition));
                        if (m.find()) {
                            String mm = m.group(1);
                            String ss = m.group(2);
                            track.trackStartPositionAsInt = Integer.parseInt(mm) * 60 + Integer.parseInt(ss);
                        }

                    }
                    continue;
                }
            }
            if (track != null) {
                tracks.add(track);
            }
        } catch (IOException e) {
            logger.error(LogCategories.CUE_SHEET, e);
        } finally {
            ClosingUtils.close(br);
            ClosingUtils.close(isr);
            ClosingUtils.close(fis);
        }
        // Use Track i+1 to fix the end time of Track i
        computeEndTimePosition(tracks);
        return tracks;
    }

    /**
     * Fetch the artist of the track or the album from the cue sheet
     * 
     * @param line
     *            The line number of the cue sheet containing the required
     *            information.
     * 
     * @return Artist of the track or the album
     */
    private static String fetchArtist(String line) {
        String artist = null;
        Pattern pattern = Pattern.compile("PERFORMER\\W+\"(.+?)\"", Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(line);
        if (m.find()) {
            artist = m.group(1);
        }
        return artist;
    }

    /**
     * Fetch audio file name of the track from cue sheet
     * 
     * @param line
     *            The line number of the cue sheet containing the required
     *            information.
     * 
     * @return The file name of the audio file which contains the track
     */
    private static String fetchFile(String line) {
        String file = null;
        Pattern pattern = Pattern.compile("FILE\\W+\"(.+?)\"", Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(line);
        if (m.find()) {
            file = m.group(1);
        }
        return file;
    }

    /**
     * Fetch the title of the track or the album name from the cue sheet
     * 
     * @param line
     *            The line number of the cue sheet containing the required
     *            information.
     * 
     * @return The title name of the track or the album name
     */
    private static String fetchTitle(String line) {
        String title = null;
        // String pattern = "TITLE\\W"(.+?)\"";
        Pattern pattern = Pattern.compile("TITLE\\W+\"(.+?)\"", Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(line);
        if (m.find()) {
            title = m.group(1);
        }
        return title;
    }

    /**
     * Fetch the track number of the track from a line of cue sheet
     * 
     * @param line
     *            The line number of the cue sheet containing the required
     *            information.
     * 
     * @return The track number of the track
     */
    private static String fetchTrackNumber(String line) {
        String trackNumber = null;
        Pattern pattern = Pattern.compile("TRACK\\W+(\\d+?)\\W+", Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(line);
        if (m.find()) {
            trackNumber = m.group(1);
        }
        return trackNumber;
    }

    /**
     * Fetch the start time of the track from cue sheet. If there are index 0
     * and index 1, use index 0.
     * 
     * @param line
     *            The line number of the cue sheet containing the required
     *            information.
     * 
     * @return The start time of the track
     */
    private static String fetchTrackStartPosition(String line) {
        String trackStartPosition = null;
        Pattern pattern = Pattern.compile("INDEX\\W+\\d*\\W*(\\d+):(\\d+):\\d+", Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(line);
        if (m.find()) {
            trackStartPosition = "00:" + m.group(1) + ":" + m.group(2);
        }
        return trackStartPosition;
    }

    /**
     * The audio file to wich to cue sheet links to (file containing the actual
     * music)
     * 
     * @return The audio file with complete path as String
     */
    public String getAudioFileName() {
        return file;
    }

    /**
     * Provides the cue file (.cue)
     * 
     * @return The .cue file
     */
    public AudioFile getCueFile() {
        return cueAudioFile;
    }

    /**
     * Get the total duration of the audio file.
     * 
     * @return Duration of the audio file in seconds (the file containing the
     *         music)
     */
    public long getTotalDuration() {
        return totalDuration;
    }

    /**
     * Get the end position of the audio track
     * 
     * @return End position in the audio file in seconds
     */
    public String getTrackEndPosition() {
        return trackEndPosition;
    }

    /**
     * Get the start position of the audio track as string
     * 
     * @return Start position in the audio file as 00:00:00 format
     */
    public String getTrackStartPosition() {
        return trackStartPosition;
    }

    /**
     * Get the start position of the audio track as integer value
     * 
     * @return Start position in the audio file in seconds
     */
    public int getTrackStartPositionAsInt() {
        return trackStartPositionAsInt;
    }

    /**
     * Override getUrl() in AudioFile. The additional parameters are to avoid
     * unexpected behaviour caused by the identical filename for all cue tracks.
     * 
     * @return Audio file path of cue track followed by the track start position
     *         and the end position
     */
    @Override
    public String getUrl() {
        if (trackEndPosition != null) {
            return file + " start " + trackStartPosition + " - " + trackEndPosition;
        } else {
            //last track
            return file + " start " + trackStartPosition;
        }
    }

    /**
     * Modify the cue sheet file. Does not operate on tags as such. Only a few
     * tags fields can be used (album, album artist, artist, title name, track
     * number).
     */
    public void modifyTag(Tag tag) {
        //TODO
        String newAlbum = tag.getAlbum();
        album = newAlbum;
        String newAlbumArtist = tag.getAlbumArtist();
        albumArtist = newAlbumArtist;
        String newTitle = tag.getTitle();
        title = newTitle;
        String newartist = tag.getArtist();
        artist = newartist;
        String oldtrackNumber = trackNumber;
        String newtrackNumber = Integer.valueOf(tag.getTrackNumber()).toString();
        trackNumber = newtrackNumber;

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        String filename = cuesheet;
        String newfilename = filename + ".new";

        boolean startmodify = false;

        try {
            fis = new FileInputStream(filename);
            fos = new FileOutputStream(newfilename);
            /**
             * i use GBK here to read some cuesheets correctly which have some
             * simple chinese characters. otherwise i will get some Unreadable
             * code :( This is only a temporary method here. We still need find
             * some graceful way to deal with chinese and japanese charset.
             */
            isr = new InputStreamReader(fis);
            osw = new OutputStreamWriter(fos);
            br = new BufferedReader(isr);
            bw = new BufferedWriter(osw);
        } catch (FileNotFoundException e) {
            logger.error(LogCategories.CUE_SHEET, e);
            return;
        }
        //read from oldfile, modify the track info, then write into new file
        try {
            String line = null;
            String album = null;
            String albumArtist = null;

            while ((line = br.readLine()) != null) {
                if (album == null) {
                    album = fetchTitle(line);
                    if (album != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("TITLE");
                        sb.append(" \"");
                        sb.append(newAlbum);
                        sb.append("\"");
                        bw.write(sb.toString());
                        bw.newLine();
                        continue;
                    }
                }
                if (albumArtist == null) {
                    albumArtist = fetchArtist(line);
                    if (albumArtist != null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("PERFORMER");
                        sb.append(" \"");
                        sb.append(newAlbumArtist);
                        sb.append("\"");
                        bw.write(sb.toString());
                        bw.newLine();
                        continue;
                    }
                }
                String trackNumber = null;
                if ((trackNumber = fetchTrackNumber(line)) != null) {
                    if (Integer.parseInt(trackNumber) == Integer.parseInt(oldtrackNumber)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("TRACK ");
                        sb.append(newtrackNumber);
                        sb.append(" AUDIO");
                        bw.write(sb.toString());
                        bw.newLine();
                        startmodify = true; //start modify
                        continue;
                    } else if (startmodify) {
                        startmodify = false; //
                    } else {
                        bw.write(line);
                        bw.newLine();
                        continue;
                    }
                }
                if ((fetchTitle(line)) != null) {
                    if (startmodify) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("TITLE");
                        sb.append(" \"");
                        sb.append(newTitle);
                        sb.append("\"");
                        bw.write(sb.toString());
                        bw.newLine();
                        continue;
                    } else {
                        bw.write(line);
                        bw.newLine();
                        continue;
                    }
                }
                if ((fetchArtist(line)) != null) {
                    if (startmodify) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("PERFORMER");
                        sb.append(" \"");
                        sb.append(newartist);
                        sb.append("\"");
                        bw.write(sb.toString());
                        bw.newLine();
                        continue;
                    } else {
                        bw.write(line);
                        bw.newLine();
                        continue;
                    }
                }
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            logger.error(LogCategories.CUE_SHEET, e);
        } finally {
            ClosingUtils.close(br);
            ClosingUtils.close(isr);
            ClosingUtils.close(fis);
            ClosingUtils.close(bw);
            ClosingUtils.close(osw);
            ClosingUtils.close(fos);
        }
        File f = new File(filename);
        f.delete();
        new File(newfilename).renameTo(f);
    }

    /**
     * Refresh tag.
     */
    @Override
    public void refreshTag() {
        tag = null;
        setCueTrackProperties();
    }

    /**
     * Set the cue file (.cue)
     * 
     * @param The
     *            .cue file
     */
    public void setCueFile(AudioFile file) {
        cueAudioFile = file;
    }

    /**
     * Sets some properties of the cue track. The properties must have been
     * fetched first!
     */
    private void setCueTrackProperties() {
        //set tag
        tag = new DefaultTag();
        tag.setAlbum(album);
        tag.setArtist(artist);
        tag.setAlbumArtist(albumArtist);
        tag.setTitle(title);
        tag.setTrackNumber(Integer.parseInt(trackNumber));
        tag.setGenre(genre);
        tag.setYear(year);
        // set audio properties
        // bitrate = ;
        // frequency = ;
        readTime = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CueTrack)) {
            return false;
        }
        return ((AudioFile) o).getUrl().equals(getUrl()) && (((CueTrack) o).trackStartPositionAsInt == this.trackStartPositionAsInt);
    }
}
