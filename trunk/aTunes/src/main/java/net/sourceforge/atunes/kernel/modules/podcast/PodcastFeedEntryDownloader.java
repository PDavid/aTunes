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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerCallback;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * The podcast feed entry downloader downloads podcast feed entries from the
 * internet to a local file.
 */
public class PodcastFeedEntryDownloader extends
		BackgroundWorker<Boolean, DownloadPodcastFeedEntryIntermediateResult> {

	private IPodcastFeedEntry podcastFeedEntry;
	/*
	 * Additional Bean properties
	 */
	private volatile long totalBytes;
	private volatile long byteProgress;
	private volatile int percentage;
	private volatile boolean failed;

	private ITable navigationTable;

	private PodcastFeedHandler podcastFeedHandler;

	private INetworkHandler networkHandler;

	private IDialogFactory dialogFactory;

	private IIconFactory rssMediumIcon;

	private ILookAndFeelManager lookAndFeelManager;

	private IProgressDialog dialog;

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param rssMediumIcon
	 */
	public void setRssMediumIcon(IIconFactory rssMediumIcon) {
		this.rssMediumIcon = rssMediumIcon;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param navigationTable
	 */
	public void setNavigationTable(ITable navigationTable) {
		this.navigationTable = navigationTable;
	}

	/**
	 * @param podcastFeedHandler
	 */
	public void setPodcastFeedHandler(PodcastFeedHandler podcastFeedHandler) {
		this.podcastFeedHandler = podcastFeedHandler;
	}

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * Downloads entry
	 *
	 * @param podcastFeedEntry
	 * @param callback
	 */
	public void download(IPodcastFeedEntry podcastFeedEntry,
			IBackgroundWorkerCallback<Boolean> callback) {
		this.podcastFeedEntry = podcastFeedEntry;
		execute(callback);
	}

	@Override
	protected void before() {
		dialog = this.dialogFactory.newDialog("transferDialog",
				IProgressDialog.class);
		dialog.setTitle(I18nUtils.getString("PODCAST_FEED_ENTRY_DOWNLOAD"));
		dialog.setIcon(this.rssMediumIcon.getIcon(this.lookAndFeelManager
				.getCurrentLookAndFeel().getPaintForSpecialControls()));
		dialog.addCancelButtonActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				podcastFeedHandler.cancelDownloading(podcastFeedEntry, dialog,
						PodcastFeedEntryDownloader.this);
			}
		});
		dialog.setInfoText(podcastFeedEntry.getTitle());
		dialog.showDialog();
	}

	@Override
	protected Boolean doInBackground() {
		Logger.info("Downloading PodcastEntry: ", podcastFeedEntry.getUrl());

		String podcastFeedEntryFileName = podcastFeedHandler.getDownloadPath(podcastFeedEntry);
		Logger.info("Downloading to: ", podcastFeedEntryFileName);
		File localFile = new File(podcastFeedEntryFileName);

    if (localFile.exists()) {
      Logger.info("Target file '{}' already exists. Download cancelled.", podcastFeedEntryFileName);
      podcastFeedEntry.setDownloaded(true);
			navigationTable.repaint();
      return true;
    }
    else {
      ReadableByteChannel readChannel = null;
      FileChannel writeChannel = null;
      InputStream in = null;
      try {
        writeChannel = new FileOutputStream(localFile).getChannel();
        URLConnection conn = networkHandler.getConnection(podcastFeedEntry.getUrl());
        in = new BufferedInputStream(conn.getInputStream());
        setTotalBytes(conn.getContentLength());

        readChannel = Channels.newChannel(in);

        int iterations = 0;
        long totalTransferred = 0;
        while (readChannel.isOpen()) {
          long transferred = writeChannel.transferFrom(readChannel, totalTransferred, 4 * 4096);
          totalTransferred += transferred;
          if (iterations % 50 == 0) {
            setByteProgress(totalTransferred);
          }
        }

        return !isCancelled();
      } catch (FileNotFoundException e) {
        Logger.info("file not found");
        setFailed(true);
        return false;
      } catch (IOException e) {
        Logger.info("Connection to ", podcastFeedEntry.getUrl(), " failed");
        setFailed(true);
        return false;
      } finally {
        ClosingUtils.close(readChannel);
        ClosingUtils.close(writeChannel);
        if (in != null) {  ClosingUtils.close(in); }
      }
    }
	}

	@Override
	protected void whileWorking(
			List<DownloadPodcastFeedEntryIntermediateResult> chunks) {
		for (DownloadPodcastFeedEntryIntermediateResult chunk : chunks) {
			if (chunk.isFailed()) {
				podcastFeedHandler.cancelDownloading(podcastFeedEntry, dialog,
						this);
			} else if (chunk.getPercentage() == 100) {
				dialog.hideDialog();
			} else {
				dialog.setCurrentProgress(chunk.getByteProgress());
				dialog.setProgressBarValue(chunk.getPercentage());
				dialog.setTotalProgress(chunk.getTotalBytes());
			}
		}
	}

	/**
	 * Sets the total bytes.
	 *
	 * @param totalBytes
	 *            the new total bytes
	 */

	private void setTotalBytes(long totalBytes) {
		this.totalBytes = totalBytes;
		publish();
	}

	/**
	 * Sets the byte progress.
	 *
	 * @param byteProgress
	 *            the new byte progress
	 */

	private void setByteProgress(long byteProgress) {
		this.byteProgress = byteProgress;
		// we want to update progress on byteProgress change
		this.percentage = (int) (((double) byteProgress / (double) totalBytes) * 100);
		publish();
	}

	/**
	 * Sets the failed.
	 *
	 * @param failed
	 *            the new failed
	 */

	private void setFailed(boolean failed) {
		this.failed = failed;
		publish();
	}

	private void publish() {
		publish(new DownloadPodcastFeedEntryIntermediateResult(this.percentage,
				this.byteProgress, this.totalBytes, this.failed));
	}

	/**
	 * Gets the total bytes.
	 *
	 * @return the total bytes
	 */
	public long getTotalBytes() {
		return totalBytes;
	}

	@Override
	protected void done(final Boolean result) {
		if (!isCancelled() && result) {
			Logger.info("Download of " + podcastFeedEntry.getUrl() + " finished.");
			podcastFeedEntry.setDownloaded(true);
			navigationTable.repaint();
		}
	}

	/**
	 * Gets the podcast feed entry.
	 *
	 * @return the podcast feed entry
	 */
	public IPodcastFeedEntry getPodcastFeedEntry() {
		return podcastFeedEntry;
	}
}
