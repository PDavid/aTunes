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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.IInputDialogFactory;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * @author alex
 *
 */
public class RenamePodcastFeedAction extends CustomAbstractAction {

    private static final long serialVersionUID = 8334487960720117561L;

    private INavigationHandler navigationHandler;
    
    private IInputDialogFactory inputDialogFactory;
    
    /**
     * @param inputDialogFactory
     */
    public void setInputDialogFactory(IInputDialogFactory inputDialogFactory) {
		this.inputDialogFactory = inputDialogFactory;
	}
    
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    public RenamePodcastFeedAction() {
        super(I18nUtils.getString("RENAME_PODCAST_FEED"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RENAME_PODCAST_FEED"));
    }

    @Override
    protected void executeAction() {
        TreePath path = navigationHandler.getView(PodcastNavigationView.class).getTree().getSelectionPath();
        IPodcastFeed podcastFeed = (IPodcastFeed) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        IInputDialog dialog = inputDialogFactory.getDialog();
        dialog.setTitle(I18nUtils.getString("RENAME_PODCAST_FEED"));
        dialog.showDialog(podcastFeed.getName());
        String result = dialog.getResult();
        if (result != null) {
            podcastFeed.setName(result);
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }
}
