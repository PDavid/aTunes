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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.data.Format;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.utils.StringUtils;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.reference.PictureTypes;

/**
 * Class for writing tags to an audio file. We only use JAudiotagger for it. In
 * general, for writing a complete tag, call setInfo.
 * 
 * In general all methods in this class should test if audio file is writable.
 * This is done calling method AudioFile.setWritable which sets write
 * permissions to underlying file if necessary.
 * 
 * @author sylvain
 */
public final class TagModifier {

    private static final class RefreshTagAfterModifyRunnable implements Runnable {
    	
		private List<ILocalAudioObject> audioFilesEditing;
		private IPlayListHandler playListHandler;

		private RefreshTagAfterModifyRunnable(List<ILocalAudioObject> audioFilesEditing, IPlayListHandler playListHandler) {
			this.audioFilesEditing = audioFilesEditing;
			this.playListHandler = playListHandler;
		}

		@Override
		public void run() {
		    // update Swing components if necessary
		    boolean playListContainsRefreshedFile = false;
		    for (int i = 0; i < audioFilesEditing.size(); i++) {
		        if (playListHandler.getCurrentPlayList(true).contains(audioFilesEditing.get(i))) {
		            playListContainsRefreshedFile = true;
		        }

		        // Changed current playing song
		        if (playListHandler.getCurrentAudioObjectFromCurrentPlayList() != null
		                && playListHandler.getCurrentAudioObjectFromCurrentPlayList().equals(audioFilesEditing.get(i))) {
		        	playListHandler.selectedAudioObjectHasChanged(audioFilesEditing.get(i));

		            if (PlayerHandler.getInstance().isEnginePlaying()) {
		                Context.getBean(IUIHandler.class).updateTitleBar(audioFilesEditing.get(i));
		            }
		        }
		    }
		    if (playListContainsRefreshedFile) {
		    	playListHandler.refreshPlayList();
		    }
		    NavigationHandler.getInstance().repositoryReloaded();
		}
	}

    private TagModifier() {

    }

    /**
     * Delete tags.
     * 
     * @param file
     *            the file
     */
    static void deleteTags(ILocalAudioObject file) {
        // Be sure file is writable before setting info
        setWritable(file);
        try {
            org.jaudiotagger.audio.AudioFileIO.delete(org.jaudiotagger.audio.AudioFileIO.read(file.getFile()));
        } catch (IOException e) {
        	reportWriteError(file, e);
        } catch (CannotReadException e) {
        	reportWriteError(file, e);
		} catch (CannotWriteException e) {
        	reportWriteError(file, e);
		} catch (TagException e) {
        	reportWriteError(file, e);
		} catch (ReadOnlyFileException e) {
        	reportWriteError(file, e);
		} catch (InvalidAudioFrameException e) {
        	reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions outside this method
			reportWriteError(file, e);
		}

    }

    /**
     * Refresh after tag modify.
     * 
     * @param audioFilesEditing
     * @param playListHandler
     */
    static void refreshAfterTagModify(final List<ILocalAudioObject> audioFilesEditing, IPlayListHandler playListHandler) {
        SwingUtilities.invokeLater(new RefreshTagAfterModifyRunnable(audioFilesEditing, playListHandler));
    }

    /**
     * Writes album to tag.
     * 
     * @param file
     *            File to which the tag should be written
     * @param album
     *            Album of file
     */
    static void setAlbum(ILocalAudioObject file, String album) {
        // Be sure file is writable before setting info
        setWritable(file);
        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            newTag.setField(FieldKey.ALBUM, album);
            audioFile.commit();
        } catch (IOException e) {
            reportWriteError(file, e);
        } catch (CannotReadException e) {
            reportWriteError(file, e);
		} catch (TagException e) {
            reportWriteError(file, e);
		} catch (ReadOnlyFileException e) {
            reportWriteError(file, e);
		} catch (InvalidAudioFrameException e) {
            reportWriteError(file, e);
		} catch (CannotWriteException e) {
            reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions outside this method
			reportWriteError(file, e);
		}
    }

    /**
     * Writes genre to tag.
     * 
     * @param file
     *            File to which the tag should be written
     * @param genre
     *            Genre of file
     */
    static void setGenre(ILocalAudioObject file, String genre) {
        // Be sure file is writable before setting info
        setWritable(file);
        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            //newTag.setGenre(genre);
            newTag.setField(newTag.createField(FieldKey.GENRE, genre));
            audioFile.commit();
        } catch (TagException e) {
            reportWriteError(file, e);
        } catch (ReadOnlyFileException e) {
            reportWriteError(file, e);
        } catch (InvalidAudioFrameException e) {
            reportWriteError(file, e);
        } catch (CannotWriteException e) {
            reportWriteError(file, e);
        } catch (CannotReadException e) {
            reportWriteError(file, e);
        } catch (IOException e) {
            reportWriteError(file, e);
        } catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions outside this method
			reportWriteError(file, e);
        }
    }
    
    /**
     * Writes tag to audiofile using JAudiotagger.
     * 
     * @param file
     *            File to which the tags should be written
     * @param tag
     *            Tag to be written
     */
    public static void setInfo(ILocalAudioObject file, AbstractTag tag) {
        setInfo(file, tag, false, null);
    }

    /**
     * Writes tag to audio file using JAudiotagger.
     * 
     * @param file
     *            File to which the tags should be written
     * @param tag
     *            Tag to be written
     * @param shouldEditCover
     *            if the cover should be edited
     * @param cover
     *            cover data as byte array
     */
    static void setInfo(ILocalAudioObject file, AbstractTag tag, boolean shouldEditCover, byte[] cover) {
        // Be sure file is writable before setting info
        setWritable(file);

        String title = tag.getTitle();
        String album = tag.getAlbum();
        String artist = tag.getArtist();
        int year = tag.getYear();
        String comment = tag.getComment();
        String genre = tag.getGenre();
        String lyrics = tag.getLyrics();
        String composer = tag.getComposer();
        int track = tag.getTrackNumber();
        int discNumber = tag.getDiscNumber();
        String albumArtist = tag.getAlbumArtist();

        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();

            if (AudioFile.isValidAudioFile(file.getFile().getName(), Format.MP3)) {
                org.jaudiotagger.audio.mp3.MP3File mp3file = (org.jaudiotagger.audio.mp3.MP3File) audioFile;
                if (mp3file.hasID3v1Tag() && !mp3file.hasID3v2Tag()) {
                    deleteTags(file);
                    audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
                    newTag = audioFile.getTagOrCreateAndSetDefault();
                }
            }

            if (shouldEditCover) {
                // Remove old images
                try {
                    newTag.deleteArtworkField();
                } catch (KeyNotFoundException e) {
                	Logger.error(StringUtils.getString("Could not delte artwork field. File: ", file.getUrl(), " Error: ", e));
                }

                if (cover != null) {
                    Artwork artwork = new Artwork();
                    artwork.setBinaryData(cover);
                    artwork.setDescription("cover");
                    artwork.setPictureType(PictureTypes.DEFAULT_ID);
                    artwork.setLinked(false);
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setInput(new ByteArrayInputStream(cover));
                    artwork.setMimeType(imageInfo.getMimeType());
                    newTag.setField(newTag.createField(artwork));
                }
            }

            // Workaround for mp4 files - strings outside genre list might not be written otherwise
            if (AudioFile.isValidAudioFile(file.getFile().getName(), Format.MP4_1, Format.MP4_2)) {
                newTag.deleteField(FieldKey.GENRE);
            }

            setStringTagField(file, newTag, FieldKey.ALBUM, album);
            setStringTagField(file, newTag, FieldKey.ARTIST, artist);
            setStringTagField(file, newTag, FieldKey.COMMENT, comment);
            setStringTagField(file, newTag, FieldKey.GENRE, genre);
            setStringTagField(file, newTag, FieldKey.TITLE, title);
            setNumberTagField(file, newTag, FieldKey.YEAR, year);
            setNumberTagField(file, newTag, FieldKey.TRACK, track);
            if (discNumber <= 0) {
                newTag.deleteField(FieldKey.DISC_NO);
            } else {
                // only set the discnumber if we have a useful one. remove previous one to avoid errors
                String discno = newTag.getFirst(FieldKey.DISC_NO);
                if (!StringUtils.isEmpty(discno)) {
                	newTag.deleteField(FieldKey.DISC_NO);
                }
                newTag.setField(FieldKey.DISC_NO, Integer.toString(discNumber));
            }
            setStringTagField(file, newTag, FieldKey.LYRICS, lyrics);
            setStringTagField(file, newTag, FieldKey.ALBUM_ARTIST, albumArtist);
            setStringTagField(file, newTag, FieldKey.COMPOSER, composer);

            audioFile.setTag(newTag);
            audioFile.commit();
        } catch (IOException e) {
            reportWriteError(file, e);
        } catch (CannotReadException e) {
            reportWriteError(file, e);
		} catch (TagException e) {
            reportWriteError(file, e);
		} catch (ReadOnlyFileException e) {
            reportWriteError(file, e);
		} catch (InvalidAudioFrameException e) {
            reportWriteError(file, e);
		} catch (CannotWriteException e) {
            reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions outside this method
			reportWriteError(file, e);
		}
    }

    private static void setStringTagField(ILocalAudioObject file, org.jaudiotagger.tag.Tag tag, FieldKey fieldKey, String fieldValue) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            // Delete tag field if value is empty
            tag.deleteField(fieldKey);
        } else {
            try {
                tag.setField(tag.createField(fieldKey, fieldValue));
            } catch (FieldDataInvalidException e) {
            	reportWriteFieldError(file, fieldKey, fieldValue, e);
            } catch (KeyNotFoundException e) {
            	reportWriteFieldError(file, fieldKey, fieldValue, e);
            }
        }
    }

    private static void setNumberTagField(ILocalAudioObject file, org.jaudiotagger.tag.Tag tag, FieldKey fieldKey, int fieldValue) {
        if (fieldValue == -1) {
            tag.deleteField(fieldKey);
        } else {
            try {
                tag.setField(fieldKey, String.valueOf(fieldValue));
            } catch (KeyNotFoundException e) {
            	reportWriteFieldError(file, fieldKey, Integer.toString(fieldValue), e);
            } catch (FieldDataInvalidException e) {
            	reportWriteFieldError(file, fieldKey, Integer.toString(fieldValue), e);

            }
        }
    }

    /**
     * Sets the lyrics.
     * 
     * @param file
     *            the file
     * @param lyrics
     *            the lyrics
     */
    static void setLyrics(ILocalAudioObject file, String lyrics) {
        // Be sure file is writable before setting info
        setWritable(file);
        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            newTag.setField(newTag.createField(FieldKey.LYRICS, lyrics));
            audioFile.commit();
        } catch (IOException e) {
            reportWriteError(file, e);
        } catch (CannotReadException e) {
            reportWriteError(file, e);
		} catch (TagException e) {
            reportWriteError(file, e);
		} catch (ReadOnlyFileException e) {
            reportWriteError(file, e);
		} catch (InvalidAudioFrameException e) {
            reportWriteError(file, e);
		} catch (CannotWriteException e) {
            reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions outside this method
			reportWriteError(file, e);
		}
    }

    /**
     * Writes title name to tag.
     * 
     * @param file
     *            File to which the tag should be written
     * @param newTitle
     *            New title
     */
    static void setTitles(ILocalAudioObject file, String newTitle) {
        // Be sure file is writable before setting info
        setWritable(file);
        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            newTag.setField(FieldKey.TITLE, newTitle);
            audioFile.commit();
        } catch (IOException e) {
        	reportWriteError(file, e);
        } catch (CannotReadException e) {
        	reportWriteError(file, e);
		} catch (TagException e) {
        	reportWriteError(file, e);
		} catch (ReadOnlyFileException e) {
        	reportWriteError(file, e);
		} catch (InvalidAudioFrameException e) {
        	reportWriteError(file, e);
		} catch (CannotWriteException e) {
        	reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions outside this method
			reportWriteError(file, e);
		}
    }

    /**
     * Sets track number on a file.
     * 
     * @param file
     *            File to which the tag should be written
     * @param track
     *            Track number
     */
    static void setTrackNumber(ILocalAudioObject file, Integer track) {
        // Be sure file is writable before setting info
        setWritable(file);
        try {
            org.jaudiotagger.audio.AudioFile audioFile = org.jaudiotagger.audio.AudioFileIO.read(file.getFile());
            org.jaudiotagger.tag.Tag newTag = audioFile.getTagOrCreateAndSetDefault();
            newTag.setField(FieldKey.TRACK, track.toString());
            audioFile.commit();
        } catch (IOException e) {
            reportWriteError(file, e);
        } catch (CannotReadException e) {
            reportWriteError(file, e);
		} catch (TagException e) {
            reportWriteError(file, e);
		} catch (ReadOnlyFileException e) {
            reportWriteError(file, e);
		} catch (InvalidAudioFrameException e) {
            reportWriteError(file, e);
		} catch (CannotWriteException e) {
            reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions outside this method
			reportWriteError(file, e);
		}
    }

    /**
     * Logs error while writing
     * @param file
     * @param e
     */
    private static final void reportWriteError(ILocalAudioObject file, Exception e) {
        Logger.error(StringUtils.getString("Could not edit tag. File: ", file.getUrl(), " Error: ", e));
        Logger.error(e);
    }
    
    /**
     * Logs error while writing a field
     * @param file
     * @param fieldKey
     * @param fieldValue
     */
    private static final void reportWriteFieldError(ILocalAudioObject file, FieldKey fieldKey, String fieldValue, Exception e) {
    	Logger.error(StringUtils.getString("Could not edit tag field ", fieldKey.name(), " with value \"", fieldValue, "\" for file: ", file.getUrl(), " Error: ", e));
    }

    /**
     * Sets write permissions if is not writable.
     */
    private static void setWritable(ILocalAudioObject file) {
        // Set write permission on file
        if (!file.getFile().canWrite()) {
            file.getFile().setWritable(true);
        }
        // Set write permission on parent
        if (!file.getFile().getParentFile().canWrite()) {
            file.getFile().getParentFile().setWritable(true);
        }
    }


}
