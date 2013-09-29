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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ScheduledFuture;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAddPodcastFeedDialog;
import net.sourceforge.atunes.model.IBackgroundWorkerCallback;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IStatePodcast;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import static net.sourceforge.atunes.utils.FileNameUtils.getValidFileName;
import static net.sourceforge.atunes.utils.FileNameUtils.getValidFolderName;

/**
 * The handler for podcast feeds.
 */
public final class PodcastFeedHandler extends AbstractHandler implements
		IPodcastFeedHandler {

	/**
	 * Time in seconds between two podcast retrieval checks
	 */
	public static final long DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL = 180;

	private List<IPodcastFeed> podcastFeeds = new ArrayList<IPodcastFeed>();

	/** The running downloads. */
	private volatile List<PodcastFeedEntryDownloader> runningDownloads = Collections
			.synchronizedList(new ArrayList<PodcastFeedEntryDownloader>());

	private ScheduledFuture<?> scheduledPodcastFeedEntryRetrieverFuture;

	private ScheduledFuture<?> scheduledPodcastFeedEntryDownloadCheckerFuture;

	private INavigationHandler navigationHandler;

	private IStateService stateService;

	private ITaskService taskService;

	private INavigationView podcastNavigationView;

	private IStatePodcast statePodcast;

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param statePodcast
	 */
	public void setStatePodcast(final IStatePodcast statePodcast) {
		this.statePodcast = statePodcast;
	}

	/**
	 * @param podcastNavigationView
	 */
	public void setPodcastNavigationView(
			final INavigationView podcastNavigationView) {
		this.podcastNavigationView = podcastNavigationView;
	}

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param stateService
	 */
	public void setStateService(final IStateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	@Override
	public void deferredInitialization() {
		startPodcastFeedEntryDownloadChecker();
		startPodcastFeedEntryRetriever();
	}

	@Override
	public void addPodcastFeed() {
		IAddPodcastFeedDialog dialog = this.dialogFactory
				.newDialog(IAddPodcastFeedDialog.class);
		dialog.showDialog();
		IPodcastFeed podcastFeed = dialog.getPodcastFeed();
		if (podcastFeed != null) {
			addPodcastFeed(podcastFeed);
			this.navigationHandler.refreshView(this.podcastNavigationView);
			retrievePodcastFeedEntries();
			startPodcastFeedEntryDownloadChecker();
			startPodcastFeedEntryRetriever();
		}
	}

	/**
	 * Adds a Podcast Feed.
	 *
	 * @param podcastFeed
	 *            A Podcast Feed that should be added
	 */
	private void addPodcastFeed(final IPodcastFeed podcastFeed) {
		Logger.info("Adding podcast feed");
		// Note: Do not use Collection.sort(...);
		boolean added = false;
		for (int i = 0; i < getPodcastFeeds().size(); i++) {
			if (new PodcastFeedComparator().compare(podcastFeed,
					getPodcastFeeds().get(i)) < 0) {
				getPodcastFeeds().add(i, podcastFeed);
				added = true;
				break;
			}
		}
		if (!added) {
			getPodcastFeeds().add(podcastFeed);
		}
		this.stateService.persistPodcastFeedCache(getPodcastFeeds());
	}

	/**
	 * Finish.
	 */
	@Override
	public void applicationFinish() {
		synchronized (this.runningDownloads) {
			for (int i = 0; i < this.runningDownloads.size(); i++) {
				PodcastFeedEntryDownloader podcastFeedEntryDownloader = this.runningDownloads
						.get(i);
				podcastFeedEntryDownloader.cancel(true);
				new File(
						getDownloadPath(podcastFeedEntryDownloader
								.getPodcastFeedEntry())).deleteOnExit();
			}
		}
	}

	@Override
	public List<IPodcastFeed> getPodcastFeeds() {
		return this.podcastFeeds;
	}

	@Override
	public List<IPodcastFeedEntry> getPodcastFeedEntries() {
		List<IPodcastFeedEntry> podcastFeedEntries = new ArrayList<IPodcastFeedEntry>();
		for (IPodcastFeed podcastFeed : getPodcastFeeds()) {
			podcastFeedEntries.addAll(podcastFeed.getPodcastFeedEntries());
		}
		return podcastFeedEntries;
	}

	@Override
	public void removePodcastFeed(final IPodcastFeed podcastFeed) {
		Logger.info("Removing podcast feed");
		getPodcastFeeds().remove(podcastFeed);
		this.navigationHandler.refreshView(this.podcastNavigationView);
		if (CollectionUtils.isEmpty(getPodcastFeeds())) {
			stopPodcastFeedEntryDownloadChecker();
			stopPodcastFeedEntryRetriever();
		}
		this.stateService.persistPodcastFeedCache(getPodcastFeeds());
	}

	private void startPodcastFeedEntryRetriever() {
		// When upgrading from a previous version, retrievel interval can be 0
		long retrieval = this.statePodcast
				.getPodcastFeedEntriesRetrievalInterval();
		long retrievalInterval = retrieval > 0 ? retrieval
				: DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL;

		schedulePodcastFeedEntryRetriever(retrievalInterval);
	}

	private void startPodcastFeedEntryDownloadChecker() {
		// Start only if podcast feeds created
		if (!CollectionUtils.isEmpty(getPodcastFeeds())) {
			this.scheduledPodcastFeedEntryDownloadCheckerFuture = this.taskService
					.submitPeriodically(
							"PodcastFeedEntryDownloadChecker",
							30,
							30,
							new PodcastFeedEntryDownloadChecker(getBean(
									"navigationTable", ITable.class), this));
		} else {
			Logger.debug("Not scheduling PodcastFeedEntryDownloadChecker");
		}
	}

	private void stopPodcastFeedEntryRetriever() {
		if (this.scheduledPodcastFeedEntryRetrieverFuture != null) {
			this.scheduledPodcastFeedEntryRetrieverFuture.cancel(true);
			Logger.debug("Stopped PodcastFeedEntryRetrieverChecker");
		}
	}

	private void stopPodcastFeedEntryDownloadChecker() {
		if (this.scheduledPodcastFeedEntryDownloadCheckerFuture != null) {
			this.scheduledPodcastFeedEntryDownloadCheckerFuture.cancel(true);
			Logger.debug("Stopped PodcastFeedEntryDownloadChecker");
		}
	}

	/**
	 * Sets the Podcast Feed Entry retrieval interval.
	 *
	 * @param newRetrievalInterval
	 *            The Podcast Feed Entry retrieval interval
	 */
	private void setPodcastFeedEntryRetrievalInterval(
			final long newRetrievalInterval) {
		if (newRetrievalInterval < 0) {
			throw new IllegalArgumentException(
					"sleeping time must not be smaller than 0");
		}
		schedulePodcastFeedEntryRetriever(newRetrievalInterval);
	}

	/**
	 * Schedules the PodcastFeedEntryRetriever with the given interval.
	 *
	 * @param newRetrievalInterval
	 *            The Podcast Feed Entry retrieval interval
	 */
	private void schedulePodcastFeedEntryRetriever(
			final long newRetrievalInterval) {
		stopPodcastFeedEntryRetriever();

		// Start only if podcast feeds created
		if (!CollectionUtils.isEmpty(getPodcastFeeds())) {
			PodcastFeedEntryRetriever retriever = getBean(PodcastFeedEntryRetriever.class);
			retriever.setPodcastFeeds(getPodcastFeeds());
			this.scheduledPodcastFeedEntryRetrieverFuture = this.taskService
					.submitPeriodically(
							"Periodically Retrieve Podcast Feed Entries",
							newRetrievalInterval, newRetrievalInterval,
							retriever);
		} else {
			Logger.debug("Not scheduling PodcastFeedEntryRetriever");
		}
	}

	@Override
	public void retrievePodcastFeedEntries() {
		PodcastFeedEntryRetriever retriever = getBean(PodcastFeedEntryRetriever.class);
		retriever.setPodcastFeeds(getPodcastFeeds());
		this.taskService.submitNow("Retrieve Podcast Feed Entries", retriever);
	}

	@Override
	public void downloadPodcastFeedEntry(
			final IPodcastFeedEntry podcastFeedEntry) {
		if (isDownloading(podcastFeedEntry)) {
			return;
		}
		final PodcastFeedEntryDownloader downloadPodcastFeedEntry = getBean(PodcastFeedEntryDownloader.class);
		synchronized (this.runningDownloads) {
			this.runningDownloads.add(downloadPodcastFeedEntry);
		}
		downloadPodcastFeedEntry.download(podcastFeedEntry,
				new IBackgroundWorkerCallback<Boolean>() {

					@Override
					public void workerFinished(Boolean result) {
						synchronized (runningDownloads) {
							runningDownloads.remove(downloadPodcastFeedEntry);
						}
					}
				});
	}

	/**
	 * Cancel downloading.
	 *
	 * @param podcastFeedEntry
	 *            the podcast feed entry
	 * @param d
	 *            the d
	 * @param downloadPodcastFeedEntry
	 *            the download podcast feed entry
	 */
	void cancelDownloading(final IPodcastFeedEntry podcastFeedEntry,
			final IProgressDialog d,
			final PodcastFeedEntryDownloader downloadPodcastFeedEntry) {
		try {
			downloadPodcastFeedEntry.cancel(false);
		} catch (CancellationException ex) {
			// Nothing to do
		}
		d.hideDialog();

		final File f = new File(getDownloadPath(podcastFeedEntry));
		Runnable r = new Runnable() {
			@Override
			public void run() {
				while (f.exists() && !Thread.currentThread().isInterrupted()) {
					if (!f.delete()) {
						Logger.error(StringUtils.getString(f, " not deleted"));
					}
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// Nothing to do
				}
				synchronized (PodcastFeedHandler.this.runningDownloads) {
					PodcastFeedHandler.this.runningDownloads
							.remove(downloadPodcastFeedEntry);
				}
				Logger.info("podcast entry download cancelled: "
						+ podcastFeedEntry.getTitle());
			}
		};
		new Thread(r).start();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#
	 * getDownloadPath(net.sourceforge.atunes.model.IPodcastFeedEntry)
	 */
	@Override
	public String getDownloadPath(final IPodcastFeedEntry podcastFeedEntry) {
		File podcastFeedsDownloadFolder = getPodcastFeedsDownloadFolder();

		if (!createFolder(podcastFeedsDownloadFolder)) {
			return "";
		}

		if (podcastFeedEntry.getPodcastFeed().getName() == null
				|| podcastFeedEntry.getPodcastFeed().getName().trim().isEmpty()) {
			return "";
		}

		// Check if sub-folder exists and otherwise create
		File podcastFeedDownloadFolder = getPodcastFeedFolder(podcastFeedsDownloadFolder,podcastFeedEntry);

		if (!createFolder(podcastFeedDownloadFolder)) {
			return "";
		}

		try {
			String url = new URI(podcastFeedEntry.getUrl()).getPath();
			String[] elements = url.split("/");
			if (elements.length > 0) {
				String filename = elements[elements.length - 1];
				int index = filename.lastIndexOf('.');
				if (index != -1) {
          // no way, why to replace a readable with hashcode ?
          // StringUtils.getString(filename.hashCode(), ".", filename.substring(index, filename.length()));
				} else {
          // TODO: consider replacing hashcode with a date
					filename = String.valueOf(filename.hashCode());
				}
				return StringUtils.getString(podcastFeedDownloadFolder
						.toString(), "/", getValidFileName(
          filename, getOsManager()));
			}
		} catch (URISyntaxException e) {
			Logger.error(e);
		}
		throw new IllegalArgumentException();
	}

  /** Provides name of a folder for a single podcast feed.
   *
   * @param podcastFeedEntry podcast feed entry to get the directory name from
   *
   * @return name of the folder
   */
  private File getPodcastFeedFolder (File podcastFeedsDownloadFolder, IPodcastFeedEntry podcastFeedEntry)
  {
     File folder;

     String userSetFolderName = podcastFeedEntry.getPodcastFeed().getFolderName();
     if (userSetFolderName != null && userSetFolderName.trim().length() != 0) {
       // the user provided a name of a custom sub-folder
       // first try if the folder already exists - keep folder name as is
       folder = new File(podcastFeedsDownloadFolder, userSetFolderName);
       if (!folder.exists()) {
         // we'll be creating a new folder, sanitize the folder's name
         folder = new File(podcastFeedsDownloadFolder,
                           getValidFileName(userSetFolderName, getOsManager()));
       }
     }
     else {
       // use the old logic - using value of podcast's name hashcode as the folder name
       String hashCodeBasedFolderName =
         String.valueOf(podcastFeedEntry.getPodcastFeed().getName().hashCode());

       folder = new File(podcastFeedsDownloadFolder,
                         getValidFileName(hashCodeBasedFolderName, getOsManager()));
     }

     return folder;
  }

  /**
   * @param podcastFeedsDownloadFolder
	 */
	private boolean createFolder(final File podcastFeedsDownloadFolder) {
		// Check if podcast folder exists
		if (!podcastFeedsDownloadFolder.exists()) {
			boolean folderCreated = podcastFeedsDownloadFolder.mkdir();
			if (!folderCreated) {
				Logger.error("Problem Creating file!");
				return false;
			}
		}
		return true;
	}

	/**
	 * @return
	 */
	private File getPodcastFeedsDownloadFolder() {
		String path = this.statePodcast.getPodcastFeedEntryDownloadPath();
		if (path == null || path.isEmpty()) {
			path = StringUtils.getString(getOsManager().getUserConfigFolder(),
					"/", Constants.DEFAULT_PODCAST_FEED_ENTRY_DOWNLOAD_DIR);
		}
		return new File(path);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#
	 * isDownloaded(net.sourceforge.atunes.model.IPodcastFeedEntry)
	 */
	@Override
	public boolean isDownloaded(final IPodcastFeedEntry podcastFeedEntry) {
		File f = new File(getDownloadPath(podcastFeedEntry));
		return f.exists();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#
	 * deleteDownloadedPodcastFeedEntry
	 * (net.sourceforge.atunes.model.IPodcastFeedEntry)
	 */
	@Override
	public void deleteDownloadedPodcastFeedEntry(
			final IPodcastFeedEntry podcastFeedEntry) {
		File f = new File(getDownloadPath(podcastFeedEntry));
		getBean(DeleteDownloadedPodcastFeedBackgroundWorker.class).delete(f,
				podcastFeedEntry);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#
	 * isDownloading(net.sourceforge.atunes.model.IPodcastFeedEntry)
	 */
	@Override
	public boolean isDownloading(final IPodcastFeedEntry podcastFeedEntry) {
		synchronized (this.runningDownloads) {
			for (int i = 0; i < this.runningDownloads.size(); i++) {
				if (this.runningDownloads.get(i).getPodcastFeedEntry()
						.equals(podcastFeedEntry)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void applicationStateChanged() {
		setPodcastFeedEntryRetrievalInterval(this.statePodcast
				.getPodcastFeedEntriesRetrievalInterval());
	}

	/**
	 * @param podcastFeeds
	 */
	void setPodcastFeeds(final List<IPodcastFeed> podcastFeeds) {
		this.podcastFeeds = podcastFeeds;
	}
}
