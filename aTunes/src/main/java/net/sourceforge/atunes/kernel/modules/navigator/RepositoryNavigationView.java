/*
 * aTunes 2.0.0-SNAPSHOT
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellDecorator;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.decorators.AlbumTreeCellDecorator;
import net.sourceforge.atunes.gui.views.decorators.ArtistTreeCellDecorator;
import net.sourceforge.atunes.gui.views.decorators.FolderTreeCellDecorator;
import net.sourceforge.atunes.gui.views.decorators.GenreTreeCellDecorator;
import net.sourceforge.atunes.gui.views.decorators.IncompleteTagsTreeCellDecorator;
import net.sourceforge.atunes.gui.views.decorators.StringTreeCellDecorator;
import net.sourceforge.atunes.gui.views.decorators.TooltipTreeCellDecorator;
import net.sourceforge.atunes.gui.views.decorators.UnknownElementTreeCellDecorator;
import net.sourceforge.atunes.gui.views.decorators.YearTreeCellDecorator;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.CopyToDeviceAction;
import net.sourceforge.atunes.kernel.actions.EditTitlesAction;
import net.sourceforge.atunes.kernel.actions.ExtractPictureAction;
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
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.Folder;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class RepositoryNavigationView extends AbstractNavigationView {

    private static final class AlbumDefaultMutableTreeNode extends DefaultMutableTreeNode {
        private static final long serialVersionUID = -1276777390072754207L;

        private AlbumDefaultMutableTreeNode(Object userObject) {
            super(userObject);
        }

        @Override
        public String toString() {
            return ((Album) getUserObject()).getNameAndArtist();
        }
    }

    private JTree tree;

    private JPopupMenu treePopupMenu;

    private JPopupMenu tablePopupMenu;

    private List<AbstractTreeCellDecorator> decorators;

    @Override
    public ImageIcon getIcon() {
        return Images.getImage(Images.AUDIO_FILE_LITTLE);
    }

    @Override
    public String getTitle() {
        return I18nUtils.getString("REPOSITORY");
    }

    @Override
    public String getTooltip() {
        return I18nUtils.getString("REPOSITORY_TAB_TOOLTIP");
    }

    @Override
    public JTree getTree() {
        if (tree == null) {
            tree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("REPOSITORY"))));
            tree.setToggleClickCount(0);
            tree.setCellRenderer(getTreeRenderer());
            ToolTipManager.sharedInstance().registerComponent(tree);
        }
        return tree;
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
        }
        return treePopupMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (tablePopupMenu == null) {
            tablePopupMenu = new JPopupMenu();
            tablePopupMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            tablePopupMenu.add(getMenuItemForAction(AddToPlayListAfterCurrentAudioObjectAction.class));
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
            tablePopupMenu.add(getMenuItemForAction(SetFavoriteSongFromNavigatorAction.class));
        }
        return tablePopupMenu;
    }

    @Override
    protected Map<String, ?> getViewData(ViewMode viewMode) {
        if (viewMode == ViewMode.YEAR) {
            return RepositoryHandler.getInstance().getYearStructure();
        } else if (viewMode == ViewMode.GENRE) {
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
        debug("Refreshing ", this.getClass().getName());

        // Get model and root
        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Get objects selected before refreshing tree
        List<TreeObject> objectsSelected = getTreeObjectsSelected(tree);
        // Get objects expanded before refreshing tree
        List<TreeObject> objectsExpanded = getTreeObjectsExpanded(tree, root);

        if (viewMode == ViewMode.YEAR) {
            refreshYearTree((Map<String, Year>) getViewData(viewMode), treeFilter, root, treeModel, objectsSelected, objectsExpanded);
        } else if (viewMode == ViewMode.GENRE) {
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
                songs.addAll(RepositoryHandler.getInstance().getAudioFilesList());
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
        root.setUserObject(I18nUtils.getString("REPOSITORY"));
        root.removeAllChildren();

        List<String> artistNamesList = new ArrayList<String>(structure.keySet());
        if (ApplicationState.getInstance().isUseSmartTagViewSorting() && !ApplicationState.getInstance().isUsePersonNamesArtistTagViewSorting()) {
            Collections.sort(artistNamesList, getSmartComparator());
        } else if (ApplicationState.getInstance().isUsePersonNamesArtistTagViewSorting()) {
            Collections.sort(artistNamesList, getArtistNamesComparator());
        } else {
            Collections.sort(artistNamesList, getDefaultComparator());
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
                Collections.sort(albumNamesList, getSmartComparator());
            } else {
                Collections.sort(albumNamesList, getDefaultComparator());
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
        root.setUserObject(I18nUtils.getString("REPOSITORY"));
        root.removeAllChildren();

        List<String> albumsNamesList = new ArrayList<String>(structure.keySet());
        if (ApplicationState.getInstance().isUseSmartTagViewSorting()) {
            Collections.sort(albumsNamesList, getSmartComparator());
        } else {
            Collections.sort(albumsNamesList, getDefaultComparator());
        }

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();

        for (int i = 0; i < albumsNamesList.size(); i++) {
            Album album = structure.get(albumsNamesList.get(i));
            if (currentFilter == null || album.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                // Special album node that shows artist name too
                DefaultMutableTreeNode albumNode = new AlbumDefaultMutableTreeNode(album);

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
        root.setUserObject(I18nUtils.getString("REPOSITORY"));
        root.removeAllChildren();
        List<String> genreNamesList = new ArrayList<String>(structure.keySet());
        Collections.sort(genreNamesList, getDefaultComparator());

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
                List<String> artistNamesList = new ArrayList<String>(genre.getArtistSet());
                Collections.sort(artistNamesList);
                Map<String, Artist> genreArtists = genre.getArtistObjects();
                for (int j = 0; j < artistNamesList.size(); j++) {
                    Artist artist = genreArtists.get(artistNamesList.get(j));
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
     * Refresh year tree.
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
    private void refreshYearTree(Map<String, Year> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded) {
        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        // Refresh nodes
        root.setUserObject(I18nUtils.getString("REPOSITORY"));
        root.removeAllChildren();
        List<String> yearNamesList = new ArrayList<String>(structure.keySet());
        Collections.sort(yearNamesList, getDefaultComparator());

        for (int i = 0; i < yearNamesList.size(); i++) {
            Year year = structure.get(yearNamesList.get(i));
            if (currentFilter == null || year.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode yearNode = new DefaultMutableTreeNode(year);
                // If node was selected before refreshing...
                if (objectsSelected.contains(yearNode.getUserObject())) {
                    nodesToSelect.add(yearNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(yearNode.getUserObject())) {
                    nodesToExpand.add(yearNode);
                }
                List<String> artistNamesList = new ArrayList<String>(year.getArtistSet());
                Collections.sort(artistNamesList);
                Map<String, Artist> yearArtists = year.getArtistObjects();
                for (int j = 0; j < artistNamesList.size(); j++) {
                    Artist artist = yearArtists.get(artistNamesList.get(j));
                    DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
                    List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
                    Collections.sort(albumNamesList);
                    for (int k = 0; k < albumNamesList.size(); k++) {
                        Album album = artist.getAlbum(albumNamesList.get(k));
                        DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
                        artistNode.add(albumNode);
                        yearNode.add(artistNode);
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
                root.add(yearNode);
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
        root.setUserObject(I18nUtils.getString("REPOSITORY"));
        root.removeAllChildren();
        RefreshUtils.addFolderNodes(structure, root, currentFilter, getDefaultComparator());

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
    public boolean isUseDefaultNavigatorColumnSet() {
        return true;
    }

    @Override
    public AbstractColumnSet getCustomColumnSet() {
        // Return null since use default navigator column set
        return null;
    }

    @Override
    public boolean isViewModeSupported() {
        return true;
    }

    @Override
    protected List<AbstractTreeCellDecorator> getTreeCellDecorators() {
        if (decorators == null) {
            decorators = new ArrayList<AbstractTreeCellDecorator>();
            decorators.add(new ArtistTreeCellDecorator());
            decorators.add(new AlbumTreeCellDecorator());
            decorators.add(new GenreTreeCellDecorator());
            decorators.add(new YearTreeCellDecorator());
            decorators.add(new FolderTreeCellDecorator());
            decorators.add(new StringTreeCellDecorator());
            decorators.add(new TooltipTreeCellDecorator());
            decorators.add(new UnknownElementTreeCellDecorator());
            decorators.add(new IncompleteTagsTreeCellDecorator());
        }
        return decorators;
    }

    /**
     * Gets the tool tip for repository.
     * 
     * @return the tool tip for repository
     */

    static String getToolTipForRepository() {
        int songs = RepositoryHandler.getInstance().getAudioFilesList().size();
        return StringUtils.getString(I18nUtils.getString("REPOSITORY"), " (", songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG")), ")");
    }
}
