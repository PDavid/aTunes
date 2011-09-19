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

package net.sourceforge.atunes.model;


/**
 * A factory to create dialogs of properties
 * @author alex
 *
 */
public interface IAudioObjectPropertiesDialogFactory {

	/**
	 * New instance.
	 * 
	 * @param a
	 * @param owner
	 * @param state
	 * @param frame
	 * @param osManager
	 * @return
	 */
	public IAudioObjectPropertiesDialog newInstance(IAudioObject a);

	/**
	 * @param frame the frame to set
	 */
	public void setFrame(IFrame frame);

	/**
	 * @param state the state to set
	 */
	public void setState(IState state);

	/**
	 * @param osManager the osManager to set
	 */
	public void setOsManager(IOSManager osManager);

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler);
}