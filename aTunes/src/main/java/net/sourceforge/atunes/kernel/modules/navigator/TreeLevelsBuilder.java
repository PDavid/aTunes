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

import java.util.List;

import net.sourceforge.atunes.model.IBeanFactory;

/**
 * Builds levels for a tree
 * 
 * @author alex
 * 
 */
public class TreeLevelsBuilder {

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	TreeLevel<?> buildLevels(final List<Class<? extends TreeLevel<?>>> list) {
		TreeLevel<?> head = null;
		TreeLevel<?> tail = null;
		for (Class<? extends TreeLevel<?>> level : list) {
			if (head == null) {
				head = this.beanFactory.getBean(level);
				tail = head;
			} else {
				TreeLevel<?> newTail = this.beanFactory.getBean(level);
				tail.setNextLevel(newTail);
				tail = newTail;
			}
		}
		return head;
	}
}
