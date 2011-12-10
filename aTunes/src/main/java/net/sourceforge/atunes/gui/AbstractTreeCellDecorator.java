/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.gui;

import java.awt.Component;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.ReflectionUtils;

public abstract class AbstractTreeCellDecorator<T extends Component, U> {
	
	private IState state;
	
	private ILookAndFeelManager lookAndFeelManager;
	
	private Class<?> componentClass;
	
	private Class<?> valueClass;
	
	public AbstractTreeCellDecorator() {
		Type[] types = ReflectionUtils.getTypeArgumentsOfParameterizedType(this.getClass());
		this.componentClass = (Class<?>) types[0];
		if (types[1] instanceof Class<?>) {
			this.valueClass = (Class<?>) types[1];
		} else if (types[1] instanceof ParameterizedType) {
			this.valueClass = (Class<?>) ((ParameterizedType)types[1]).getRawType();
		}
	}
	
	/**
	 * @param state
	 */
	public final void setState(IState state) {
		this.state = state;
	}
	
	/**
	 * @param lookAndFeelManager
	 */
	public final void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
	
	/**
	 * @return
	 */
	protected final IState getState() {
		return state;
	}
	
	/**
	 * @return
	 */
	protected final ILookAndFeelManager getLookAndFeelManager() {
		return lookAndFeelManager;
	}
	
	/**
	 * @return current look and feel
	 */
	protected final ILookAndFeel getLookAndFeel() {
		return lookAndFeelManager.getCurrentLookAndFeel();
	}

	/**
	 * @return
	 */
	public final Class<?> getComponentClass() {
		return componentClass;
	}
	
	/**
	 * @return
	 */
	public final Class<?> getValueClass() {
		return valueClass;
	}
	
    /**
     * Decorates a tree cell component in some way given the user object
     * @param component
     * @param userObject
     * @param isSelected
     * @return
     */
    public abstract Component decorateTreeCellComponent(T component, U userObject, boolean isSelected);

}
