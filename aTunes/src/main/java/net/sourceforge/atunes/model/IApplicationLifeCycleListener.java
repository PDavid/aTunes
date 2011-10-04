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

package net.sourceforge.atunes.model;

import java.util.List;


/**
 * Interface for classes that must execute at different phases of application (start, end) (usually
 * handlers)
 * 
 * @author fleax
 * 
 */
public interface IApplicationLifeCycleListener {

    /**
     * Called after application start
     * @param playList 
     */
    public void applicationStarted(List<IAudioObject> playList);
    
    /**
     * Code to be executed when all handlers have been initialized
     */
    public void allHandlersInitialized();
    
    /**
     * Code to ask each component to interact with user after app starts
     * @return order to interact with user or -1 if no user interaction needed
     */
    public int requestUserInteraction();
    
    /**
     * Allow component to interact with user. Calls to this method will be performed in
     * the order returned by each call to requestUserInteraction
     */
    public void doUserInteraction();
    
    /**
     * Code to be executed when application finishes
     */
    public void applicationFinish();

}
