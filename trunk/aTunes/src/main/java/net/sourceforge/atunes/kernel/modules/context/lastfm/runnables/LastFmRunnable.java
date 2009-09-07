/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.context.lastfm.runnables;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.sourceforge.atunes.kernel.modules.context.ContextListener;
import net.sourceforge.atunes.kernel.modules.context.lastfm.LastFmService;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;

public class LastFmRunnable implements Runnable {

    private static Logger logger = new Logger();

    private volatile boolean interrupted;
    private LastFmAlbumsRunnable albumsRunnable;
    private LastFmCoversRunnable coversRunnable;
    private LastFmSimilarArtistsRunnable artistsRunnable;
    private ContextListener listener;
    private LastFmService service;
    private AudioObject audioObject;
    private boolean retrieveArtistInfo = true;
    private long id;
    private ExecutorService executorService;

    /**
     * Instantiates a new audio scrobbler runnable.
     * 
     * @param listener
     *            the listener
     * @param service
     *            the service
     * @param audioObject
     *            the audio object
     * @param id
     *            the id
     */
    public LastFmRunnable(ContextListener listener, LastFmService service, AudioObject audioObject, long id, ExecutorService executorService) {
        this.listener = listener;
        this.service = service;
        this.audioObject = audioObject;
        this.id = id;
        this.executorService = executorService;
    }

    /**
     * Interrupt.
     */
    public void interrupt() {
        interrupted = true;
        if (albumsRunnable != null) {
            albumsRunnable.interrupt();
        }
        if (coversRunnable != null) {
            coversRunnable.interrupt();
        }
        if (artistsRunnable != null) {
            artistsRunnable.interrupt();
        }
    }

    @Override
    public void run() {
        albumsRunnable = new LastFmAlbumsRunnable(listener, service, audioObject, id);
        albumsRunnable.setRetrieveArtistInfo(retrieveArtistInfo);
        Future<?> albumsRunnableFuture = executorService.submit(albumsRunnable);
        logger.debug(LogCategories.SERVICE, "LastFmAlbumsRunnable started with id " + id + " for  " + audioObject.getArtist());
        try {
            albumsRunnableFuture.get();
        } catch (ExecutionException e) {
            logger.internalError(e);
        } catch (InterruptedException e) {
            logger.debug(LogCategories.SERVICE, "albums runnable interrupted");
        }

        if (retrieveArtistInfo && !interrupted) {
            coversRunnable = new LastFmCoversRunnable(listener, service, listener.getAlbums(), id, audioObject);
            executorService.submit(coversRunnable);
            logger.debug(LogCategories.SERVICE, "LastFmCoversRunnable started with id " + id);

            artistsRunnable = new LastFmSimilarArtistsRunnable(listener, service, audioObject.getArtist(), id);
            executorService.submit(artistsRunnable);
            logger.debug(LogCategories.SERVICE, "LastFmSimilarArtistsRunnable started with id " + id + " for " + audioObject.getArtist());
        }
    }

    /**
     * Sets the retrieve artist info.
     * 
     * @param retrieveArtistInfo
     *            the new retrieve artist info
     */
    public void setRetrieveArtistInfo(boolean retrieveArtistInfo) {
        this.retrieveArtistInfo = retrieveArtistInfo;
    }
}
