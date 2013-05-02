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

import java.util.Map;

import net.sourceforge.atunes.model.INavigationViewSorter;

/**
 * @author alex
 * 
 * @param <T>
 */
public abstract class TreeLevel<T> {

	private TreeLevel<?> nextLevel;

	private TreeFilterMatch match;

	/**
	 * @param nextLevel
	 */
	public void setNextLevel(final TreeLevel<?> nextLevel) {
		this.nextLevel = nextLevel;
	}

	/**
	 * @return
	 */
	public final TreeLevel<?> getNextLevel() {
		return this.nextLevel;
	}

	/**
	 * @return
	 */
	public final TreeFilterMatch getMatch() {
		return this.match;
	}

	/**
	 * @param match
	 *            the match to set
	 */
	public final void setMatch(final TreeFilterMatch match) {
		this.match = match;
	}

	@SuppressWarnings("unchecked")
	final boolean filterMatchAtLevel(final Object object, final String filter) {
		return matches((T) object, filter);
	}

	@SuppressWarnings("unchecked")
	final Map<String, ?> getNextLevelObjects(final Object levelObject) {
		return getChildObjects((T) levelObject);
	}

	abstract boolean matches(T object, String filter);

	abstract Map<String, ?> getChildObjects(T object);

	abstract INavigationViewSorter getSorter();

}
