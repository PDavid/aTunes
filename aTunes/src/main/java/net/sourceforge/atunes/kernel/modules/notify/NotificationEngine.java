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

package net.sourceforge.atunes.kernel.modules.notify;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IState;

public interface NotificationEngine {

	/**
	 * @return true if engine is supported and available
	 */
	public boolean isEngineAvailable();
	
    /**
     * @return name of notification engine
     */
    public String getName();

    /**
     * Called to show a notification
     * @param audioObject
     */
    public void showNotification(IAudioObject audioObject);
    
    /**
     * Called to free any resources held by notification engine
     */
    public void disposeNotifications();
    
    /**
     * Update notification engine when settings change
     * @param newState
     */
    public void updateNotification(IState newState);
    
    /**
     * Brief description of notification engine
     * @return
     */
    public String getDescription();
    
    /**
     * URL to find more information
     * @return
     */
    public String getUrl();

}
