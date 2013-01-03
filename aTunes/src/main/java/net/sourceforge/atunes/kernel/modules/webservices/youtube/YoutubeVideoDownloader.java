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

package net.sourceforge.atunes.kernel.modules.webservices.youtube;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IVideoEntry;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
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
	private final IVideoEntry entry;

	/**
	 * The file where video should be downloaded
	 */
	private final File file;

	/**
	 * Progress dialog to show download
	 */
	private IProgressDialog progressDialog;

	/**
	 * Flag indicating whether download must be aborted
	 */
	private boolean cancelled = false;

	private final YoutubeService youtubeService;

	private final INetworkHandler networkHandler;

	private final IDialogFactory dialogFactory;

	/**
	 * Creates a new downloader
	 * @param entry
	 * @param file
	 * @param youtubeService
	 * @param networkHandler
	 * @param dialogFactory
	 */
	public YoutubeVideoDownloader(final IVideoEntry entry, final File file, final YoutubeService youtubeService, final INetworkHandler networkHandler, final IDialogFactory dialogFactory) {
		this.entry = entry;
		this.youtubeService = youtubeService;
		this.dialogFactory = dialogFactory;
		File f = file;
		// Adds FLV extension if necessary
		if (!net.sourceforge.atunes.utils.FileUtils.getPath(file).toUpperCase().endsWith(".mp4")) {
			f = new File(StringUtils.getString(net.sourceforge.atunes.utils.FileUtils.getPath(file), ".mp4"));
		}
		this.file = f;
		prepareProgressDialog(entry);
		this.networkHandler = networkHandler;
		this.progressDialog.showDialog();
	}

	/**
	 * @param entry
	 */
	private void prepareProgressDialog(final IVideoEntry entry) {
		this.progressDialog = dialogFactory.newDialog("transferDialog", IProgressDialog.class);
		this.progressDialog.setTitle(entry.getName());
		this.progressDialog.setIcon(entry.getImage());
		this.progressDialog.setInfoText(I18nUtils.getString("DOWNLOADING"));
		this.progressDialog.addCancelButtonActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				cancelled = true;
			}
		});
	}

	@Override
	protected Void doInBackground() {
		String url = youtubeService.getDirectUrlToBeAbleToPlaySong(entry.getUrl());
		InputStream input = null;
		FileOutputStream fout = null;
		try {
			URLConnection connection = networkHandler.getConnection(url);
			publish(StringUtils.getString(TOTAL, Integer.toString(connection.getContentLength())));
			input = connection.getInputStream();

			fout = new FileOutputStream(file);
			byte buf[] = new byte[1024];
			// Update every second
			long lastTime = System.currentTimeMillis();
			long allBytes = 0;
			int len = input.read(buf);
			while (len > 0 && !cancelled) {
				fout.write(buf, 0, len);
				allBytes += len;
				if (System.currentTimeMillis() - lastTime > 1000) {
					publish(StringUtils.getString(CURRENT, Long.toString(allBytes)));
					lastTime = System.currentTimeMillis();
				}
				len = input.read(buf);
			}
		} catch (IOException e) {
			Logger.error(e);
		} finally {
			ClosingUtils.close(input);
			ClosingUtils.close(fout);
			// If has been cancelled then delete file
			if (cancelled && file.exists() && !file.delete()) {
				Logger.error(StringUtils.getString(file, " not deleted"));
			}
		}
		return null;
	}

	@Override
	protected void process(final List<String> chunks) {
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
