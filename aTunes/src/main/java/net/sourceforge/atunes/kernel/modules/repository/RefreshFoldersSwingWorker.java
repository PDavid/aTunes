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

import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStatisticsHandler;

final class RefreshFoldersSwingWorker extends SwingWorker<Void, Void> {
	
	private IRepositoryHandler repositoryHandler;
	
	private IRepository repository;
	
	private List<Folder> folders;
	
	private IStatisticsHandler statisticsHandler;
	
	private IOSManager osManager;

	private IState state;
	
	public RefreshFoldersSwingWorker(IRepositoryHandler repositoryHandler, IRepository repository, List<Folder> folders, IStatisticsHandler statisticsHandler, IOSManager osManager,IState state) {
		this.repositoryHandler = repositoryHandler;
		this.repository = repository;
		this.folders = folders;
		this.statisticsHandler = statisticsHandler;
		this.osManager = osManager;
		this.state = state;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		repositoryHandler.startTransaction();
        RepositoryLoader.refreshFolders(state, repository, folders, statisticsHandler, osManager, repositoryHandler);
        repositoryHandler.endTransaction();
		return null;
	}
	
	@Override
	protected void done() {
		super.done();
		repositoryHandler.notifyFinishRefresh(null);
	}
}