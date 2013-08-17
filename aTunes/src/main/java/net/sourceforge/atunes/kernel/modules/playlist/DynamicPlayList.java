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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.ISearchNodeRepresentation;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.PointedList;

import org.apache.commons.collections.CollectionUtils;

/**
 * This class represents a dynamic play list.
 * 
 * @author alex
 * 
 */
public class DynamicPlayList extends AbstractPlayList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5586679639735635929L;

	ISearchNodeRepresentation query;

	/**
	 * Pointed List of audio objects of this play list
	 */
	private transient PointedList<IAudioObject> audioObjects;

	/**
	 * Pointer to list of audio objects, as pointed list is transient we need to
	 * store that value
	 */
	int pointer = 0;

	/**
	 * Name of class of column to use for sorting items
	 */
	String columnSorted;

	/**
	 * @return column sorted
	 */
	String getColumnSorted() {
		return this.columnSorted;
	}

	/**
	 * No args constructor for serialization
	 */
	DynamicPlayList() {
		super();
	}

	/**
	 * Default constructor
	 * 
	 * @param statePlayer
	 * @param query
	 * @param pointer
	 */
	protected DynamicPlayList(final IStatePlayer statePlayer,
			final ISearchNodeRepresentation query, final int pointer) {
		this((List<IAudioObject>) null, statePlayer, query, pointer);
	}

	/**
	 * Builds a new play list with the given list of audio objects
	 * 
	 * @param audioObjectsList
	 * @param statePlayer
	 * @param query
	 * @param pointer
	 */
	protected DynamicPlayList(
			final Collection<? extends IAudioObject> audioObjectsList,
			final IStatePlayer statePlayer,
			final ISearchNodeRepresentation query, final int pointer) {
		this.query = query;
		this.audioObjects = new PlayListPointedList(statePlayer);
		setMode(PlayListMode.getPlayListMode(this, statePlayer));
		setStatePlayer(statePlayer);
		if (audioObjectsList != null) {
			this.audioObjects.addAll(0, audioObjectsList);
			notifyAudioObjectsAdded(0, audioObjectsList);
		}
		this.audioObjects.setPointer(pointer);
	}

	@Override
	public IPlayList copyPlayList() {
		DynamicPlayList copy = new DynamicPlayList();
		copy.setName(getName());
		copy.audioObjects = new PlayListPointedList(this.audioObjects,
				getStatePlayer());
		copy.setStatePlayer(getStatePlayer());
		copy.setMode(PlayListMode.getPlayListMode(this, getStatePlayer()));
		copy.setListeners(getListeners());
		copy.query = this.query;
		return copy;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	/**
	 * @return query representation for this dynamic play list
	 */
	protected ISearchNodeRepresentation getQuery() {
		return this.query;
	}

	/**
	 * @param query
	 */
	protected void setQuery(final ISearchNodeRepresentation query) {
		this.query = query;
	}

	@Override
	public void sortByColumn(final IColumn<?> column) {
		this.columnSorted = column.getClass().getName();
		this.audioObjects.sort(column.getComparator());
		notifyCurrentAudioObjectChanged(this.audioObjects.getCurrentObject());
	}

	// METHODS NOT IMPLEMENTED BY THIS TYPE OF PLAY LIST

	@Override
	public void moveRowTo(final int sourceRow, final int targetRow) {
	}

	@Override
	public void add(final int newLocation,
			final List<? extends IAudioObject> audioObjects) {
	}

	@Override
	public void clear() {
	}

	@Override
	public void remove(final int i) {
	}

	@Override
	public void add(final int newLocation, final IAudioObject aux) {
	}

	@Override
	public void shuffle() {
	}

	@Override
	public void remove(final Collection<? extends IAudioObject> list) {
		// First get all positions of objects to remove
		List<IPlayListAudioObject> playListAudioObjects = new ArrayList<IPlayListAudioObject>();
		for (IAudioObject ao : list) {
			List<IAudioObject> clonedList = new ArrayList<IAudioObject>(
					this.audioObjects.getList());
			while (clonedList.indexOf(ao) != -1) {
				int index = clonedList.indexOf(ao);
				PlayListAudioObject playListAudioObject = new PlayListAudioObject();
				playListAudioObject.setPosition(index);
				playListAudioObject.setAudioObject(ao);
				playListAudioObjects.add(playListAudioObject);
				clonedList = clonedList.subList(index + 1, clonedList.size());
			}
		}

		// Sort in reverse order to remove last index first and avoid shift
		Collections.sort(playListAudioObjects,
				new PlayListAudioObjectComparator());

		for (IPlayListAudioObject plao : playListAudioObjects) {
			this.audioObjects.remove(plao.getPosition());
		}
		notifyAudioObjectsRemoved(playListAudioObjects);
	}

	/**
	 * Replaces content
	 * 
	 * @param content
	 */
	@SuppressWarnings("unchecked")
	protected void replaceContent(final Collection<IAudioObject> content,
			final IAudioObjectComparator comparator,
			final IBeanFactory beanFactory) {
		if (this.audioObjects == null) {
			this.audioObjects = new PointedList<IAudioObject>();
			this.audioObjects.addAll(0, content);
			notifyAudioObjectsAdded(0, content);
		} else {
			Collection<IAudioObject> toRemove = CollectionUtils.subtract(
					this.audioObjects.getList(), content);
			Collection<IAudioObject> toAdd = CollectionUtils.subtract(content,
					this.audioObjects.getList());

			remove(toRemove);
			this.audioObjects.addAll(this.audioObjects.size(), toAdd);
			notifyAudioObjectsAdded(this.audioObjects.size(), toAdd);
		}

		if (this.columnSorted != null) {
			IColumn<?> column = beanFactory.getBeanByClassName(
					this.columnSorted, IColumn.class);
			if (column != null) {
				sortByColumn(column);
			} else {
				this.audioObjects.sort(comparator);
			}
		} else {
			this.audioObjects.sort(comparator);
		}
	}

	@Override
	protected PointedList<IAudioObject> getAudioObjectsPointedList() {
		return this.audioObjects;
	}

	/**
	 * @return pointer
	 */
	protected int getPointer() {
		return this.pointer;
	}

	@Override
	public void saveInternalData() {
		this.pointer = this.audioObjects != null
				&& this.audioObjects.getPointer() != null ? this.audioObjects
				.getPointer() : 0;
	}
}
