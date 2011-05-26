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

package net.sourceforge.atunes.kernel.actions;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;

import net.sourceforge.atunes.gui.model.AudioObjectsSource;
import net.sourceforge.atunes.gui.model.TreeObjectsSource;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public final class Actions {

    /**
     * Map of actions.
     */
    private static Map<ActionInstance, AbstractAction> actions = new HashMap<ActionInstance, AbstractAction>();

    private Actions() {
    }

    /**
     * Returns an action object
     * 
     * @param clazz
     * @return
     */
    public static <T extends AbstractAction> AbstractAction getAction(Class<T> clazz) {
        return getAction(clazz, null);
    }

    /**
     * Returns an action object
     * 
     * @param clazz
     * @return
     */
    public static <T extends AbstractAction> AbstractAction getAction(Class<T> clazz, String actionId) {
        ActionInstance actionInstance = new ActionInstance(clazz, actionId);
        AbstractAction action = actions.get(actionInstance);
        if (action == null) {
            try {
                action = clazz.newInstance();
                action.setActionId(actionId);
                action.setProperties(action.getProperties(actionId));
                action.initialize();
                actions.put(actionInstance, action);
            } catch (InstantiationException e) {
                Logger.internalError(e);
            } catch (IllegalAccessException e) {
                Logger.internalError(e);
            }
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
    public static <T extends AbstractActionOverSelectedObjects<? extends AudioObject>> AbstractAction getActionAndBind(Class<T> clazz, Component actionSource, AudioObjectsSource selectedObjectsSource) {
        AbstractActionOverSelectedObjects.addRegisteredComponent(actionSource, selectedObjectsSource);
        return getAction(clazz, null);
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
    public static <T extends AbstractActionOverSelectedTreeObjects<? extends TreeObject>> AbstractAction getTreeActionAndBind(Class<T> clazz, Component actionSource, TreeObjectsSource selectedObjectsSource) {
        AbstractActionOverSelectedTreeObjects.addRegisteredComponent(actionSource, selectedObjectsSource);
        return getAction(clazz, null);
    }
    

    /**
     * Returns an action object of type ActionOverSelectedObjects and binds to a
     * component and a objects source
     * 
     * @param clazz
     * @param actionId
     * @param actionSource
     * @param selectedObjectsSource
     * @return
     */
    public static <T extends AbstractActionOverSelectedObjects<? extends AudioObject>> AbstractAction getActionAndBind(Class<T> clazz, String actionId, Component actionSource, AudioObjectsSource selectedObjectsSource) {
        AbstractActionOverSelectedObjects.addRegisteredComponent(actionSource, selectedObjectsSource);
        return getAction(clazz, actionId);
    }

    /**
     * Returns a menu item bound to an Action
     * 
     * @param clazz
     * @param actionId
     * @param audioObjectsSource
     * @return
     */
    public static JMenuItem getMenuItemForAction(Class<? extends AbstractActionOverSelectedObjects<? extends AudioObject>> clazz, AudioObjectsSource audioObjectsSource) {
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
    public static JMenuItem getMenuItemForTreeAction(Class<? extends AbstractActionOverSelectedTreeObjects<? extends TreeObject>> clazz, TreeObjectsSource audioObjectsSource) {
        JMenuItem menuItem = new JMenuItem();
        menuItem.setAction(Actions.getTreeActionAndBind(clazz, menuItem, audioObjectsSource));
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
    public static JMenuItem getMenuItemForAction(Class<? extends AbstractActionOverSelectedObjects<? extends AudioObject>> clazz, String actionId, AudioObjectsSource audioObjectsSource) {
        JMenuItem menuItem = new JMenuItem();
        menuItem.setAction(Actions.getActionAndBind(clazz, actionId, menuItem, audioObjectsSource));
        return menuItem;
    }

}
