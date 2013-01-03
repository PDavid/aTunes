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

import java.io.File;

import net.sourceforge.atunes.model.CDMetadata;


/**
 * Interface to implement by components responsible of transform a file of an audio object to another file with a different format
 */
public interface Encoder {

	/**
	 * Called to test if encoder is available
	 * @return
	 */
	boolean testEncoder();
	
    /**
     * Encode.
     * @param originalFile
     * @param encodedFile
     * @return
     */
    boolean encode(File originalFile, File encodedFile);

    /**
     * Gets the extension of encoded files.
     * 
     * @return the extension of encoded files
     */
    String getExtensionOfEncodedFiles();

    /**
     * Sets the listener.
     * 
     * @param listener
     *            the new listener
     */
    void setListener(ProgressListener listener);

    /**
     * Sets the quality.
     * 
     * @param quality
     *            the new quality
     */
    void setQuality(String quality);

    /**
     * Stop.
     */
    void stop();

    /**
     * Returns a list of all available qualities for this encoder
     * 
     * @return
     */
    String[] getAvailableQualities();

    /**
     * Returns default quality
     * 
     * @return
     */
    String getDefaultQuality();

    /**
     * Get format name
     * 
     * @return
     */
    String getFormatName();

	/**
	 * Sets tag to encoded file
	 * @param file
	 * @param trackNumber
	 * @param metadata
	 * @return
	 */
	boolean setTag(File file, int trackNumber, CDMetadata metadata);
}
