/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.model;

import java.io.File;

/**
 * Responsible of creating local audio objects
 * @author alex
 *
 */
public interface ILocalAudioObjectFactory {

	/**
	 * Returns local audio object for given file
	 * @param file
	 * @return
	 */
	ILocalAudioObject getLocalAudioObject(File file);
	
	/**
	 * Refreshes audio object
	 * @param audioObject
	 * @return
	 */
	ILocalAudioObject refreshAudioObject(ILocalAudioObject audioObject);
}
