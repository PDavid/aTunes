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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sourceforge.atunes.model.IHandlerBackgroundInitializationTask;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IStateHandler;

/**
 * Reads podcasts
 * @author alex
 *
 */
public class PodcastsInitializationTask implements IHandlerBackgroundInitializationTask {

	private PodcastFeedHandler podcastFeedHandler;
	
	private IStateHandler stateHandler;
	
	private INavigationHandler navigationHandler;
	
	private INavigationView podcastNavigationView;

	/**
	 * @param podcastNavigationView
	 */
	public void setPodcastNavigationView(INavigationView podcastNavigationView) {
		this.podcastNavigationView = podcastNavigationView;
	}
	
	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param podcastFeedHandler
	 */
	public void setPodcastFeedHandler(PodcastFeedHandler podcastFeedHandler) {
		this.podcastFeedHandler = podcastFeedHandler;
	}
	
	/**
	 * @param stateHandler
	 */
	public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}
	
	@Override
	public Runnable getInitializationTask() {
        return new Runnable() {
            @Override
            public void run() {
                List<IPodcastFeed> podcastFeeds = stateHandler.retrievePodcastFeedCache();
            	/*
                 * java.util.concurrent.CopyOnWriteArrayList instead of e.g.
                 * java.util.ArrayList to avoid ConcurrentModificationException
                 */
                podcastFeedHandler.setPodcastFeeds(podcastFeeds != null ? podcastFeeds : new CopyOnWriteArrayList<IPodcastFeed>());
            }
        };
	}
	
	@Override
	public Runnable getInitializationCompletedTask() {
		return new Runnable() {
			@Override
			public void run() {
				navigationHandler.refreshView(podcastNavigationView);
			}
		};
	}
}
