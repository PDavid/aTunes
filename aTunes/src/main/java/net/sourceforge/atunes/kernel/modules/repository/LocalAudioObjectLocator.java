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
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectLocator;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;

/**
 * Used to find and load local audio objects
 * @author alex
 *
 */
public class LocalAudioObjectLocator implements ILocalAudioObjectLocator {

	private ILocalAudioObjectFactory localAudioObjectFactory;
	
	private ILocalAudioObjectValidator localAudioObjectValidator;
	
	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}
	
	/**
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}
	
	/**
	 * Gets the local audio objects from folder
	 * @param folder
	 * @param listener
	 * @return
	 */
	@Override
	public List<ILocalAudioObject> locateLocalAudioObjectsInFolder(File folder, IRepositoryLoaderListener listener) {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		locateLocalAudioObjectsInFolder(folder, listener, result);
		return result;
	}

	/**
	 * @param folder
	 * @param listener
	 * @param result
	 */
	private void locateLocalAudioObjectsInFolder(File folder, IRepositoryLoaderListener listener, List<ILocalAudioObject> result) {
		File[] list = folder.listFiles();
		if (list != null) {
			// First find audio and files
			for (File element : list) {
				if (localAudioObjectValidator.isValidAudioFile(element)) {
					result.add(localAudioObjectFactory.getLocalAudioObject(element));
					if (listener != null) {
						listener.notifyFileLoaded();
					}
				} else if (element.isDirectory()) {
					locateLocalAudioObjectsInFolder(element, listener, result);
				}
			}
		}
	}
}
