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

package net.sourceforge.atunes.kernel.modules.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearchResult;
import net.sourceforge.atunes.model.ISearchableObject;
import net.sourceforge.atunes.utils.I18nUtils;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 * Searchable object for repository
 * 
 * @author alex
 * 
 */
public final class RepositorySearchableObject implements ISearchableObject {

	private FSDirectory indexDirectory;

	private IOSManager osManager;

	private IRepositoryHandler repositoryHandler;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	public String getSearchableObjectName() {
		return I18nUtils.getString("REPOSITORY");
	}

	@Override
	public FSDirectory getIndexDirectory() throws IOException {
		if (this.indexDirectory == null) {
			this.indexDirectory = new SimpleFSDirectory(this.osManager.getFile(
					this.osManager.getUserConfigFolder(),
					Constants.REPOSITORY_INDEX_DIR));
		}
		return this.indexDirectory;
	}

	@Override
	public List<IAudioObject> getSearchResult(
			final List<ISearchResult> rawSearchResults) {
		List<IAudioObject> result = new ArrayList<IAudioObject>();
		for (ISearchResult rawSearchResult : rawSearchResults) {
			ILocalAudioObject audioFile = this.repositoryHandler
					.getFileIfLoaded(rawSearchResult.getObject().get("url"));
			if (audioFile != null) {
				result.add(audioFile);
			}
		}
		return result;
	}

	@Override
	public List<IAudioObject> getElementsToIndex() {
		return new ArrayList<IAudioObject>(
				this.repositoryHandler.getAudioFilesList());
	}
}
