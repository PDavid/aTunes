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

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.ISearch;

/**
 * Desktop utility methods
 */

public final class DesktopUtils implements IDesktop {

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
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
			this.beanFactory.getBean(OpenFileBackgroundWorker.class).open(file);
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
			this.beanFactory.getBean(OpenBrowserBackgroundWorker.class).open(
					uri);
		}
	}
}
