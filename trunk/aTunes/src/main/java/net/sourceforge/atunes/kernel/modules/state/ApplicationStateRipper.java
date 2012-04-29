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

package net.sourceforge.atunes.kernel.modules.state;

import net.sourceforge.atunes.model.IStateRipper;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateRipper implements IStateRipper {

	/**
     * Component responsible of store state
     */
    private IStateStore stateStore;
    
    /**
     * Sets state store
     * @param store
     */
    public void setStateStore(IStateStore store) {
		this.stateStore = store;
	}

    @Override
	public boolean isUseCdErrorCorrection() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_CD_ERROR_CORRECTION, false);
    }

    @Override
	public void setUseCdErrorCorrection(boolean useCdErrorCorrection) {
    	this.stateStore.storePreference(Preferences.USE_CD_ERROR_CORRECTION, useCdErrorCorrection);
    }
    
    @Override
	public String getEncoder() {
    	return (String) this.stateStore.retrievePreference(Preferences.ENCODER, "OGG");
    }

    @Override
	public void setEncoder(String encoder) {
    	this.stateStore.storePreference(Preferences.ENCODER, encoder);
    }

    @Override
	public String getEncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.ENCODER_QUALITY, "5");
    }

    @Override
	public void setEncoderQuality(String encoderQuality) {
    	this.stateStore.storePreference(Preferences.ENCODER_QUALITY, encoderQuality);
    }

    @Override
	public String getMp3EncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.MP3_ENCODER_QUALITY, "medium");
    }

    @Override
	public void setMp3EncoderQuality(String mp3EncoderQuality) {
    	this.stateStore.storePreference(Preferences.MP3_ENCODER_QUALITY, mp3EncoderQuality);
    }
    
    @Override
	public String getFlacEncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.FLAC_ENCODER_QUALITY, "-5");
    }

    @Override
	public void setFlacEncoderQuality(String flacEncoderQuality) {
    	this.stateStore.storePreference(Preferences.FLAC_ENCODER_QUALITY, flacEncoderQuality);
    }

    @Override
	public String getCdRipperFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.CD_RIPPER_FILENAME_PATTERN, null);
    }

    @Override
	public void setCdRipperFileNamePattern(String cdRipperFileNamePattern) {
    	this.stateStore.storePreference(Preferences.CD_RIPPER_FILENAME_PATTERN, cdRipperFileNamePattern);
    }
}
