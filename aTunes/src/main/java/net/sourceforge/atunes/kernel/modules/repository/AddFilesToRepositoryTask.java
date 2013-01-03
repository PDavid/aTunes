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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Adds to repository a list of files (used after an import to update
 * repository)
 * 
 * @author alex
 * 
 */
public class AddFilesToRepositoryTask {

    private IFrame frame;

    private IRepositoryHandler repositoryHandler;

    private ShowRepositoryDataHelper showRepositoryDataHelper;

    private IBackgroundWorkerFactory backgroundWorkerFactory;

    private RepositoryAddService repositoryAddService;

    private IPlayListHandler playListHandler;

    /**
     * @param playListHandler
     */
    public void setPlayListHandler(final IPlayListHandler playListHandler) {
	this.playListHandler = playListHandler;
    }

    /**
     * @param repositoryAddService
     */
    public void setRepositoryAddService(
	    final RepositoryAddService repositoryAddService) {
	this.repositoryAddService = repositoryAddService;
    }

    /**
     * @param backgroundWorkerFactory
     */
    public void setBackgroundWorkerFactory(
	    final IBackgroundWorkerFactory backgroundWorkerFactory) {
	this.backgroundWorkerFactory = backgroundWorkerFactory;
    }

    /**
     * @param showRepositoryDataHelper
     */
    public void setShowRepositoryDataHelper(
	    final ShowRepositoryDataHelper showRepositoryDataHelper) {
	this.showRepositoryDataHelper = showRepositoryDataHelper;
    }

    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
	this.repositoryHandler = repositoryHandler;
    }

    /**
     * @param frame
     */
    public void setFrame(final IFrame frame) {
	this.frame = frame;
    }

    /**
     * Adds files to repository and refreshes it
     * 
     * @param repository
     * @param files
     */
    public void execute(final IRepository repository, final List<File> files) {
	IBackgroundWorker<Void> worker = backgroundWorkerFactory.getWorker();
	worker.setActionsBeforeBackgroundStarts(new Runnable() {

	    @Override
	    public void run() {
		frame.showProgressBar(true, StringUtils.getString(
			I18nUtils.getString("REFRESHING"), "..."));
	    }
	});
	worker.setBackgroundActions(new Callable<Void>() {

	    @Override
	    public Void call() {
		repositoryAddService.addFilesToRepository(repository, files);
		return null;
	    }
	});
	worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<Void>() {

	    @Override
	    public void call(final Void result) {
		frame.hideProgressBar();
		showRepositoryDataHelper.showRepositoryAudioFileNumber(
			repositoryHandler.getAudioFilesList().size(),
			repositoryHandler.getRepositoryTotalSize(),
			repository.getTotalDurationInSeconds());
		Logger.info("Repository refresh done");

		// Create a playlist with imported files
		List<ILocalAudioObject> audioObjects = new ArrayList<ILocalAudioObject>();
		for (File file : files) {
		    ILocalAudioObject ao = repositoryHandler
			    .getFileIfLoaded(net.sourceforge.atunes.utils.FileUtils
				    .getPath(file));
		    if (ao != null) {
			audioObjects.add(ao);
		    }
		}

		Date now = new Date();
		String dateString = DateFormat.getDateTimeInstance()
			.format(now);

		String name = StringUtils.getString(
			I18nUtils.getString("FILES_IMPORTED"), " - ",
			dateString);
		playListHandler.newPlayList(name, audioObjects);

	    }
	});
	worker.execute();
    }
}
