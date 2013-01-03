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

package net.sourceforge.atunes.kernel.modules.context.audioobject;

import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsRetrieveOperation;
import net.sourceforge.atunes.model.ILyricsService;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

/**
 * Context data source to retrieve lyrics
 * @author alex
 *
 */
public class LyricsDataSource implements IContextInformationSource {

	private ILyricsService lyricsService;

	private ILyrics lyrics;

	private IAudioObject audioObject;

	private ILyricsRetrieveOperation lyricsRetrieveOperation;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	public void getData(final IAudioObject audioObject) {
		this.audioObject = audioObject;
		this.lyrics = getLyricsData(audioObject);
	}

	/**
	 * @return
	 */
	public IAudioObject getAudioObject() {
		return audioObject;
	}

	/**
	 * @return
	 */
	public ILyrics getLyrics() {
		return lyrics;
	}

	/**
	 * Returns lyrics
	 * 
	 * @param audioObject
	 * @return
	 */
	private ILyrics getLyricsData(final IAudioObject audioObject) {
		ILyrics lyricsData = null;
		// First check if tag contains the lyrics. Favour this over internet services.
		if (!audioObject.getLyrics().trim().isEmpty()) {
			lyricsData = new Lyrics(audioObject.getLyrics(), null);
		}
		// Query internet service for lyrics
		else {
			if (!audioObject.getTitle().trim().isEmpty() && !audioObject.getArtist(unknownObjectChecker).trim().isEmpty() && !audioObject.getArtist(unknownObjectChecker).equals(unknownObjectChecker.getUnknownArtist())) {
				lyricsRetrieveOperation = lyricsService.getLyricsRetrieveOperation(audioObject.getArtist(unknownObjectChecker).trim(), audioObject.getTitle().trim());
				lyricsData = lyricsRetrieveOperation.getLyrics();
			}
		}

		if (lyricsData == null) {
			lyricsData = new Lyrics("", "");
		}

		return lyricsData;
	}

	/**
	 * @param lyricsService
	 */
	public void setLyricsService(final ILyricsService lyricsService) {
		this.lyricsService = lyricsService;
	}

	@Override
	public void cancel() {
		if (lyricsRetrieveOperation != null) {
			lyricsRetrieveOperation.cancelRetrieve();
		}
	}
}
