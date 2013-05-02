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

package net.sourceforge.atunes.kernel.modules.search;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * Creates index for audio objects
 * 
 * @author alex
 * 
 */
public class AudioObjectIndexCreator {

	private IUnknownObjectChecker unknownObjectChecker;

	private AttributesList searchAttributesList;

	/**
	 * @param searchAttributesList
	 */
	public void setSearchAttributesList(
			final AttributesList searchAttributesList) {
		this.searchAttributesList = searchAttributesList;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param audioObject
	 * @return element indexed
	 */
	final Document getDocumentForElement(final IAudioObject audioObject) {
		if (audioObject instanceof ILocalAudioObject) {
			ILocalAudioObject audioFile = (ILocalAudioObject) audioObject;

			Document d = new Document();

			this.searchAttributesList.addFieldsToDocument(d, audioFile);

			/*
			 * All important fields
			 */
			d.add(new Field(
					ISearchHandler.DEFAULT_INDEX,
					StringUtils.getString(
							audioFile.getTitle(),
							" ",
							audioFile.getTrackNumber(),
							" ",
							audioFile.getArtist(this.unknownObjectChecker),
							" ",
							audioFile.getAlbumArtist(this.unknownObjectChecker),
							" ", audioFile.getAlbum(this.unknownObjectChecker),
							" ", audioFile.getComposer(), " ",
							audioFile.getYear(this.unknownObjectChecker), " ",
							audioFile.getGenre(this.unknownObjectChecker)),
					Field.Store.YES, Field.Index.ANALYZED));

			return d;
		}
		return null;
	}
}
