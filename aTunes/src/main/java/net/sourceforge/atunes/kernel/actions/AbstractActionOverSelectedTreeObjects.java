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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ITreeObjectsSource;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * An action called after selecting nodes of a tree
 * @author alex
 *
 * @param <T>
 */
public abstract class AbstractActionOverSelectedTreeObjects<T extends ITreeObject<? extends IAudioObject>> extends CustomAbstractAction {

	private static final long serialVersionUID = -2396109319433549043L;

	private ITreeObjectsSource treeObjectsSource;

	private final Class<?> clazz;

	/**
	 * @param name
	 */
	public AbstractActionOverSelectedTreeObjects(final String name) {
		super(name);
		clazz = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * @param name
	 * @param icon
	 */
	public AbstractActionOverSelectedTreeObjects(final String name, final Icon icon) {
		super(name, icon);
		clazz = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * @param treeObjectsSource
	 */
	public void setTreeObjectsSource(final ITreeObjectsSource treeObjectsSource) {
		this.treeObjectsSource = treeObjectsSource;
	}

	/**
	 * Returns if preprocess is needed
	 * 
	 * Default implementation does not need preprocess
	 * 
	 * @return
	 */
	protected boolean isPreprocessNeeded() {
		return false;
	}

	/**
	 * Given a tree object performs a preprocess returning tree object to
	 * include in list or null if given tree object must be excluded from list
	 * 
	 * Default implementation returns the same object
	 * 
	 * @param treeObject
	 * @return
	 */
	protected T preprocessObject(final T treeObject) {
		return treeObject;
	}

	protected abstract void executeAction(List<T> objects);

	@Override
	protected final void executeAction() {
		// Use executionAction(List<T> objects)
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void actionPerformed(final ActionEvent e) {
		Logger.debug("Executing action: ", this.getClass().getName());

		if (this.treeObjectsSource == null) {
			throw new IllegalArgumentException(StringUtils.getString("No treeObjectsSource for action: ", this.getClass().getName()));
		}

		List<ITreeObject<? extends IAudioObject>> treeObjects = treeObjectsSource.getSelectedTreeObjects();

		if (treeObjects == null || treeObjects.isEmpty()) {
			return;
		}

		List<T> selectedTreeObjects = new ArrayList<T>();

		for (ITreeObject<? extends IAudioObject> ao : treeObjects) {
			if (clazz.isAssignableFrom(ao.getClass())) {
				if (isPreprocessNeeded()) {
					T processedTreeObject = preprocessObject((T) ao);
					if (processedTreeObject != null) {
						selectedTreeObjects.add(processedTreeObject);
					}
				} else {
					selectedTreeObjects.add((T)ao);
				}
			}
		}

		// Call to perform action
		executeAction(selectedTreeObjects);
	}
}
