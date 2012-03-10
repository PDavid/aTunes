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

package net.sourceforge.atunes.kernel;

import java.awt.Component;

import net.sourceforge.atunes.model.IController;
import net.sourceforge.atunes.model.IState;

public abstract class AbstractSimpleController<T extends Component> implements IController {

    private T componentControlled;

    /**
     * State of app
     */
    private IState state;

    /**
     * Default constructor
     */
    public AbstractSimpleController() {
    }

    /**
     * Instantiates a new controller.
     */
    public AbstractSimpleController(T componentControlled, IState state) {
        this.componentControlled = componentControlled;
        this.state = state;
    }
    
    /**
     * @param componentControlled
     */
    public final void setComponentControlled(T componentControlled) {
		this.componentControlled = componentControlled;
	}
    
    /**
     * @param state
     */
    public final void setState(IState state) {
		this.state = state;
	}
    
    /**
     * @return
     */
    public T getComponentControlled() {
        return componentControlled;
    }
    
    /**
     * @return
     */
    protected IState getState() {
    	return state;
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
