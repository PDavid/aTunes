/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.webservices.youtube;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.views.dialogs.TransferProgressDialog;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Downloads a video from YouTube
 * 
 * @author Tobias Melcher
 * 
 */
public class YoutubeVideoDownloader extends SwingWorker<Void, String> {

    /**
     * Logger
     */
    private Logger logger;

    /**
     * String for total byte amount chunk
     */
    private static final String TOTAL = "total=";

    /**
     * String for current byte amount chunk
     */
    private static final String CURRENT = "current=";

    /**
     * Total bytes of video being downloaded
     */
    private long totalBytes;

    /**
     * The entry containing data about the video
     */
    private YoutubeResultEntry entry;

    /**
     * The file where video should be downloaded
     */
    private File file;

    /**
     * Progress dialog to show download
     */
    private TransferProgressDialog progressDialog;

    /**
     * Flag indicating whether download must be aborted
     */
    private boolean cancelled = false;

    /**
     * Creates a new downloader
     * 
     * @param entry
     * @param file
     */
    public YoutubeVideoDownloader(YoutubeResultEntry entry, File file) {
        this.entry = entry;
        File f = file;
        // Adds FLV extension if necessary
        if (!file.getAbsolutePath().toUpperCase().endsWith(".mp4")) {
            f = new File(StringUtils.getString(file.getAbsolutePath(), ".mp4"));
        }
        this.file = f;
        this.progressDialog = GuiHandler.getInstance().getNewTransferProgressDialog(entry.getName(), null);
        this.progressDialog.setIcon(entry.getImage());
        this.progressDialog.setInfoText(I18nUtils.getString("DOWNLOADING"));
        this.progressDialog.addCancelButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelled = true;
            }
        });
        this.progressDialog.setVisible(true);
    }

    @Override
    protected Void doInBackground() throws Exception {
        String url = YoutubeService.getInstance().getDirectUrlToBeAbleToPlaySong(entry.getUrl());
        InputStream input = null;
        FileOutputStream fout = null;
        try {
            URLConnection connection = NetworkUtils.getConnection(url, Proxy.getProxy(ApplicationState.getInstance().getProxy()));
            publish(StringUtils.getString(TOTAL, Integer.toString(connection.getContentLength())));
            input = connection.getInputStream();

            fout = new FileOutputStream(file);
            byte buf[] = new byte[1024];
            // Update every second
            long lastTime = System.currentTimeMillis();
            long allBytes = 0;
            int len;
            while ((len = input.read(buf)) > 0 && !cancelled) {
                fout.write(buf, 0, len);
                allBytes += len;
                if (System.currentTimeMillis() - lastTime > 1000) {
                    publish(StringUtils.getString(CURRENT, Long.toString(allBytes)));
                    lastTime = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
            getLogger().error(LogCategories.SERVICE, e);
        } finally {
            ClosingUtils.close(input);
            ClosingUtils.close(fout);
            // If has been cancelled then delete file
            if (cancelled && file.exists()) {
                file.delete();
            }
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        super.process(chunks);
        if (chunks.get(0).startsWith(TOTAL)) {
            totalBytes = Long.parseLong(chunks.get(0).substring(TOTAL.length()));
            progressDialog.setTotalProgress(totalBytes);
        } else if (chunks.get(0).startsWith(CURRENT)) {
            long current = Long.parseLong(chunks.get(0).substring(CURRENT.length()));
            progressDialog.setCurrentProgress(current);
            int perCent = (int) ((current * 100.0) / totalBytes);
            progressDialog.setProgressBarValue(perCent);
        }
    }

    @Override
    protected void done() {
        super.done();
        progressDialog.setVisible(false);
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
