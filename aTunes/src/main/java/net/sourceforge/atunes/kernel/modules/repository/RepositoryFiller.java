package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.repository.model.Folder;
import net.sourceforge.atunes.kernel.modules.repository.model.Genre;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

public class RepositoryFiller {

    /**
     * Logger
     */
    private static Logger logger;

    /**
     * Adds basic information of given audio file to repository
     * 
     * @param repository
     * @param audioFile
     */
    protected static void addToRepository(Repository repository, AudioFile audioFile) {
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
    protected static void addToArtistStructure(Repository repository, AudioFile audioFile) {
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

            albumObject.addSong(audioFile);
        } catch (Exception e) {
            logger.error(LogCategories.FILE_READ, e.getMessage());
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
    protected static void addToGenreStructure(Repository repository, AudioFile audioFile) {
        try {
            String genre = audioFile.getGenre();

            Genre genreObject = repository.getGenreStructure().get(genre);
            if (genreObject == null) {
                genreObject = new Genre(genre);
                repository.getGenreStructure().put(genre, genreObject);
            }

            genreObject.addSong(audioFile);
        } catch (Exception e) {
            getLogger().error(LogCategories.FILE_READ, e.getMessage());
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
    protected static void addToFolderStructure(Repository repository, File relativeTo, String relativePath, AudioFile file) {
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
        parentFolder.addFile(file);
    }

    /**
     * Private getter of logger
     * 
     * @return
     */
    private static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }
}
