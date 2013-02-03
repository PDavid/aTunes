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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.ITagAttributesReviewed;
import net.sourceforge.atunes.model.ITagHandler;

/**
 * Keeps information about a set of changes to be made on tags when importing a
 * set of files
 * 
 * @author fleax
 * 
 */
public class TagAttributesReviewed implements ITagAttributesReviewed {

	/**
	 * List of tag attributes to be reviewed
	 */
	private List<AbstractTagAttributeReviewed> tagAttributes;

	private ITagHandler tagHandler;

	private IRepositoryHandler repositoryHandler;

	private Genres genresHelper;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param genresHelper
	 */
	public void setGenresHelper(final Genres genresHelper) {
		this.genresHelper = genresHelper;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param tagHandler
	 */
	public void setTagHandler(final ITagHandler tagHandler) {
		this.tagHandler = tagHandler;
	}

	/**
	 * Returns all TagAttributeReviewed objects to be used
	 * 
	 * @return
	 */
	private List<AbstractTagAttributeReviewed> getTagAttributes() {
		if (this.tagAttributes == null) {
			this.tagAttributes = new ArrayList<AbstractTagAttributeReviewed>();
			this.tagAttributes.add(new ArtistTagAttributeReviewed("ARTIST",
					this.repositoryHandler));
			this.tagAttributes.add(new AlbumArtistTagAttributeReviewed(
					"ALBUM_ARTIST"));
			this.tagAttributes
					.add(new ComposerTagAttributeReviewed("COMPOSER"));
			this.tagAttributes.add(new AlbumTagAttributeReviewed("ALBUM"));
			this.tagAttributes.add(new GenreTagAttributeReviewed("GENRE",
					this.genresHelper.getGenres()));
			this.tagAttributes.add(new YearTagAttributeReviewed("YEAR"));
			this.tagAttributes.add(new DiscNumberTagAttributeReviewed(
					"DISC_NUMBER"));
		}
		return this.tagAttributes;
	}

	@Override
	public int getTagAttributesCount() {
		return getTagAttributes().size();
	}

	@Override
	public String getTagAttributeName(final int index) {
		if (getTagAttributes().size() <= index) {
			return null;
		}
		return getTagAttributes().get(index).getName();
	}

	@Override
	public String getValueForTagAttribute(final int index,
			final ILocalAudioObject audioFile) {
		if (getTagAttributes().size() <= index) {
			return null;
		}
		return getTagAttributes().get(index).getValue(audioFile);
	}

	@Override
	public String getChangeForAttributeAndFolder(final int index,
			final File folder) {
		if (getTagAttributes().size() <= index) {
			return null;
		}
		if (getTagAttributes().get(index).getChangesMade().containsKey(folder)) {
			return getTagAttributes().get(index).getChangesMade().get(folder);
		}
		return null;
	}

	@Override
	public void setTagAttributeForFolder(final int index, final File folder,
			final String value) {
		if (getTagAttributes().size() <= index) {
			return;
		}
		getTagAttributes().get(index).getChangesMade().put(folder, value);
	}

	@Override
	public ITag getTagForAudioFile(final ILocalAudioObject file) {
		File parentFolder = this.fileManager.getFolder(file);
		ITag tag = null;
		for (AbstractTagAttributeReviewed tagAttribute : getTagAttributes()) {
			if (tagAttribute.getChangesMade().containsKey(parentFolder)) {
				if (tag == null) {
					tag = file.getTag() != null ? file.getTag()
							: this.tagHandler.getNewTag();
				}
				tag = tagAttribute.changeTag(tag, tagAttribute.getChangesMade()
						.get(parentFolder));
			}
		}
		return tag;
	}

	@Override
	public TableCellEditor getCellEditorForTagAttribute(final int index) {
		if (getTagAttributes().size() <= index) {
			return null;
		}
		return getTagAttributes().get(index).getCellEditor();
	}

	@Override
	public int getTagAttributeIndex(final String tagAttributeName) {
		for (int i = 0; i < this.tagAttributes.size(); i++) {
			if (this.tagAttributes.get(i).getName().equals(tagAttributeName)) {
				return i;
			}
		}
		return -1;
	}
}
