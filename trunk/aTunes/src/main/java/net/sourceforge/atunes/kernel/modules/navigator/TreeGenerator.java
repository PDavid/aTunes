/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.TreeObject;

/**
 * Interface for methods that generate a tree for a particular view mode
 * @author fleax
 *
 */
public interface TreeGenerator {

	/**
	 * Builds a tree
	 * @param state
	 * @param rootTextKey
	 * @param view
	 * @param structure
	 * @param currentFilter
	 * @param root
	 * @param treeModel
	 * @param objectsSelected
	 * @param objectsExpanded
	 */
	public void buildTree(IState state, 
				          String rootTextKey, 
						  AbstractNavigationView view, 
						  Map<String, ?> structure, 
						  String currentFilter, 
						  DefaultMutableTreeNode root, 
						  DefaultTreeModel treeModel, 
						  List<TreeObject<? extends AudioObject>> objectsSelected, 
						  List<TreeObject<? extends AudioObject>> objectsExpanded);
	
	public void selectAudioObject(JTree tree, AudioObject audioObject);

	/**
	 * Request generator to select given artist
	 * @param tree
	 * @param artist
	 */
	public void selectArtist(JTree tree, String artist);
}
