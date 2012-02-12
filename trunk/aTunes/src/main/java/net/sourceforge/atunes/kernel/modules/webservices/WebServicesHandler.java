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
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IWebServicesHandler;

public class WebServicesHandler extends AbstractHandler implements IWebServicesHandler {

	private LastFmService lastFmService;
	
	private ILyricsService lyricsService;
	
	private ITaskService taskService;
	
	public final void setLastFmService(LastFmService lastFmService) {
		this.lastFmService = lastFmService;
	}
	
	public final void setLyricsService(ILyricsService lyricsService) {
		this.lyricsService = lyricsService;
	}
	
	public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}

	@Override
	public void allHandlersInitialized() {
        lastFmService.submitCacheToLastFm(taskService);
	}

	@Override
	public void applicationFinish() {
        lastFmService.finishService();
        lyricsService.finishService();
	}

	@Override
	public void applicationStateChanged(IState newState) {
        lyricsService.updateService();
	}

	@Override
	protected void initHandler() {
        lyricsService.updateService();
	}

	@Override
	public boolean clearCache() {
		boolean exception = lastFmService.clearCache();
		exception = lyricsService.clearCache() || exception;
		return exception;
	}
	
	@Override
	public void submitNowPlayingInfo(IAudioObject audioObject) {
		if (audioObject instanceof ILocalAudioObject) {
			lastFmService.submitNowPlayingInfoToLastFm((ILocalAudioObject) audioObject, taskService);
		}
	}
	
	@Override
	public void addBannedSong(IAudioObject audioObject) {
		lastFmService.addBannedSong(audioObject);
	}
	
	@Override
	public void addLovedSong(IAudioObject audioObject) {
		lastFmService.addLovedSong(audioObject);
	}
	
	@Override
	public IAlbumInfo getAlbum(String artist, String album) {
		return lastFmService.getAlbum(artist, album);
	}
	
	@Override
	public IAlbumListInfo getAlbumList(String artist) {
		return lastFmService.getAlbumList(artist);
	}
	
	@Override
	public ImageIcon getAlbumImage(IAlbumInfo albumInfo) {
		return lastFmService.getAlbumImage(albumInfo);
	}

	@Override
	public ImageIcon getAlbumThumbImage(IAlbumInfo albumInfo) {
		return lastFmService.getAlbumThumbImage(albumInfo);
	}
	
	@Override
	public ImageIcon getAlbumImage(String artist, String album) {
		return lastFmService.getAlbumImage(artist, album);
	}

	@Override
	public ImageIcon getArtistImage(String artist) {
		return lastFmService.getArtistImage(artist);
	}

	@Override
	public String getBioText(String artist) {
		return lastFmService.getWikiText(artist);
	}

	@Override
	public String getBioURL(String artist) {
		return lastFmService.getWikiURL(artist);
	}
	
	@Override
	public IArtistTopTracks getTopTracks(String artist) {
		return lastFmService.getTopTracks(artist);
	}
	
	@Override
	public String getTitleForAudioObject(IAudioObject f) {
		if (f instanceof ILocalAudioObject) {
			return lastFmService.getTitleForFile((ILocalAudioObject) f);
		}
		return null;
	}
	
	@Override
	public List<ILovedTrack> getLovedTracks() {
		return lastFmService.getLovedTracks();
	}
	
	@Override
	public Boolean testLogin(String user, String password) {
		return lastFmService.testLogin(user, password);
	}
	
	@Override
	public void submit(IAudioObject audioObject, long listened) {
		lastFmService.submitToLastFm(audioObject, listened, taskService);
	}
	
	@Override
	public String getArtistTopTag(String artist) {
		return lastFmService.getArtistTopTag(artist);
	}
	
	@Override
	public ImageIcon getArtistThumbImage(IArtistInfo artist) {
		return lastFmService.getArtistThumbImage(artist);
	}

	@Override
	public ISimilarArtistsInfo getSimilarArtists(String artist) {
		return lastFmService.getSimilarArtists(artist);
	}
	
	@Override
	public int getTrackNumber(ILocalAudioObject audioObject) {
		return lastFmService.getTrackNumberForFile(audioObject);
	}
	
	@Override
	public void removeLovedSong(IAudioObject song) {
		lastFmService.removeLovedSong(song);
	}
	
	@Override
	public ILyrics getLyrics(String artist, String title) {
		return lyricsService.getLyrics(artist, title);
	}
}
