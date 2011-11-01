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
import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.ILovedTrack;
import net.sourceforge.atunes.model.IMessageDialogFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class ImportLovedTracksFromLastFMAction extends CustomAbstractAction {

    private static final long serialVersionUID = 5620935204300321285L;

	private IIndeterminateProgressDialog dialog;
	
	private IFrame frame;
	
	private ILookAndFeelManager lookAndFeelManager;
	
    private IBackgroundWorkerFactory backgroundWorkerFactory;
    
    private IWebServicesHandler webServicesHandler;
    
    private IRepositoryHandler repositoryHandler;
    
    private IIndeterminateProgressDialogFactory indeterminateProgressDialogFactory;
    
    private IFavoritesHandler favoritesHandler;
    
    private IMessageDialogFactory messageDialogFactory;
    
    public ImportLovedTracksFromLastFMAction() {
        super(I18nUtils.getString("IMPORT_LOVED_TRACKS_FROM_LASTFM"));
    }
    
    @Override
    protected void initialize() {
        setEnabled(getState().isLastFmEnabled());
    }

    @Override
    protected void executeAction() {
        IBackgroundWorker<List<ILocalAudioObject>> worker = backgroundWorkerFactory.getWorker();
        worker.setActionsAfterBackgroundStarted(new Runnable() {
        	@Override
        	public void run() {
        		dialog = indeterminateProgressDialogFactory.newDialog(frame, lookAndFeelManager);
        		dialog.setTitle(I18nUtils.getString("GETTING_LOVED_TRACKS_FROM_LASTFM"));
        		dialog.showDialog();
        	}
        });
        
        worker.setBackgroundActions(new Callable<List<ILocalAudioObject>>() {
			
			@Override
			public List<ILocalAudioObject> call() throws Exception {
				return fetchFavorites();
			}
		});
        
        worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<List<ILocalAudioObject>>() {
        	@Override
        	public void call(List<ILocalAudioObject> lovedTracks) {
        		finishAction(lovedTracks);
        	}
		});
        
        worker.execute();
    }
    
    /**
     * Retrieves favorites
     * @return
     */
    private List<ILocalAudioObject> fetchFavorites() {
        List<ILocalAudioObject> favorites = new ArrayList<ILocalAudioObject>();
        List<ILovedTrack> lovedTracks = webServicesHandler.getLovedTracks();
        if (!lovedTracks.isEmpty()) {
            for (ILovedTrack lovedTrack : lovedTracks) {
                Artist artist = repositoryHandler.getArtist(lovedTrack.getArtist());
                if (artist != null) {
                    for (ILocalAudioObject audioObject : artist.getAudioObjects()) {
                        if (audioObject.getTitleOrFileName().equalsIgnoreCase(lovedTrack.getTitle())) {
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
     * @param lovedTracks
     */
    private void finishAction(List<ILocalAudioObject> lovedTracks) {
		dialog.hideDialog();
        // Set favorites, but not in last.fm again
		favoritesHandler.addFavoriteSongs(lovedTracks, false);
		messageDialogFactory.getDialog()
			.showMessage(StringUtils.getString(I18nUtils.getString("LOVED_TRACKS_IMPORTED"), ": ", lovedTracks == null ? "0" : lovedTracks.size()), frame);
    }
    
    /**
     * @param frame
     */
    public void setFrame(IFrame frame) {
		this.frame = frame;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
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
     * @param indeterminateProgressDialogFactory
     */
    public void setIndeterminateProgressDialogFactory(IIndeterminateProgressDialogFactory indeterminateProgressDialogFactory) {
		this.indeterminateProgressDialogFactory = indeterminateProgressDialogFactory;
	}
    
    /**
     * @param favoritesHandler
     */
    public void setFavoritesHandler(IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}
    
    /**
     * @param messageDialogFactory
     */
    public void setMessageDialogFactory(IMessageDialogFactory messageDialogFactory) {
		this.messageDialogFactory = messageDialogFactory;
	}
    

}
