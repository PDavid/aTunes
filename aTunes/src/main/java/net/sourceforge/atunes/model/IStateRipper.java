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
 * @author alex
 * 
 */
public interface IStateRipper extends IState {

	/**
	 * Use cd error correction
	 * 
	 * @return
	 */
	public boolean isUseCdErrorCorrection();

	/**
	 * Use cd error correction
	 * 
	 * @param useCdErrorCorrection
	 */
	public void setUseCdErrorCorrection(boolean useCdErrorCorrection);

	/**
	 * Encoder used
	 * 
	 * @return
	 */
	public String getEncoder();

	/**
	 * Encoder used
	 * 
	 * @param encoder
	 */
	public void setEncoder(String encoder);

	/**
	 * Encoder quality
	 * 
	 * @return
	 */
	public String getEncoderQuality();

	/**
	 * Encoder quality
	 * 
	 * @param encoderQuality
	 */
	public void setEncoderQuality(String encoderQuality);

	/**
	 * MP3 encoder quality
	 * 
	 * @return
	 */
	public String getMp3EncoderQuality();

	/**
	 * MP3 encoder quality
	 * 
	 * @param mp3EncoderQuality
	 */
	public void setMp3EncoderQuality(String mp3EncoderQuality);

	/**
	 * Flac encoder quality
	 * 
	 * @return
	 */
	public String getFlacEncoderQuality();

	/**
	 * Flac encoder quality
	 * 
	 * @param flacEncoderQuality
	 */
	public void setFlacEncoderQuality(String flacEncoderQuality);
}
