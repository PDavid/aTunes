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
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectsSource;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public abstract class AbstractActionOverSelectedObjects<T extends IAudioObject> extends CustomAbstractAction {

    private static final long serialVersionUID = 1673432955671008277L;

    private static Map<Component, IAudioObjectsSource> registeredComponents = new HashMap<Component, IAudioObjectsSource>();

    private Class<T> objectsClass;

    public AbstractActionOverSelectedObjects(Class<T> objectsClass) {
        super();
        this.objectsClass = objectsClass;
    }

    public AbstractActionOverSelectedObjects(String name, Class<T> objectsClass) {
        super(name);
        this.objectsClass = objectsClass;
    }

    public AbstractActionOverSelectedObjects(String name, Icon icon, Class<T> objectsClass) {
        super(name, icon);
        this.objectsClass = objectsClass;
    }

    static final void addRegisteredComponent(Component source, IAudioObjectsSource objectsSource) {
        registeredComponents.put(source, objectsSource);
    }

    /**
     * Given an audio object performs a preprocess returning audio object to
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

    protected abstract void performAction(List<T> objects);

    @Override
    public final void actionPerformed(ActionEvent e) {
        // Get objects source from registered components
        Component eventSource = (Component) e.getSource();
        IAudioObjectsSource objectsSource = registeredComponents.get(eventSource);

        if (objectsSource == null) {
            return;
        }

        List<IAudioObject> audioObjects = objectsSource.getSelectedAudioObjects();

        if (audioObjects == null || audioObjects.isEmpty()) {
            return;
        }

        List<T> selectedObjects = new ArrayList<T>();

        for (IAudioObject ao : audioObjects) {
            if (objectsClass.isAssignableFrom(ao.getClass())) {
                @SuppressWarnings("unchecked")
                T processedAudioObject = preprocessObject((T) ao);
                if (processedAudioObject != null) {
                    selectedObjects.add(processedAudioObject);
                }
            }
        }

        // Call to perform action
        performAction(selectedObjects);
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        if (selection.isEmpty()) {
            return false;
        }

        for (IAudioObject ao : selection) {
            if (!(this.objectsClass.isAssignableFrom(ao.getClass()))) {
                return false;
            }
        }
        return true;
    }

}
