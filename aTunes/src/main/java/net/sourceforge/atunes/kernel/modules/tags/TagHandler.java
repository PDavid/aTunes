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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.EditTagSources;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.ITreeObject;

public class TagHandler extends AbstractHandler implements ITagHandler {

	private ILookAndFeelManager lookAndFeelManager;
	
	private IPlayListHandler playListHandler;
	
	private IRepositoryHandler repositoryHandler;
	
	private ILocalAudioObjectValidator localAudioObjectValidator;
	
	private IProcessFactory processFactory;
	
	private IPlayerHandler playerHandler;
	
    private IStateNavigation stateNavigation;

    /**
     * @param stateNavigation
     */
    public void setStateNavigation(IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}
	
	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
	
	/**
	 * @param processFactory
	 */
	public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}
	
	/**
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}
	
	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
	
	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
	
	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
	
	
    /** The edit tag dialog controller. */
    private Map<EditTagSources, EditTagDialogController> editTagDialogControllerMap;

    /**
     * Gets the edits the tag dialog controller.
     * 
     * @return the edits the tag dialog controller
     */
    private EditTagDialogController getEditTagDialogController(EditTagSources sourceOfEditTagDialog) {
        if (editTagDialogControllerMap == null) {
            editTagDialogControllerMap = new HashMap<EditTagSources, EditTagDialogController>();
        }

        if (!editTagDialogControllerMap.containsKey(sourceOfEditTagDialog)) {
            boolean arePrevNextButtonsShown = sourceOfEditTagDialog != EditTagSources.NAVIGATOR;
            editTagDialogControllerMap.put(sourceOfEditTagDialog, new EditTagDialogController(new EditTagDialog(getFrame(), arePrevNextButtonsShown, lookAndFeelManager), getOsManager(), playListHandler, repositoryHandler, localAudioObjectValidator, processFactory));
        }
        return editTagDialogControllerMap.get(sourceOfEditTagDialog);
    }

	@Override
	public void editFiles(EditTagSources navigator, List<ILocalAudioObject> asList) {
		getEditTagDialogController(navigator).editFiles(asList);
	}
	
	@Override
	public void editFiles(IAlbum a) {
		new EditTitlesDialogController(new EditTitlesDialog(getFrame().getFrame(), lookAndFeelManager), processFactory).editFiles(a);
	}
	
	@Override
	public void setTag(ILocalAudioObject audioObject, ITag tag) {
		new TagModifier().setInfo(audioObject, tag, localAudioObjectValidator);
	}
	
	@Override
	public void refreshAfterTagModify(Collection<ILocalAudioObject> audioObjectsChanged) {
		new TagModifier().refreshAfterTagModify(audioObjectsChanged, playListHandler, playerHandler);
	}

	@Override
	public void deleteTags(ILocalAudioObject audioObject) {
		new TagModifier().deleteTags(audioObject);
	}
	
	@Override
	public void setTag(ILocalAudioObject audioObject, ITag tag, boolean editCover, byte[] cover) {
		new TagModifier().setInfo(audioObject, tag, editCover, cover, localAudioObjectValidator);
	}
	
	@Override
	public void setTitle(ILocalAudioObject audioObject, String newTitle) {
		new TagModifier().setTitles(audioObject, newTitle);
	}
	
	@Override
	public void setAlbum(ILocalAudioObject audioObject, String albumName) {
		new TagModifier().setAlbum(audioObject, albumName);
	}
	
	@Override
	public void setGenre(ILocalAudioObject audioObject, String genre) {
		new TagModifier().setGenre(audioObject, genre);
	}
	
	@Override
	public void setLyrics(ILocalAudioObject audioObject, String lyricsString) {
		new TagModifier().setLyrics(audioObject, lyricsString);
	}
	
	@Override
	public void setTrackNumber(ILocalAudioObject audioObject, Integer integer) {
		new TagModifier().setTrackNumber(audioObject, integer);
	}

	@Override
	public ITag getNewTag() {
		return new TagFactory().getNewTag();
	}
	
	@Override
	public ITag getNewTag(ILocalAudioObject file, Map<String, Object> tagInformation) {
		return new TagFactory().getNewTag(file, tagInformation);
	}
	
	@Override
	public boolean hasIncompleteTags(IAudioObject audioObject) {
		return IncompleteTagsChecker.hasIncompleteTags(audioObject, stateNavigation.getHighlightIncompleteTagFoldersAttributes());
	}
	
	@Override
	public boolean hasIncompleteTags(ITreeObject<? extends IAudioObject> treeObject) {
		return IncompleteTagsChecker.hasIncompleteTags(treeObject, stateNavigation.getHighlightIncompleteTagFoldersAttributes());
	}
}
