package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.model.TreeObject;

/**
 * Interface for methods that generate a tree for a particular view mode
 * @author fleax
 *
 */
public interface TreeGenerator {

	/**
	 * Builds a tree
	 * @param rootTextKey
	 * @param view
	 * @param structure
	 * @param currentFilter
	 * @param root
	 * @param treeModel
	 * @param objectsSelected
	 * @param objectsExpanded
	 */
	public void buildTree(String rootTextKey, 
						  AbstractNavigationView view, 
						  Map<String, ?> structure, 
						  String currentFilter, 
						  DefaultMutableTreeNode root, 
						  DefaultTreeModel treeModel, 
						  List<TreeObject> objectsSelected, 
						  List<TreeObject> objectsExpanded);
}
