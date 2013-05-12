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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

final class ReadCddaThread extends Thread {

	private final Cdda2wav cdda2wav;

	private String id = null;

	private String album;

	private String artist;

	private int tracks;

	private String totalDuration;

	private final List<String> durations = new ArrayList<String>();

	private final List<String> titles = new ArrayList<String>();

	private final List<String> artists = new ArrayList<String>();

	private final List<String> composers = new ArrayList<String>();

	private boolean cddbError = false;

	/**
	 * @param cdda2wav
	 */
	public ReadCddaThread(final Cdda2wav cdda2wav) {
		super();
		this.cdda2wav = cdda2wav;
	}

	@Override
	public void run() {
		BufferedReader stdInput = null;
		try {
			stdInput = new BufferedReader(new InputStreamReader(this.cdda2wav
					.getProcess().getErrorStream(), "ISO8859_1"));
			Logger.info("Trying to read cdda2wav stream");

			String s = null;

			// read the output from the command
			while ((s = stdInput.readLine()) != null) {
				Logger.info(StringUtils.getString("While loop: ", s));
				analyzeOutputLine(s);
			}

			// Write data to variable cd
			if (!this.cddbError) {
				this.artist = this.artist != null ? this.artist.replace("\\",
						"\'") : null;
				this.album = this.album != null ? this.album
						.replace("\\", "\'") : null;
			}

			this.artist = this.artist != null ? this.artist.trim() : null;
			this.album = this.album != null ? this.album.trim() : null;

			fillCdInfo();
		} catch (IOException e) {
			Logger.error(e);
		} finally {
			ClosingUtils.close(stdInput);
		}
	}

	/**
	 * Fills cd info object
	 */
	private void fillCdInfo() {
		CDInfo info = this.cdda2wav.getCDInfo();
		info.setTracks(this.tracks);
		info.setDurations(this.durations);
		info.setDuration(this.totalDuration);
		info.setID(this.id);
		if (this.album != null && !this.album.equals("")) {
			info.setAlbum(this.album);
		}

		if (this.artist != null && !this.artist.equals("")) {
			info.setArtist(this.artist);
		}

		info.setTitles(this.titles);
		info.setArtists(this.artists);
		info.setComposers(this.composers);
	}

	/**
	 * Analyzes each line of process output
	 * 
	 * @param outputLine
	 */
	private void analyzeOutputLine(final String outputLine) {
		checkCdLoaded(outputLine);
		checkNoAudioTracks(outputLine);
		analyzeTracks(outputLine);
		analyzeCDDBId(outputLine);
		checkCDDBConnection(outputLine);
		analyzeAlbumAndTrackInformation(outputLine);
		analyzeDataTrack(outputLine);
	}

	/**
	 * @param outputLine
	 */
	private void analyzeDataTrack(final String outputLine) {
		// If there is a data track do remove one track.
		if (outputLine.matches("......................data.*")) {
			this.tracks = this.tracks - 1;
		}
	}

	/**
	 * @param outputLine
	 */
	private void analyzeAlbumAndTrackInformation(final String outputLine) {
		// Get album info (only if connection to cddb could be established)
		if (outputLine.matches("Album title:.*") && !this.cddbError) {
			this.cdda2wav.setCdLoaded(true);
			readAlbumAndArtist(outputLine);
		}

		// Get track info (track number, title name) - Data tracks get ignored.
		else if (outputLine.matches("T..:.*")
				&& !outputLine.matches("......................data.*")
				&& !this.cddbError) {
			this.cdda2wav.setCdLoaded(true);
			readTrackInfo(outputLine);
		}
	}

	/**
	 * @param outputLine
	 */
	private void checkCDDBConnection(final String outputLine) {
		// We need to check if there was an connection error to avoid an
		// exception
		// In this case aTunes will behave as previously (no Artist/Album info).
		if (outputLine.matches(".cddb connect failed.*")) {
			this.cddbError = true;
		}
	}

	/**
	 * @param outputLine
	 */
	private void analyzeCDDBId(final String outputLine) {
		if (outputLine.matches("CDDB discid.*")) {
			this.cdda2wav.setCdLoaded(true);
			this.id = outputLine.substring(outputLine.indexOf('0'));
		}
	}

	/**
	 * @param outputLine
	 */
	private void analyzeTracks(final String outputLine) {
		if (outputLine.matches("Tracks:.*")) {
			this.cdda2wav.setCdLoaded(true);
			this.tracks = Integer.parseInt(outputLine.substring(
					outputLine.indexOf(':') + 1, outputLine.indexOf(' ')));
			this.totalDuration = outputLine
					.substring(outputLine.indexOf(' ') + 1);
		}
	}

	/**
	 * @param outputLine
	 */
	private void checkCdLoaded(final String outputLine) {
		// Used to detect if a CD is present. Don't know if this gets returned
		// on
		// all drive, so may not work as expected. But if it does, this means a
		// CD
		// is present and we don't have to wait until the disk info is read out.
		// This
		// means we can give the "no CD" error much faster!
		if (outputLine.contains("bytes buffer memory requested")) {
			this.cdda2wav.setCdLoaded(true);
		}
	}

	/**
	 * @param outputLine
	 */
	private void checkNoAudioTracks(final String outputLine) {
		// Sometimes cdda2wav gives an error message
		// when a data CD is inserted
		if (outputLine.contains("This disk has no audio tracks")) {
			this.cdda2wav.setCdLoaded(false);
		}
	}

	/**
	 * Gets album and artist from output line of process
	 * 
	 * @param lineOutput
	 */
	private void readAlbumAndArtist(final String lineOutput) {
		String line = lineOutput.trim();

		// Avoid '' sequences
		line = line.replace("''", "' '");

		StringTokenizer albumInfoTokenizer = new java.util.StringTokenizer(
				line, "'");
		// The first part is not interesting, we look for the second token, thus
		// the next line
		if (albumInfoTokenizer.hasMoreTokens()) {
			albumInfoTokenizer.nextToken();
		}
		if (albumInfoTokenizer.hasMoreTokens()) {
			this.album = albumInfoTokenizer.nextToken();
		}
		String token = null;
		if (albumInfoTokenizer.hasMoreElements()) {
			token = albumInfoTokenizer.nextToken();
		}
		// Album names can contain "'" so check if there is something left
		StringBuilder sb = new StringBuilder(this.album);
		while (albumInfoTokenizer.hasMoreElements() && token != null
				&& !token.matches(" from ")) {
			sb.append(token);
			token = albumInfoTokenizer.nextToken();
		}
		this.album = sb.toString();
		if (albumInfoTokenizer.hasMoreTokens()) {
			this.artist = albumInfoTokenizer.nextToken();
		}
		// Artist names can contain "'" so check if there is something left
		sb = new StringBuilder(this.artist);
		while (albumInfoTokenizer.hasMoreTokens()) {
			token = albumInfoTokenizer.nextToken();
			sb.append(token);
		}
		this.artist = sb.toString();
	}

	/**
	 * Reads tracks information
	 * 
	 * @param s
	 */
	private void readTrackInfo(final String s) {
		String duration = s.substring(12, 18).trim();
		this.durations.add(duration);
		// If connection to cddb could be established do
		if (!this.cddbError) {
			String line = s.trim();

			// Avoid '' sequences
			line = line.replace("''", "' '");

			StringTokenizer titleInfoTokenizer = new StringTokenizer(line, "'");
			// The first part is not interesting, we look for the second token,
			// thus the next line
			if (titleInfoTokenizer.hasMoreElements()) {
				titleInfoTokenizer.nextToken();
			}
			String title = null;
			if (titleInfoTokenizer.hasMoreElements()) {
				title = titleInfoTokenizer.nextToken();
			}
			String token = null;
			if (titleInfoTokenizer.hasMoreTokens()) {
				token = titleInfoTokenizer.nextToken();
			}
			if (title != null) {
				// Album names can contain "'" so check if there is something
				// left.
				// Also, add "\" for Windows
				StringBuilder sb = new StringBuilder(title);
				while (titleInfoTokenizer.hasMoreTokens() && token != null
						&& !token.matches(" from ")) {
					sb.append(token);
					token = titleInfoTokenizer.nextToken();
				}
				title = sb.toString();
				title = title.trim();
				title = !title.equals("") ? title.replace("\\", "\'") : null;
				this.titles.add(title);
			}
			// TODO add Song artist
			this.artists.add("");
			this.composers.add("");
		}
	}
}