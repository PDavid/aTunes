/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Common code for tree generation
 * 
 * @author alex
 * 
 */
public abstract class AbstractTreeGenerator implements ITreeGenerator {

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public final void buildTree(final INavigationTree tree,
			final String rootTextKey, final INavigationView view,
			final Map<String, ?> structure, final String currentFilter,
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<ITreeObject<? extends IAudioObject>> objectsExpanded) {

		// Set root
		tree.setRoot(new NavigationTreeRoot(I18nUtils.getString(rootTextKey),
				view.getIcon()));

		// Nodes to be selected after refresh
		List<ITreeNode> nodesToSelect = new ArrayList<ITreeNode>();
		// Nodes to be expanded after refresh
		List<ITreeNode> nodesToExpand = new ArrayList<ITreeNode>();

		TreeLevel<?> firstLevel = buildTreeLevels();
		List<TreeFilterMatch> listOfMatches = getListOfMatchesPerLevel(firstLevel);

		List<String> namesOfObjectsOfFirstLevel = getNamesOfObjectsOfFirstLevel(structure);

		sortNamesOfObjectsOfLevel(namesOfObjectsOfFirstLevel, firstLevel);

		for (String name : namesOfObjectsOfFirstLevel) {
			Object levelElementObject = structure.get(name);
			TreeFilterMatch match = getFilterMatch(firstLevel,
					levelElementObject, currentFilter);
			if (match != TreeFilterMatch.NONE) {
				tree.addNode(createNode(firstLevel, tree, levelElementObject,
						match, currentFilter, objectsSelected, objectsExpanded,
						nodesToSelect, nodesToExpand, listOfMatches));
			}
		}

		// Reload the tree to refresh content
		tree.reload();

		// Expand nodes
		tree.expandNodes(nodesToExpand);

		// Once tree has been refreshed, select previously selected nodes
		tree.selectNodes(nodesToSelect);
	}

	private List<String> getNamesOfObjectsOfFirstLevel(
			final Map<String, ?> structure) {
		// First level names are always keys of structure
		return new ArrayList<String>(structure.keySet());
	}

	private ITreeNode createNode(final TreeLevel<?> level,
			final INavigationTree tree, final Object object,
			final TreeFilterMatch match, final String filter,
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<ITreeObject<? extends IAudioObject>> objectsExpanded,
			final List<ITreeNode> nodesToSelect,
			final List<ITreeNode> nodesToExpand,
			final List<TreeFilterMatch> listOfMatches) {

		ITreeNode node = tree.createNode(object);
		// If node was selected before refreshing...
		if (objectsSelected.contains(object)) {
			nodesToSelect.add(node);
		}
		// If node was expanded before refreshing...
		if (objectsExpanded.contains(object)) {
			nodesToExpand.add(node);
		}

		TreeLevel<?> nextLevel = level.getNextLevel();
		if (nextLevel != null) {
			Map<String, ?> nextLevelObjects = level.getNextLevelObjects(object);
			List<String> names = new ArrayList<String>(
					nextLevelObjects.keySet());
			sortNamesOfObjectsOfLevel(names, level);
			for (String name : names) {
				Object nextLevelObject = nextLevelObjects.get(name);
				if (objectPassesFilter(nextLevelObject, match, nextLevel,
						filter, listOfMatches)) {
					node.add(createNode(nextLevel, tree, nextLevelObject,
							match, filter, objectsSelected, objectsExpanded,
							nodesToSelect, nodesToExpand, listOfMatches));
				}
			}
		}

		return node;
	}

	private void sortNamesOfObjectsOfLevel(final List<String> names,
			final TreeLevel<?> level) {
		level.getSorter().sort(names);
	}

	private boolean objectPassesFilter(final Object object,
			final TreeFilterMatch match, final TreeLevel<?> level,
			final String filter, final List<TreeFilterMatch> listOfMatches) {
		if (matchInASuperlevel(match, level, listOfMatches)
				|| level.filterMatchAtLevel(object, filter)
				|| (getFilterMatchAtSublevels(level, object, filter) != TreeFilterMatch.NONE)) {
			return true;
		}
		return false;
	}

	private boolean matchInASuperlevel(final TreeFilterMatch match,
			final TreeLevel<?> level, final List<TreeFilterMatch> listOfMatches) {
		for (TreeFilterMatch levelMatch : listOfMatches) {
			if (levelMatch == level.getMatch()) {
				// Reached level -> false
				return false;
			} else if (levelMatch == match) {
				return true;
			}
		}
		return false;
	}

	private List<TreeFilterMatch> getListOfMatchesPerLevel(
			final TreeLevel<?> firstLevel) {
		List<TreeFilterMatch> listOfMatches = new ArrayList<TreeFilterMatch>();
		listOfMatches.add(TreeFilterMatch.ALL);
		TreeLevel<?> level = firstLevel;
		while (level != null) {
			listOfMatches.add(level.getMatch());
			level = level.getNextLevel();
		}
		listOfMatches.add(TreeFilterMatch.NONE);
		return listOfMatches;
	}

	private TreeFilterMatch getFilterMatch(final TreeLevel<?> level,
			final Object levelObject, final String filter) {
		if (StringUtils.isEmpty(filter)) {
			return TreeFilterMatch.ALL;
		}

		if (level.filterMatchAtLevel(levelObject, filter)) {
			return level.getMatch();
		}

		return getFilterMatchAtSublevels(level, levelObject, filter);
	}

	private TreeFilterMatch getFilterMatchAtSublevels(final TreeLevel<?> level,
			final Object levelObject, final String filter) {
		Map<String, ?> nextLevelObjects = level
				.getNextLevelObjects(levelObject);
		TreeLevel<?> nextLevel = level.getNextLevel();
		if (nextLevel != null) {
			// Check all objects of next level
			for (Object nextLevelObject : nextLevelObjects.values()) {
				if (nextLevel.filterMatchAtLevel(nextLevelObject, filter)) {
					return nextLevel.getMatch();
				}
			}

			// all objects checked need to go down a level
			for (Object nextLevelObject : nextLevelObjects.values()) {
				TreeFilterMatch sublevelsMatch = getFilterMatchAtSublevels(
						nextLevel, nextLevelObject, filter);
				if (sublevelsMatch != TreeFilterMatch.NONE) {
					return sublevelsMatch;
				}
			}
		}

		return TreeFilterMatch.NONE;
	}

	private final TreeLevel<?> buildTreeLevels() {
		return this.beanFactory.getBean(TreeLevelsBuilder.class).buildLevels(
				getTreeLevels());
	}

	abstract List<Class<? extends TreeLevel<?>>> getTreeLevels();

}
