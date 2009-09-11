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
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.utils.LanguageTool;

public class RenamePodcastFeedAction extends Action {

    private static final long serialVersionUID = 8334487960720117561L;

    RenamePodcastFeedAction() {
        super(LanguageTool.getString("RENAME_PODCAST_FEED"), ImageLoader.getImage(ImageLoader.EDIT_ALBUM));
        putValue(SHORT_DESCRIPTION, LanguageTool.getString("RENAME_PODCAST_FEED"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath path = NavigationHandler.getInstance().getView(PodcastNavigationView.class).getTree().getSelectionPath();
        PodcastFeed podcastFeed = (PodcastFeed) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        String result = VisualHandler.getInstance().showInputDialog(LanguageTool.getString("RENAME_PODCAST_FEED"), podcastFeed.getName(),
                ImageLoader.getImage(ImageLoader.RSS_LITTLE).getImage());
        if (result != null) {
            podcastFeed.setName(result);
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

}
