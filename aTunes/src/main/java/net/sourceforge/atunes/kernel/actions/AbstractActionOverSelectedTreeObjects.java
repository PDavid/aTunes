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
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import net.sourceforge.atunes.gui.model.TreeObjectsSource;
import net.sourceforge.atunes.model.TreeObject;

public abstract class AbstractActionOverSelectedTreeObjects<T extends TreeObject> extends AbstractAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2396109319433549043L;

	private static Map<Component, TreeObjectsSource> registeredComponents = new HashMap<Component, TreeObjectsSource>();

    private Class<T> objectsClass;

    public AbstractActionOverSelectedTreeObjects(Class<T> objectsClass) {
        super();
        this.objectsClass = objectsClass;
    }

    public AbstractActionOverSelectedTreeObjects(String name, Class<T> objectsClass) {
        super(name);
        this.objectsClass = objectsClass;
    }

    public AbstractActionOverSelectedTreeObjects(String name, Icon icon, Class<T> objectsClass) {
        super(name, icon);
        this.objectsClass = objectsClass;
    }

    static final void addRegisteredComponent(Component source, TreeObjectsSource objectsSource) {
        registeredComponents.put(source, objectsSource);
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
    protected T preprocessObject(T treeObject) {
        return treeObject;
    }

    protected abstract void performAction(List<T> objects);

    @Override
    public final void actionPerformed(ActionEvent e) {
        // Get objects source from registered components
        Component eventSource = (Component) e.getSource();
        TreeObjectsSource objectsSource = registeredComponents.get(eventSource);

        if (objectsSource == null) {
            return;
        }

        List<TreeObject> treeObjects = objectsSource.getSelectedTreeObjects();

        if (treeObjects == null || treeObjects.isEmpty()) {
            return;
        }

        List<T> selectedTreeObjects = new ArrayList<T>();

        for (TreeObject ao : treeObjects) {
            if (objectsClass.isAssignableFrom(ao.getClass())) {
                @SuppressWarnings("unchecked")
                T processedTreeObject = preprocessObject((T) ao);
                if (processedTreeObject != null) {
                    selectedTreeObjects.add(processedTreeObject);
                }
            }
        }

        // Call to perform action
        performAction(selectedTreeObjects);
    }

}
