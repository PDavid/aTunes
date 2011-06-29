/*
 * aTunes 2.1.0-SNAPSHOT
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

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.internetsearch.Search;
import net.sourceforge.atunes.misc.log.Logger;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * Desktop utility methods
 */
@PluginApi
public final class DesktopUtils {

    private static boolean isDesktopSupported;
    private static Desktop desktop;

    static {
        isDesktopSupported = Desktop.isDesktopSupported();
        if (isDesktopSupported) {
            desktop = Desktop.getDesktop();
        }
    }

    private DesktopUtils() {
    }

    /**
     * Starts web browser.
     * 
     * @param search
     *            Search object
     * @param query
     *            query
     */
    public static void openSearch(Search search, String query) {
        if (search != null && isDesktopSupported) {
            try {
                browse(search.getURL(query).toURI());
            } catch (MalformedURLException e) {
                Logger.error(e);
            } catch (URISyntaxException e) {
                Logger.error(e);
            }
        }
    }

    /**
     * Starts web browser with specified URI.
     * 
     * @param uri
     *            URI
     */
    public static void openURI(URI uri) {
        if (isDesktopSupported) {
            browse(uri);
        }
    }

    private static void browse(final URI uri) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    desktop.browse(uri);
                } catch (IOException e) {
                    Logger.error(e);
                }
            }
        });
    }

    /**
     * Starts web browser with specified URL.
     * 
     * @param url
     *            URL
     */
    public static void openURL(String url) {
        if (isDesktopSupported) {
            try {
                openURI(new URL(url).toURI());
            } catch (MalformedURLException e) {
                Logger.error(e);
            } catch (URISyntaxException e) {
                Logger.error(e);
            }
        }
    }

    /**
     * Starts web browser with specified URL.
     * 
     * @param url
     *            URL
     */
    public static void openURL(URL url) {
        if (isDesktopSupported) {
            try {
                openURI(url.toURI());
            } catch (URISyntaxException e) {
                Logger.error(e);
            }
        }
    }

    /**
     * Opens a file with the associated program.
     * 
     * @param file
     *            The file that should be opened
     */
    public static void openFile(File file) {
        if (isDesktopSupported) {
            final File fileToOpen;
            /*
             * Needed for UNC filenames with spaces ->
             * http://bugs.sun.com/view_bug.do?bug_id=6550588
             */
            if (OsManager.usesShortPathNames()) {
                fileToOpen = new File(FileNameUtils.getShortPathNameW(file.getAbsolutePath()));
            } else {
                fileToOpen = file;
            }
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        desktop.open(fileToOpen);
                    } catch (IOException e) {
                        Logger.error(e);
                    }
                    return null;
                }
            }.execute();
        }
    }
}
