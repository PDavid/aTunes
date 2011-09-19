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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.utils.I18nUtils;

public class RemoveOldPodcastEntryAction extends AbstractActionOverSelectedObjects<PodcastFeedEntry> {

    private static final long serialVersionUID = -1499729879534990802L;

    RemoveOldPodcastEntryAction() {
        super(I18nUtils.getString("REMOVE_OLD_PODCAST_ENTRY"), PodcastFeedEntry.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("REMOVE_OLD_PODCAST_ENTRY"));
    }

    @Override
    protected void performAction(List<PodcastFeedEntry> objects) {
        for (IPodcastFeedEntry pfe : objects) {
            pfe.getPodcastFeed().removeEntry(pfe);
        }
        getBean(INavigationHandler.class).refreshView(PodcastNavigationView.class);
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        for (IAudioObject ao : selection) {
            if (!(ao instanceof IPodcastFeedEntry) || !((IPodcastFeedEntry) ao).isOld()) {
                return false;
            }
        }
        return true;
    }

}
