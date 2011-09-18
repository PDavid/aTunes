/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;
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
     * @param state
     */
    public static void addLyrics(List<ILocalAudioObject> files, IState state) {
        SetLyricsProcess process = new SetLyricsProcess(files, state);
        process.execute();
    }

    /**
     * Edits the album name.
     * 
     * @param files
     * @param state
     */
    private static void editAlbumName(List<ILocalAudioObject> files, IState state) {
        SetAlbumNamesProcess process = new SetAlbumNamesProcess(files, state);
        process.execute();
    }

    /**
     * Sets genre based on Last.fm tags
     * 
     * @param files
     * @param state
     */
    public static void editGenre(List<ILocalAudioObject> files, IState state) {
        SetGenresProcess process = new SetGenresProcess(files, state);
        process.execute();
    }

    public static void editCover(List<ILocalAudioObject> files, IState state) {
        SetCoversProcess process = new SetCoversProcess(files, state);
        process.execute();
    }

    /**
     * Sets track number based on file name to an array of files.
     * 
     * @param files
     * @param state
     */
    public static void editTrackNumber(List<ILocalAudioObject> files, IState state) {
        /*
         * Given an array of files, returns a map containing each file and its
         * track number based on information found on file name.
         */
        Map<ILocalAudioObject, Integer> filesToSet = new HashMap<ILocalAudioObject, Integer>();
        for (int j = 0; j < files.size(); j++) {
            int trackNumber = getTrackNumber(files.get(j));

            if (trackNumber != 0) {
                filesToSet.put(files.get(j), trackNumber);
            }
        }
        if (!filesToSet.isEmpty()) {
            // Call process
            SetTrackNumberProcess process = new SetTrackNumberProcess(filesToSet, state);
            process.execute();
        }
    }

    /**
     * Returns track number for a given audio file
     * 
     * @param audioFile
     * @return
     */
    public static int getTrackNumber(ILocalAudioObject audioFile) {
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
            trackNumber = Context.getBean(IWebServicesHandler.class).getTrackNumber(audioFile);
        }

        return trackNumber;
    }

    /**
     * Repair album names.
     */
    public static void repairAlbumNames(IState state) {
        // Show confirmation dialog
        if (GuiHandler.getInstance().showConfirmationDialog(I18nUtils.getString("REPAIR_ALBUM_NAMES_MESSAGE")) == JOptionPane.OK_OPTION) {

            // Get all repository audio files
            Collection<ILocalAudioObject> repositoryAudioFiles = RepositoryHandler.getInstance().getAudioFilesList();

            // Get audio files with empty track number
            List<ILocalAudioObject> audioFilesToBeRepaired = new ArrayList<ILocalAudioObject>();
            for (ILocalAudioObject file : repositoryAudioFiles) {
                if ((file.getGenre() == null) || (file.getGenre().isEmpty())) {
                    audioFilesToBeRepaired.add(file);
                }
            }

            // Call album name edit
            editAlbumName(audioFilesToBeRepaired, state);
        }
    }

    /**
     * Sets genres on audio files that have an empty genre.
     * @param state
     */
    public static void repairGenres(IState state) {
        // Show confirmation dialog
        if (GuiHandler.getInstance().showConfirmationDialog(I18nUtils.getString("REPAIR_GENRES_MESSAGE")) == JOptionPane.OK_OPTION) {

            // Get all repository audio files
        	Collection<ILocalAudioObject> repositoryAudioFiles = RepositoryHandler.getInstance().getAudioFilesList();

            // Get audio files with empty track number
            List<ILocalAudioObject> audioFilesToBeRepaired = new ArrayList<ILocalAudioObject>();
            for (ILocalAudioObject file : repositoryAudioFiles) {
                if ((file.getGenre() == null) || (file.getGenre().isEmpty())) {
                    audioFilesToBeRepaired.add(file);
                }
            }

            // Call genre edit
            editGenre(audioFilesToBeRepaired, state);
        }

    }

    /**
     * Sets track number to audio files that have an empty track number.
     */
    public static void repairTrackNumbers(IState state) {
        // Show confirmation dialog
        if (GuiHandler.getInstance().showConfirmationDialog(I18nUtils.getString("REPAIR_TRACK_NUMBERS_MESSAGE")) == JOptionPane.OK_OPTION) {

            // Get all repository audio files
        	Collection<ILocalAudioObject> repositoryAudioFiles = RepositoryHandler.getInstance().getAudioFilesList();

            // Get audio files with empty track number
            List<ILocalAudioObject> audioFilesToBeRepaired = new ArrayList<ILocalAudioObject>();
            for (ILocalAudioObject song : repositoryAudioFiles) {
                if (song.getTrackNumber() == 0) {
                    audioFilesToBeRepaired.add(song);
                }
            }

            // Call track number edit
            editTrackNumber(audioFilesToBeRepaired, state);
        }
    }
}
