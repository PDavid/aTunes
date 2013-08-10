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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectReader;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.TextTagAttribute;

/**
 * Responsible of change tags
 * 
 * @author alex
 * 
 */
public class TagHandler extends AbstractHandler implements ITagHandler {

	private IProcessFactory processFactory;

	private IStateNavigation stateNavigation;

	private IDialogFactory dialogFactory;

	private Map<TextTagAttribute, ITagChecker> checkers;

	private IUnknownObjectChecker unknownObjectChecker;

	private IWebServicesHandler webServicesHandler;

	private TagModifier tagModifier;

	private IFileManager fileManager;

	private TagFactory tagFactory;

	private ILocalAudioObjectReader localAudioObjectReader;

	/**
	 * @param localAudioObjectReader
	 */
	public void setLocalAudioObjectReader(
			final ILocalAudioObjectReader localAudioObjectReader) {
		this.localAudioObjectReader = localAudioObjectReader;
	}

	/**
	 * @param tagFactory
	 */
	public void setTagFactory(final TagFactory tagFactory) {
		this.tagFactory = tagFactory;
	}

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param tagModifier
	 */
	public void setTagModifier(final TagModifier tagModifier) {
		this.tagModifier = tagModifier;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(final IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * Gets the edits the tag dialog controller.
	 * 
	 * @return the edits the tag dialog controller
	 */
	private EditTagDialogController getEditTagDialogController() {
		return getBean(EditTagDialogController.class);
	}

	@Override
	public void editFiles(final List<ILocalAudioObject> list) {
		getEditTagDialogController().editFiles(list);
	}

	@Override
	public void editFiles(final IAlbum a) {
		new EditTitlesDialogController(
				this.dialogFactory.newDialog(EditTitlesDialog.class),
				this.processFactory, this.webServicesHandler, this.fileManager)
				.editFiles(a);
	}

	@Override
	public void setTag(final ILocalAudioObject audioObject, final ITag tag) {
		this.tagModifier.setInfo(audioObject, tag);
	}

	@Override
	public void refreshAfterTagModify(
			final Collection<ILocalAudioObject> audioObjectsChanged) {
		this.tagModifier.refreshAfterTagModify(audioObjectsChanged);
	}

	@Override
	public void deleteTags(final ILocalAudioObject audioObject) {
		this.tagModifier.deleteTags(audioObject);
	}

	@Override
	public void setTag(final ILocalAudioObject audioObject, final ITag tag,
			final boolean editCover, final byte[] cover) {
		this.tagModifier.setInfo(audioObject, tag, editCover, cover);
	}

	@Override
	public void setTitle(final ILocalAudioObject audioObject,
			final String newTitle) {
		this.tagModifier.setTitles(audioObject, newTitle);
	}

	@Override
	public void setAlbum(final ILocalAudioObject audioObject,
			final String albumName) {
		this.tagModifier.setAlbum(audioObject, albumName);
	}

	@Override
	public void setGenre(final ILocalAudioObject audioObject, final String genre) {
		this.tagModifier.setGenre(audioObject, genre);
	}

	@Override
	public void setLyrics(final ILocalAudioObject audioObject,
			final String lyricsString) {
		this.tagModifier.setLyrics(audioObject, lyricsString);
	}

	@Override
	public void setStars(final ILocalAudioObject audioObject,
			final Integer value) {
		this.tagModifier.setStars(audioObject, value);
	}

	@Override
	public void setTrackNumber(final ILocalAudioObject audioObject,
			final Integer integer) {
		this.tagModifier.setTrackNumber(audioObject, integer);
	}

	@Override
	public ITag getNewTag() {
		return this.tagFactory.getNewTag();
	}

	@Override
	public ITag getNewTag(final ILocalAudioObject file,
			final Map<String, Object> tagInformation) {
		return this.tagFactory.getNewTag(file, tagInformation);
	}

	@Override
	public boolean hasIncompleteTags(final IAudioObject audioObject) {
		return hasIncompleteTags(audioObject,
				this.stateNavigation
						.getHighlightIncompleteTagFoldersAttributes());
	}

	@Override
	public boolean hasIncompleteTags(
			final ITreeObject<? extends IAudioObject> treeObject) {
		return hasIncompleteTags(treeObject,
				this.stateNavigation
						.getHighlightIncompleteTagFoldersAttributes());
	}

	/**
	 * Returns true if audio file has filled all enabled attributes
	 * 
	 * @param localAudioObject
	 * @param tagAttributesToCheck
	 * @return
	 */
	private boolean hasTagAttributesFilled(
			final ILocalAudioObject localAudioObject,
			final List<TextTagAttribute> tagAttributesToCheck) {
		if (localAudioObject.getTag() == null) {
			return false;
		}

		for (TextTagAttribute ta : tagAttributesToCheck) {
			if (!analyzeAttribute(localAudioObject, ta)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @param localAudioObject
	 * @param ta
	 * @return if attribute is filled
	 */
	private boolean analyzeAttribute(final ILocalAudioObject localAudioObject,
			final TextTagAttribute ta) {
		return getTagCheckerForAttribute(ta)
				.checkTagAttribute(localAudioObject);
	}

	/**
	 * Returns <code>true</code> if tree object contains audio objects with
	 * incomplete tags
	 * 
	 * @param treeObject
	 * @param tagAttributes
	 * @return
	 */
	private boolean hasIncompleteTags(
			final ITreeObject<? extends IAudioObject> treeObject,
			final List<TextTagAttribute> tagAttributes) {
		for (IAudioObject f : treeObject.getAudioObjects()) {
			if (hasIncompleteTags(f, tagAttributes)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <code>true</code> if object has incomplete tag tags
	 * 
	 * @param audioObject
	 * @param tagAttributes
	 * @return
	 */
	private boolean hasIncompleteTags(final IAudioObject audioObject,
			final List<TextTagAttribute> tagAttributes) {
		if (audioObject == null) {
			throw new IllegalArgumentException("Null audioObject");
		}
		if (audioObject instanceof ILocalAudioObject) {
			return !hasTagAttributesFilled((ILocalAudioObject) audioObject,
					tagAttributes);
		}
		return false;
	}

	/**
	 * @param attribute
	 * @return tag checker for given attribute
	 */
	private ITagChecker getTagCheckerForAttribute(
			final TextTagAttribute attribute) {
		if (this.checkers == null) {
			this.checkers = new HashMap<TextTagAttribute, ITagChecker>();
			this.checkers.put(TextTagAttribute.ALBUM, new AlbumTagChecker(
					this.unknownObjectChecker));
			this.checkers.put(TextTagAttribute.ALBUM_ARTIST,
					new AlbumArtistTagChecker(this.unknownObjectChecker));
			this.checkers.put(TextTagAttribute.ARTIST, new ArtistTagChecker(
					this.unknownObjectChecker));
			this.checkers
					.put(TextTagAttribute.COMMENT, new CommentTagChecker());
			this.checkers.put(TextTagAttribute.COMPOSER,
					new ComposerTagChecker());
			this.checkers.put(TextTagAttribute.DISC_NUMBER,
					new DiscNumberTagChecker());
			this.checkers.put(TextTagAttribute.GENRE, new GenreTagChecker(
					this.unknownObjectChecker));
			this.checkers.put(TextTagAttribute.LYRICS, new LyricsTagChecker());
			this.checkers.put(TextTagAttribute.TITLE, new TitleTagChecker());
			this.checkers.put(TextTagAttribute.TRACK, new TrackTagChecker());
			this.checkers.put(TextTagAttribute.YEAR, new YearTagChecker(
					this.unknownObjectChecker));
		}
		return this.checkers.get(attribute);
	}

	@Override
	public ImageIcon getImage(final ILocalAudioObject audioObject,
			final int width, final int height) {
		return this.localAudioObjectReader.getImage(audioObject, width, height);
	}
}
