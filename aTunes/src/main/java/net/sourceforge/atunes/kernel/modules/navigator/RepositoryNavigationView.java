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
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;

import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.CopyToDeviceAction;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.ExtractPictureAction;
import net.sourceforge.atunes.kernel.actions.FilterNavigatorAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.RenameAudioFileInNavigationTableAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAction;
import net.sourceforge.atunes.kernel.actions.SearchArtistAtAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteAlbumFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteArtistFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteSongFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.repository.HighlightFoldersByIncompleteTags;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.repository.model.Folder;
import net.sourceforge.atunes.kernel.modules.repository.model.Genre;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

public class RepositoryNavigationView extends NavigationView {

    private static JTree tree;

    private JPopupMenu treePopupMenu;

    private JPopupMenu tablePopupMenu;

    @Override
    public ImageIcon getIcon() {
        return ImageLoader.getImage(ImageLoader.AUDIO_FILE_LITTLE);
    }

    @Override
    public String getTitle() {
        return LanguageTool.getString("REPOSITORY");
    }

    @Override
    public String getTooltip() {
        return LanguageTool.getString("REPOSITORY_TAB_TOOLTIP");
    }

    @Override
    public JTree getTree() {
        if (tree == null) {
            tree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(LanguageTool.getString("REPOSITORY"))));
            tree.setToggleClickCount(0);
            tree.setCellRenderer(getTreeRenderer());
            ToolTipManager.sharedInstance().registerComponent(tree);
        }
        return tree;
    }

    @Override
    public boolean isAudioObjectsFromNodeNeedSort() {
        return true;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (treePopupMenu == null) {
            treePopupMenu = new JPopupMenu();
            treePopupMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            treePopupMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(getMenuItemForAction(OpenFolderFromNavigatorAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(new EditTagMenu(false, this));
            treePopupMenu.add(Actions.getAction(EditTitlesAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(Actions.getAction(RemoveFromDiskAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(getMenuItemForAction(CopyToDeviceAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(getMenuItemForAction(SetFavoriteAlbumFromNavigatorAction.class));
            treePopupMenu.add(getMenuItemForAction(SetFavoriteArtistFromNavigatorAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(Actions.getAction(SearchArtistAction.class));
            treePopupMenu.add(Actions.getAction(SearchArtistAtAction.class));
            treePopupMenu.add(new JSeparator());
            treePopupMenu.add(Actions.getAction(FilterNavigatorAction.class));
        }
        return treePopupMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (tablePopupMenu == null) {
            tablePopupMenu = new JPopupMenu();
            tablePopupMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            tablePopupMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            tablePopupMenu.add(Actions.getAction(PlayNowAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(Actions.getAction(ShowNavigatorTableItemInfoAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(getMenuItemForAction(OpenFolderFromNavigatorAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(new EditTagMenu(false, this));
            tablePopupMenu.add(getMenuItemForAction(ExtractPictureAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(Actions.getAction(RemoveFromDiskAction.class));
            tablePopupMenu.add(Actions.getAction(RenameAudioFileInNavigationTableAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(getMenuItemForAction(CopyToDeviceAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(Actions.getAction(SetFavoriteSongFromNavigatorAction.class));
            tablePopupMenu.add(new JSeparator());
            tablePopupMenu.add(Actions.getAction(FilterNavigatorAction.class));
        }
        return tablePopupMenu;
    }

    @Override
    protected Map<String, ?> getViewData(ViewMode viewMode) {
        if (viewMode == ViewMode.GENRE) {
            return RepositoryHandler.getInstance().getGenreStructure();
        } else if (viewMode == ViewMode.FOLDER) {
            return RepositoryHandler.getInstance().getFolderStructure();
        } else if (viewMode == ViewMode.ALBUM) {
            return RepositoryHandler.getInstance().getAlbumStructure();
        } else {
            return RepositoryHandler.getInstance().getArtistStructure();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing " + this.getClass().getName());

        // Get model and root
        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Get objects selected before refreshing tree
        List<TreeObject> objectsSelected = getTreeObjectsSelected(tree);
        // Get objects expanded before refreshing tree
        List<TreeObject> objectsExpanded = getTreeObjectsExpanded(tree, root);

        if (viewMode == ViewMode.GENRE) {
            refreshGenreTree((Map<String, Genre>) getViewData(viewMode), treeFilter, root, treeModel, objectsSelected, objectsExpanded);
        } else if (viewMode == ViewMode.FOLDER) {
            refreshFolderView((Map<String, Folder>) getViewData(viewMode), treeFilter, root, treeModel, objectsSelected, objectsExpanded);
        } else if (viewMode == ViewMode.ALBUM) {
            refreshAlbumView((Map<String, Album>) getViewData(viewMode), treeFilter, root, treeModel, objectsSelected, objectsExpanded);
        } else {
            refreshArtistView((Map<String, Artist>) getViewData(viewMode), treeFilter, root, treeModel, objectsSelected, objectsExpanded);
        }

        getTree().expandRow(0);
    }

    @Override
    public List<AudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<AudioObject> songs = new ArrayList<AudioObject>();
        if (node.isRoot()) {
            if (treeFilter == null) {
                songs.addAll(RepositoryHandler.getInstance().getAudioFiles());
            } else {
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

    /**
     * Refresh.
     * 
     * @param structure
     *            the structure
     * @param tree
     *            the tree
     * @param treeModel
     *            the tree model
     * @param viewMode
     *            the view mode
     * @param currentFilter
     *            the current filter
     */
    private void refreshArtistView(Map<String, Artist> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded) {
        // Set root
        root.setUserObject(LanguageTool.getString("REPOSITORY"));
        root.removeAllChildren();

        List<String> artistNamesList = new ArrayList<String>(structure.keySet());
        if (ApplicationState.getInstance().isUseSmartTagViewSorting() && !ApplicationState.getInstance().isUsePersonNamesArtistTagViewSorting()) {
            Collections.sort(artistNamesList, smartComparator);
        } else if (ApplicationState.getInstance().isUsePersonNamesArtistTagViewSorting()) {
            Collections.sort(artistNamesList, artistNamesComparator);
        } else {
            Collections.sort(artistNamesList, comparator);
        }

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        for (int i = 0; i < artistNamesList.size(); i++) {
            Artist artist = structure.get(artistNamesList.get(i));
            DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
            List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
            if (ApplicationState.getInstance().isUseSmartTagViewSorting()) {
                Collections.sort(albumNamesList, smartComparator);
            } else {
                Collections.sort(albumNamesList, comparator);
            }
            if (currentFilter == null || artist.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                for (int j = 0; j < albumNamesList.size(); j++) {
                    Album album = artist.getAlbum(albumNamesList.get(j));
                    DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
                    artistNode.add(albumNode);
                    // If node was selected before refreshing...
                    if (objectsSelected.contains(albumNode.getUserObject())) {
                        nodesToSelect.add(albumNode);
                    }
                }
                root.add(artistNode);
                // Reload causes very important lag on large collections and if it is not used
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

        // Expand nodes
        expandNodes(tree, nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        selectNodes(tree, nodesToSelect);
    }

    /**
     * Refresh.
     * 
     * @param structure
     *            the structure
     * @param tree
     *            the tree
     * @param treeModel
     *            the tree model
     * @param viewMode
     *            the view mode
     * @param currentFilter
     *            the current filter
     */
    private void refreshAlbumView(Map<String, Album> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded) {
        // Set root
        root.setUserObject(LanguageTool.getString("REPOSITORY"));
        root.removeAllChildren();

        List<String> albumsNamesList = new ArrayList<String>(structure.keySet());
        if (ApplicationState.getInstance().isUseSmartTagViewSorting()) {
            Collections.sort(albumsNamesList, smartComparator);
        } else {
            Collections.sort(albumsNamesList, comparator);
        }

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();

        for (int i = 0; i < albumsNamesList.size(); i++) {
            Album album = structure.get(albumsNamesList.get(i));
            if (currentFilter == null || album.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                // Special album node that shows artist name too
                DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album) {

                    private static final long serialVersionUID = -1276777390072754207L;

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

        // Once tree has been refreshed, select previously selected nodes
        selectNodes(tree, nodesToSelect);
    }

    /**
     * Refresh genre tree.
     * 
     * @param structure
     *            the structure
     * @param tree
     *            the tree
     * @param treeModel
     *            the tree model
     * @param currentFilter
     *            the current filter
     */
    private void refreshGenreTree(Map<String, Genre> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded) {
        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        // Refresh nodes
        root.setUserObject(LanguageTool.getString("REPOSITORY"));
        root.removeAllChildren();
        List<String> genreNamesList = new ArrayList<String>(structure.keySet());
        Collections.sort(genreNamesList, comparator);

        for (int i = 0; i < genreNamesList.size(); i++) {
            Genre genre = structure.get(genreNamesList.get(i));
            if (currentFilter == null || genre.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode genreNode = new DefaultMutableTreeNode(genre);
                // If node was selected before refreshing...
                if (objectsSelected.contains(genreNode.getUserObject())) {
                    nodesToSelect.add(genreNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(genreNode.getUserObject())) {
                    nodesToExpand.add(genreNode);
                }
                List<String> artistNamesList = new ArrayList<String>(genre.getArtists().keySet());
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

        // Expand nodes
        expandNodes(tree, nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        selectNodes(tree, nodesToSelect);
    }

    /**
     * Refresh folder view
     * 
     * @param structure
     *            the structure
     * @param tree
     *            the tree
     * @param treeModel
     *            the tree model
     * @param currentFilter
     *            the current filter
     */
    private void refreshFolderView(Map<String, Folder> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded) {

        // Refresh nodes
        root.setUserObject(LanguageTool.getString("REPOSITORY"));
        root.removeAllChildren();
        RefreshUtils.addFolderNodes(structure, root, currentFilter, comparator);

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
        List<DefaultMutableTreeNode> nodesToSelect = RefreshUtils.getNodes(root, objectsSelected);

        // Get nodes to expand after refresh
        List<DefaultMutableTreeNode> nodesToExpand = RefreshUtils.getNodes(root, objectsExpanded);

        // Expand nodes
        expandNodes(tree, nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        selectNodes(tree, nodesToSelect);
    }

    @Override
    public Class<?> getNavigatorTableColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Property.class;
        } else if (columnIndex == 1) {
            if (ApplicationState.getInstance().getViewMode() != ViewMode.FOLDER) {
                return Integer.class;
            }
            return String.class;
        } else if (columnIndex == 2) {
            if (ApplicationState.getInstance().getViewMode() != ViewMode.FOLDER) {
                return String.class;
            }
            return Long.class;
        } else if (columnIndex == 3) {
            return Long.class;
        } else {
            return Long.class;
        }
    }

    @Override
    public int getNavigatorTableColumnCount() {
        if (ApplicationState.getInstance().getViewMode() != ViewMode.FOLDER) {
            return 4;
        }
        return 3;
    }

    @Override
    public String getNavigatorTableColumnName(int columnIndex) {
        if (ApplicationState.getInstance().getViewMode() == ViewMode.FOLDER) {
            switch (columnIndex) {
            case 0:
                return "";
            case 1:
                return LanguageTool.getString("FILE");
            case 2:
                return LanguageTool.getString("DURATION");
            }
        } else {
            switch (columnIndex) {
            case 0:
                return "";
            case 1:
                return "";
            case 2:
                return LanguageTool.getString("TITLE");
            case 3:
                return LanguageTool.getString("DURATION");
            }
        }
        return "";
    }

    @Override
    public Object getNavigatorTableValueAt(AudioObject audioObject, int columnIndex) {
        if (audioObject instanceof AudioFile) {
            if (columnIndex == 0) {
                return ApplicationState.getInstance().isShowFavoritesInNavigator() && FavoritesHandler.getInstance().getFavoriteSongs().contains(audioObject) ? Property.FAVORITE
                        : Property.NO_PROPERTIES;
            } else if (columnIndex == 1) {
                if (ApplicationState.getInstance().getViewMode() != ViewMode.FOLDER) {
                    return audioObject.getTrackNumber();
                }
                return ((AudioFile) audioObject).getFile().getName();
            } else if (columnIndex == 2) {
                if (ApplicationState.getInstance().getViewMode() != ViewMode.FOLDER) {
                    return audioObject.getTitleOrFileName();
                }
                return audioObject.getDuration();
            } else {
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
            if (ApplicationState.getInstance().getViewMode() == ViewMode.FOLDER) {
                return -1;
            }
            return 25;
        } else if (columnIndex == 2) {
            if (ApplicationState.getInstance().getViewMode() != ViewMode.FOLDER) {
                return -1;
            }
            return 50;
        } else if (columnIndex == 3) {
            if (ApplicationState.getInstance().getViewMode() == ViewMode.FOLDER) {
                return -1;
            }
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
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean isHasFocus) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                final Object content = node.getUserObject();

                JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, isHasFocus);

                if (content instanceof Artist) {
                    if (!ApplicationState.getInstance().isShowFavoritesInNavigator() || !FavoritesHandler.getInstance().getFavoriteArtistsInfo().containsValue(content)) {
                        label.setIcon(ImageLoader.getImage(ImageLoader.ARTIST));
                    } else {
                        label.setIcon(ImageLoader.getImage(ImageLoader.ARTIST_FAVORITE));
                    }
                } else if (content instanceof Album) {
                    if (!ApplicationState.getInstance().isShowFavoritesInNavigator() || !FavoritesHandler.getInstance().getFavoriteAlbumsInfo().containsValue(content)) {
                        label.setIcon(ImageLoader.getImage(ImageLoader.ALBUM));
                    } else {
                        label.setIcon(ImageLoader.getImage(ImageLoader.ALBUM_FAVORITE));
                    }
                } else if (content instanceof Genre) {
                    label.setIcon(ImageLoader.getImage(ImageLoader.GENRE));
                } else if (content instanceof Folder) {
                    label.setIcon(ImageLoader.getImage(ImageLoader.FOLDER));
                    if (ApplicationState.getInstance().isHighlightIncompleteTagFolders() && HighlightFoldersByIncompleteTags.hasIncompleteTags((Folder) content)) {
                        label.setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);
                    }
                } else if (content instanceof String) {
                    if (((String) content).equals(LanguageTool.getString("REPOSITORY"))) {
                        label.setIcon(ImageLoader.getImage(ImageLoader.AUDIO_FILE_LITTLE));
                    } else {
                        label.setIcon(ImageLoader.getImage(ImageLoader.FOLDER));
                    }
                    label.setToolTipText(null);
                }

                if (!ApplicationState.getInstance().isShowExtendedTooltip() || !ExtendedToolTip.canObjectBeShownInExtendedToolTip(content)) {
                    if (content instanceof TreeObject) {
                        label.setToolTipText(((TreeObject) content).getToolTip());
                    } else {
                        label.setToolTipText(content.toString());
                    }
                } else {
                    // If using extended tooltip we must set tooltip to null. If not will appear the tooltip of the parent node
                    label.setToolTipText(null);
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

    /**
     * Gets the tool tip for repository.
     * 
     * @return the tool tip for repository
     */

    static String getToolTipForRepository() {
        int songs = RepositoryHandler.getInstance().getAudioFiles().size();
        return StringUtils.getString(LanguageTool.getString("REPOSITORY"), " (", songs, " ", (songs > 1 ? LanguageTool.getString("SONGS") : LanguageTool.getString("SONG")), ")");
    }
}
