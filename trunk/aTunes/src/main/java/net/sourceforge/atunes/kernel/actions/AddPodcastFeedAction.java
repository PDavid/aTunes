/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens dialog to add a podcast feed
 * 
 * @author fleax
 * 
 */
public class AddPodcastFeedAction extends Action {

    private static final long serialVersionUID = 2866782020999148427L;

    AddPodcastFeedAction() {
        super(I18nUtils.getString("ADD_PODCAST_FEED"), ImageLoader.getImage(ImageLoader.RSS_ADD));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PodcastFeedHandler.getInstance().addPodcastFeed();
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return true;
    }

}
