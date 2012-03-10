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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;

import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.Folder;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.model.ArtistViewMode;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

import org.apache.commons.lang.StringUtils;

/**
 * A class responsible of load repository structure
 * @author fleax
 *
 */
final class RepositoryFiller {

	private IRepository repository;
	private IState state;

	/**
     * Creates a new filler for given repository
     * @param repository
     */
    RepositoryFiller(IRepository repository, IState state) {
    	if (repository == null) {
    		throw new IllegalArgumentException("Repository is null");
    	}
    	this.repository = repository;
    	this.state = state;
    }

    /**
     * Adds a new audio file with a relative path
     * @param audioFile
     * @param repositoryFolderRoot
     * @param relativePathToRepositoryFolderRoot
     */
    void addAudioFile(ILocalAudioObject audioFile, File repositoryFolderRoot, String relativePathToRepositoryFolderRoot) {
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
    void refreshAudioFile(ILocalAudioObject audioFile, ITag oldTag) {
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
    private void addToRepository(ILocalAudioObject audioFile) {
        repository.putFile(audioFile);
        repository.addSizeInBytes(audioFile.getFile().length());
        repository.addDurationInSeconds(audioFile.getDuration());
    }

    /**
     * Adds given audio file to artist structure of given repository
     * 
     * @param audioFile
     */
    private void addToArtistStructure(ILocalAudioObject audioFile) {
    	String artist = null; 
    	if (ArtistViewMode.ARTIST.equals(state.getArtistViewMode())) {
    		 artist = audioFile.getArtist();	
    	} else if (ArtistViewMode.ARTIST_OF_ALBUM.equals(state.getArtistViewMode())) {
   		 	artist = audioFile.getAlbumArtist();	
    	} else {
    		artist = audioFile.getAlbumArtistOrArtist();
    	}
    	
    	String album = audioFile.getAlbum();

    	// Create artist object if needed
    	IArtist artistObject = repository.getArtist(artist);
    	if (artistObject == null) {
    		artistObject = repository.putArtist(new Artist(artist));
    	}

    	// Create album object if needed
    	IAlbum albumObject = artistObject.getAlbum(album);
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
    private void addToGenreStructure(ILocalAudioObject audioFile) {
    	String genre = audioFile.getGenre();
    	IGenre genreObject = repository.getGenre(genre);
    	if (genreObject == null) {
    		genreObject = repository.putGenre(new Genre(genre));
    	}
    	genreObject.addAudioObject(audioFile);
    }

    /**
     * Adds given audio file to year structure of given repository
     * 
     * @param audioFile
     *            the audio file
     */
    private void addToYearStructure(ILocalAudioObject audioFile) {
    	String year = audioFile.getYear();

    	IYear yearObject = repository.getYear(year);
    	if (yearObject == null) {
    		yearObject = new Year(year);
    		repository.putYear(yearObject);
    	}

    	yearObject.addAudioObject(audioFile);
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
    private void addToFolderStructure(File relativeTo, String relativePath, ILocalAudioObject file) {
        IFolder relativeFolder = repository.getFolder(relativeTo.getAbsolutePath());
        if (relativeFolder == null) {
            relativeFolder = new Folder(relativeTo.getAbsolutePath());
            repository.putFolder(relativeFolder);
        }

        String[] foldersInPath = relativePath.split("/");
        IFolder parentFolder = relativeFolder;
        IFolder f = null;
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
    private void updateArtistStructure(ITag oldTag, ILocalAudioObject file) {
		String albumArtist = getAlbumArtist(oldTag);
		String artist = getArtist(oldTag);
		String album = getAlbum(oldTag);
		
		boolean albumArtistPresent = true;
		IArtist a = repository.getArtist(albumArtist);
		if (a == null) {
			a = repository.getArtist(artist);
			albumArtistPresent = false;
		}
		if (a != null) {
			storeArtistInStructure(file, artist, album, albumArtistPresent, a);
		}
    }

	/**
	 * @param file
	 * @param artist
	 * @param album
	 * @param albumArtistPresent
	 * @param a
	 */
	private void storeArtistInStructure(ILocalAudioObject file, String artist, String album, boolean albumArtistPresent, IArtist a) {
		IAlbum alb = a.getAlbum(album);
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
    
    private String getAlbumArtist(ITag tag) {
    	return tag != null ? tag.getAlbumArtist() : null;
    }
    
    private String getArtist(ITag tag) {
    	String artist = tag != null ? tag.getArtist() : null;
		if (StringUtils.isBlank(artist)) {
			artist = UnknownObjectCheck.getUnknownArtist();
		}
		return artist;
    }
    
    private String getAlbum(ITag tag) {
    	String album = tag != null ? tag.getAlbum() : null;
		if (StringUtils.isBlank(album)) {
			album = UnknownObjectCheck.getUnknownAlbum();
		}
		return album;
    }
    
    /**
     * Removes from genre structure if necessary
     * @param oldTag
     * @param file
     */
    private void updateGenreStructure(ITag oldTag, ILocalAudioObject file) {
		String genre = null;
		if (oldTag != null) {
			genre = oldTag.getGenre();
		}
		if (genre == null || genre.equals("")) {
			genre = UnknownObjectCheck.getUnknownGenre();
		}

		IGenre g = repository.getGenre(genre);
		if (g != null) {
			g.removeAudioObject(file);

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
    private void updateYearStructure(ITag oldTag, ILocalAudioObject file) {
		String year = null;
		if (oldTag != null) {
			year = oldTag.getYear() > 0 ? Integer.toString(oldTag.getYear()) : "";
		}
		if (year == null || year.equals("")) {
			year = UnknownObjectCheck.getUnknownYear();
		}

		// Remove from year structure if necessary
		IYear y = repository.getYear(year);
		if (y != null) {
			y.removeAudioObject(file);

			if (y.size() <= 1) {
				repository.removeYear(y);
			}
		}
    }
}
