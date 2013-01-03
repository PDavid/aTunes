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

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IRepositoryLoader;
import net.sourceforge.atunes.model.IRepositoryLoaderListener;

/**
 * Loader listener used when importing folders to repository
 * 
 * @author alex
 * 
 */
final class ImportFoldersLoaderListener implements IRepositoryLoaderListener {

    private final IProgressDialog progressDialog;

    private int filesLoaded = 0;
    private int totalFiles;

    /**
     * @param progressDialog
     */
    public ImportFoldersLoaderListener(final IProgressDialog progressDialog) {
	this.progressDialog = progressDialog;
    }

    @Override
    public void notifyRemainingTime(final long time) {
    }

    @Override
    public void notifyReadProgress() {
    }

    @Override
    public void notifyFinishRefresh(final IRepositoryLoader loader) {
    }

    @Override
    public void notifyCurrentAlbum(final String artist, final String album) {
    }

    @Override
    public void notifyFinishRead(final IRepositoryLoader loader) {
	progressDialog.hideDialog();
    }

    @Override
    public void notifyFilesInRepository(final int files) {
	this.totalFiles = files;
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		progressDialog.setTotalProgress(files);
	    }
	});
    }

    @Override
    public void notifyFileLoaded() {
	this.filesLoaded++;
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		progressDialog.setCurrentProgress(filesLoaded);
		progressDialog
			.setProgressBarValue((int) (filesLoaded * 100.0 / totalFiles));
	    }
	});
    }

    @Override
    public void notifyCurrentPath(final String path) {
    }
}