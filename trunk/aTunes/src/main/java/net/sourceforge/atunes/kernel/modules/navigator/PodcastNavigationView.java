/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellDecorator;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.decorators.PodcastFeedTreeCellDecorator;
import net.sourceforge.atunes.gui.views.decorators.StringTreeCellDecorator;
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
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.columns.DateColumn;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class PodcastNavigationView extends AbstractNavigationView {

    private List<AbstractTreeCellDecorator> decorators;

    /** The podcast feed tree. */
    private JTree podcastFeedTree;

    private JPopupMenu podcastFeedTreeMenu;

    private JPopupMenu podcastFeedTableMenu;

    /** The column set */
    private AbstractColumnSet columnSet;

    @Override
    public ImageIcon getIcon() {
        return Images.getImage(Images.RSS_LITTLE);
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
            podcastFeedTreeMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            podcastFeedTreeMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            podcastFeedTreeMenu.add(new JSeparator());
            podcastFeedTreeMenu.add(Actions.getAction(AddPodcastFeedAction.class));
            podcastFeedTreeMenu.add(Actions.getAction(RenamePodcastFeedAction.class));
            podcastFeedTreeMenu.add(Actions.getAction(MarkPodcastListenedAction.class));
            podcastFeedTreeMenu.add(Actions.getAction(RemovePodcastFeedAction.class));
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

        for (PodcastFeed pf : podcastFeeds) {
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
    public AbstractColumnSet getCustomColumnSet() {
        if (columnSet == null) {
            columnSet = new PodcastNavigationColumnSet(this.getClass().getName());
        }
        return columnSet;
    }

    @Override
    public boolean isViewModeSupported() {
        return false;
    }

    @Override
    protected List<AbstractTreeCellDecorator> getTreeCellDecorators() {
        if (decorators == null) {
            decorators = new ArrayList<AbstractTreeCellDecorator>();
            decorators.add(new StringTreeCellDecorator());
            decorators.add(new PodcastFeedTreeCellDecorator());
        }
        return decorators;
    }

    private static class PodcastNavigationColumnSet extends AbstractCustomNavigatorColumnSet {

        private static final class DurationColumn extends AbstractColumn {
            /**
			 * 
			 */
            private static final long serialVersionUID = -5577224920500040774L;

            private DurationColumn(String name, Class<?> columnClass) {
                super(name, columnClass);
            }

            @Override
            public Object getValueFor(AudioObject audioObject) {
                return StringUtils.seconds2String(audioObject.getDuration());
            }

            @Override
            protected int ascendingCompare(AudioObject o1, AudioObject o2) {
                return Integer.valueOf(o1.getDuration()).compareTo(Integer.valueOf(o2.getDuration()));
            }
        }

        private static final class PodcastEntriesColumn extends AbstractColumn {
            /**
			 * 
			 */
            private static final long serialVersionUID = -1788596965509543581L;

            private PodcastEntriesColumn(String name, Class<?> columnClass) {
                super(name, columnClass);
            }

            @Override
            public Object getValueFor(AudioObject audioObject) {
                return audioObject.getTitleOrFileName();
            }

            @Override
            protected int ascendingCompare(AudioObject o1, AudioObject o2) {
                return o1.getTitleOrFileName().compareTo(o2.getTitleOrFileName());
            }
        }

        private static final class OldEntryPropertyColumn extends AbstractColumn {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            private OldEntryPropertyColumn(String name, Class<?> columnClass) {
                super(name, columnClass);
            }

            @Override
            public Object getValueFor(AudioObject audioObject) {
                return ((PodcastFeedEntry) audioObject).isOld() ? Property.OLD_ENTRY : Property.NO_PROPERTIES;
            }

            @Override
            protected int ascendingCompare(AudioObject o1, AudioObject o2) {
                return Boolean.valueOf(((PodcastFeedEntry) o1).isOld()).compareTo(Boolean.valueOf(((PodcastFeedEntry) o2).isOld()));
            }
        }

        private static final class DownloadedPropertyColumn extends AbstractColumn {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            private DownloadedPropertyColumn(String name, Class<?> columnClass) {
                super(name, columnClass);
            }

            @Override
            public Object getValueFor(AudioObject audioObject) {
                return ((PodcastFeedEntry) audioObject).isDownloaded() ? Property.DOWNLOADED_ENTRY : Property.NO_PROPERTIES;
            }

            @Override
            protected int ascendingCompare(AudioObject o1, AudioObject o2) {
                return Boolean.valueOf(((PodcastFeedEntry) o1).isDownloaded()).compareTo(Boolean.valueOf(((PodcastFeedEntry) o2).isDownloaded()));
            }
        }

        private static final class NotListenedPropertyColumn extends AbstractColumn {
            /**
			 * 
			 */
            private static final long serialVersionUID = 1L;

            private NotListenedPropertyColumn(String name, Class<?> columnClass) {
                super(name, columnClass);
            }

            @Override
            public Object getValueFor(AudioObject audioObject) {
                return ((PodcastFeedEntry) audioObject).isListened() ? Property.NO_PROPERTIES : Property.NOT_LISTENED_ENTRY;
            }

            @Override
            protected int ascendingCompare(AudioObject o1, AudioObject o2) {
                return Boolean.valueOf(((PodcastFeedEntry) o1).isListened()).compareTo(Boolean.valueOf(((PodcastFeedEntry) o2).isListened()));
            }
        }

        public PodcastNavigationColumnSet(String columnSetName) {
            super(columnSetName);
        }

        @Override
        protected List<AbstractColumn> getAllowedColumns() {
            List<AbstractColumn> columns = new ArrayList<AbstractColumn>();

            AbstractColumn property1 = new NotListenedPropertyColumn("", Property.class);
            property1.setVisible(true);
            property1.setWidth(20);
            property1.setResizable(false);
            columns.add(property1);

            AbstractColumn property2 = new DownloadedPropertyColumn("", Property.class);
            property2.setVisible(true);
            property2.setWidth(20);
            property2.setResizable(false);
            columns.add(property2);

            AbstractColumn property3 = new OldEntryPropertyColumn("", Property.class);
            property3.setVisible(true);
            property3.setWidth(20);
            property3.setResizable(false);
            columns.add(property3);

            AbstractColumn entries = new PodcastEntriesColumn("PODCAST_ENTRIES", String.class);
            entries.setVisible(true);
            entries.setWidth(300);
            entries.setUsedForFilter(true);
            columns.add(entries);

            AbstractColumn duration = new DurationColumn("DURATION", String.class);
            duration.setVisible(true);
            duration.setWidth(60);
            duration.setUsedForFilter(true);
            columns.add(duration);
            
            AbstractColumn date = new DateColumn();
            date.setVisible(true);
            date.setUsedForFilter(true);
            columns.add(date);

            return columns;
        }

    }

}
