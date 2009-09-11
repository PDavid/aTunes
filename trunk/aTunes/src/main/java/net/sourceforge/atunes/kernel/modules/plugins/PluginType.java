/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.plugins;

import org.commonjukebox.plugins.PluginListener;

public class PluginType {

    /**
     * The class type of a set of plugins
     */
    private String classType;

    /**
     * Listener of plugins
     */
    private PluginListener listener;

    /**
     * Indicates if application must be restarted after a change in
     * configuration or an activation or deactivation of a plugin of this type
     */
    private boolean applicationNeedsRestart;

    /**
     * @param classType
     * @param listener
     * @param applicationNeedsRestart
     */
    protected PluginType(String classType, PluginListener listener, boolean applicationNeedsRestart) {
        this.classType = classType;
        this.listener = listener;
        this.applicationNeedsRestart = applicationNeedsRestart;
    }

    /**
     * @return the classType
     */
    protected String getClassType() {
        return classType;
    }

    /**
     * @return the listener
     */
    protected PluginListener getListener() {
        return listener;
    }

    /**
     * @return the applicationNeedsRestart
     */
    protected boolean isApplicationNeedsRestart() {
        return applicationNeedsRestart;
    }

}
