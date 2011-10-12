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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialog;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IState;

public class AudioObjectPropertiesDialogFactory implements IAudioObjectPropertiesDialogFactory {

	private IFrame frame;
	
	private IState state;
	
	private IOSManager osManager;
	
	private IPlayListHandler playListHandler;
	
	private IRepositoryHandler repositoryHandler;
	
    @Override
	public IAudioObjectPropertiesDialog newInstance(IAudioObject a, ILookAndFeelManager lookAndFeelManager, IPlayerHandler playerHandler) {
    	AudioObjectPropertiesDialog dialog = null;
    	if (a instanceof IPodcastFeedEntry) {
    		dialog = new PodcastFeedEntryPropertiesDialog((IPodcastFeedEntry) a, frame, state, lookAndFeelManager);
    	} else if (a instanceof IRadio) {
    		dialog = new RadioPropertiesDialog((Radio) a, frame, lookAndFeelManager);
    	} else if (a instanceof AudioFile) {
    		dialog = new AudioFilePropertiesDialog((AudioFile) a, state, frame, osManager, playListHandler, lookAndFeelManager, repositoryHandler, playerHandler);
    	}
    	if (dialog != null) {
    		dialog.setAudioObject(a);
    	}
    	return dialog;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.properties.IAudioObjectPropertiesDialogFactory#setFrame(net.sourceforge.atunes.model.IFrame)
	 */
	@Override
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.properties.IAudioObjectPropertiesDialogFactory#setState(net.sourceforge.atunes.model.IState)
	 */
	@Override
	public void setState(IState state) {
		this.state = state;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.properties.IAudioObjectPropertiesDialogFactory#setOsManager(net.sourceforge.atunes.model.IOSManager)
	 */
	@Override
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	@Override
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
	
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
}
