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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.RssImageIcon;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
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
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

public final class PodcastNavigationView extends AbstractNavigationView {

    /** The podcast feed tree. */
    private JTree podcastFeedTree;

    private JPopupMenu podcastFeedTreeMenu;

    private JPopupMenu podcastFeedTableMenu;

    /** The column set */
    private IColumnSet podcastNavigationColumnSet;
    
    private IPodcastFeedHandler podcastFeedHandler;

    /**
     * @param podcastFeedHandler
     */
    public void setPodcastFeedHandler(IPodcastFeedHandler podcastFeedHandler) {
		this.podcastFeedHandler = podcastFeedHandler;
	}
    
    /**
     * @param podcastNavigationColumnSet
     */
    public void setPodcastNavigationColumnSet(IColumnSet podcastNavigationColumnSet) {
		this.podcastNavigationColumnSet = podcastNavigationColumnSet;
	}

    
    @Override
    public IColorMutableImageIcon getIcon() {
        return new IColorMutableImageIcon() {
			
			@Override
			public ImageIcon getIcon(Color paint) {
				return RssImageIcon.getSmallIcon(paint, getLookAndFeelManager().getCurrentLookAndFeel());
			}
		};
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
    public JTree getTree() {
        if (podcastFeedTree == null) {
            podcastFeedTree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("PODCAST_FEEDS"))));
            podcastFeedTree.setToggleClickCount(0);
            podcastFeedTree.setCellRenderer(getTreeRenderer());
            podcastFeedTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }
        return podcastFeedTree;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (podcastFeedTreeMenu == null) {
            podcastFeedTreeMenu = new JPopupMenu();
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = Context.getBean("addToPlayListFromPodcastNavigationView", AddToPlayListAction.class);
            addToPlayListAction.setAudioObjectsSource(this);
            podcastFeedTreeMenu.add(addToPlayListAction);
            
            SetAsPlayListAction setAsPlayListAction = Context.getBean("setAsPlaylistFromPodcastNavigationView", SetAsPlayListAction.class);
            setAsPlayListAction.setAudioObjectsSource(this);
            podcastFeedTreeMenu.add(setAsPlayListAction);
            
            podcastFeedTreeMenu.add(new JSeparator());
            podcastFeedTreeMenu.add(Context.getBean(AddPodcastFeedAction.class));
            podcastFeedTreeMenu.add(Context.getBean(RenamePodcastFeedAction.class));
            podcastFeedTreeMenu.add(Context.getBean(MarkPodcastListenedAction.class));
            podcastFeedTreeMenu.add(Context.getBean(RemovePodcastFeedAction.class));
        }
        return podcastFeedTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (podcastFeedTableMenu == null) {
            podcastFeedTableMenu = new JPopupMenu();
            
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = Context.getBean("addToPlayListFromPodcastNavigationView", AddToPlayListAction.class);
            addToPlayListAction.setAudioObjectsSource(this);
            podcastFeedTableMenu.add(addToPlayListAction);
            
            AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = Context.getBean("addToPlayListAfterCurrentAudioObjectFromPodcastNavigationView", AddToPlayListAfterCurrentAudioObjectAction.class);
            addToPlayListAfterCurrentAudioObjectAction.setAudioObjectsSource(this);
            podcastFeedTableMenu.add(addToPlayListAfterCurrentAudioObjectAction);
            
            SetAsPlayListAction setAsPlayListAction = Context.getBean("setAsPlaylistFromPodcastNavigationView", SetAsPlayListAction.class);
            setAsPlayListAction.setAudioObjectsSource(this);
            podcastFeedTableMenu.add(setAsPlayListAction);
            
            podcastFeedTableMenu.add(Context.getBean(PlayNowAction.class));
            podcastFeedTableMenu.add(new JSeparator());
            podcastFeedTableMenu.add(Context.getBean(ShowNavigatorTableItemInfoAction.class));
            podcastFeedTableMenu.add(new JSeparator());
            
            AbstractActionOverSelectedObjects<IPodcastFeedEntry> downloadPodcastEntryAction = Context.getBean(DownloadPodcastEntryAction.class);
            downloadPodcastEntryAction.setAudioObjectsSource(this);
            podcastFeedTableMenu.add(downloadPodcastEntryAction);
            
            RemoveOldPodcastEntryAction removeOldPodcastEntryAction = Context.getBean(RemoveOldPodcastEntryAction.class);
            removeOldPodcastEntryAction.setAudioObjectsSource(this);
            podcastFeedTableMenu.add(removeOldPodcastEntryAction);
            
            MarkPodcastEntryListenedAction markPodcastEntryListenedAction = Context.getBean(MarkPodcastEntryListenedAction.class);
            markPodcastEntryListenedAction.setAudioObjectsSource(this);
            podcastFeedTableMenu.add(markPodcastEntryListenedAction);
            
            podcastFeedTableMenu.add(new JSeparator());
            
            AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = Context.getBean("copyToDeviceFromPodcastNavigationView", CopyToDeviceAction.class);
            copyToDeviceAction.setAudioObjectsSource(this);
            podcastFeedTableMenu.add(copyToDeviceAction);
            
            podcastFeedTableMenu.add(new JSeparator());
            podcastFeedTableMenu.add(Context.getBean(RemoveFromDiskAction.class));
        }
        return podcastFeedTableMenu;
    }

    @Override
    protected Map<String, ?> getViewData(ViewMode viewMode) {
        Map<String, List<IPodcastFeed>> data = new HashMap<String, List<IPodcastFeed>>();
        data.put("PODCASTS", podcastFeedHandler.getPodcastFeeds());
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing ", this.getClass().getName());

        Map<String, ?> data = getViewData(viewMode);

        // remember selected rows (if no podcast feed was added or removed)
        int[] selectedRows = getTree().getSelectionRows();
        int podcastCount = ((List<PodcastFeed>) data.get("PODCASTS")).size();
        int podcastInTreeModelCount = getTree().getModel().getChildCount(getTree().getModel().getRoot());
        if (podcastCount != podcastInTreeModelCount) {
            selectedRows = null;
        }

        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.setUserObject(I18nUtils.getString("PODCAST_FEEDS"));
        root.removeAllChildren();
        addPodcastFeedNodes((List<PodcastFeed>) data.get("PODCASTS"), root, treeFilter);
        treeModel.reload();

        if (selectedRows != null) {
            getTree().setSelectionRows(selectedRows);
        } else {
            getTree().setSelectionRow(0);
        }

        getTree().expandRow(0);

    }

    @Override
    public List<IPodcastFeedEntry> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<IPodcastFeedEntry> songs = new ArrayList<IPodcastFeedEntry>();
        if (node.isRoot()) {
            if (treeFilter == null) {
                List<IPodcastFeed> podcastFeeds = podcastFeedHandler.getPodcastFeeds();
                for (IPodcastFeed pf : podcastFeeds) {
                    songs.addAll(pf.getAudioObjects());
                }
            } else {
                for (int i = 0; i < node.getChildCount(); i++) {
                    PodcastFeed obj = (PodcastFeed) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
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
     *            the podcast feeds
     * @param root
     *            the root
     * @param currentFilter
     *            the current filter
     */
    private static void addPodcastFeedNodes(List<PodcastFeed> podcastFeeds, DefaultMutableTreeNode root, String currentFilter) {
        if (podcastFeeds == null) {
            return;
        }

        for (IPodcastFeed pf : podcastFeeds) {
            if (currentFilter == null || pf.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(pf);
                root.add(child);
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
