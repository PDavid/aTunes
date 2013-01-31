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

package net.sourceforge.atunes.kernel.modules.tags;

import java.io.File;
import java.io.IOException;

import net.sourceforge.atunes.utils.Logger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

/**
 * Reads files with JAudiotagger returning an org.jaudiotagger.audio.AudioFile
 * object
 * 
 * @author alex
 * 
 */
public class JAudiotaggerFileReader {

	/**
	 * Reads file with jaudiotagger
	 * 
	 * @param file
	 * @return audio file or null
	 */
	public AudioFile getAudioFile(final File file) {
		AudioFile audioFile = null;
		try {
			audioFile = AudioFileIO.read(file);
		} catch (NegativeArraySizeException e) {
			// Reading wrong tags can happen this
			// java.lang.NegativeArraySizeException: null
			// org.jaudiotagger.tag.id3.ID3Compression.uncompress(ID3Compression.java:38)
			logExceptionReading(file, e);
		} catch (NumberFormatException e) {
			// With fields with very big numbers this exception can be thrown
			logExceptionReading(file, e);
		} catch (IllegalArgumentException e) {
			// Reading wrong tags can happen this
			// java.lang.IllegalArgumentException: null
			// java.nio.Buffer.position(Unknown Source)
			// org.jaudiotagger.tag.id3.ID3v23Frame.read(ID3v23Frame.java:460)
			logExceptionReading(file, e);
		} catch (RuntimeException e) {
			// Reading wrong tags can happen this
			// java.lang.RuntimeException:
			// org.jaudiotagger.tag.id3.framebody.FrameBodyEQUA.<init>(java.nio.ByteBuffer,
			// int)
			logExceptionReading(file, e);
		} catch (CannotReadException e) {
			logExceptionReading(file, e);
		} catch (IOException e) {
			logExceptionReading(file, e);
		} catch (TagException e) {
			logExceptionReading(file, e);
		} catch (ReadOnlyFileException e) {
			logExceptionReading(file, e);
		} catch (InvalidAudioFrameException e) {
			logExceptionReading(file, e);
		}
		return audioFile;
	}

	private void logExceptionReading(final File file, final Exception e) {
		Logger.error(net.sourceforge.atunes.utils.FileUtils.getPath(file));
		Logger.error(e.getMessage());
	}
}
