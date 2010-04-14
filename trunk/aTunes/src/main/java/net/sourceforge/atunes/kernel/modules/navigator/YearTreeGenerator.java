package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Year ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class YearTreeGenerator implements TreeGenerator {

	/**
	 * Builds tree
	 * @param rootTextKey
	 * @param view
	 * @param structure
	 * @param currentFilter
	 * @param root
	 * @param treeModel
	 * @param objectsSelected
	 * @param objectsExpanded
	 */
    public void buildTree(String rootTextKey, AbstractNavigationView view, Map<String, ?> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded) {
        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        // Refresh nodes
        root.setUserObject(I18nUtils.getString(rootTextKey));
        root.removeAllChildren();
        List<String> yearNamesList = new ArrayList<String>(structure.keySet());
        Collections.sort(yearNamesList, view.getIntegerComparator());

        for (int i = 0; i < yearNamesList.size(); i++) {
            Year year = (Year) structure.get(yearNamesList.get(i));
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
        AbstractNavigationView.expandNodes(view.getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        AbstractNavigationView.selectNodes(view.getTree(), nodesToSelect);
    }


}
