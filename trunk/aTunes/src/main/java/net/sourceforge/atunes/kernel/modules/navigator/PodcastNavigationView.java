/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.kernel.actions.Actions;
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
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.repository.Repository;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

public final class PodcastNavigationView extends NavigationView {

    /** The podcast feed tree. */
    private JTree podcastFeedTree;

    private JPopupMenu podcastFeedTreeMenu;

    private JPopupMenu podcastFeedTableMenu;

    @Override
    public ImageIcon getIcon() {
        return ImageLoader.getImage(ImageLoader.RSS_LITTLE);
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
    public boolean isAudioObjectsFromNodeNeedSort() {
        return false;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (podcastFeedTreeMenu == null) {
            podcastFeedTreeMenu = new JPopupMenu();
            podcastFeedTreeMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            podcastFeedTreeMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            podcastFeedTreeMenu.add(new JSeparator());
            podcastFeedTreeMenu.add(Actions.getAction(AddPodcastFeedAction.class));
            podcastFeedTreeMenu.add(Actions.getAction(RenamePodcastFeedAction.class));
            podcastFeedTreeMenu.add(Actions.getAction(MarkPodcastListenedAction.class));
            podcastFeedTreeMenu.add(getMenuItemForAction(RemovePodcastFeedAction.class));
        }
        return podcastFeedTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (podcastFeedTableMenu == null) {
            podcastFeedTableMenu = new JPopupMenu();
            podcastFeedTableMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            podcastFeedTableMenu.add(getMenuItemForAction(AddToPlayListAfterCurrentAudioObjectAction.class));
            podcastFeedTableMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            podcastFeedTableMenu.add(Actions.getAction(PlayNowAction.class));
            podcastFeedTableMenu.add(new JSeparator());
            podcastFeedTableMenu.add(Actions.getAction(ShowNavigatorTableItemInfoAction.class));
            podcastFeedTableMenu.add(new JSeparator());
            podcastFeedTableMenu.add(getMenuItemForAction(DownloadPodcastEntryAction.class));
            podcastFeedTableMenu.add(getMenuItemForAction(RemoveOldPodcastEntryAction.class));
            podcastFeedTableMenu.add(getMenuItemForAction(MarkPodcastEntryListenedAction.class));
            podcastFeedTableMenu.add(new JSeparator());
            podcastFeedTableMenu.add(getMenuItemForAction(CopyToDeviceAction.class));
            podcastFeedTableMenu.add(new JSeparator());
            podcastFeedTableMenu.add(Actions.getAction(RemoveFromDiskAction.class));
        }
        return podcastFeedTableMenu;
    }

    @Override
    protected Map<String, ?> getViewData(ViewMode viewMode) {
        Map<String, List<PodcastFeed>> data = new HashMap<String, List<PodcastFeed>>();
        data.put("PODCASTS", PodcastFeedHandler.getInstance().getPodcastFeeds());
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
    public List<AudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<AudioObject> songs = new ArrayList<AudioObject>();
        if (node.isRoot()) {
            if (treeFilter == null) {
                List<PodcastFeed> podcastFeeds = PodcastFeedHandler.getInstance().getPodcastFeeds();
                for (PodcastFeed pf : podcastFeeds) {
                    songs.addAll(pf.getAudioObjects(getSourceRepository()));
                }
            } else {
                for (int i = 0; i < node.getChildCount(); i++) {
                    PodcastFeed obj = (PodcastFeed) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
                    songs.addAll(obj.getAudioObjects(getSourceRepository()));
                }
            }
        } else {
            PodcastFeed obj = (PodcastFeed) node.getUserObject();
            songs = obj.getAudioObjects(getSourceRepository());
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

        for (PodcastFeed pf : podcastFeeds) {
            if (currentFilter == null || pf.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(pf);
                root.add(child);
            }
        }
    }

    @Override
    public Class<?> getNavigatorTableColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Property.class;
        case 1:
            return Property.class;
        case 2:
            return Property.class;
        case 3:
            return String.class;
        case 4:
            return Long.class;
        default:
            return Long.class;
        }
    }

    @Override
    public int getNavigatorTableColumnCount() {
        return 5;
    }

    @Override
    public String getNavigatorTableColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return "";
        case 1:
            return "";
        case 2:
            return "";
        case 3:
            return I18nUtils.getString("PODCAST_ENTRIES");
        case 4:
            return I18nUtils.getString("DURATION");
        default:
            return "";
        }
    }

    @Override
    public Object getNavigatorTableValueAt(AudioObject audioObject, int columnIndex) {
        if (audioObject instanceof PodcastFeedEntry) {
            if (columnIndex == 0) {
                return ((PodcastFeedEntry) audioObject).isListened() ? Property.NO_PROPERTIES : Property.NOT_LISTENED_ENTRY;
            } else if (columnIndex == 1) {
                return ((PodcastFeedEntry) audioObject).isDownloaded() ? Property.DOWNLOADED_ENTRY : Property.NO_PROPERTIES;
            } else if (columnIndex == 2) {
                return ((PodcastFeedEntry) audioObject).isOld() ? Property.OLD_ENTRY : Property.NO_PROPERTIES;
            } else if (columnIndex == 3) {
                return audioObject.getTitleOrFileName();
            } else if (columnIndex == 4) {
                return audioObject.getDuration();
            }
        }
        return "";
    }

    @Override
    public int getNavigatorTableMaxWidthForColumn(int columnIndex) {
        if (columnIndex == 0) {
            return 20;
        } else if (columnIndex == 1) {
            return 20;
        } else if (columnIndex == 2) {
            return 18;
        } else if (columnIndex == 4) {
            return 50;
        }
        return -1;
    }

    @Override
    public boolean isViewModeSupported() {
        return false;
    }

    @Override
    public List<AudioObject> filterNavigatorTable(List<AudioObject> audioObjects, String filter) {
        return null;
    }

    @Override
    public boolean isNavigatorTableFilterSupported() {
        return false;
    }

    @Override
    protected TreeCellRenderer getTreeRenderer() {
        return new SubstanceDefaultTreeCellRenderer() {
            private static final long serialVersionUID = -4467842082863144354L;

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                JLabel icon = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                icon.setIcon(ImageLoader.getImage(ImageLoader.RSS_LITTLE));
                return icon;
            }
        };
    }
    
    @Override
    public Repository getSourceRepository() {
    	return null;
    }
}
