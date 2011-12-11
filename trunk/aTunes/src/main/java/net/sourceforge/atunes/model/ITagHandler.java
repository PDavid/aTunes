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

package net.sourceforge.atunes.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Responsible of tag edition
 * @author alex
 *
 */
public interface ITagHandler extends IHandler {

	/**
	 * Edits a list of local audio objects retrieved from a given source
	 * @param source
	 * @param list
	 */
	void editFiles(EditTagSources source, List<ILocalAudioObject> list);

	/**
	 * Edits all elements of an album
	 * @param a
	 */
	void editFiles(IAlbum a);

	/**
	 * Sets tag to audio object
	 * @param audioObject
	 * @param tag
	 */
	void setTag(ILocalAudioObject audioObject, ITag tag);
	
	/**
	 * Sets tags to audio object and optionally cover
	 * @param audioObject
	 * @param tag
	 * @param editCover
	 * @param cover
	 */
	void setTag(ILocalAudioObject audioObject, ITag tag, boolean editCover, byte[] cover);

	/**
	 * Refreshes after change tags of fiven audio objects
	 * @param audioObjectsChanged
	 */
	void refreshAfterTagModify(Collection<ILocalAudioObject> audioObjectsChanged);

	/**
	 * Removes tag from given file
	 * @param file
	 */
	void deleteTags(ILocalAudioObject file);

	/**
	 * Sets title to audio object
	 * @param audioObject
	 * @param newTitle
	 */
	void setTitle(ILocalAudioObject audioObject, String newTitle);

	/**
	 * Sets album to audio object
	 * @param audioObject
	 * @param albumName
	 */
	void setAlbum(ILocalAudioObject audioObject, String albumName);

	/**
	 * Sets genre to audio object
	 * @param audioObject
	 * @param genre
	 */
	void setGenre(ILocalAudioObject audioObject, String genre);

	/**
	 * Sets lyrics to audio object
	 * @param audioObject
	 * @param lyricsString
	 */
	void setLyrics(ILocalAudioObject audioObject, String lyricsString);

	/**
	 * Sets track number to audio object
	 * @param audioObject
	 * @param integer
	 */
	void setTrackNumber(ILocalAudioObject audioObject, Integer integer);

	/**
	 * Creates a new empty tag
	 * @return
	 */
	ITag getNewTag();

	/**
	 * Creates a new tag with given tag information
	 * @param file
	 * @param tagInformation
	 * @return
	 */
	ITag getNewTag(ILocalAudioObject file, Map<String, Object> tagInformation);

}