package net.sourceforge.atunes.plugins.favorites;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
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

import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.gui.views.controls.NavigationTree;
import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
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
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationView;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginConfiguration;
import org.commonjukebox.plugins.PluginInfo;
import org.jvnet.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

public final class FavoritesNavigationView extends NavigationView implements Plugin {

    private JTree favoritesTree;

    /** The favorite tree menu. */
    private JPopupMenu favoriteTreeMenu;

    /** The favorite table menu. */
    private JPopupMenu favoriteTableMenu;

    @Override
    public ImageIcon getIcon() {
        return ImageLoader.getImage(ImageLoader.FAVORITE);
    }

    @Override
    public String getTitle() {
        return I18nUtils.getString("FAVORITES");
    }

    @Override
    public String getTooltip() {
        return I18nUtils.getString("FAVORITES_TAB_TOOLTIP");
    }

    @Override
    public JTree getTree() {
        if (favoritesTree == null) {
            favoritesTree = new NavigationTree(new DefaultTreeModel(new DefaultMutableTreeNode(I18nUtils.getString("FAVORITES"))));
            favoritesTree.setToggleClickCount(0);
            favoritesTree.setCellRenderer(getTreeRenderer());
        }
        return favoritesTree;
    }

    @Override
    public boolean isAudioObjectsFromNodeNeedSort() {
        return true;
    }

    @Override
    public JPopupMenu getTreePopupMenu() {
        if (favoriteTreeMenu == null) {
            favoriteTreeMenu = new JPopupMenu();
            favoriteTreeMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            favoriteTreeMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(getMenuItemForAction(OpenFolderFromNavigatorAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(new EditTagMenu(false, this));
            favoriteTreeMenu.add(Actions.getAction(EditTitlesAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(Actions.getAction(RemoveFromDiskAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(getMenuItemForAction(CopyToDeviceAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(Actions.getAction(RemoveFromFavoritesAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(Actions.getAction(SearchArtistAction.class));
            favoriteTreeMenu.add(Actions.getAction(SearchArtistAtAction.class));
            favoriteTreeMenu.add(new JSeparator());
            favoriteTreeMenu.add(Actions.getAction(FilterNavigatorAction.class));
        }
        return favoriteTreeMenu;
    }

    @Override
    public JPopupMenu getTablePopupMenu() {
        if (favoriteTableMenu == null) {
            favoriteTableMenu = new JPopupMenu();
            favoriteTableMenu.add(getMenuItemForAction(AddToPlayListAction.class));
            favoriteTableMenu.add(getMenuItemForAction(AddToPlayListAfterCurrentAudioObjectAction.class));
            favoriteTableMenu.add(getMenuItemForAction(SetAsPlayListAction.class));
            favoriteTableMenu.add(Actions.getAction(PlayNowAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(Actions.getAction(ShowNavigatorTableItemInfoAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(getMenuItemForAction(OpenFolderFromNavigatorAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(new EditTagMenu(false, this));
            favoriteTableMenu.add(getMenuItemForAction(ExtractPictureAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(Actions.getAction(RemoveFromDiskAction.class));
            favoriteTableMenu.add(Actions.getAction(RenameAudioFileInNavigationTableAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(getMenuItemForAction(CopyToDeviceAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(Actions.getAction(RemoveFromFavoritesAction.class));
            favoriteTableMenu.add(new JSeparator());
            favoriteTableMenu.add(Actions.getAction(FilterNavigatorAction.class));
        }
        return favoriteTableMenu;
    }

    @Override
    public Map<String, ?> getViewData(ViewMode viewMode) {
        Map<String, Map<?, ?>> data = new HashMap<String, Map<?, ?>>();
        data.put("ARTISTS", FavoritesHandler.getInstance().getFavoriteArtistsInfo());
        data.put("ALBUMS", FavoritesHandler.getInstance().getFavoriteAlbumsInfo());
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void refreshTree(ViewMode viewMode, String treeFilter) {
        debug("Refreshing " + this.getClass().getName());

        DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        // Get objects selected before refreshing tree
        List<TreeObject> objectsSelected = getTreeObjectsSelected(getTree());
        // Get objects expanded before refreshing tree
        List<TreeObject> objectsExpanded = getTreeObjectsExpanded(getTree(), root);

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        Map<String, ?> data = getViewData(viewMode);

        root.setUserObject(I18nUtils.getString("FAVORITES"));
        root.removeAllChildren();

        DefaultMutableTreeNode artistsNode = new DefaultMutableTreeNode();
        artistsNode.setUserObject(I18nUtils.getString("ARTISTS"));
        nodesToExpand.add(artistsNode);
        addArtistNodes(artistsNode, treeFilter, (Map<String, Artist>) data.get("ARTISTS"), objectsSelected, objectsExpanded, nodesToSelect, nodesToExpand);
        root.add(artistsNode);

        DefaultMutableTreeNode albumsNode = new DefaultMutableTreeNode();
        albumsNode.setUserObject(I18nUtils.getString("ALBUMS"));
        nodesToExpand.add(albumsNode);
        addAlbumNodes(albumsNode, treeFilter, (Map<String, Album>) data.get("ALBUMS"), objectsSelected, objectsExpanded, nodesToSelect, nodesToExpand);
        root.add(albumsNode);

        DefaultMutableTreeNode songsNode = new DefaultMutableTreeNode();
        songsNode.setUserObject(I18nUtils.getString("SONGS"));
        root.add(songsNode);

        treeModel.reload();

        // Expand nodes
        expandNodes(getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        selectNodes(getTree(), nodesToSelect);
    }

    @Override
    public List<AudioObject> getAudioObjectForTreeNode(DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<AudioObject> songs = null;

        if (node.isRoot()) {
            songs = new ArrayList<AudioObject>();
            songs.addAll(RepositoryHandler.getInstance().getAudioFilesForArtists(FavoritesHandler.getInstance().getFavoriteArtistsInfo()));
            songs.addAll(RepositoryHandler.getInstance().getAudioFilesForAlbums(FavoritesHandler.getInstance().getFavoriteAlbumsInfo()));
            songs.addAll(FavoritesHandler.getInstance().getFavoriteSongsInfo().values());
        } else {
            if (node.getUserObject() instanceof TreeObject) {
                songs = ((TreeObject) node.getUserObject()).getAudioObjects();
            } else {
                songs = new ArrayList<AudioObject>();
                if (node.getUserObject().toString().equals(I18nUtils.getString("ARTISTS"))) {
                    songs.addAll(RepositoryHandler.getInstance().getAudioFilesForArtists(FavoritesHandler.getInstance().getFavoriteArtistsInfo()));
                } else if (node.getUserObject().toString().equals(I18nUtils.getString("ALBUMS"))) {
                    songs.addAll(RepositoryHandler.getInstance().getAudioFilesForAlbums(FavoritesHandler.getInstance().getFavoriteAlbumsInfo()));
                } else {
                    songs.addAll(new ArrayList<AudioFile>(FavoritesHandler.getInstance().getFavoriteSongsInfo().values()));
                }
            }
        }
        return songs;
    }

    /**
     * Adds the album nodes.
     * 
     * @param root
     *            the root
     * @param albums
     *            the albums
     */
    private static void addAlbumNodes(DefaultMutableTreeNode root, String currentFilter, Map<String, Album> albums, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded, List<DefaultMutableTreeNode> nodesToSelect, List<DefaultMutableTreeNode> nodesToExpand) {
        List<String> albumsNamesList = new ArrayList<String>(albums.keySet());
        Collections.sort(albumsNamesList);

        for (int i = 0; i < albumsNamesList.size(); i++) {
            Album album = albums.get(albumsNamesList.get(i));
            if (currentFilter == null || album.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);

                // If node was selected before refreshing...
                if (objectsSelected.contains(albumNode.getUserObject())) {
                    nodesToSelect.add(albumNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(albumNode.getUserObject())) {
                    nodesToExpand.add(albumNode);
                }

                root.add(albumNode);
            }
        }
    }

    /**
     * Adds the artist nodes.
     * 
     * @param root
     *            the root
     * @param artists
     *            the artists
     */
    private static void addArtistNodes(DefaultMutableTreeNode root, String currentFilter, Map<String, Artist> artists, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded, List<DefaultMutableTreeNode> nodesToSelect, List<DefaultMutableTreeNode> nodesToExpand) {
        List<String> artistNamesList = new ArrayList<String>(artists.keySet());
        Collections.sort(artistNamesList);

        for (int i = 0; i < artistNamesList.size(); i++) {
            Artist artist = artists.get(artistNamesList.get(i));
            if (currentFilter == null || artist.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);

                // If node was selected before refreshing...
                if (objectsSelected.contains(artistNode.getUserObject())) {
                    nodesToSelect.add(artistNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(artistNode.getUserObject())) {
                    nodesToExpand.add(artistNode);
                }

                // If an artist fits current filter we want to show all albums so put filter to null
                addAlbumNodes(artistNode, null, artist.getAlbums(), objectsSelected, objectsExpanded, nodesToSelect, nodesToExpand);

                root.add(artistNode);
            }
        }
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
            return I18nUtils.getString("TITLE");
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
                return audioObject.getTitleOrFileName();
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
        return false;
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
            private static final long serialVersionUID = 2880969518313022116L;

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object content = node.getUserObject();

                JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                // Set custom icon for every type of node: artist, album, song...
                if (content instanceof Artist) {
                    label.setIcon(ImageLoader.getImage(ImageLoader.ARTIST));
                } else if (content instanceof Album) {
                    label.setIcon(ImageLoader.getImage(ImageLoader.ALBUM));
                } else if (content instanceof String) {
                    String str = (String) content;
                    if (str.equals(I18nUtils.getString("ARTISTS"))) {
                        label.setIcon(ImageLoader.getImage(ImageLoader.ARTIST));
                    } else if (str.equals(I18nUtils.getString("ALBUMS"))) {
                        label.setIcon(ImageLoader.getImage(ImageLoader.ALBUM));
                    } else if (str.equals(I18nUtils.getString("SONGS"))) {
                        label.setIcon(ImageLoader.getImage(ImageLoader.AUDIO_FILE_LITTLE));
                    } else {
                        label.setIcon(ImageLoader.getImage(ImageLoader.FAVORITE));
                    }
                }

                if (value.toString() != null) {
                    if (Artist.isUnknownArtist(value.toString()) || Album.isUnknownAlbum(value.toString())) {
                        label.setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);
                    }
                }

                return label;
            }
        };
    }

    @Override
    public void configurationChanged(PluginConfiguration arg0) {
        // No configuration
    }

    @Override
    public PluginConfiguration getDefaultConfiguration() {
        // No configuration
        return null;
    }

    @Override
    public void setConfiguration(PluginConfiguration arg0) {
        // No configuration
    }

    @Override
    public void setPluginInfo(PluginInfo arg0) {
        // No info needed
    }
}
