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

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * @author alex
 * 
 */
public class RenamePodcastFeedAction extends CustomAbstractAction {

    private static final long serialVersionUID = 8334487960720117561L;

    private INavigationView podcastNavigationView;

    private IDialogFactory dialogFactory;

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(final IDialogFactory dialogFactory) {
	this.dialogFactory = dialogFactory;
    }

    /**
     * @param podcastNavigationView
     */
    public void setPodcastNavigationView(
	    final INavigationView podcastNavigationView) {
	this.podcastNavigationView = podcastNavigationView;
    }

    /**
     * Default constructor
     */
    public RenamePodcastFeedAction() {
	super(I18nUtils.getString("RENAME_PODCAST_FEED"));
    }

    @Override
    protected void executeAction() {
	IPodcastFeed podcastFeed = (IPodcastFeed) podcastNavigationView
		.getTree().getSelectedNode().getUserObject();
	IInputDialog dialog = dialogFactory.newDialog(IInputDialog.class);
	dialog.setTitle(I18nUtils.getString("RENAME_PODCAST_FEED"));
	dialog.setText(podcastFeed.getName());
	dialog.showDialog();
	String result = dialog.getResult();
	if (result != null) {
	    podcastFeed.setName(result);
	}
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	return !rootSelected && !selection.isEmpty();
    }
}
