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

package net.sourceforge.atunes.model;


/**
 * A component responsible of notify user of audio objects being played
 * @author alex
 *
 */
public interface INotificationEngine {

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
    public void updateNotification(IStateUI newState);
    
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
