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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

import net.sourceforge.atunes.ApplicationArguments;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.model.IAddPodcastFeedDialog;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The handler for podcast feeds.
 */
public final class PodcastFeedHandler extends AbstractHandler implements IPodcastFeedHandler {

    private final Comparator<IPodcastFeed> COMPARATOR = new PodcastFeedComparator();
	
    public static final long DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL = 180;

    private List<IPodcastFeed> podcastFeeds;

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
    
    private ScheduledFuture<?> scheduledPodcastFeedEntryRetrieverFuture;
    
    private CachedIconFactory rssMediumIcon;
    
    private INetworkHandler networkHandler;
    
    private INavigationHandler navigationHandler;
    
    private IStateHandler stateHandler;
    
    private ITaskService taskService;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private ApplicationArguments applicationArguments;
    
    /**
     * @param applicationArguments
     */
    public void setApplicationArguments(ApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * @param taskService
     */
    public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}
    
    public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @param networkHandler
     */
    public void setNetworkHandler(INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}
    
    /**
     * @param rssMediumIcon
     */
    public void setRssMediumIcon(CachedIconFactory rssMediumIcon) {
		this.rssMediumIcon = rssMediumIcon;
	}

    @Override
    public void allHandlersInitialized() {
        startPodcastFeedEntryDownloadChecker();
        startPodcastFeedEntryRetriever();
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#addPodcastFeed()
	 */
    @Override
	public void addPodcastFeed() {
    	IAddPodcastFeedDialog dialog = getBean(IAddPodcastFeedDialog.class);
    	dialog.showDialog();    	
        IPodcastFeed podcastFeed = dialog.getPodcastFeed(); 
        if (podcastFeed != null) {
            addPodcastFeed(podcastFeed);
            navigationHandler.refreshView(PodcastNavigationView.class);
            retrievePodcastFeedEntries();
        }
    }

    /**
     * Adds a Podcast Feed.
     * 
     * @param podcastFeed
     *            A Podcast Feed that should be added
     */
    private void addPodcastFeed(IPodcastFeed podcastFeed) {
        Logger.info("Adding podcast feed");
        // Note: Do not use Collection.sort(...);
        boolean added = false;
        for (int i = 0; i < getPodcastFeeds().size(); i++) {
            if (COMPARATOR.compare(podcastFeed, getPodcastFeeds().get(i)) < 0) {
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
        synchronized (runningDownloads) {
            for (int i = 0; i < runningDownloads.size(); i++) {
                PodcastFeedEntryDownloader podcastFeedEntryDownloader = runningDownloads.get(i);
                podcastFeedEntryDownloader.cancel(true);
                new File(getDownloadPath(podcastFeedEntryDownloader.getPodcastFeedEntry())).deleteOnExit();
            }
        }
        if (podcastFeedsDirty) {
        	stateHandler.persistPodcastFeedCache(getPodcastFeeds());
        } else {
            Logger.info("Podcast list is clean");
        }

    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new Runnable() {
            @Override
            public void run() {
                podcastFeeds = stateHandler.retrievePodcastFeedCache();
                if (podcastFeeds == null) {
                	/*
                     * java.util.concurrent.CopyOnWriteArrayList instead of e.g.
                     * java.util.ArrayList to avoid ConcurrentModificationException
                     */
                	podcastFeeds = new CopyOnWriteArrayList<IPodcastFeed>();
                }
            }
        };
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#getPodcastFeeds()
	 */
    @Override
	public List<IPodcastFeed> getPodcastFeeds() {
        return podcastFeeds;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#getPodcastFeedEntries()
	 */
    @Override
	public List<IPodcastFeedEntry> getPodcastFeedEntries() {
        List<IPodcastFeedEntry> podcastFeedEntries = new ArrayList<IPodcastFeedEntry>();
        for (IPodcastFeed podcastFeed : getPodcastFeeds()) {
            podcastFeedEntries.addAll(podcastFeed.getPodcastFeedEntries());
        }
        return podcastFeedEntries;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#removePodcastFeed(net.sourceforge.atunes.model.IPodcastFeed)
	 */
    @Override
	public void removePodcastFeed(IPodcastFeed podcastFeed) {
        Logger.info("Removing podcast feed");
        getPodcastFeeds().remove(podcastFeed);
        podcastFeedsDirty = true;
        navigationHandler.refreshView(PodcastNavigationView.class);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#startPodcastFeedEntryRetriever()
	 */
    @Override
	public void startPodcastFeedEntryRetriever() {
        // When upgrading from a previous version, retrievel interval can be 0
        long retrieval = getState().getPodcastFeedEntriesRetrievalInterval();
        long retrievalInterval = retrieval > 0 ? retrieval : DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL;

        schedulePodcastFeedEntryRetriever(retrievalInterval);
    }

    @Override
	public void startPodcastFeedEntryDownloadChecker() {
    	taskService.submitPeriodically("PodcastFeedEntryDownloadChecker", 30, 30, new PodcastFeedEntryDownloadChecker((ITable)getBean("navigationTable"), this));
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
        scheduledPodcastFeedEntryRetrieverFuture = taskService.submitPeriodically("Periodically Retrieve Podcast Feed Entries", newRetrievalInterval, newRetrievalInterval, new PodcastFeedEntryRetriever(getPodcastFeeds(), getState(), getFrame(), navigationHandler, networkHandler));
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#retrievePodcastFeedEntries()
	 */
    @Override
	public void retrievePodcastFeedEntries() {
    	taskService.submitNow("Retrieve Podcast Feed Entries", new PodcastFeedEntryRetriever(getPodcastFeeds(), getState(), getFrame(), navigationHandler, networkHandler));
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#downloadPodcastFeedEntry(net.sourceforge.atunes.model.IPodcastFeedEntry)
	 */
    @Override
	public void downloadPodcastFeedEntry(final IPodcastFeedEntry podcastFeedEntry) {
        if (isDownloading(podcastFeedEntry)) {
            return;
        }
        final IProgressDialog d = (IProgressDialog) getBean("transferDialog");
        d.setTitle(I18nUtils.getString("PODCAST_FEED_ENTRY_DOWNLOAD"));
        d.setIcon(rssMediumIcon.getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls()));
        final PodcastFeedEntryDownloader downloadPodcastFeedEntry = new PodcastFeedEntryDownloader(podcastFeedEntry, (ITable)getBean("navigationTable"), this, networkHandler);
        synchronized (runningDownloads) {
            runningDownloads.add(downloadPodcastFeedEntry);
        }
        downloadPodcastFeedEntry.addPropertyChangeListener(new DownloadPodcastFeedEntryPropertyChangeListener(this, podcastFeedEntry, d, downloadPodcastFeedEntry, runningDownloads));
        d.addCancelButtonActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelDownloading(podcastFeedEntry, d, downloadPodcastFeedEntry);
            }
        });
        d.setInfoText(podcastFeedEntry.getTitle());
        getPodcastFeedEntryDownloaderExecutorService().execute(downloadPodcastFeedEntry);
        d.showDialog();
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
    void cancelDownloading(final IPodcastFeedEntry podcastFeedEntry, final IProgressDialog d, final PodcastFeedEntryDownloader downloadPodcastFeedEntry) {
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
                synchronized (runningDownloads) {
                    runningDownloads.remove(downloadPodcastFeedEntry);
                }
                Logger.info("podcast entry download cancelled: " + podcastFeedEntry.getTitle());
            }
        };
        new Thread(r).start();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#getDownloadPath(net.sourceforge.atunes.model.IPodcastFeedEntry)
	 */
    @Override
	public String getDownloadPath(IPodcastFeedEntry podcastFeedEntry) {
        String path = getState().getPodcastFeedEntryDownloadPath();
        if (path == null || path.isEmpty()) {
            path = StringUtils.getString(getOsManager().getUserConfigFolder(applicationArguments.isDebug()), "/", Constants.DEFAULT_PODCAST_FEED_ENTRY_DOWNLOAD_DIR);
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
                .getValidFileName(String.valueOf(podcastFeedEntry.getPodcastFeed().getName().hashCode()), getOsManager()));
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
                    filename = StringUtils.getString(filename.hashCode(), ".", filename.substring(index, filename.length()));
                } else {
                    filename = String.valueOf(filename.hashCode());
                }
                return StringUtils.getString(podcastFeedDownloadFolder.toString(), "/", FileNameUtils.getValidFileName(filename, getOsManager()));
            }
        } catch (URISyntaxException e) {
            Logger.error(e);
        }
        throw new IllegalArgumentException();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#isDownloaded(net.sourceforge.atunes.model.IPodcastFeedEntry)
	 */
    @Override
	public boolean isDownloaded(IPodcastFeedEntry podcastFeedEntry) {
        File f = new File(getDownloadPath(podcastFeedEntry));
        return f.exists();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#deleteDownloadedPodcastFeedEntry(net.sourceforge.atunes.model.IPodcastFeedEntry)
	 */
    @Override
	public void deleteDownloadedPodcastFeedEntry(final IPodcastFeedEntry podcastFeedEntry) {
        File f = new File(getDownloadPath(podcastFeedEntry));
        new DeleteDownloadedPodcastFeedEntryWorker(f, podcastFeedEntry, (ITable)getBean("navigationTable")).execute();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedHandler#isDownloading(net.sourceforge.atunes.model.IPodcastFeedEntry)
	 */
    @Override
	public boolean isDownloading(IPodcastFeedEntry podcastFeedEntry) {
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
    public void applicationStateChanged(IState newState) {
        setPodcastFeedEntryRetrievalInterval(newState.getPodcastFeedEntriesRetrievalInterval());
    }
    
	/**
	 * @return the podcastFeedEntryDownloaderExecutorService
	 */
	private ExecutorService getPodcastFeedEntryDownloaderExecutorService() {
		if (podcastFeedEntryDownloaderExecutorService == null) {
			podcastFeedEntryDownloaderExecutorService = Executors.newCachedThreadPool();
		}
		return podcastFeedEntryDownloaderExecutorService;
	}
}
