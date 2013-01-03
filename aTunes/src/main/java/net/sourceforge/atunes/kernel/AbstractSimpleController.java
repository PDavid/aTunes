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

package net.sourceforge.atunes.kernel;

import java.awt.Component;

import net.sourceforge.atunes.model.IController;

/**
 * Base controller for UI components
 * @author alex
 *
 * @param <T>
 */
public abstract class AbstractSimpleController<T extends Component> implements IController {

    private T componentControlled;

    /**
     * Default constructor
     */
    public AbstractSimpleController() {
    }

    /**
     * Instantiates a new controller.
     * @param componentControlled
     */
    public AbstractSimpleController(T componentControlled) {
        this.componentControlled = componentControlled;
    }
    
    /**
     * @param componentControlled
     */
    public final void setComponentControlled(T componentControlled) {
		this.componentControlled = componentControlled;
	}
    
    /**
     * @return
     */
    public T getComponentControlled() {
        return componentControlled;
    }
    
    @Override
    public void addBindings() {
    }

    @Override
    public void addStateBindings() {
    }
    
    @Override
    public void notifyReload() {
    }
}
