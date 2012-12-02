/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.webservices;

import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILovedTrack;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsService;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * Responsible of interact with web services (last.fm, lyrics, etc)
 * @author alex
 *
 */
public class WebServicesHandler extends AbstractHandler implements IWebServicesHandler {

	private ILyricsService lyricsService;
	
	private ITaskService taskService;
	
	/**
	 * @return last fm service
	 */
	private LastFmService getLastFmService() {
		// use lazy initialization to speedup startup
		return getBean(LastFmService.class);
	}
	
	/**
	 * @param lyricsService
	 */
	public final void setLyricsService(ILyricsService lyricsService) {
		this.lyricsService = lyricsService;
	}
	
	/**
	 * @param taskService
	 */
	public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}

	@Override
	public void deferredInitialization() {
        getLastFmService().submitCacheToLastFm(taskService);
	}
	
	@Override
	public void applicationFinish() {
        getLastFmService().finishService();
        lyricsService.finishService();
	}

	@Override
	public void applicationStateChanged() {
        lyricsService.updateService();
	}

	@Override
	protected void initHandler() {
        lyricsService.updateService();
	}

	@Override
	public boolean clearCache() {
		boolean exception = getLastFmService().clearCache();
		exception = lyricsService.clearCache() || exception;
		return exception;
	}
	
	@Override
	public void submitNowPlayingInfo(IAudioObject audioObject) {
		if (audioObject instanceof ILocalAudioObject) {
			getLastFmService().submitNowPlayingInfoToLastFm((ILocalAudioObject) audioObject, taskService);
		}
	}
	
	@Override
	public void addBannedSong(IAudioObject audioObject) {
		getLastFmService().addBannedSong(audioObject);
	}
	
	@Override
	public void addLovedSong(IAudioObject audioObject) {
		getLastFmService().addLovedSong(audioObject);
	}
	
	@Override
	public IAlbumInfo getAlbum(String artist, String album) {
		return getLastFmService().getAlbum(artist, album);
	}
	
	@Override
	public IAlbumListInfo getAlbumList(String artist) {
		return getLastFmService().getAlbumList(artist);
	}
	
	@Override
	public ImageIcon getAlbumImage(IAlbumInfo albumInfo) {
		return getLastFmService().getAlbumImage(albumInfo);
	}

	@Override
	public ImageIcon getAlbumThumbImage(IAlbumInfo albumInfo) {
		return getLastFmService().getAlbumThumbImage(albumInfo);
	}
	
	@Override
	public ImageIcon getAlbumImage(String artist, String album) {
		return getLastFmService().getAlbumImage(artist, album);
	}

	@Override
	public ImageIcon getArtistImage(String artist) {
		return getLastFmService().getArtistImage(artist);
	}

	@Override
	public String getBioText(String artist) {
		return getLastFmService().getWikiText(artist);
	}

	@Override
	public String getBioURL(String artist) {
		return getLastFmService().getWikiURL(artist);
	}
	
	@Override
	public IArtistTopTracks getTopTracks(String artist) {
		return getLastFmService().getTopTracks(artist);
	}
	
	@Override
	public String getTitleForAudioObject(IAudioObject f) {
		if (f instanceof ILocalAudioObject) {
			return getLastFmService().getTitleForFile((ILocalAudioObject) f);
		}
		return null;
	}
	
	@Override
	public List<ILovedTrack> getLovedTracks() {
		return getLastFmService().getLovedTracks();
	}
	
	@Override
	public Boolean testLogin(String user, String password) {
		return getLastFmService().testLogin(user, password);
	}
	
	@Override
	public void submit(IAudioObject audioObject, long listened) {
		getLastFmService().submitToLastFm(audioObject, listened, taskService);
	}
	
	@Override
	public String getArtistTopTag(String artist) {
		return getLastFmService().getArtistTopTag(artist);
	}
	
	@Override
	public ImageIcon getArtistThumbImage(IArtistInfo artist) {
		return getLastFmService().getArtistThumbImage(artist);
	}

	@Override
	public ISimilarArtistsInfo getSimilarArtists(String artist) {
		return getLastFmService().getSimilarArtists(artist);
	}
	
	@Override
	public int getTrackNumber(ILocalAudioObject audioObject) {
		return getLastFmService().getTrackNumberForFile(audioObject);
	}
	
	@Override
	public void removeLovedSong(IAudioObject song) {
		getLastFmService().removeLovedSong(song);
	}
	
	@Override
	public ILyrics getLyrics(String artist, String title) {
		return lyricsService.getLyrics(artist, title);
	}
	
	@Override
	public void consolidateWebContent() {
		getLastFmService().flush();
	}
}
