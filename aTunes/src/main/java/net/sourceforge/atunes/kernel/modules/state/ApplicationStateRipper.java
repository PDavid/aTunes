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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.Map;

import net.sourceforge.atunes.model.IStateRipper;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateRipper implements IStateRipper {

	private PreferenceHelper preferenceHelper;

	/**
	 * @param preferenceHelper
	 */
	public void setPreferenceHelper(final PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	@Override
	public boolean isUseCdErrorCorrection() {
		return this.preferenceHelper.getPreference(
				Preferences.USE_CD_ERROR_CORRECTION, Boolean.class, false);
	}

	@Override
	public void setUseCdErrorCorrection(final boolean useCdErrorCorrection) {
		this.preferenceHelper.setPreference(
				Preferences.USE_CD_ERROR_CORRECTION, useCdErrorCorrection);
	}

	@Override
	public String getEncoder() {
		return this.preferenceHelper.getPreference(Preferences.ENCODER,
				String.class, "OGG");
	}

	@Override
	public void setEncoder(final String encoder) {
		this.preferenceHelper.setPreference(Preferences.ENCODER, encoder);
	}

	@Override
	public String getEncoderQuality() {
		return this.preferenceHelper.getPreference(Preferences.ENCODER_QUALITY,
				String.class, "5");
	}

	@Override
	public void setEncoderQuality(final String encoderQuality) {
		this.preferenceHelper.setPreference(Preferences.ENCODER_QUALITY,
				encoderQuality);
	}

	@Override
	public String getMp3EncoderQuality() {
		return this.preferenceHelper.getPreference(
				Preferences.MP3_ENCODER_QUALITY, String.class, "medium");
	}

	@Override
	public void setMp3EncoderQuality(final String mp3EncoderQuality) {
		this.preferenceHelper.setPreference(Preferences.MP3_ENCODER_QUALITY,
				mp3EncoderQuality);
	}

	@Override
	public String getFlacEncoderQuality() {
		return this.preferenceHelper.getPreference(
				Preferences.FLAC_ENCODER_QUALITY, String.class, "-5");
	}

	@Override
	public void setFlacEncoderQuality(final String flacEncoderQuality) {
		this.preferenceHelper.setPreference(Preferences.FLAC_ENCODER_QUALITY,
				flacEncoderQuality);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}

}
