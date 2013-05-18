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

import javax.swing.Action;
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
		return this.rssSmallIcon.getColorMutableIcon();
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
		if (this.podcastFeedTree == null) {
			this.podcastFeedTree = new NavigationTree(
					I18nUtils.getString("PODCAST_FEEDS"), getTreeRenderer());
			this.podcastFeedTree.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
		}
		return this.podcastFeedTree;
	}

	@Override
	public JPopupMenu getTreePopupMenu() {
		if (this.podcastFeedTreeMenu == null) {
			this.podcastFeedTreeMenu = new JPopupMenu();
			AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = getBeanFactory()
					.getBean("addToPlayListFromPodcastNavigationView",
							AddToPlayListAction.class);
			addToPlayListAction.setAudioObjectsSource(this);
			this.podcastFeedTreeMenu.add(addToPlayListAction);

			SetAsPlayListAction setAsPlayListAction = getBeanFactory().getBean(
					"setAsPlaylistFromPodcastNavigationView",
					SetAsPlayListAction.class);
			setAsPlayListAction.setAudioObjectsSource(this);
			this.podcastFeedTreeMenu.add(setAsPlayListAction);

			this.podcastFeedTreeMenu.add(new JSeparator());
			this.podcastFeedTreeMenu.add(getBeanFactory().getBean(
					AddPodcastFeedAction.class));
			this.podcastFeedTreeMenu.add(getBeanFactory().getBean(
					RenamePodcastFeedAction.class));
			AbstractActionOverSelectedTreeObjects<IPodcastFeed> markListened = getBeanFactory()
					.getBean(MarkPodcastListenedAction.class);
			markListened.setTreeObjectsSource(this);
			this.podcastFeedTreeMenu.add(markListened);
			this.podcastFeedTreeMenu.add(getBeanFactory().getBean(
					RemovePodcastFeedAction.class));
		}
		return this.podcastFeedTreeMenu;
	}

	@Override
	public JPopupMenu getTablePopupMenu() {
		if (this.podcastFeedTableMenu == null) {
			this.podcastFeedTableMenu = new JPopupMenu();

			AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = getBeanFactory()
					.getBean("addToPlayListFromPodcastNavigationView",
							AddToPlayListAction.class);
			addToPlayListAction.setAudioObjectsSource(this);
			this.podcastFeedTableMenu.add(addToPlayListAction);

			AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = getBeanFactory()
					.getBean(
							"addToPlayListAfterCurrentAudioObjectFromPodcastNavigationView",
							AddToPlayListAfterCurrentAudioObjectAction.class);
			addToPlayListAfterCurrentAudioObjectAction
					.setAudioObjectsSource(this);
			this.podcastFeedTableMenu
					.add(addToPlayListAfterCurrentAudioObjectAction);

			SetAsPlayListAction setAsPlayListAction = getBeanFactory().getBean(
					"setAsPlaylistFromPodcastNavigationView",
					SetAsPlayListAction.class);
			setAsPlayListAction.setAudioObjectsSource(this);
			this.podcastFeedTableMenu.add(setAsPlayListAction);

			this.podcastFeedTableMenu.add(getBeanFactory().getBean(
					PlayNowAction.class));
			this.podcastFeedTableMenu.add(new JSeparator());
			this.podcastFeedTableMenu.add(getBeanFactory().getBean(
					ShowNavigatorTableItemInfoAction.class));
			this.podcastFeedTableMenu.add(new JSeparator());

			AbstractActionOverSelectedObjects<IPodcastFeedEntry> downloadPodcastEntryAction = getBeanFactory()
					.getBean(DownloadPodcastEntryAction.class);
			downloadPodcastEntryAction.setAudioObjectsSource(this);
			this.podcastFeedTableMenu.add(downloadPodcastEntryAction);

			RemoveOldPodcastEntryAction removeOldPodcastEntryAction = getBeanFactory()
					.getBean(RemoveOldPodcastEntryAction.class);
			removeOldPodcastEntryAction.setAudioObjectsSource(this);
			this.podcastFeedTableMenu.add(removeOldPodcastEntryAction);

			MarkPodcastEntryListenedAction markPodcastEntryListenedAction = getBeanFactory()
					.getBean(MarkPodcastEntryListenedAction.class);
			markPodcastEntryListenedAction.setAudioObjectsSource(this);
			this.podcastFeedTableMenu.add(markPodcastEntryListenedAction);

			this.podcastFeedTableMenu.add(new JSeparator());

			AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = getBeanFactory()
					.getBean("copyToDeviceFromPodcastNavigationView",
							CopyToDeviceAction.class);
			copyToDeviceAction.setAudioObjectsSource(this);
			this.podcastFeedTableMenu.add(copyToDeviceAction);

			this.podcastFeedTableMenu.add(new JSeparator());
			this.podcastFeedTableMenu.add(getBeanFactory().getBean(
					RemoveFromDiskAction.class));
		}
		return this.podcastFeedTableMenu;
	}

	@Override
	protected Map<String, ?> getViewData(final ViewMode viewMode) {
		Map<String, List<IPodcastFeed>> data = new HashMap<String, List<IPodcastFeed>>();
		data.put("PODCASTS", this.podcastFeedHandler.getPodcastFeeds());
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

		getTree().setRoot(
				new NavigationTreeRoot(I18nUtils.getString("PODCAST_FEEDS"),
						getIcon()));

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
	public List<IAudioObject> getAudioObjectForTreeNode(final ITreeNode node,
			final ViewMode viewMode, final String treeFilter,
			final String tableFilter) {
		List<IAudioObject> songs = new ArrayList<IAudioObject>();
		if (node.isRoot()) {
			if (treeFilter == null) {
				List<IPodcastFeed> podcastFeeds = this.podcastFeedHandler
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
			songs.addAll(obj.getAudioObjects());
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
		return this.podcastNavigationColumnSet;
	}

	@Override
	public boolean isViewModeSupported() {
		return false;
	}

	@Override
	public boolean overlayNeedsToBeVisible() {
		return this.podcastFeedHandler.getPodcastFeeds().isEmpty();
	}

	@Override
	public Action getOverlayAction() {
		return getBeanFactory().getBean(AddPodcastFeedAction.class);
	}

	@Override
	public String getOverlayText() {
		return I18nUtils.getString("NO_PODCASTS_INFORMATION");
	}
}
