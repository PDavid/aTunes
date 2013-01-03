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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedTreeObjects;
import net.sourceforge.atunes.kernel.actions.AddPodcastFeedAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.CopyToDeviceAction;
import net.sourceforge.atunes.kernel.actions.DownloadPodcastEntryAction;
import net.sourceforge.atunes.kernel.actions.MarkPodcastEntryListenedAction;
import net.sourceforge.atunes.kernel.actions.MarkPodcastListenedAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.RemoveOldPodcastEntryAction;
import net.sourceforge.atunes.kernel.actions.RemovePodcastFeedAction;
import net.sourceforge.atunes.kernel.actions.RenamePodcastFeedAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Navigation view for podcasts
 * 
 * @author alex
 * 
 */
public final class PodcastNavigationView extends AbstractNavigationView {

    /** The podcast feed tree. */
    private NavigationTree podcastFeedTree;

    private JPopupMenu podcastFeedTreeMenu;

    private JPopupMenu podcastFeedTableMenu;

    /** The column set */
    private IColumnSet podcastNavigationColumnSet;

    private IPodcastFeedHandler podcastFeedHandler;

    private IIconFactory rssSmallIcon;

    private IBeanFactory beanFactory;

    /**
     * @param beanFactory
     */
    public void setBeanFactory(final IBeanFactory beanFactory) {
	this.beanFactory = beanFactory;
    }

    /**
     * @param rssSmallIcon
     */
    public void setRssSmallIcon(final IIconFactory rssSmallIcon) {
	this.rssSmallIcon = rssSmallIcon;
    }

    /**
     * @param podcastFeedHandler
     */
    public void setPodcastFeedHandler(
	    final IPodcastFeedHandler podcastFeedHandler) {
	this.podcastFeedHandler = podcastFeedHandler;
    }

    /**
     * @param podcastNavigationColumnSet
     */
    public void setPodcastNavigationColumnSet(
	    final IColumnSet podcastNavigationColumnSet) {
	this.podcastNavigationColumnSet = podcastNavigationColumnSet;
    }

    @Override
    public IColorMutableImageIcon getIcon() {
	return rssSmallIcon.getColorMutableIcon();
    }

    @Override
    public String getTitle() {
	return I18nUtils.getString("PODCAST_FEEDS");
    }

    @Override
    public String getTooltip() {
	return I18nUtils.getString("PODCAST_FEED_TAB_TOOLTIP");
    }

    @Override
    public NavigationTree getTree() {
	if (podcastFeedTree == null) {
	    podcastFeedTree = new NavigationTree(
		    I18nUtils.getString("PODCAST_FEEDS"), getTreeRenderer());
	    podcastFeedTree.getSelectionModel().setSelectionMode(
		    TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	return podcastFeedTree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
	if (podcastFeedTreeMenu == null) {
	    podcastFeedTreeMenu = new JPopupMenu();
	    AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = beanFactory
		    .getBean("addToPlayListFromPodcastNavigationView",
			    AddToPlayListAction.class);
	    addToPlayListAction.setAudioObjectsSource(this);
	    podcastFeedTreeMenu.add(addToPlayListAction);

	    SetAsPlayListAction setAsPlayListAction = beanFactory.getBean(
		    "setAsPlaylistFromPodcastNavigationView",
		    SetAsPlayListAction.class);
	    setAsPlayListAction.setAudioObjectsSource(this);
	    podcastFeedTreeMenu.add(setAsPlayListAction);

	    podcastFeedTreeMenu.add(new JSeparator());
	    podcastFeedTreeMenu.add(beanFactory
		    .getBean(AddPodcastFeedAction.class));
	    podcastFeedTreeMenu.add(beanFactory
		    .getBean(RenamePodcastFeedAction.class));
	    AbstractActionOverSelectedTreeObjects<IPodcastFeed> markListened = beanFactory
		    .getBean(MarkPodcastListenedAction.class);
	    markListened.setTreeObjectsSource(this);
	    podcastFeedTreeMenu.add(markListened);
	    podcastFeedTreeMenu.add(beanFactory
		    .getBean(RemovePodcastFeedAction.class));
	}
	return podcastFeedTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
	if (podcastFeedTableMenu == null) {
	    podcastFeedTableMenu = new JPopupMenu();

	    AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = beanFactory
		    .getBean("addToPlayListFromPodcastNavigationView",
			    AddToPlayListAction.class);
	    addToPlayListAction.setAudioObjectsSource(this);
	    podcastFeedTableMenu.add(addToPlayListAction);

	    AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = beanFactory
		    .getBean(
			    "addToPlayListAfterCurrentAudioObjectFromPodcastNavigationView",
			    AddToPlayListAfterCurrentAudioObjectAction.class);
	    addToPlayListAfterCurrentAudioObjectAction
		    .setAudioObjectsSource(this);
	    podcastFeedTableMenu
		    .add(addToPlayListAfterCurrentAudioObjectAction);

	    SetAsPlayListAction setAsPlayListAction = beanFactory.getBean(
		    "setAsPlaylistFromPodcastNavigationView",
		    SetAsPlayListAction.class);
	    setAsPlayListAction.setAudioObjectsSource(this);
	    podcastFeedTableMenu.add(setAsPlayListAction);

	    podcastFeedTableMenu.add(beanFactory.getBean(PlayNowAction.class));
	    podcastFeedTableMenu.add(new JSeparator());
	    podcastFeedTableMenu.add(beanFactory
		    .getBean(ShowNavigatorTableItemInfoAction.class));
	    podcastFeedTableMenu.add(new JSeparator());

	    AbstractActionOverSelectedObjects<IPodcastFeedEntry> downloadPodcastEntryAction = beanFactory
		    .getBean(DownloadPodcastEntryAction.class);
	    downloadPodcastEntryAction.setAudioObjectsSource(this);
	    podcastFeedTableMenu.add(downloadPodcastEntryAction);

	    RemoveOldPodcastEntryAction removeOldPodcastEntryAction = beanFactory
		    .getBean(RemoveOldPodcastEntryAction.class);
	    removeOldPodcastEntryAction.setAudioObjectsSource(this);
	    podcastFeedTableMenu.add(removeOldPodcastEntryAction);

	    MarkPodcastEntryListenedAction markPodcastEntryListenedAction = beanFactory
		    .getBean(MarkPodcastEntryListenedAction.class);
	    markPodcastEntryListenedAction.setAudioObjectsSource(this);
	    podcastFeedTableMenu.add(markPodcastEntryListenedAction);

	    podcastFeedTableMenu.add(new JSeparator());

	    AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = beanFactory
		    .getBean("copyToDeviceFromPodcastNavigationView",
			    CopyToDeviceAction.class);
	    copyToDeviceAction.setAudioObjectsSource(this);
	    podcastFeedTableMenu.add(copyToDeviceAction);

	    podcastFeedTableMenu.add(new JSeparator());
	    podcastFeedTableMenu.add(beanFactory
		    .getBean(RemoveFromDiskAction.class));
	}
	return podcastFeedTableMenu;
    }

    @Override
    protected Map<String, ?> getViewData(final ViewMode viewMode) {
	Map<String, List<IPodcastFeed>> data = new HashMap<String, List<IPodcastFeed>>();
	data.put("PODCASTS", podcastFeedHandler.getPodcastFeeds());
	return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void refreshTree(final ViewMode viewMode, final String treeFilter) {
	Map<String, ?> data = getViewData(viewMode);

	// remember selected rows (if no podcast feed was added or removed)
	int[] selectedRows = getTree().getSelectionRows();
	int podcastCount = ((List<PodcastFeed>) data.get("PODCASTS")).size();
	int podcastInTreeModelCount = getTree().getModel().getChildCount(
		getTree().getModel().getRoot());
	if (podcastCount != podcastInTreeModelCount) {
	    selectedRows = null;
	}

	getTree().setRoot(I18nUtils.getString("PODCAST_FEEDS"));

	addPodcastFeedNodes((List<PodcastFeed>) data.get("PODCASTS"),
		getTree(), treeFilter);
	getTree().reload();

	if (selectedRows != null) {
	    getTree().setSelectionRows(selectedRows);
	} else {
	    getTree().setSelectionRow(0);
	}

	getTree().expandRow(0);

    }

    @Override
    public List<IPodcastFeedEntry> getAudioObjectForTreeNode(
	    final ITreeNode node, final ViewMode viewMode,
	    final String treeFilter) {
	List<IPodcastFeedEntry> songs = new ArrayList<IPodcastFeedEntry>();
	if (node.isRoot()) {
	    if (treeFilter == null) {
		List<IPodcastFeed> podcastFeeds = podcastFeedHandler
			.getPodcastFeeds();
		for (IPodcastFeed pf : podcastFeeds) {
		    songs.addAll(pf.getAudioObjects());
		}
	    } else {
		for (int i = 0; i < node.getChildCount(); i++) {
		    PodcastFeed obj = (PodcastFeed) node.getChildAt(i)
			    .getUserObject();
		    songs.addAll(obj.getAudioObjects());
		}
	    }
	} else {
	    PodcastFeed obj = (PodcastFeed) node.getUserObject();
	    songs = obj.getAudioObjects();
	}
	return songs;
    }

    /**
     * Adds the podcast feed nodes.
     * 
     * @param podcastFeeds
     * @param tree
     * @param currentFilter
     */
    private static void addPodcastFeedNodes(
	    final List<PodcastFeed> podcastFeeds, final INavigationTree tree,
	    final String currentFilter) {
	if (podcastFeeds == null) {
	    return;
	}

	for (IPodcastFeed pf : podcastFeeds) {
	    if (currentFilter == null
		    || pf.getName().toUpperCase()
			    .contains(currentFilter.toUpperCase())) {
		tree.addNode(tree.createNode(pf));
	    }
	}
    }

    @Override
    public boolean isUseDefaultNavigatorColumnSet() {
	return false;
    }

    @Override
    public IColumnSet getCustomColumnSet() {
	return podcastNavigationColumnSet;
    }

    @Override
    public boolean isViewModeSupported() {
	return false;
    }
}
