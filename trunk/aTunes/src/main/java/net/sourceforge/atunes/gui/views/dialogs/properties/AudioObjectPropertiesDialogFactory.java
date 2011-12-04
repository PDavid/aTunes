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
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialog;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
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
	
	private IAudioObjectImageLocator audioObjectImageLocator;
	
	private ILocalAudioObjectValidator localAudioObjectValidator;
	
    @Override
	public IAudioObjectPropertiesDialog newInstance(IAudioObject a, ILookAndFeelManager lookAndFeelManager, IPlayerHandler playerHandler) {
    	AudioObjectPropertiesDialog dialog = null;
    	if (a instanceof IPodcastFeedEntry) {
    		dialog = new PodcastFeedEntryPropertiesDialog((IPodcastFeedEntry) a, frame, state, lookAndFeelManager);
    	} else if (a instanceof IRadio) {
    		dialog = new RadioPropertiesDialog((Radio) a, frame, lookAndFeelManager);
    	} else if (a instanceof ILocalAudioObject) {
    		dialog = new LocalAudioObjectPropertiesDialog((ILocalAudioObject) a, state, frame, osManager, playListHandler, lookAndFeelManager, repositoryHandler, playerHandler, audioObjectImageLocator, localAudioObjectValidator);
    	}
    	if (dialog != null) {
    		dialog.setAudioObject(a);
    	}
    	return dialog;
    }

    /**
     * @param localAudioObjectValidator
     */
    public void setLocalAudioObjectValidator(ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}
    
	@Override
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}

	@Override
	public void setState(IState state) {
		this.state = state;
	}

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
	
	/**
	 * @param audioObjectImageLocator
	 */
	public void setAudioObjectImageLocator(IAudioObjectImageLocator audioObjectImageLocator) {
		this.audioObjectImageLocator = audioObjectImageLocator;
	}
}
