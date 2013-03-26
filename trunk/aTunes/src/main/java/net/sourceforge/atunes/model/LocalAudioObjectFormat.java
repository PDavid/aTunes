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

package net.sourceforge.atunes.model;

/**
 * Formats for local audio objects supported by application
 * 
 * @author alex
 * 
 */
public enum LocalAudioObjectFormat {

	/**
	 * MP3
	 */
	MP3("mp3"),

	/**
	 * OGG
	 */
	OGG("ogg"),

	/**
	 * MP4
	 */
	MP4_1("m4a"),

	/**
	 * MP4
	 */
	MP4_2("mp4"),

	/**
	 * WAV
	 */
	WAV("wav"),

	/**
	 * WMA
	 */
	WMA("wma"),

	/**
	 * FLAC
	 */
	FLAC("flac"),

	/**
	 * APE
	 */
	APE("ape"),

	/**
	 * MPC
	 */
	MPC("mpc"),

	/**
	 * Real media
	 */
	REAL_1("ra"),

	/**
	 * Real media
	 */
	REAL_2("rm"),

	/**
	 * mp+
	 */
	MPPLUS("mp+"),

	/**
	 * mac
	 */
	MAC("mac"),

	/**
	 * FLV
	 */
	FLV("flv");

	private String extension;

	private LocalAudioObjectFormat(final String extension) {
		this.extension = extension;
	}

	/**
	 * @return
	 */
	public String getExtension() {
		return this.extension;
	}
}
