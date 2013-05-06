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

package net.sourceforge.atunes.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IOSManager;

/**
 * Calls desktop to open a file
 * 
 * @author alex
 * 
 */
public final class OpenFileBackgroundWorker extends
		BackgroundWorker<Void, Void> {

	private File fileToOpen;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param fileToOpen
	 */
	public void open(final File fileToOpen) {
		this.fileToOpen = this.osManager.getFileNormalized(fileToOpen);
		execute();
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected Void doInBackground() {
		try {
			Desktop.getDesktop().open(fileToOpen);
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

	@Override
	protected void done(Void result) {
	}
}