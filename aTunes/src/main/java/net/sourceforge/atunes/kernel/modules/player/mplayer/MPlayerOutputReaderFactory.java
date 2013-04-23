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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStateRadio;

/**
 * Selectes which output reader use depending on type audio object
 * 
 * @author alex
 * 
 */
public class MPlayerOutputReaderFactory {

	private MPlayerEngine mplayerEngine;

	private IStateRadio stateRadio;

	private IPlayListHandler playListHandler;

	private ILocalAudioObjectValidator localAudioObjectValidator;

	private IContextHandler contextHandler;

	/**
	 * @param mplayerEngine
	 */
	public void setMplayerEngine(final MPlayerEngine mplayerEngine) {
		this.mplayerEngine = mplayerEngine;
	}

	/**
	 * @param stateRadio
	 */
	public void setStateRadio(final IStateRadio stateRadio) {
		this.stateRadio = stateRadio;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(
			final ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}

	/**
	 * @param contextHandler
	 */
	public void setContextHandler(final IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}

	AbstractMPlayerOutputReader newInstance(final MPlayerProcess process,
			final IAudioObject ao) {
		if (ao instanceof ILocalAudioObject) {
			return new AudioFileMPlayerOutputReader(this.mplayerEngine,
					process, (ILocalAudioObject) ao,
					this.localAudioObjectValidator);
		} else if (ao instanceof IRadio) {
			return new RadioMPlayerOutputReader(this.mplayerEngine, process,
					(IRadio) ao, this.stateRadio, this.playListHandler,
					this.contextHandler);
		} else if (ao instanceof IPodcastFeedEntry) {
			return new PodcastFeedEntryMPlayerOutputReader(this.mplayerEngine,
					process, (IPodcastFeedEntry) ao);
		} else {
			throw new IllegalArgumentException(
					"audio object is not from type AudioFile, Radio or PodcastFeedEntry");
		}
	}
}
