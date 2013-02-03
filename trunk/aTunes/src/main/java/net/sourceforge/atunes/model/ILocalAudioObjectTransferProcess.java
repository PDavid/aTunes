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

import java.util.List;

/**
 * A type of processes that move local audio objects
 * 
 * @author alex
 * 
 */
public interface ILocalAudioObjectTransferProcess extends
		IProcess<List<ILocalAudioObject>> {

	/**
	 * Sets the files to transfer by this process
	 * 
	 * @param filesToTransfer
	 */
	void setFilesToTransfer(List<ILocalAudioObject> filesToTransfer);

	/**
	 * @return the filesTransferred
	 */
	List<ILocalAudioObject> getFilesTransferred();

	/**
	 * Destination of transfer
	 * 
	 * @param destination
	 */
	void setDestination(String destination);

}