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
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;

/**
 * Selects which output reader use depending on type audio object
 * 
 * @author alex
 * 
 */
public class MPlayerOutputReaderFactory {

	private MPlayerEngine mplayerEngine;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param mplayerEngine
	 */
	public void setMplayerEngine(final MPlayerEngine mplayerEngine) {
		this.mplayerEngine = mplayerEngine;
	}

	AbstractMPlayerOutputReader newInstance(final MPlayerProcess process,
			final IAudioObject ao) {
		AbstractMPlayerOutputReader reader = null;
		if (ao instanceof ILocalAudioObject) {
			reader = this.beanFactory
					.getBean(AudioFileMPlayerOutputReader.class);
			((AudioFileMPlayerOutputReader) reader)
					.setAudioFile((ILocalAudioObject) ao);
		} else if (ao instanceof IRadio) {
			reader = this.beanFactory.getBean(RadioMPlayerOutputReader.class);
			((RadioMPlayerOutputReader) reader).setRadio((IRadio) ao);
		} else if (ao instanceof IPodcastFeedEntry) {
			reader = this.beanFactory
					.getBean(PodcastFeedEntryMPlayerOutputReader.class);
			((PodcastFeedEntryMPlayerOutputReader) reader)
					.setPodcastFeedEntry((IPodcastFeedEntry) ao);
		} else {
			throw new IllegalArgumentException(
					"audio object is not from type AudioFile, Radio or PodcastFeedEntry");
		}
		reader.setProcess(process);
		reader.setEngine(this.mplayerEngine);
		return reader;
	}
}
