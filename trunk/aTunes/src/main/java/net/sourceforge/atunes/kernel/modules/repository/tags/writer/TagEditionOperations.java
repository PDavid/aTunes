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
package net.sourceforge.atunes.kernel.modules.repository.tags.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class TagEditionOperations.
 */
public final class TagEditionOperations {

    /** The number separator pattern. */
    private static final Pattern NUMBER_SEPARATOR_PATTERN = Pattern.compile("[^0-9]+");

    private TagEditionOperations() {
    }

    /**
     * Adds the lyrics.
     * 
     * @param files
     *            the files
     */
    public static void addLyrics(List<AudioFile> files) {
        SetLyricsProcess process = new SetLyricsProcess(files);
        process.execute();
    }

    /**
     * Edits the album name.
     * 
     * @param files
     *            the files
     */
    private static void editAlbumName(List<AudioFile> files) {
        SetAlbumNamesProcess process = new SetAlbumNamesProcess(files);
        process.execute();
    }

    /**
     * Sets genre based on Last.fm tags
     * 
     * @param files
     *            the files
     */
    public static void editGenre(List<AudioFile> files) {
        SetGenresProcess process = new SetGenresProcess(files);
        process.execute();
    }

    public static void editCover(List<AudioFile> files) {
        SetCoversProcess process = new SetCoversProcess(files);
        process.execute();
    }

    /**
     * Sets track number based on file name to an array of files.
     * 
     * @param files
     *            the files
     */
    public static void editTrackNumber(List<AudioFile> files) {
        /*
         * Given an array of files, returns a map containing each file and its
         * track number based on information found on file name.
         */
        Map<AudioFile, Integer> filesToSet = new HashMap<AudioFile, Integer>();
        for (int j = 0; j < files.size(); j++) {
            int trackNumber = getTrackNumber(files.get(j));

            if (trackNumber != 0) {
                filesToSet.put(files.get(j), trackNumber);
            }
        }
        if (!filesToSet.isEmpty()) {
            // Call process
            SetTrackNumberProcess process = new SetTrackNumberProcess(filesToSet);
            process.execute();
        }
    }

    /**
     * Returns track number for a given audio file
     * 
     * @param audioFile
     * @return
     */
    public static int getTrackNumber(AudioFile audioFile) {
        // Try to get a number from file name
        String fileName = audioFile.getNameWithoutExtension();
        String[] aux = NUMBER_SEPARATOR_PATTERN.split(fileName);
        int trackNumber = 0;
        int i = 0;
        while (trackNumber == 0 && i < aux.length) {
            String token = aux[i];
            try {
                trackNumber = Integer.parseInt(token);
                // If trackNumber >= 1000 maybe it's not a track number (year?) 
                if (trackNumber >= 1000) {
                    trackNumber = 0;
                }
            } catch (NumberFormatException e) {
                // Ok, it's not a valid number, skip it
            }
            i++;
        }

        // If trackNumber could not be retrieved from file name, try to get from last.fm
        // To get this, titles must match
        if (trackNumber == 0) {
            trackNumber = LastFmService.getInstance().getTrackNumberForFile(audioFile);
        }

        return trackNumber;
    }

    /**
     * Repair album names.
     */
    public static void repairAlbumNames() {
        // Show confirmation dialog
        if (GuiHandler.getInstance().showConfirmationDialog(I18nUtils.getString("REPAIR_ALBUM_NAMES_MESSAGE"), I18nUtils.getString("REPAIR_ALBUM_NAMES")) == JOptionPane.OK_OPTION) {

            // Get all repository audio files
            List<AudioFile> repositoryAudioFiles = RepositoryHandler.getInstance().getAudioFilesList();

            // Get audio files with empty track number
            List<AudioFile> audioFilesToBeRepaired = new ArrayList<AudioFile>();
            for (AudioFile file : repositoryAudioFiles) {
                if ((file.getGenre() == null) || (file.getGenre().isEmpty())) {
                    audioFilesToBeRepaired.add(file);
                }
            }

            // Call album name edit
            editAlbumName(audioFilesToBeRepaired);
        }
    }

    /**
     * Sets genres on audio files that have an empty genre.
     */
    public static void repairGenres() {
        // Show confirmation dialog
        if (GuiHandler.getInstance().showConfirmationDialog(I18nUtils.getString("REPAIR_GENRES_MESSAGE"), I18nUtils.getString("REPAIR_GENRES")) == JOptionPane.OK_OPTION) {

            // Get all repository audio files
            List<AudioFile> repositoryAudioFiles = RepositoryHandler.getInstance().getAudioFilesList();

            // Get audio files with empty track number
            List<AudioFile> audioFilesToBeRepaired = new ArrayList<AudioFile>();
            for (AudioFile file : repositoryAudioFiles) {
                if ((file.getGenre() == null) || (file.getGenre().isEmpty())) {
                    audioFilesToBeRepaired.add(file);
                }
            }

            // Call genre edit
            editGenre(audioFilesToBeRepaired);
        }

    }

    /**
     * Sets track number to audio files that have an empty track number.
     */
    public static void repairTrackNumbers() {
        // Show confirmation dialog
        if (GuiHandler.getInstance().showConfirmationDialog(I18nUtils.getString("REPAIR_TRACK_NUMBERS_MESSAGE"), I18nUtils.getString("REPAIR_TRACK_NUMBERS")) == JOptionPane.OK_OPTION) {

            // Get all repository audio files
            List<AudioFile> repositoryAudioFiles = RepositoryHandler.getInstance().getAudioFilesList();

            // Get audio files with empty track number
            List<AudioFile> audioFilesToBeRepaired = new ArrayList<AudioFile>();
            for (AudioFile song : repositoryAudioFiles) {
                if (song.getTrackNumber() == 0) {
                    audioFilesToBeRepaired.add(song);
                }
            }

            // Call track number edit
            editTrackNumber(audioFilesToBeRepaired);
        }
    }
}
