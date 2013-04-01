/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;

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

	private IBeanFactory beanFactory;

	private RatingsToStars ratingsToStars;

	private TagAdapterSelector tagAdapterSelector;

	/**
	 * @param tagAdapterSelector
	 */
	public void setTagAdapterSelector(
			final TagAdapterSelector tagAdapterSelector) {
		this.tagAdapterSelector = tagAdapterSelector;
	}

	/**
	 * @param ratingsToStars
	 */
	public void setRatingsToStars(final RatingsToStars ratingsToStars) {
		this.ratingsToStars = ratingsToStars;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Delete tags.
	 * 
	 * @param file
	 *            the file
	 */
	void deleteTags(final ILocalAudioObject file) {
		this.tagAdapterSelector.selectAdapter(file).deleteTags(file);
	}

	/**
	 * Refresh after tag modify.
	 * 
	 * @param audioFilesEditing
	 */
	void refreshAfterTagModify(
			final Collection<ILocalAudioObject> audioFilesEditing) {
		RefreshTagAfterModifyRunnable runnable = this.beanFactory
				.getBean(RefreshTagAfterModifyRunnable.class);
		runnable.setAudioFilesEditing(audioFilesEditing);
		SwingUtilities.invokeLater(runnable);
	}

	/**
	 * Writes album to tag.
	 * 
	 * @param file
	 *            File to which the tag should be written
	 * @param album
	 *            Album of file
	 */
	void setAlbum(final ILocalAudioObject file, final String album) {
		this.tagAdapterSelector.selectAdapter(file).modifyAlbum(file, album);
	}

	/**
	 * Writes genre to tag.
	 * 
	 * @param file
	 *            File to which the tag should be written
	 * @param genre
	 *            Genre of file
	 */
	void setGenre(final ILocalAudioObject file, final String genre) {
		this.tagAdapterSelector.selectAdapter(file).modifyGenre(file, genre);
	}

	/**
	 * Writes tag to audiofile using JAudiotagger.
	 * 
	 * @param file
	 * @param tag
	 */
	void setInfo(final ILocalAudioObject file, final ITag tag) {
		setInfo(file, tag, false, null);
	}

	/**
	 * Writes tag to audio file using JAudiotagger.
	 * 
	 * @param file
	 * @param tag
	 * @param shouldEditCover
	 * @param cover
	 */
	void setInfo(final ILocalAudioObject file, final ITag tag,
			final boolean shouldEditCover, final byte[] cover) {

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

		this.tagAdapterSelector.selectAdapter(file).writeTag(file,
				shouldEditCover, cover, title, album, artist, year, comment,
				genre, lyrics, composer, track, discNumber, albumArtist);
	}

	/**
	 * Sets the lyrics.
	 * 
	 * @param file
	 *            the file
	 * @param lyrics
	 *            the lyrics
	 */
	void setLyrics(final ILocalAudioObject file, final String lyrics) {
		this.tagAdapterSelector.selectAdapter(file).modifyLyrics(file, lyrics);
	}

	/**
	 * Writes title name to tag.
	 * 
	 * @param file
	 *            File to which the tag should be written
	 * @param newTitle
	 *            New title
	 */
	void setTitles(final ILocalAudioObject file, final String newTitle) {
		this.tagAdapterSelector.selectAdapter(file).modifyTitle(file, newTitle);
	}

	/**
	 * Sets track number on a file.
	 * 
	 * @param file
	 *            File to which the tag should be written
	 * @param track
	 *            Track number
	 */
	void setTrackNumber(final ILocalAudioObject file, final Integer track) {
		this.tagAdapterSelector.selectAdapter(file).modifyTrack(file, track);
	}

	/**
	 * Sets rating
	 * 
	 * @param audioObject
	 * @param value
	 */
	public void setStars(final ILocalAudioObject audioObject,
			final Integer value) {
		this.tagAdapterSelector.selectAdapterForRating(audioObject)
				.modifyRating(audioObject,
						this.ratingsToStars.starsToRating(value));
	}
}
