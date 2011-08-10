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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.RssImageIcon;
import net.sourceforge.atunes.gui.views.dialogs.TransferProgressDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The handler for podcast feeds.
 */
public final class PodcastFeedHandler extends AbstractHandler {

	/**
	 * Initial delay until start podcast retriever 
	 */
	private static final int INITIAL_RETRIEVER_DELAY = 5000; // 5 seconds
	
    private final class DownloadPodcastFeedEntryPropertyChangeListener
			implements PropertyChangeListener {
		private final PodcastFeedEntry podcastFeedEntry;
		private final TransferProgressDialog d;
		private final PodcastFeedEntryDownloader downloadPodcastFeedEntry;

		private DownloadPodcastFeedEntryPropertyChangeListener(
				PodcastFeedEntry podcastFeedEntry, TransferProgressDialog d,
				PodcastFeedEntryDownloader downloadPodcastFeedEntry) {
			this.podcastFeedEntry = podcastFeedEntry;
			this.d = d;
			this.downloadPodcastFeedEntry = downloadPodcastFeedEntry;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
		    if (evt.getPropertyName().equals("progress")) {
		        d.setProgressBarValue((Integer) evt.getNewValue());
		        if ((Integer) evt.getNewValue() == 100) {
		            d.dispose();
		            synchronized (runningDownloads) {
		                runningDownloads.remove(downloadPodcastFeedEntry);
		            }
		        }
		    } else if (evt.getPropertyName().equals("byteProgress")) {
		        d.setCurrentProgress((Long) evt.getNewValue());
		    } else if (evt.getPropertyName().equals("totalBytes")) {
		        d.setTotalProgress((Long) evt.getNewValue());
		    } else if (evt.getPropertyName().equals("failed")) {
		        cancelDownloading(podcastFeedEntry, d, downloadPodcastFeedEntry);
		    }
		}
	}

	private static final class DeleteDownloadedPodcastFeedEntryWorker extends SwingWorker<Boolean, Void> {
        private final File f;
        private final PodcastFeedEntry podcastFeedEntry;

        private DeleteDownloadedPodcastFeedEntryWorker(File f, PodcastFeedEntry podcastFeedEntry) {
            this.f = f;
            this.podcastFeedEntry = podcastFeedEntry;
        }

        @Override
        protected Boolean doInBackground() {
            return f.delete();
        }

        @Override
        protected void done() {
            try {
                if (get()) {
                    podcastFeedEntry.setDownloaded(false);
                    GuiHandler.getInstance().getNavigationTablePanel().getNavigationTable().repaint();
                }
            } catch (InterruptedException e) {
                Logger.error(e);
            } catch (ExecutionException e) {
                Logger.error(e);
            }
        }
    }

    private static PodcastFeedHandler instance = new PodcastFeedHandler();

    public static final long DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL = 180000;

    private List<PodcastFeed> podcastFeeds;

    /**
     * Flag indicating if podcast list needs to be written to disk
     */
    private boolean podcastFeedsDirty;

    /**
     * Podcast Feed Entry downloading
     */
    private ExecutorService podcastFeedEntryDownloaderExecutorService;
    
    /** The running downloads. */
    private volatile List<PodcastFeedEntryDownloader> runningDownloads = Collections.synchronizedList(new ArrayList<PodcastFeedEntryDownloader>());
    
    /**
     * Podcast Feed Entry download checker
     */
    private ScheduledExecutorService podcastFeedEntryDownloadCheckerExecutorService;
    
    private ScheduledExecutorService podcastFeedEntryRetrieverExecutorService = Executors.newScheduledThreadPool(1);
    
    private ScheduledFuture<?> scheduledPodcastFeedEntryRetrieverFuture;

    @Override
    protected void initHandler() {
    }

    @Override
    public void applicationStarted(List<AudioObject> playList) {
    }

    @Override
    public void allHandlersInitialized() {
        startPodcastFeedEntryDownloadChecker();
        startPodcastFeedEntryRetriever();
    }
    
    /**
     * Gets the single instance of PodcastFeedHandler.
     * 
     * @return single instance of PodcastFeedHandler
     */
    public static PodcastFeedHandler getInstance() {
        return instance;
    }

    /**
     * Adds a Podcast Feed.
     */
    public void addPodcastFeed() {
        PodcastFeed podcastFeed = GuiHandler.getInstance().showAddPodcastFeedDialog();
        if (podcastFeed != null) {
            addPodcastFeed(podcastFeed);
            NavigationHandler.getInstance().refreshView(PodcastNavigationView.class);
            retrievePodcastFeedEntries();
        }
    }

    /**
     * Adds a Podcast Feed.
     * 
     * @param podcastFeed
     *            A Podcast Feed that should be added
     */
    private void addPodcastFeed(PodcastFeed podcastFeed) {
        Logger.info("Adding podcast feed");
        // Note: Do not use Collection.sort(...);
        boolean added = false;
        Comparator<PodcastFeed> comparator = PodcastFeed.getComparator();
        for (int i = 0; i < getPodcastFeeds().size(); i++) {
            if (comparator.compare(podcastFeed, getPodcastFeeds().get(i)) < 0) {
                getPodcastFeeds().add(i, podcastFeed);
                added = true;
                break;
            }
        }
        if (!added) {
            getPodcastFeeds().add(podcastFeed);
        }
        podcastFeedsDirty = true;
    }

    /**
     * Finish.
     */
    public void applicationFinish() {
    	getPodcastFeedEntryRetrieverExecutorService().shutdownNow();
        synchronized (runningDownloads) {
            for (int i = 0; i < runningDownloads.size(); i++) {
                PodcastFeedEntryDownloader podcastFeedEntryDownloader = runningDownloads.get(i);
                podcastFeedEntryDownloader.cancel(true);
                new File(getDownloadPath(podcastFeedEntryDownloader.getPodcastFeedEntry())).deleteOnExit();
            }
        }
        getPodcastFeedEntryDownloadCheckerExecutorService().shutdownNow();
        if (podcastFeedsDirty) {
            ApplicationStateHandler.getInstance().persistPodcastFeedCache(getPodcastFeeds());
        } else {
            Logger.info("Podcast list is clean");
        }

    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new Runnable() {
            @Override
            public void run() {
                podcastFeeds = ApplicationStateHandler.getInstance().retrievePodcastFeedCache();
            }
        };
    }

    /**
     * Returns a list with all Podcast Feeds.
     * 
     * @return The podcast feeds
     */
    public List<PodcastFeed> getPodcastFeeds() {
        return podcastFeeds;
    }

    /**
     * Returns a list with all Podcast Feed Entries.
     * 
     * @return A list with all Podcast Feed Entries
     */
    public List<PodcastFeedEntry> getPodcastFeedEntries() {
        List<PodcastFeedEntry> podcastFeedEntries = new ArrayList<PodcastFeedEntry>();
        for (PodcastFeed podcastFeed : getPodcastFeeds()) {
            podcastFeedEntries.addAll(podcastFeed.getPodcastFeedEntries());
        }
        return podcastFeedEntries;
    }

    /**
     * Removes a Podcast Feed.
     * 
     * @param podcastFeed
     *            A Podcast Feed that should be removed
     */
    public void removePodcastFeed(PodcastFeed podcastFeed) {
        Logger.info("Removing podcast feed");
        getPodcastFeeds().remove(podcastFeed);
        podcastFeedsDirty = true;
        NavigationHandler.getInstance().refreshView(PodcastNavigationView.class);
    }

    /**
     * Starts the Podcast Feed Entry Retriever.
     */
    public void startPodcastFeedEntryRetriever() {
        // When upgrading from a previous version, retrievel interval can be 0
        long retrieval = ApplicationState.getInstance().getPodcastFeedEntriesRetrievalInterval();
        long retrievalInterval = retrieval > 0 ? retrieval : DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL;

        schedulePodcastFeedEntryRetriever(retrievalInterval);
    }

    /**
     * Start podcast feed entry download checker.
     */
    public void startPodcastFeedEntryDownloadChecker() {
        getPodcastFeedEntryDownloadCheckerExecutorService().scheduleWithFixedDelay(new PodcastFeedEntryDownloadChecker(), 0, 10000, TimeUnit.MILLISECONDS);
    }

    /**
     * Sets the Podcast Feed Entry retrieval interval.
     * 
     * @param newRetrievalInterval
     *            The Podcast Feed Entry retrieval interval
     */
    private void setPodcastFeedEntryRetrievalInterval(long newRetrievalInterval) {
        if (newRetrievalInterval < 0) {
            throw new IllegalArgumentException("sleeping time must not be smaller than 0");
        }
        schedulePodcastFeedEntryRetriever(newRetrievalInterval);
    }

    /**
     * Schedules the PodcastFeedEntryRetriever with the given interval.
     * 
     * @param newRetrievalInterval
     *            The Podcast Feed Entry retrieval interval
     */
    private void schedulePodcastFeedEntryRetriever(long newRetrievalInterval) {
        if (scheduledPodcastFeedEntryRetrieverFuture != null) {
            scheduledPodcastFeedEntryRetrieverFuture.cancel(true);
        }
        scheduledPodcastFeedEntryRetrieverFuture = getPodcastFeedEntryRetrieverExecutorService().scheduleWithFixedDelay(new PodcastFeedEntryRetriever(getPodcastFeeds()), INITIAL_RETRIEVER_DELAY,
                newRetrievalInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * Retrieves Podcast Feed Entries and refreshes view asynchronously.
     * 
     * @see net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntryRetriever#retrievePodcastFeedEntries()
     */
    public void retrievePodcastFeedEntries() {
    	getPodcastFeedEntryRetrieverExecutorService().execute(new PodcastFeedEntryRetriever(getPodcastFeeds()));
    }

    /**
     * Download Podcast Feed Entry.
     * 
     * @param podcastFeedEntry
     *            the podcast feed entry
     */
    public void downloadPodcastFeedEntry(final PodcastFeedEntry podcastFeedEntry) {
        if (isDownloading(podcastFeedEntry)) {
            return;
        }
        final TransferProgressDialog d = GuiHandler.getInstance().getNewTransferProgressDialog(I18nUtils.getString("PODCAST_FEED_ENTRY_DOWNLOAD"), null);
        d.setIcon(RssImageIcon.getIcon());
        final PodcastFeedEntryDownloader downloadPodcastFeedEntry = new PodcastFeedEntryDownloader(podcastFeedEntry);
        synchronized (runningDownloads) {
            runningDownloads.add(downloadPodcastFeedEntry);
        }
        downloadPodcastFeedEntry.addPropertyChangeListener(new DownloadPodcastFeedEntryPropertyChangeListener(podcastFeedEntry, d,
				downloadPodcastFeedEntry));
        d.addCancelButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelDownloading(podcastFeedEntry, d, downloadPodcastFeedEntry);
            }
        });
        d.setInfoText(podcastFeedEntry.getTitle());
        getPodcastFeedEntryDownloaderExecutorService().execute(downloadPodcastFeedEntry);
        d.setVisible(true);
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
    void cancelDownloading(final PodcastFeedEntry podcastFeedEntry, final TransferProgressDialog d, final PodcastFeedEntryDownloader downloadPodcastFeedEntry) {
        try {
            downloadPodcastFeedEntry.cancel(false);
        } catch (CancellationException ex) {
            // Nothing to do
        }
        d.dispose();

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
                synchronized (runningDownloads) {
                    runningDownloads.remove(downloadPodcastFeedEntry);
                }
                Logger.info("podcast entry download cancelled: " + podcastFeedEntry.getTitle());
            }
        };
        new Thread(r).start();
    }

    /**
     * Gets the download path.
     * 
     * @param podcastFeedEntry
     *            the podcast feed entry
     * 
     * @return the download path
     */
    public String getDownloadPath(PodcastFeedEntry podcastFeedEntry) {
        String path = ApplicationState.getInstance().getPodcastFeedEntryDownloadPath();
        if (path == null || path.isEmpty()) {
            path = StringUtils.getString(OsManager.getUserConfigFolder(Kernel.isDebug()), "/", Constants.DEFAULT_PODCAST_FEED_ENTRY_DOWNLOAD_DIR);
        }
        File podcastFeedsDownloadFolder = new File(path);

        // Check if podcast folder exists
        if (!podcastFeedsDownloadFolder.exists()) {
            boolean check = podcastFeedsDownloadFolder.mkdir();
            if (!check) {
                Logger.error("Problem Creating file!");
                return "";
            }
        }

        if (podcastFeedEntry.getPodcastFeed().getName() == null || podcastFeedEntry.getPodcastFeed().getName().trim().isEmpty()) {
            return "";
        }

        // Check if subfolder exists and otherwise create
        File podcastFeedDownloadFolder = new File(podcastFeedsDownloadFolder, FileNameUtils
                .getValidFileName(String.valueOf(podcastFeedEntry.getPodcastFeed().getName().hashCode())));
        if (!podcastFeedDownloadFolder.exists()) {
            boolean check = podcastFeedDownloadFolder.mkdir();
            if (!check) {
                Logger.error("Problem Creating file!");
                return "";
            }
        }

        try {
            String url = new URI(podcastFeedEntry.getUrl()).getPath();
            String[] elements = url.split("/");
            if (elements.length > 0) {
                String filename = elements[elements.length - 1];
                int index = filename.lastIndexOf('.');
                if (index != -1) {
                    filename = filename.hashCode() + "." + filename.substring(index, filename.length());
                } else {
                    filename = String.valueOf(filename.hashCode());
                }
                return podcastFeedDownloadFolder.toString() + "/" + FileNameUtils.getValidFileName(filename);
            }
        } catch (URISyntaxException e) {
            Logger.error(e);
        }
        throw new IllegalArgumentException();
    }

    /**
     * Checks if is downloaded.
     * 
     * @param podcastFeedEntry
     *            the podcast feed entry
     * 
     * @return true, if is downloaded
     */
    public boolean isDownloaded(PodcastFeedEntry podcastFeedEntry) {
        File f = new File(getDownloadPath(podcastFeedEntry));
        return f.exists();
    }

    /**
     * Delete downloaded podcast feed entry.
     * 
     * @param podcastFeedEntry
     *            the podcast feed entry
     */
    public void deleteDownloadedPodcastFeedEntry(final PodcastFeedEntry podcastFeedEntry) {
        File f = new File(getDownloadPath(podcastFeedEntry));
        new DeleteDownloadedPodcastFeedEntryWorker(f, podcastFeedEntry).execute();
    }

    /**
     * Checks if is downloading.
     * 
     * @param podcastFeedEntry
     *            the podcast feed entry
     * 
     * @return true, if is downloading
     */
    public boolean isDownloading(PodcastFeedEntry podcastFeedEntry) {
        synchronized (runningDownloads) {
            for (int i = 0; i < runningDownloads.size(); i++) {
                if (runningDownloads.get(i).getPodcastFeedEntry().equals(podcastFeedEntry)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        setPodcastFeedEntryRetrievalInterval(newState.getPodcastFeedEntriesRetrievalInterval());
    }
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(AudioObject audioObject) {}

	/**
	 * @return the podcastFeedEntryDownloaderExecutorService
	 */
	private ExecutorService getPodcastFeedEntryDownloaderExecutorService() {
		if (podcastFeedEntryDownloaderExecutorService == null) {
			podcastFeedEntryDownloaderExecutorService = Executors.newCachedThreadPool();
		}
		return podcastFeedEntryDownloaderExecutorService;
	}

	/**
	 * @return the podcastFeedEntryDownloadCheckerExecutorService
	 */
	private ScheduledExecutorService getPodcastFeedEntryDownloadCheckerExecutorService() {
		if (podcastFeedEntryDownloadCheckerExecutorService == null) {
			podcastFeedEntryDownloadCheckerExecutorService = Executors.newScheduledThreadPool(1);
		}
		return podcastFeedEntryDownloadCheckerExecutorService;
	}

	/**
	 * @return the podcastFeedEntryRetrieverExecutorService
	 */
	private ScheduledExecutorService getPodcastFeedEntryRetrieverExecutorService() {
		if (podcastFeedEntryRetrieverExecutorService == null) {
			podcastFeedEntryRetrieverExecutorService = Executors.newScheduledThreadPool(1);
		}
		return podcastFeedEntryRetrieverExecutorService;
	}

}
