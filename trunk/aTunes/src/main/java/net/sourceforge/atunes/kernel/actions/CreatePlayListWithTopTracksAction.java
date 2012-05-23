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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.kernel.modules.repository.UnknownObjectChecker;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

public class CreatePlayListWithTopTracksAction extends AbstractActionOverSelectedTreeObjects<IArtist> {

	private static final long serialVersionUID = -8993769615827375740L;

	private IPlayListHandler playListHandler;

	private IRepositoryHandler repositoryHandler;

	private IWebServicesHandler webServicesHandler;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IIndeterminateProgressDialogFactory indeterminateProgressDialogFactory;

	private IIndeterminateProgressDialog dialog;
	
	private IUnknownObjectChecker unknownObjectChecker;
	
	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param indeterminateProgressDialogFactory
	 */
	public void setIndeterminateProgressDialogFactory(IIndeterminateProgressDialogFactory indeterminateProgressDialogFactory) {
		this.indeterminateProgressDialogFactory = indeterminateProgressDialogFactory;
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
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
	 * Default constructor
	 */
	public CreatePlayListWithTopTracksAction() {
		super(I18nUtils.getString("CREATE_PLAYLIST_WITH_TOP_TRACKS"));
		putValue(SHORT_DESCRIPTION, I18nUtils.getString("CREATE_PLAYLIST_WITH_TOP_TRACKS"));
	}

	@Override
	protected void executeAction(List<IArtist> objects) {
		IBackgroundWorker<Map<String, List<ILocalAudioObject>>> worker = backgroundWorkerFactory.getWorker();
		worker.setActionsAfterBackgroundStarted(new Runnable() {
			@Override
			public void run() {
				dialog = indeterminateProgressDialogFactory.newDialog();
				dialog.showDialog();
			}
		});

		worker.setBackgroundActions(new GetTopTracksCallable(objects));

		worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<Map<String, List<ILocalAudioObject>>>() {
			@Override
			public void call(Map<String, List<ILocalAudioObject>> topTracksByArtist) {
				for (Map.Entry<String, List<ILocalAudioObject>> artistTopTracks : topTracksByArtist.entrySet()) {
	    			// Create a new play list with artist as name and audio objects selected
	    			playListHandler.newPlayList(artistTopTracks.getKey(), artistTopTracks.getValue());
				}
				dialog.hideDialog();
			}
		});

		worker.execute();
	}

	@Override
	public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
		if (selection.isEmpty()) {
			return false;
		}
		for (DefaultMutableTreeNode node : selection) {
			if (!(node.getUserObject() instanceof IArtist) || unknownObjectChecker.isUnknownArtist((IArtist) node.getUserObject())) {
				return false;
			}
		}
		return true;
	}

	private final class GetTopTracksCallable implements Callable<Map<String, List<ILocalAudioObject>>> {
		
		private List<IArtist> artists;
		
		/**
		 * @param artists
		 */
		public GetTopTracksCallable(List<IArtist> artists) {
			this.artists = artists;
		}
		
		@Override
		public Map<String, List<ILocalAudioObject>> call() {
			Map<String, List<ILocalAudioObject>> result = new HashMap<String, List<ILocalAudioObject>>();
			for (IArtist artist : artists) {
				IArtistTopTracks topTracks = webServicesHandler.getTopTracks(artist.getName());
				if (topTracks != null) {
					// Get titles for top tracks
					List<String> artistTopTracks = new ArrayList<String>();
					for (ITrackInfo track : topTracks.getTracks()) {
						artistTopTracks.add(track.getTitle());
					}

					// Find in repository
					List<ILocalAudioObject> audioObjectsInRepository = repositoryHandler.getAudioObjectsByTitle(topTracks.getArtist(), artistTopTracks);
					if (!CollectionUtils.isEmpty(audioObjectsInRepository)) {
						result.put(topTracks.getArtist(), audioObjectsInRepository);
						Logger.info("Found ", audioObjectsInRepository.size(), " top tracks for artist: ", artist.getName());
					}
				} else {
					Logger.info("No top tracks found for: ", artist.getName());
				}
			}	
			return result;
		}
	}

}
