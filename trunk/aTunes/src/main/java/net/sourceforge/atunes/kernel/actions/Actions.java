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

package net.sourceforge.atunes.kernel.actions;

import java.awt.Component;

import javax.swing.JMenuItem;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectsSource;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ITreeObjectsSource;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public final class Actions {

    private static IState state = Context.getBean(IState.class);
    
    private Actions() {
    }

    /**
     * Returns an action object
     * 
     * @param clazz
     * @return
     */
    public static <T extends CustomAbstractAction> CustomAbstractAction getAction(Class<T> clazz) {
    	CustomAbstractAction action = null;
    	try {
    		action = clazz.newInstance();
    		action.setState(state);
    		action.initialize();
    	} catch (InstantiationException e) {
    		Logger.error(e);
    	} catch (IllegalAccessException e) {
    		Logger.error(e);
    	}
    	return action;
    }

    /**
     * Returns an action object of type ActionOverSelectedObjects and binds to a
     * component and a objects source
     * 
     * @param clazz
     * @param actionSource
     * @param selectedObjectsSource
     * @return
     */
    public static <T extends AbstractActionOverSelectedObjects<? extends IAudioObject>> CustomAbstractAction getActionAndBind(Class<T> clazz, Component actionSource, IAudioObjectsSource selectedObjectsSource) {
        AbstractActionOverSelectedObjects.addRegisteredComponent(actionSource, selectedObjectsSource);
        return getAction(clazz);
    }
    
    /**
     * Returns an action object of type ActionOverSelectedTreeObjects and binds to a
     * component and a objects source
     * 
     * @param clazz
     * @param actionSource
     * @param selectedObjectsSource
     * @return
     */
    public static <T extends AbstractActionOverSelectedTreeObjects<? extends ITreeObject<? extends IAudioObject>>> CustomAbstractAction getTreeActionAndBind(Class<T> clazz, Component actionSource, ITreeObjectsSource selectedObjectsSource) {
        AbstractActionOverSelectedTreeObjects.addRegisteredComponent(actionSource, selectedObjectsSource);
        return getAction(clazz);
    }
    
    /**
     * Returns a menu item bound to an Action
     * 
     * @param clazz
     * @param actionId
     * @param audioObjectsSource
     * @return
     */
    public static JMenuItem getMenuItemForAction(Class<? extends AbstractActionOverSelectedObjects<? extends IAudioObject>> clazz, IAudioObjectsSource audioObjectsSource) {
        JMenuItem menuItem = new JMenuItem();
        menuItem.setAction(Actions.getActionAndBind(clazz, menuItem, audioObjectsSource));
        return menuItem;
    }

    /**
     * Returns a menu item bound to an Action
     * 
     * @param clazz
     * @param actionId
     * @param audioObjectsSource
     * @return
     */
    public static JMenuItem getMenuItemForTreeAction(Class<? extends AbstractActionOverSelectedTreeObjects<? extends ITreeObject<? extends IAudioObject>>> clazz, ITreeObjectsSource audioObjectsSource) {
        JMenuItem menuItem = new JMenuItem();
        menuItem.setAction(Actions.getTreeActionAndBind(clazz, menuItem, audioObjectsSource));
        return menuItem;
    }
}
