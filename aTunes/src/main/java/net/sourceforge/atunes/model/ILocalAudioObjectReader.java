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

import javax.swing.ImageIcon;

/**
 * Gets a local audio object and reads data (tag and audio properties)
 * 
 * @author alex
 * 
 */
public interface ILocalAudioObjectReader {

	/**
	 * Reads audio object and sets tags
	 * 
	 * @param ao
	 * @param readAudioProperties
	 */
	void readAudioObject(ILocalAudioObject ao, boolean readAudioProperties);

	/**
	 * @param ao
	 * @param width
	 * @param height
	 * @return
	 */
	ImageIcon getImage(ILocalAudioObject ao, int width, int height);

}
