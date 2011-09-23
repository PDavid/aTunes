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

package net.sourceforge.atunes.kernel.modules.webservices.youtube;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
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
    private IProgressDialog progressDialog;

    /**
     * Flag indicating whether download must be aborted
     */
    private boolean cancelled = false;

    private ProxyBean proxy;
    
    private YoutubeService youtubeService;
    
    /**
     * Creates a new downloader
     * 
     * @param entry
     * @param file
     * @param proxy
     */
    public YoutubeVideoDownloader(YoutubeResultEntry entry, File file, ProxyBean proxy, YoutubeService youtubeService) {
        this.entry = entry;
        this.youtubeService = youtubeService;
        File f = file;
        // Adds FLV extension if necessary
        if (!file.getAbsolutePath().toUpperCase().endsWith(".mp4")) {
            f = new File(StringUtils.getString(file.getAbsolutePath(), ".mp4"));
        }
        this.file = f;
        this.progressDialog = (IProgressDialog) Context.getBean("transferDialog");
        this.progressDialog.setTitle(entry.getName());
        this.progressDialog.setIcon(entry.getImage());
        this.progressDialog.setInfoText(I18nUtils.getString("DOWNLOADING"));
        this.progressDialog.addCancelButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelled = true;
            }
        });
        this.proxy = proxy;
        this.progressDialog.showDialog();
    }

    @Override
    protected Void doInBackground() throws Exception {
        String url = youtubeService.getDirectUrlToBeAbleToPlaySong(entry.getUrl());
        InputStream input = null;
        FileOutputStream fout = null;
        try {
            URLConnection connection = NetworkUtils.getConnection(url, ExtendedProxy.getProxy(proxy));
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
            Logger.error(e);
        } finally {
            ClosingUtils.close(input);
            ClosingUtils.close(fout);
            // If has been cancelled then delete file
            if (cancelled && file.exists()) {
                if (!file.delete()) {
                	Logger.error(StringUtils.getString(file, " not deleted"));
                }
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
        progressDialog.hideDialog();
    }

}
