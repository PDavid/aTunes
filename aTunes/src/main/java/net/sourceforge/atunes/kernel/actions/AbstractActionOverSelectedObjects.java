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
import net.sourceforge.atunes.model.IAudioObjectsSource;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * An action called after selecting audio objects which are parameters of action
 * 
 * @author alex
 * 
 * @param <T>
 */

public abstract class AbstractActionOverSelectedObjects<T extends IAudioObject>
		extends CustomAbstractAction {

	private static final long serialVersionUID = 1673432955671008277L;

	private IAudioObjectsSource audioObjectsSource;

	private final Class<?> clazz;

	/**
	 * @param name
	 */
	public AbstractActionOverSelectedObjects(String name) {
		super(name);
		clazz = (Class<?>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * @param name
	 * @param icon
	 */
	public AbstractActionOverSelectedObjects(String name, Icon icon) {
		super(name, icon);
		clazz = (Class<?>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
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
	 * Given an audio object performs a pre-process returning audio object to
	 * include in list or null if given audio object must be excluded from list
	 * 
	 * Default implementation returns the same object
	 * 
	 * @param audioObject
	 * @return
	 */
	protected T preprocessObject(T audioObject) {
		return audioObject;
	}

	/**
	 * @param audioObjectsSource
	 */
	public final void setAudioObjectsSource(
			IAudioObjectsSource audioObjectsSource) {
		this.audioObjectsSource = audioObjectsSource;
	}

	protected abstract void executeAction(List<T> objects);

	@Override
	protected final void executeAction() {
		// Use executionAction(List<T> objects)
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void actionPerformed(ActionEvent e) {
		Logger.debug("Executing action: ", this.getClass().getName());

		if (this.audioObjectsSource == null) {
			return;
		}

		List<IAudioObject> audioObjects = this.audioObjectsSource
				.getSelectedAudioObjects();

		if (audioObjects == null || audioObjects.isEmpty()) {
			return;
		}

		List<T> selectedObjects = new ArrayList<T>();

		for (IAudioObject ao : audioObjects) {
			if (clazz.isAssignableFrom(ao.getClass())) {
				if (isPreprocessNeeded()) {
					T processedAudioObject = preprocessObject((T) ao);
					if (processedAudioObject != null) {
						selectedObjects.add(processedAudioObject);
					}
				} else {
					selectedObjects.add((T) ao);
				}
			}
		}

		// Call to perform action if some object is selected and valid
		if (!CollectionUtils.isEmpty(selectedObjects)) {
			executeAction(selectedObjects);
		}
	}

	@Override
	public final boolean isEnabledForPlayListSelection(
			List<IAudioObject> selection) {
		if (selection.isEmpty()) {
			return false;
		}

		for (IAudioObject ao : selection) {
			if (!clazz.isAssignableFrom(ao.getClass())) {
				return false;
			}
		}
		return true;
	}
}
