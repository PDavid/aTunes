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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.io.File;
import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.ITable;

/**
 * Deletes a downloaded podcast entry
 * 
 * @author alex
 * 
 */
public final class DeleteDownloadedPodcastFeedBackgroundWorker extends
		BackgroundWorker<Boolean, Void> {

	private File f;
	private IPodcastFeedEntry podcastFeedEntry;
	private ITable navigationTable;

	/**
	 * @param navigationTable
	 */
	public void setNavigationTable(ITable navigationTable) {
		this.navigationTable = navigationTable;
	}

	protected void delete(File f, IPodcastFeedEntry podcastFeedEntry) {
		this.f = f;
		this.podcastFeedEntry = podcastFeedEntry;
		execute();
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected Boolean doInBackground() {
		return f.delete();
	}

	@Override
	protected void done(final Boolean result) {
		if (result) {
			podcastFeedEntry.setDownloaded(false);
			navigationTable.repaint();
		}
	}
}