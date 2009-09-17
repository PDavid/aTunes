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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;

import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.CopyToRepositoryAction;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.ExtractPictureAction;
import net.sourceforge.atunes.kernel.actions.FillDeviceWithRandomSongsAction;
import net.sourceforge.atunes.kernel.actions.FilterNavigatorAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAtAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.repository.model.Folder;
import net.sourceforge.atunes.kernel.modules.repository.model.Genre;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

public final class DeviceNavigationView extends NavigationView {

    /** The device tree. */
    private JTree deviceTree;

    /** The device tree menu. */
    private JPopupMenu deviceTreeMenu;

    /** The device table menu. */
    private JPopupMenu deviceTableMenu;

    @Override
    public ImageIcon getIcon() {
        return ImageLoader.getImage(ImageLoader.DEVICE);
    }

    @Override
    public String getTitle() {
        return I18nUtils.getString("DEVICE");
    }

    @Override
    public String getTooltip() {
        return I18nUtils.getString("DEVICE_TAB_TOOLTIP");
    }

    @Override
    public JTree getTree() {
        if (deviceTree == null) {
            deviceTree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("DEVICE"))));
            deviceTree.setToggleClickCount(0);
            deviceTree.setCellRenderer(getTreeRenderer());
        }
        return deviceTree;
    }

    @Override
    public boolean isAudioObjectsFromNodeNeedSort() {
        return true;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (deviceTreeMenu == null) {
            deviceTreeMenu = new JPopupMenu();
            deviceTreeMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            deviceTreeMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(getMenuItemForAction(OpenFolderFromNavigatorAction.class));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(new EditTagMenu(false, this));
            deviceTreeMenu.add(Actions.getAction(EditTitlesAction.class));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(new JMenuItem(Actions.getAction(RemoveFromDiskAction.class)));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(getMenuItemForAction(CopyToRepositoryAction.class));
            deviceTreeMenu.add(new JMenuItem(Actions.getAction(FillDeviceWithRandomSongsAction.class)));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(Actions.getAction(SearchArtistAction.class));
            deviceTreeMenu.add(Actions.getAction(SearchArtistAtAction.class));
            deviceTreeMenu.add(new JSeparator());
            deviceTreeMenu.add(new JMenuItem(Actions.getAction(FilterNavigatorAction.class)));

        }
        return deviceTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (deviceTableMenu == null) {
            deviceTableMenu = new JPopupMenu();
            deviceTableMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            deviceTableMenu.add(getMenuItemForAction(AddToPlayListAfterCurrentAudioObjectAction.class));
            deviceTableMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            deviceTableMenu.add(new JMenuItem(Actions.getAction(PlayNowAction.class)));
            deviceTableMenu.add(new JSeparator());
            deviceTableMenu.add(new JMenuItem(Actions.getAction(OpenFolderFromNavigatorAction.class)));
            deviceTableMenu.add(new JSeparator());
            deviceTableMenu.add(new EditTagMenu(false, this));
            deviceTableMenu.add(getMenuItemForAction(ExtractPictureAction.class));
            deviceTableMenu.add(new JSeparator());
            deviceTableMenu.add(new JMenuItem(Actions.getAction(RemoveFromDiskAction.class)));
            deviceTableMenu.add(new JSeparator());
            deviceTableMenu.add(getMenuItemForAction(CopyToRepositoryAction.class));
            deviceTableMenu.add(new JSeparator());
            deviceTableMenu.add(new JMenuItem(Actions.getAction(FilterNavigatorAction.class)));
        }
        return deviceTableMenu;
    }

    @Override
    protected Map<String, ?> getViewData(ViewMode viewMode) {
        Map<String, ?> data;
        if (viewMode == ViewMode.GENRE) {
            data = DeviceHandler.getInstance().getDeviceGenreStructure();
        } else if (viewMode == ViewMode.FOLDER) {
            data = DeviceHandler.getInstance().getDeviceFolderStructure();
        } else if (viewMode == ViewMode.ALBUM) {
            data = DeviceHandler.getInstance().getDeviceAlbumStructure();
        } else {
            data = DeviceHandler.getInstance().getDeviceArtistStructure();
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing " + this.getClass().getName());

        Map<String, ?> data = getViewData(viewMode);

        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        // Get objects selected before refreshing tree
        List<TreeObject> objectsSelected = getTreeObjectsSelected(getTree());
        // Get objects expanded before refreshing tree
        List<TreeObject> objectsExpanded = getTreeObjectsExpanded(getTree(), root);

        root.removeAllChildren();
        List<String> artistNamesList = new ArrayList<String>(data.keySet());
        if (ApplicationState.getInstance().isUseSmartTagViewSorting()) {
            Collections.sort(artistNamesList, smartComparator);
        } else {
            Collections.sort(artistNamesList, comparator);
        }

        if (viewMode == ViewMode.ARTIST) {
            refreshArtistView(artistNamesList, (Map<String, Artist>) data, treeFilter, root, objectsSelected, nodesToSelect, nodesToExpand, objectsExpanded, treeModel);
        } else if (viewMode == ViewMode.ALBUM) {
            refreshAlbumView(artistNamesList, (Map<String, Album>) data, treeFilter, root, objectsSelected, nodesToSelect, nodesToExpand, objectsExpanded, treeModel);
        } else if (viewMode == ViewMode.GENRE) {
            refreshGenreView(artistNamesList, (Map<String, Genre>) data, treeFilter, root, objectsSelected, nodesToSelect, nodesToExpand, objectsExpanded, treeModel);
        } else { // Folder view
            refreshFolderView((Map<String, Folder>) data, root, objectsSelected, nodesToSelect, nodesToExpand, objectsExpanded, treeModel);
        }

        // Expand nodes
        expandNodes(getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        selectNodes(getTree(), nodesToSelect);
    }

    /**
     * Fills artist view
     * 
     * @param artistNamesList
     * @param data
     * @param treeFilter
     * @param root
     * @param objectsSelected
     * @param nodesToSelect
     * @param nodesToExpand
     * @param objectsExpanded
     * @param treeModel
     */
    private void refreshArtistView(List<String> artistNamesList, Map<String, Artist> data, String treeFilter, DefaultMutableTreeNode root, List<TreeObject> objectsSelected, List<DefaultMutableTreeNode> nodesToSelect, List<DefaultMutableTreeNode> nodesToExpand, List<TreeObject> objectsExpanded, DefaultTreeModel treeModel) {
        for (int i = 0; i < artistNamesList.size(); i++) {
            Artist artist = data.get(artistNamesList.get(i));
            DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
            List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
            if (ApplicationState.getInstance().isUseSmartTagViewSorting() && !ApplicationState.getInstance().isUsePersonNamesArtistTagViewSorting()) {
                Collections.sort(artistNamesList, smartComparator);
            } else if (ApplicationState.getInstance().isUsePersonNamesArtistTagViewSorting()) {
                Collections.sort(artistNamesList, artistNamesComparator);
            } else {
                Collections.sort(artistNamesList, comparator);
            }
            if (treeFilter == null || artist.getName().toUpperCase().contains(treeFilter.toUpperCase())) {
                for (int j = 0; j < albumNamesList.size(); j++) {
                    Album album = artist.getAlbum(albumNamesList.get(j));
                    DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
                    // If node was selected before refreshing...
                    if (objectsSelected.contains(albumNode.getUserObject())) {
                        nodesToSelect.add(albumNode);
                    }
                    artistNode.add(albumNode);
                }
                root.add(artistNode);
                //  Reload causes very important lag on large collections and if it is not used
                // selection does not work.

                // If node was selected before refreshing...
                if (objectsSelected.contains(artistNode.getUserObject())) {
                    nodesToSelect.add(artistNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(artistNode.getUserObject())) {
                    nodesToExpand.add(artistNode);
                }
            }
        }

        // Reload the tree to refresh content
        treeModel.reload();
    }

    /**
     * Fills album view
     * 
     * @param artistNamesList
     * @param data
     * @param treeFilter
     * @param root
     * @param objectsSelected
     * @param nodesToSelect
     * @param nodesToExpand
     * @param objectsExpanded
     * @param treeModel
     */
    private void refreshAlbumView(List<String> artistNamesList, Map<String, Album> data, String treeFilter, DefaultMutableTreeNode root, List<TreeObject> objectsSelected, List<DefaultMutableTreeNode> nodesToSelect, List<DefaultMutableTreeNode> nodesToExpand, List<TreeObject> objectsExpanded, DefaultTreeModel treeModel) {

        List<String> albumsNamesList = new ArrayList<String>(data.keySet());
        if (ApplicationState.getInstance().isUseSmartTagViewSorting()) {
            Collections.sort(albumsNamesList, smartComparator);
        } else {
            Collections.sort(albumsNamesList, comparator);
        }

        for (int i = 0; i < albumsNamesList.size(); i++) {
            Album album = data.get(albumsNamesList.get(i));
            if (treeFilter == null || album.getName().toUpperCase().contains(treeFilter.toUpperCase())) {
                // Special album node that shows artist name too
                DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album) {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = 0L;

                    @Override
                    public String toString() {
                        return ((Album) getUserObject()).getNameAndArtist();
                    }
                };

                root.add(albumNode);

                // If node was selected before refreshing...
                if (objectsSelected.contains(albumNode.getUserObject())) {
                    nodesToSelect.add(albumNode);
                }
            }
        }

        // Reload the tree to refresh content
        treeModel.reload();

    }

    /**
     * Fill Genre view
     * 
     * @param artistNamesList
     * @param data
     * @param treeFilter
     * @param root
     * @param objectsSelected
     * @param nodesToSelect
     * @param nodesToExpand
     * @param objectsExpanded
     * @param treeModel
     */
    private void refreshGenreView(List<String> artistNamesList, Map<String, Genre> data, String treeFilter, DefaultMutableTreeNode root, List<TreeObject> objectsSelected, List<DefaultMutableTreeNode> nodesToSelect, List<DefaultMutableTreeNode> nodesToExpand, List<TreeObject> objectsExpanded, DefaultTreeModel treeModel) {
        List<String> genreNamesList = new ArrayList<String>(data.keySet());
        Collections.sort(genreNamesList, comparator);

        for (int i = 0; i < genreNamesList.size(); i++) {
            Genre genre = data.get(genreNamesList.get(i));
            if (treeFilter == null || genre.getName().toUpperCase().contains(treeFilter.toUpperCase())) {
                DefaultMutableTreeNode genreNode = new DefaultMutableTreeNode(genre);
                // If node was selected before refreshing...
                if (objectsSelected.contains(genreNode.getUserObject())) {
                    nodesToSelect.add(genreNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(genreNode.getUserObject())) {
                    nodesToExpand.add(genreNode);
                }
                artistNamesList = new ArrayList<String>(genre.getArtists().keySet());
                Collections.sort(artistNamesList);
                for (int j = 0; j < artistNamesList.size(); j++) {
                    Artist artist = genre.getArtist(artistNamesList.get(j));
                    DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
                    List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
                    Collections.sort(albumNamesList);
                    for (int k = 0; k < albumNamesList.size(); k++) {
                        Album album = artist.getAlbum(albumNamesList.get(k));
                        DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
                        artistNode.add(albumNode);
                        genreNode.add(artistNode);
                        // If node was selected before refreshing...
                        if (objectsSelected.contains(artistNode.getUserObject())) {
                            nodesToSelect.add(artistNode);
                        }
                        // If node was selected before refreshing...
                        if (objectsSelected.contains(albumNode.getUserObject())) {
                            nodesToSelect.add(albumNode);
                        }
                        // If node was expanded before refreshing...
                        if (objectsExpanded.contains(artistNode.getUserObject()) && objectsExpanded.contains(((DefaultMutableTreeNode) artistNode.getParent()).getUserObject())) {
                            nodesToExpand.add(artistNode);
                        }
                    }
                }
                root.add(genreNode);
            }
        }
        // Reload the tree to refresh content
        treeModel.reload();

    }

    /**
     * Fill folder view
     * 
     * @param data
     * @param root
     * @param objectsSelected
     * @param nodesToSelect
     * @param nodesToExpand
     * @param objectsExpanded
     * @param treeModel
     */
    private void refreshFolderView(Map<String, Folder> data, DefaultMutableTreeNode root, List<TreeObject> objectsSelected, List<DefaultMutableTreeNode> nodesToSelect, List<DefaultMutableTreeNode> nodesToExpand, List<TreeObject> objectsExpanded, DefaultTreeModel treeModel) {
        List<String> rootFolderKeys = new ArrayList<String>(data.keySet());
        if (rootFolderKeys.isEmpty()) {
            root.setUserObject(I18nUtils.getString("DEVICE"));
        } else {
            root.setUserObject(StringUtils.getString(I18nUtils.getString("DEVICE"), " (", rootFolderKeys.get(0), ")"));
            Folder rootFolder = data.get(rootFolderKeys.get(0));
            RefreshUtils.addFolderNodes(rootFolder.getFolders(), root, null, comparator);
        }
        // Reload the tree to refresh content
        treeModel.reload();

        if (objectsExpanded.isEmpty()) {
            // In folder view root child nodes must be expanded always
            // So when refreshing folder view for first time add these nodes to list of expanded objects
            for (int i = 0; i < root.getChildCount(); i++) {
                TreeNode childNode = root.getChildAt(i);
                objectsExpanded.add((TreeObject) ((DefaultMutableTreeNode) childNode).getUserObject());
            }
        }

        // Get nodes to select after refresh
        nodesToSelect = RefreshUtils.getNodes(root, objectsSelected);

        // Get nodes to expand after refresh
        nodesToExpand = RefreshUtils.getNodes(root, objectsExpanded);

    }

    @Override
    public List<AudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<AudioObject> songs = new ArrayList<AudioObject>();
        if (node.isRoot()) {
            if (treeFilter == null) {
                songs.addAll(DeviceHandler.getInstance().getAudioFilesList());
            } else {
                songs = new ArrayList<AudioObject>();
                for (int i = 0; i < node.getChildCount(); i++) {
                    TreeObject obj = (TreeObject) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
                    songs.addAll(obj.getAudioObjects());
                }
            }
        } else {
            TreeObject obj = (TreeObject) node.getUserObject();
            songs = obj.getAudioObjects();
        }
        return songs;
    }

    @Override
    public Class<?> getNavigatorTableColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Property.class;
        case 1:
            return String.class;
        case 2:
            return Long.class;
        default:
            return Long.class;
        }
    }

    @Override
    public int getNavigatorTableColumnCount() {
        return 3;
    }

    @Override
    public String getNavigatorTableColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return "";
        case 1:
            return I18nUtils.getString("FILE");
        case 2:
            return I18nUtils.getString("DURATION");
        default:
            return "";
        }
    }

    @Override
    public Object getNavigatorTableValueAt(AudioObject audioObject, int columnIndex) {
        if (audioObject instanceof AudioFile) {
            if (columnIndex == 0) {
                return ApplicationState.getInstance().isShowFavoritesInNavigator() && FavoritesHandler.getInstance().getFavoriteSongs().contains(audioObject) ? Property.FAVORITE
                        : Property.NO_PROPERTIES;
            } else if (columnIndex == 1) {
                if (ApplicationState.getInstance().getViewMode() != ViewMode.FOLDER) {
                    return audioObject.getTitleOrFileName();
                }
                return ((AudioFile) audioObject).getFile().getName();
            } else if (columnIndex == 2) {
                return audioObject.getDuration();
            }
        }
        return "";
    }

    @Override
    public int getNavigatorTableMaxWidthForColumn(int columnIndex) {
        if (columnIndex == 0) {
            return 20;
        } else if (columnIndex == 2) {
            return 50;
        }
        return -1;
    }

    @Override
    public boolean isViewModeSupported() {
        return true;
    }

    @Override
    public List<AudioObject> filterNavigatorTable(List<AudioObject> audioObjects, String filter) {
        // Filter by title
        List<AudioObject> filteredObjects = new ArrayList<AudioObject>();
        for (AudioObject audioObject : audioObjects) {
            if (audioObject.getTitleOrFileName().toLowerCase().contains(filter.toLowerCase())) {
                filteredObjects.add(audioObject);
            }
        }
        return filteredObjects;
    }

    @Override
    public boolean isNavigatorTableFilterSupported() {
        return true;
    }

    @Override
    protected TreeCellRenderer getTreeRenderer() {
        return new SubstanceDefaultTreeCellRenderer() {
            private static final long serialVersionUID = -7992021225213275134L;

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                final Object content = node.getUserObject();

                JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                if (content instanceof Artist) {
                    label.setIcon(ImageLoader.getImage(ImageLoader.ARTIST));
                } else if (content instanceof Album) {
                    label.setIcon(ImageLoader.getImage(ImageLoader.ALBUM));
                } else if (content instanceof Genre) {
                    label.setIcon(ImageLoader.getImage(ImageLoader.GENRE));
                } else if (content instanceof Folder) {
                    label.setIcon(ImageLoader.getImage(ImageLoader.FOLDER));
                } else {
                    label.setIcon(ImageLoader.getImage(ImageLoader.DEVICE));
                }

                if (value.toString() != null) {
                    if (Artist.isUnknownArtist(value.toString()) || Album.isUnknownAlbum(value.toString()) || Genre.isUnknownGenre(value.toString())) {
                        label.setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);
                    }
                }
                return label;
            }
        };
    }
}
