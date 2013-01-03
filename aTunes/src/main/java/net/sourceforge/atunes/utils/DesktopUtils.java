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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ISearch;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * Desktop utility methods
 */
@PluginApi
public final class DesktopUtils implements IDesktop {

    private IBackgroundWorkerFactory backgroundWorkerFactory;

    private IOSManager osManager;

    /**
     * @param osManager
     */
    public void setOsManager(final IOSManager osManager) {
	this.osManager = osManager;
    }

    /**
     * @param backgroundWorkerFactory
     */
    public void setBackgroundWorkerFactory(
	    final IBackgroundWorkerFactory backgroundWorkerFactory) {
	this.backgroundWorkerFactory = backgroundWorkerFactory;
    }

    @Override
    public void openSearch(final ISearch search, final String query) {
	if (search != null) {
	    try {
		browse(search.getURL(query).toURI());
	    } catch (MalformedURLException e) {
		Logger.error(e);
	    } catch (URISyntaxException e) {
		Logger.error(e);
	    }
	}
    }

    @Override
    public void openURL(final String url) {
	if (isDesktopSupported()) {
	    try {
		openURI(new URL(url).toURI());
	    } catch (MalformedURLException e) {
		Logger.error(e);
	    } catch (URISyntaxException e) {
		Logger.error(e);
	    }
	}
    }

    @Override
    public void openFile(final File file) {
	if (isDesktopSupported()) {
	    final File fileToOpen;
	    /*
	     * Needed for UNC filenames with spaces ->
	     * http://bugs.sun.com/view_bug.do?bug_id=6550588
	     */
	    if (osManager.usesShortPathNames()) {
		fileToOpen = new File(FileNameUtils.getShortPathNameW(
			net.sourceforge.atunes.utils.FileUtils.getPath(file),
			osManager));
	    } else {
		fileToOpen = file;
	    }
	    IBackgroundWorker<Void> backgroundWorker = backgroundWorkerFactory
		    .getWorker();
	    backgroundWorker.setBackgroundActions(new OpenFile(fileToOpen));
	    backgroundWorker.execute();
	}
    }

    /**
     * Returns if desktop actions are supported
     * 
     * @return
     */
    private boolean isDesktopSupported() {
	return Desktop.isDesktopSupported();
    }

    /**
     * Starts web browser with specified URI.
     * 
     * @param uri
     *            URI
     */
    private void openURI(final URI uri) {
	browse(uri);
    }

    /**
     * Opens desktop browser with given address
     * 
     * @param uri
     */
    private void browse(final URI uri) {
	if (isDesktopSupported()) {
	    IBackgroundWorker<Void> backgroundWorker = backgroundWorkerFactory
		    .getWorker();
	    backgroundWorker.setBackgroundActions(new OpenBrowser(uri));
	    backgroundWorker.execute();
	}
    }
}
