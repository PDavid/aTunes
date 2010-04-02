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
package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;

import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.data.Folder;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Repository;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

final class RepositoryFiller {

    private RepositoryFiller() {

    }

    /**
     * Adds basic information of given audio file to repository
     * 
     * @param repository
     * @param audioFile
     */
    static void addToRepository(Repository repository, AudioFile audioFile) {
        repository.getAudioFiles().put(audioFile.getUrl(), audioFile);
        repository.setTotalSizeInBytes(repository.getTotalSizeInBytes() + audioFile.getFile().length());
        repository.addDurationInSeconds(audioFile.getDuration());
    }

    /**
     * Adds given audio file to artist structure of given repository
     * 
     * @param repository
     *            Repository to use
     * @param audioFile
     *            AudioFile to display
     */
    static void addToArtistStructure(Repository repository, AudioFile audioFile) {
        try {
            String artist = audioFile.getAlbumArtistOrArtist();
            String album = audioFile.getAlbum();

            Artist artistObject = repository.getArtistStructure().get(artist);
            if (artistObject == null) {
                artistObject = new Artist(artist);
                repository.getArtistStructure().put(artist, artistObject);
            }

            Album albumObject = artistObject.getAlbum(album);
            if (albumObject == null) {
                albumObject = new Album(album);
                albumObject.setArtist(artistObject);
                artistObject.addAlbum(albumObject);
            }

            albumObject.addAudioFile(audioFile);
        } catch (Exception e) {
            new Logger().error(LogCategories.FILE_READ, e.getMessage());
        }
    }

    /**
     * Adds given audio file to genre structure of given repository
     * 
     * @param repository
     *            the repository
     * @param audioFile
     *            the audio file
     */
    static void addToGenreStructure(Repository repository, AudioFile audioFile) {
        try {
            String genre = audioFile.getGenre();

            Genre genreObject = repository.getGenreStructure().get(genre);
            if (genreObject == null) {
                genreObject = new Genre(genre);
                repository.getGenreStructure().put(genre, genreObject);
            }

            genreObject.addAudioFile(audioFile);
        } catch (Exception e) {
            new Logger().error(LogCategories.FILE_READ, e.getMessage());
        }
    }

    /**
     * Adds given audio file to year structure of given repository
     * 
     * @param repository
     *            the repository
     * @param audioFile
     *            the audio file
     */
    static void addToYearStructure(Repository repository, AudioFile audioFile) {
        try {
            String year = audioFile.getYear();

            Year yearObject = repository.getYearStructure().get(year);
            if (yearObject == null) {
                yearObject = new Year(year);
                repository.getYearStructure().put(year, yearObject);
            }

            yearObject.addAudioFile(audioFile);
        } catch (Exception e) {
            new Logger().error(LogCategories.FILE_READ, e.getMessage());
        }
    }

    /**
     * Adds given audio file to folder structure of given repository.
     * 
     * @param repository
     *            the repository
     * @param relativeTo
     *            the relative to
     * @param relativePath
     *            the relative path
     * @param file
     *            the file
     */
    static void addToFolderStructure(Repository repository, File relativeTo, String relativePath, AudioFile file) {
        Folder relativeFolder = repository.getFolderStructure().get(relativeTo.getAbsolutePath());
        if (relativeFolder == null) {
            relativeFolder = new Folder(relativeTo.getAbsolutePath());
            repository.getFolderStructure().put(relativeFolder.getName(), relativeFolder);
        }

        String[] foldersInPath = relativePath.split("/");
        Folder parentFolder = relativeFolder;
        Folder f = null;
        for (String folderName : foldersInPath) {
            if (parentFolder != null) {
                f = parentFolder.getFolder(folderName);
                if (f == null) {
                    f = new Folder(folderName);
                    parentFolder.addFolder(f);
                }
            } else {
                f = repository.getFolderStructure().get(folderName);
                if (f == null) {
                    f = new Folder(folderName);
                    repository.getFolderStructure().put(f.getName(), f);
                }
            }
            parentFolder = f;
        }
        if (parentFolder == null) {
            parentFolder = new Folder(".");
        }
        parentFolder.addAudioFile(file);
    }
}
