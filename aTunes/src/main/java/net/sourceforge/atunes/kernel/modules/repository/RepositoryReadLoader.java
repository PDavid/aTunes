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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.util.List;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;

/**
 * Executes repository load when loading a new repository
 * @author alex
 *
 */
public class RepositoryReadLoader extends AbstractRepositoryLoader {

	private int totalFilesToLoad;

	private ILocalAudioObjectValidator localAudioObjectValidator;

	/**
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(final ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}

	@Override
	protected void execute() {
		Thread t = new Thread(this);
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}

	@Override
	protected void runTasksBeforeLoadRepository() {
		totalFilesToLoad = countFilesInDir(getFolders());
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				getRepositoryLoaderListener().notifyFilesInRepository(totalFilesToLoad);
			}
		});
	}

	@Override
	protected void notifyCurrentPath(final String relativePath) {
		getRepositoryLoaderListener().notifyCurrentPath(relativePath);
	}

	@Override
	protected void notifyCurrentProgress() {
		// Update remaining time after a number of files
		if (getFilesLoaded() % 100 == 0 && !isInterrupt()) {
			long t1 = System.currentTimeMillis();
			final long remainingTime = getFilesLoaded() != 0 ? (totalFilesToLoad - getFilesLoaded()) * (t1 - getStartReadTime()) / getFilesLoaded() : 0;
			getRepositoryLoaderListener().notifyRemainingTime(remainingTime);
			getRepositoryLoaderListener().notifyReadProgress();
		}
	}

	@Override
	protected void notifyFileLoaded() {
		getRepositoryLoaderListener().notifyFileLoaded();
	}

	@Override
	protected void notifyCurrentAlbum(final String artist, final String album) {
		// Update current artist and album
		getRepositoryLoaderListener().notifyCurrentAlbum(artist, album);
	}

	@Override
	protected void notifyFinishLoader() {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				getRepositoryLoaderListener().notifyFinishRead(RepositoryReadLoader.this);
			}
		});
	}

	/**
	 * Count files in dir.
	 * 
	 * @param dir
	 *            the dir
	 * 
	 * @return the int
	 */
	private int countFilesInDir(final File dir) {
		int files = 0;
		if (!isInterrupt()) {
			File[] list = dir.listFiles();
			if (list == null) {
				return files;
			}
			for (File element : list) {
				if (localAudioObjectValidator.isValidAudioFile(element)) {
					files++;
				} else if (element.isDirectory()) {
					files = files + countFilesInDir(element);
				}
			}
		}
		return files;
	}

	/**
	 * Count files in dir.
	 * 
	 * @param folders1
	 *            the folders1
	 * 
	 * @return the int
	 */
	private int countFilesInDir(final List<File> folders1) {
		int files = 0;
		for (File f : folders1) {
			files = files + countFilesInDir(f);
		}
		return files;
	}
}
