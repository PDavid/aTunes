/*
 * aTunes 2.2.0-SNAPSHOT
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Removes files from repository
 * 
 * @author alex
 * 
 */
public final class DeleteFilesWorker extends SwingWorker<Void, Void> {

    private final IDialogFactory dialogFactory;

    private final IRepositoryHandler repositoryHandler;

    private IIndeterminateProgressDialog dialog;

    private final List<ILocalAudioObject> files;

    /**
     * @param dialogFactory
     * @param repositoryHandler
     * @param files
     */
    public DeleteFilesWorker(final IDialogFactory dialogFactory,
	    final IRepositoryHandler repositoryHandler,
	    final List<ILocalAudioObject> files) {
	this.dialogFactory = dialogFactory;
	this.repositoryHandler = repositoryHandler;
	this.files = files;
    }

    @Override
    protected Void doInBackground() {
	GuiUtils.callInEventDispatchThread(new Runnable() {
	    @Override
	    public void run() {
		dialog = dialogFactory
			.newDialog(IIndeterminateProgressDialog.class);
		dialog.setTitle(I18nUtils.getString("PLEASE_WAIT"));
		dialog.showDialog();
	    }
	});

	List<ILocalAudioObject> filesDeleted = new ArrayList<ILocalAudioObject>();
	for (ILocalAudioObject audioFile : files) {
	    File file = audioFile.getFile();
	    if (file != null && !file.delete()) {
		Logger.error(StringUtils.getString(file, " not deleted"));
	    } else {
		filesDeleted.add(audioFile);
	    }
	}
	repositoryHandler.remove(filesDeleted);
	return null;
    }

    @Override
    protected void done() {
	dialog.hideDialog();
    }
}