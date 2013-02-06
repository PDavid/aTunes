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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectReader;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.LocalAudioObjectFormat;
import net.sourceforge.atunes.utils.FileUtils;

/**
 * Returns local audio objects by reading or refreshing a file
 * 
 * @author alex
 * 
 */
public class LocalAudioObjectFactory implements ILocalAudioObjectFactory {

	private ILocalAudioObjectReader localAudioObjectReader;

	private ILocalAudioObjectValidator localAudioObjectValidator;

	/**
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(
			final ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}

	/**
	 * @param localAudioObjectReader
	 */
	public void setLocalAudioObjectReader(
			final ILocalAudioObjectReader localAudioObjectReader) {
		this.localAudioObjectReader = localAudioObjectReader;
	}

	@Override
	public ILocalAudioObject getLocalAudioObject(final File file) {
		ILocalAudioObject audioObject = new AudioFile(
				FileUtils.getNormalizedPath(file));
		readAudioObject(audioObject);
		return audioObject;
	}

	@Override
	public ILocalAudioObject refreshAudioObject(
			final ILocalAudioObject audioObject) {
		audioObject.setTag(null);
		readInformation(audioObject, false);
		audioObject.setReadTime(System.currentTimeMillis());
		return audioObject;
	}

	/**
	 * Reads a file
	 * 
	 * @param audioObject
	 */
	private void readAudioObject(final ILocalAudioObject audioObject) {
		// Don't read from formats not supported by Jaudiotagger
		if (!this.localAudioObjectValidator.isOneOfTheseFormats(audioObject,
				LocalAudioObjectFormat.APE, LocalAudioObjectFormat.MPC)) {
			readInformation(audioObject, true);
		}
		audioObject.setReadTime(System.currentTimeMillis());
	}

	/**
	 * Introspect tags. Get the tag for the file and audio properties
	 * 
	 * @param audioObject
	 * @param readAudioProperties
	 */
	private void readInformation(final ILocalAudioObject audioObject,
			final boolean readAudioProperties) {
		this.localAudioObjectReader.readAudioObject(audioObject,
				readAudioProperties);
	}
}
