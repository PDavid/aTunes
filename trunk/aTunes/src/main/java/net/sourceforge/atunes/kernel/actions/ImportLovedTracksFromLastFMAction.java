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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILovedTrack;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Gets loved tracks from last.fm and sets as favorites
 * 
 * @author alex
 * 
 */
public class ImportLovedTracksFromLastFMAction extends CustomAbstractAction {

	private static final long serialVersionUID = 5620935204300321285L;

	private IIndeterminateProgressDialog dialog;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IWebServicesHandler webServicesHandler;

	private IRepositoryHandler repositoryHandler;

	private IFavoritesHandler favoritesHandler;

	private IDialogFactory dialogFactory;

	private IStateContext stateContext;

	private ITaskService taskService;

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
	  * 
	  */
	public ImportLovedTracksFromLastFMAction() {
		super(I18nUtils.getString("IMPORT_LOVED_TRACKS_FROM_LASTFM"));
	}

	@Override
	protected void initialize() {
		super.initialize();
		setEnabled(this.stateContext.isLastFmEnabled());
	}

	@Override
	protected void executeAction() {
		IBackgroundWorker<List<ILocalAudioObject>, Void> worker = this.backgroundWorkerFactory
				.getWorker();
		worker.setActionsBeforeBackgroundStarts(new Runnable() {
			@Override
			public void run() {
				ImportLovedTracksFromLastFMAction.this.dialog = ImportLovedTracksFromLastFMAction.this.dialogFactory
						.newDialog(IIndeterminateProgressDialog.class);
				ImportLovedTracksFromLastFMAction.this.dialog
						.setTitle(I18nUtils
								.getString("GETTING_LOVED_TRACKS_FROM_LASTFM"));
				ImportLovedTracksFromLastFMAction.this.dialog.showDialog();
			}
		});

		worker.setBackgroundActions(new Callable<List<ILocalAudioObject>>() {

			@Override
			public List<ILocalAudioObject> call() {
				return fetchFavorites();
			}
		});

		worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<List<ILocalAudioObject>>() {
			@Override
			public void call(final List<ILocalAudioObject> lovedTracks) {
				finishAction(lovedTracks);
			}
		});

		worker.execute(this.taskService);
	}

	/**
	 * Retrieves favorites
	 * 
	 * @return
	 */
	private List<ILocalAudioObject> fetchFavorites() {
		List<ILocalAudioObject> favorites = new ArrayList<ILocalAudioObject>();
		List<ILovedTrack> lovedTracks = this.webServicesHandler
				.getLovedTracks();
		if (!lovedTracks.isEmpty()) {
			for (ILovedTrack lovedTrack : lovedTracks) {
				IArtist artist = this.repositoryHandler.getArtist(lovedTrack
						.getArtist());
				if (artist != null) {
					for (ILocalAudioObject audioObject : artist
							.getAudioObjects()) {
						if (audioObject.getTitleOrFileName().equalsIgnoreCase(
								lovedTrack.getTitle())) {
							favorites.add(audioObject);
						}
					}
				}
			}
		}
		return favorites;
	}

	/**
	 * Add favorites and show message
	 * 
	 * @param lovedTracks
	 */
	private void finishAction(final List<ILocalAudioObject> lovedTracks) {
		this.dialog.hideDialog();
		this.favoritesHandler.importFavoriteSongsFromLastFm(lovedTracks);
		this.dialogFactory.newDialog(IMessageDialog.class).showMessage(
				StringUtils.getString(
						I18nUtils.getString("LOVED_TRACKS_IMPORTED"), ": ",
						lovedTracks == null ? "0" : lovedTracks.size()));
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(
			final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(final IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}
}
