/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.controllers.model;

import java.awt.Component;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

public abstract class Controller<T extends Component> {

    private static Logger logger = new Logger();

    private T componentControlled;

    /**
     * Instantiates a new controller.
     */
    public Controller(T componentControlled) {
        this.componentControlled = componentControlled;
        logger.debug(LogCategories.CONTROLLER, StringUtils.getString("Creating ", this.getClass().getSimpleName()));
    }

    public T getComponentControlled() {
        return componentControlled;
    }

    /**
     * Adds the bindings.
     */
    protected abstract void addBindings();

    /**
     * Adds the state bindings.
     */
    protected abstract void addStateBindings();

    /**
     * Notify reload.
     */
    protected abstract void notifyReload();

    /**
     * Return logger
     * 
     * @return
     */
    protected Logger getLogger() {
        return logger;
    }
}
