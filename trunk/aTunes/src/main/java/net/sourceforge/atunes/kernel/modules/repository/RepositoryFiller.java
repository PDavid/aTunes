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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;

import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.kernel.modules.tags.AbstractTag;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.model.Repository;

/**
 * A class responsible of load repository structure
 * @author fleax
 *
 */
final class RepositoryFiller {

	private Repository repository;

	/**
     * Creates a new filler for given repository
     * @param repository
     */
    RepositoryFiller(Repository repository) {
    	if (repository == null) {
    		throw new IllegalArgumentException("Repository is null");
    	}
    	this.repository = repository;
    }

    /**
     * Adds a new audio file with a relative path
     * @param audioFile
     * @param repositoryFolderRoot
     * @param relativePathToRepositoryFolderRoot
     */
    void addAudioFile(LocalAudioObject audioFile, File repositoryFolderRoot, String relativePathToRepositoryFolderRoot) {
    	if (audioFile == null) {
    		throw new IllegalArgumentException("AudioFile is null");
    	}
    	
    	if (repositoryFolderRoot == null) {
    		throw new IllegalArgumentException("RelativeTo file is null");
    	}

    	if (relativePathToRepositoryFolderRoot == null) {
    		throw new IllegalArgumentException("Relative path is null");
    	}

		addToRepository(audioFile);
		addToArtistStructure(audioFile);
		addToFolderStructure(repositoryFolderRoot, relativePathToRepositoryFolderRoot, audioFile);
		addToGenreStructure(audioFile);
		addToYearStructure(audioFile);
    }
    
    /**
     * Refreshes audio file already added to repository
     * @param audioFile
     */
    void refreshAudioFile(LocalAudioObject audioFile, AbstractTag oldTag) {
    	if (audioFile == null) {
    		throw new IllegalArgumentException("AudioFile is null");
    	}

    	updateArtistStructure(oldTag, audioFile);
    	updateGenreStructure(oldTag, audioFile);
    	updateYearStructure(oldTag, audioFile);
    	
		addToArtistStructure(audioFile);
		addToGenreStructure(audioFile);
		addToYearStructure(audioFile);
    }
    
    /**
     * Adds basic information of given audio file to repository
     * 
     * @param audioFile
     */
    private void addToRepository(LocalAudioObject audioFile) {
        repository.putFile(audioFile);
        repository.addSizeInBytes(audioFile.getFile().length());
        repository.addDurationInSeconds(audioFile.getDuration());
    }

    /**
     * Adds given audio file to artist structure of given repository
     * 
     * @param audioFile
     */
    private void addToArtistStructure(LocalAudioObject audioFile) {
    	String artist = audioFile.getAlbumArtistOrArtist();
    	String album = audioFile.getAlbum();

    	// Create artist object if needed
    	Artist artistObject = repository.getArtist(artist);
    	if (artistObject == null) {
    		artistObject = repository.putArtist(artist);
    	}

    	// Create album object if needed
    	Album albumObject = artistObject.getAlbum(album);
    	if (albumObject == null) {
    		albumObject = new Album(artistObject, album);
    		artistObject.addAlbum(albumObject);
    	}

    	// Add file to album
    	albumObject.addAudioFile(audioFile);
    }

    /**
     * Adds given audio file to genre structure of given repository
     * 
     * @param audioFile
     *            the audio file
     */
    private void addToGenreStructure(LocalAudioObject audioFile) {
    	String genre = audioFile.getGenre();
    	Genre genreObject = repository.getGenre(genre);
    	if (genreObject == null) {
    		genreObject = repository.putGenre(genre);
    	}
    	genreObject.addAudioFile(audioFile);
    }

    /**
     * Adds given audio file to year structure of given repository
     * 
     * @param audioFile
     *            the audio file
     */
    private void addToYearStructure(LocalAudioObject audioFile) {
    	String year = audioFile.getYear();

    	Year yearObject = repository.getYear(year);
    	if (yearObject == null) {
    		yearObject = new Year(year);
    		repository.putYear(yearObject);
    	}

    	yearObject.addAudioFile(audioFile);
    }

    /**
     * Adds given audio file to folder structure of given repository.
     * 
     * @param relativeTo
     *            the relative to
     * @param relativePath
     *            the relative path
     * @param file
     *            the file
     */
    private void addToFolderStructure(File relativeTo, String relativePath, LocalAudioObject file) {
        Folder relativeFolder = repository.getFolder(relativeTo.getAbsolutePath());
        if (relativeFolder == null) {
            relativeFolder = new Folder(relativeTo.getAbsolutePath());
            repository.putFolder(relativeFolder);
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
                f = repository.getFolder(folderName);
                if (f == null) {
                    f = new Folder(folderName);
                    repository.putFolder(f);
                }
            }
            parentFolder = f;
        }
        parentFolder.addAudioFile(file);
    }
    
    /**
     * Removes from artist structure if necessary
     * @param file
     */
    private void updateArtistStructure(AbstractTag oldTag, LocalAudioObject file) {
		String albumArtist = null;
		String artist = null;
		String album = null;
		if (oldTag != null) {
			albumArtist = oldTag.getAlbumArtist();
			artist = oldTag.getArtist();
			album = oldTag.getAlbum();
		}
		if (artist == null || artist.equals("")) {
			artist = Artist.getUnknownArtist();
		}
		if (album == null || album.equals("")) {
			album = Album.getUnknownAlbum();
		}
		
		boolean albumArtistPresent = true;
		Artist a = repository.getArtist(albumArtist);
		if (a == null) {
			a = repository.getArtist(artist);
			albumArtistPresent = false;
		}
		if (a != null) {
			Album alb = a.getAlbum(album);
			if (alb != null) {
				if (alb.size() == 1) {
					a.removeAlbum(alb);
				} else {
					alb.removeAudioFile(file);
				}

				if (a.size() <= 0) {
					repository.removeArtist(a);
				}
			}
			// If album artist field is present, audio file might still be
			// present under artist name so check
			if (albumArtistPresent) {
				a = repository.getArtist(artist);
				if (a != null) {
					alb = a.getAlbum(album);
					if (alb != null) {
						if (alb.size() == 1) {
							a.removeAlbum(alb);
						} else {
							alb.removeAudioFile(file);
						}
						// Maybe needs to be set to 0 in case node gets
						// deleted
						if (a.size() <= 1) {
							repository.removeArtist(a);
						}
					}
				}
			}
		}
    }
    
    /**
     * Removes from genre structure if necessary
     * @param oldTag
     * @param file
     */
    private void updateGenreStructure(AbstractTag oldTag, LocalAudioObject file) {
		String genre = null;
		if (oldTag != null) {
			genre = oldTag.getGenre();
		}
		if (genre == null || genre.equals("")) {
			genre = Genre.getUnknownGenre();
		}

		Genre g = repository.getGenre(genre);
		if (g != null) {
			g.removeAudioFile(file);

			if (g.size() <= 1) {
				repository.removeGenre(g);
			}
		}
    }
    
    /**
     * Removes from year structure if necessary
     * @param oldTag
     * @param file
     */
    private void updateYearStructure(AbstractTag oldTag, LocalAudioObject file) {
		String year = null;
		if (oldTag != null) {
			year = oldTag.getYear() > 0 ? Integer.toString(oldTag.getYear()) : "";
		}
		if (year == null || year.equals("")) {
			year = Year.getUnknownYear();
		}

		// Remove from year structure if necessary
		Year y = repository.getYear(year);
		if (y != null) {
			y.removeAudioFile(file);

			if (y.size() <= 1) {
				repository.removeYear(y);
			}
		}
    }
}
