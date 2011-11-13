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

package net.sourceforge.atunes.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.Callable;

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

	/**
	 * Calls desktop to open a file
	 * @author alex
	 *
	 */
	private static final class OpenFile implements Callable<Void> {
		private final File fileToOpen;

		private OpenFile(File fileToOpen) {
			this.fileToOpen = fileToOpen;
		}

		@Override
		public Void call() {
			try {
				Desktop.getDesktop().open(fileToOpen);
			} catch (IOException e) {
				Logger.error(e);
			}
			return null;
		}
	}

	/**
	 * Calls desktop to open a browser
	 * @author alex
	 *
	 */
	private static final class OpenBrowser implements Callable<Void> {
		private final URI uri;

		private OpenBrowser(URI uri) {
			this.uri = uri;
		}

		@Override
		public Void call() {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) {
				Logger.error(e);
			}
			return null;
		}
	}

	private IBackgroundWorkerFactory backgroundWorkerFactory;
	
	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}
	
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.utils.IDesktop#openSearch(net.sourceforge.atunes.model.ISearch, java.lang.String)
	 */
    @Override
	public void openSearch(ISearch search, String query) {
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.utils.IDesktop#openURL(java.lang.String)
	 */
    @Override
	public void openURL(String url) {
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.utils.IDesktop#openFile(java.io.File, net.sourceforge.atunes.model.IOSManager)
	 */
    @Override
	public void openFile(File file, IOSManager osManager) {
        if (isDesktopSupported()) {
            final File fileToOpen;
            /*
             * Needed for UNC filenames with spaces ->
             * http://bugs.sun.com/view_bug.do?bug_id=6550588
             */
            if (osManager.usesShortPathNames()) {
                fileToOpen = new File(FileNameUtils.getShortPathNameW(file.getAbsolutePath(), osManager));
            } else {
                fileToOpen = file;
            }
            IBackgroundWorker<Void> backgroundWorker = backgroundWorkerFactory.getWorker();
            backgroundWorker.setBackgroundActions(new OpenFile(fileToOpen));
            backgroundWorker.execute();
        }
    }
    
    /**
     * Returns if desktop actions are supported
     * @return
     */
    private boolean isDesktopSupported() {
    	return Desktop.isDesktopSupported();
    }
    
    /**
     * Starts web browser with specified URI.
     * @param uri
     *            URI
     */
    private void openURI(URI uri) {
    	browse(uri);
    }

    /**
     * Opens desktop browser with given address
     * @param uri
     */
    private void browse(final URI uri) {
    	if (isDesktopSupported()) {
            IBackgroundWorker<Void> backgroundWorker = backgroundWorkerFactory.getWorker();
            backgroundWorker.setBackgroundActions(new OpenBrowser(uri));
            backgroundWorker.execute();
    	}
    }
}
