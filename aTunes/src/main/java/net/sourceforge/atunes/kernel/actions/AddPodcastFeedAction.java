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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens dialog to add a podcast feed
 * 
 * @author fleax
 * 
 */
public class AddPodcastFeedAction extends CustomAbstractAction {

	private static final long serialVersionUID = 2866782020999148427L;

	private IPodcastFeedHandler podcastFeedHandler;

	/**
	 * Default constructor
	 */
	public AddPodcastFeedAction() {
		super(I18nUtils.getString("ADD_PODCAST_FEED"));
	}

	/**
	 * @param podcastFeedHandler
	 */
	public void setPodcastFeedHandler(final IPodcastFeedHandler podcastFeedHandler) {
		this.podcastFeedHandler = podcastFeedHandler;
	}

	@Override
	protected void executeAction() {
		podcastFeedHandler.addPodcastFeed();
	}

	@Override
	public boolean isEnabledForNavigationTreeSelection(final boolean rootSelected, final List<ITreeNode> selection) {
		return true;
	}
}
