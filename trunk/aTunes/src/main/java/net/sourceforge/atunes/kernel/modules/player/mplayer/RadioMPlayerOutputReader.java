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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import java.util.regex.Pattern;

import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStateRadio;
import net.sourceforge.atunes.utils.Logger;

/**
 * Output reader for radios
 * 
 * @author alex
 * 
 */
public class RadioMPlayerOutputReader extends AbstractMPlayerOutputReader {

	/** Pattern of end of play back */
	private static final Pattern END_PATTERN = Pattern
			.compile(".*\\x2e\\x2e\\x2e.*\\(.*\\).*");

	private IRadio radio;
	private String lastArtist = "";
	private String lastTitle = "";
	private boolean started;
	private IStateRadio stateRadio;
	private IPlayListHandler playListHandler;
	private IContextHandler contextHandler;

	/**
	 * @param radio
	 */
	public void setRadio(final IRadio radio) {
		this.radio = radio;
	}

	/**
	 * @param stateRadio
	 */
	public void setStateRadio(final IStateRadio stateRadio) {
		this.stateRadio = stateRadio;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param contextHandler
	 */
	public void setContextHandler(final IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}

	@Override
	protected void init() {
		getEngine().setCurrentLength(this.radio.getDuration() * 1000L);
	}

	@Override
	protected void read(final String line) {
		if (!isReadStopped()) {
			super.read(line);
			// When starting playback, update status bar
			if (!this.started && line.startsWith("Starting playback")) {
				getEngine().notifyRadioOrPodcastFeedEntry();
				this.started = true;
			}

			// Read bitrate and frequency of radios
			if (line.startsWith("AUDIO:")) {
				readBitrateAndFrequencyOfRadio(line);
			}

			// Read song info from radio stream
			if (this.stateRadio.isReadInfoFromRadioStream()
					&& line.startsWith("ICY Info:")) {
				readInfoFromRadioStream(line);
			}

			// End (Quit)
			if (END_PATTERN.matcher(line).matches()) {
				this.radio.deleteSongInfo();
				this.playListHandler.refreshPlayList();
			}
		}
	}

	/**
	 * @param line
	 */
	private void readBitrateAndFrequencyOfRadio(final String line) {
		final String[] s = line.split(" ");
		if (s.length >= 2) {
			try {
				this.radio.setFrequency(Integer.parseInt(s[1]));
			} catch (NumberFormatException e) {
				Logger.info("Could not read radio frequency");
			}
		}
		if (s.length >= 7) {
			try {
				this.radio.setBitrate((long) Double.parseDouble(s[6]));
			} catch (NumberFormatException e) {
				Logger.info("Could not read radio bitrate");
			}
		}
		this.playListHandler.refreshPlayList();
	}

	/**
	 * @param line
	 */
	private void readInfoFromRadioStream(final String line) {
		try {
			int i = line.indexOf("StreamTitle=");
			int j = line.indexOf(';', i);
			String info = line.substring(i + 13, j - 1);
			int k = info.indexOf('-');
			String artist = info.substring(0, k).trim();
			this.radio.setArtist(artist);
			String title = info.substring(k + 1, info.length()).trim();
			this.radio.setTitle(title);
			this.radio.setSongInfoAvailable(true);
			this.playListHandler.refreshPlayList();
			if ((!title.equals(this.lastTitle) || !artist
					.equals(this.lastArtist))
					&& this.radio.equals(this.playListHandler
							.getCurrentAudioObjectFromCurrentPlayList())) {
				this.contextHandler.retrieveInfoAndShowInPanel(this.radio);
			}
			this.lastArtist = artist;
			this.lastTitle = title;
		} catch (IndexOutOfBoundsException e) {
			Logger.info("Could not read song info from radio");
		}
	}

}
