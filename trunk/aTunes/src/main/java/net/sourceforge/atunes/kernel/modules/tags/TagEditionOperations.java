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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.IConfirmationDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

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
     * @param files
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     */
    public static void addLyrics(List<ILocalAudioObject> files, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler) {
        SetLyricsProcess process = new SetLyricsProcess(files, state, playListHandler, repositoryHandler, playerHandler);
        process.execute();
    }

    /**
     * Edits the album name.
     * 
     * @param files
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     */
    private static void editAlbumName(Collection<ILocalAudioObject> files, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler) {
        SetAlbumNamesProcess process = new SetAlbumNamesProcess(files, state, playListHandler, repositoryHandler, playerHandler);
        process.execute();
    }

    /**
     * Sets genre based on Last.fm tags
     * 
     * @param files
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     */
    public static void editGenre(Collection<ILocalAudioObject> files, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler) {
        SetGenresProcess process = new SetGenresProcess(files, state, playListHandler, repositoryHandler, playerHandler);
        process.execute();
    }

    /**
     * Sets covers
     * @param files
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     * @param localAudioObjectValidator
     */
    public static void editCover(List<ILocalAudioObject> files, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler, ILocalAudioObjectValidator localAudioObjectValidator) {
        SetCoversProcess process = new SetCoversProcess(files, state, playListHandler, repositoryHandler, playerHandler, localAudioObjectValidator);
        process.execute();
    }

    /**
     * Sets track number based on file name to an array of files.
     * 
     * @param files
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     */
    public static void editTrackNumber(Collection<ILocalAudioObject> files, IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler) {
        /*
         * Given an array of files, returns a map containing each file and its
         * track number based on information found on file name.
         */
        Map<ILocalAudioObject, Integer> filesToSet = new HashMap<ILocalAudioObject, Integer>();
        for (ILocalAudioObject ao : files) {
            int trackNumber = getTrackNumber(ao);

            if (trackNumber != 0) {
                filesToSet.put(ao, trackNumber);
            }
        }
        if (!filesToSet.isEmpty()) {
            // Call process
            SetTrackNumberProcess process = new SetTrackNumberProcess(filesToSet, state, playListHandler, repositoryHandler, playerHandler);
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
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     */
    public static void repairAlbumNames(IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler) {
        // Show confirmation dialog
        if (Context.getBean(IConfirmationDialogFactory.class).getDialog().showDialog(I18nUtils.getString("REPAIR_ALBUM_NAMES_MESSAGE"))) {
            // Call album name edit
            editAlbumName(getFilesWithEmptyAlbum(repositoryHandler.getAudioFilesList()), state, playListHandler, repositoryHandler, playerHandler);
        }
    }

    /**
     * Sets genres on audio files that have an empty genre.
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     */
    public static void repairGenres(IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler) {
        // Show confirmation dialog
        if (Context.getBean(IConfirmationDialogFactory.class).getDialog().showDialog(I18nUtils.getString("REPAIR_GENRES_MESSAGE"))) {
            // Call genre edit
            editGenre(getFilesWithEmptyGenre(repositoryHandler.getAudioFilesList()), state, playListHandler, repositoryHandler, playerHandler);
        }

    }

    /**
     * Sets track number to audio files that have an empty track number.
     * @param state
     * @param playListHandler
     * @param repositoryHandler
     * @param playerHandler
     */
    public static void repairTrackNumbers(IState state, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, IPlayerHandler playerHandler) {
        // Show confirmation dialog
        if (Context.getBean(IConfirmationDialogFactory.class).getDialog().showDialog(I18nUtils.getString("REPAIR_TRACK_NUMBERS_MESSAGE"))) {
            // Call track number edit
            editTrackNumber(getFilesWithEmptyTracks(repositoryHandler.getAudioFilesList()), state, playListHandler, repositoryHandler, playerHandler);
        }
    }
    
    /**
     * Returns files without track number
     * @param audioFiles
     * @return
     */
    private static Collection<ILocalAudioObject> getFilesWithEmptyTracks(Collection<ILocalAudioObject> audioFiles) {
    	return Collections2.filter(audioFiles, new Predicate<ILocalAudioObject>() {
    		@Override
    		public boolean apply(ILocalAudioObject ao) {
    			return ao.getTrackNumber() == 0;
    		}
		});
    }
    
    /**
     * Returns files without genre
     * @param audioFiles
     * @return
     */
    private static Collection<ILocalAudioObject> getFilesWithEmptyGenre(Collection<ILocalAudioObject> audioFiles) {
    	return Collections2.filter(audioFiles, new Predicate<ILocalAudioObject>() {
    		@Override
    		public boolean apply(ILocalAudioObject ao) {
    			return ao.getGenre() == null || ao.getGenre().isEmpty();
    		}
		});
    }

    /**
     * Returns files without album
     * @param audioFiles
     * @return
     */
    private static Collection<ILocalAudioObject> getFilesWithEmptyAlbum(Collection<ILocalAudioObject> audioFiles) {
    	return Collections2.filter(audioFiles, new Predicate<ILocalAudioObject>() {
    		@Override
    		public boolean apply(ILocalAudioObject ao) {
    			return ao.getAlbum() == null || Album.isUnknownAlbum(ao.getAlbum()) || ao.getAlbum().isEmpty();
    		}
		});
    }
}
