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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Marks a podcast feed entry as listened
 * 
 * @author alex
 * 
 */
public class MarkPodcastEntryListenedAction extends
	AbstractActionOverSelectedObjects<IPodcastFeedEntry> {

    private static final long serialVersionUID = 1563803489549692850L;

    private INavigationHandler navigationHandler;

    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(final INavigationHandler navigationHandler) {
	this.navigationHandler = navigationHandler;
    }

    /**
     * Default constructor
     */
    public MarkPodcastEntryListenedAction() {
	super(I18nUtils.getString("MARK_PODCAST_ENTRY_AS_LISTENED"));
    }

    @Override
    protected void executeAction(final List<IPodcastFeedEntry> objects) {
	for (IPodcastFeedEntry pfe : objects) {
	    pfe.setListened(true);
	}
	navigationHandler.refreshNavigationTable();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(
	    final List<IAudioObject> selection) {
	for (IAudioObject ao : selection) {
	    if (!(ao instanceof IPodcastFeedEntry)
		    || ((IPodcastFeedEntry) ao).isListened()) {
		return false;
	    }
	}
	return true;
    }

}
