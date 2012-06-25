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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.File;
import java.util.ArrayList;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Fake cd to wav converter used for simulation
 * @author alex
 *
 */
public class FakeCDToWavConverter extends AbstractCdToWavConverter {

	@Override
	public boolean cdda2wav(int track, File file) {
		Logger.info("Converting with fake wav converter: Track ", track, " File ", file.getAbsolutePath());
		return waitConversion();
	}

	@Override
	public boolean cdda2wav(int track, File file, boolean useParanoia) {
		Logger.info("Converting with fake wav converter: Track ", track, " File ", file.getAbsolutePath(), " useParanoia ", useParanoia);
		return waitConversion();
	}

	/**
	 * @return
	 */
	private boolean waitConversion() {
		// Simulate converting to wav for an amount of time
		for (int i = 0; i <= 10; i++) {
			final int progress = i * 10; 
			try {
				Thread.sleep(200);
				GuiUtils.callInEventDispatchThread(new Runnable() {
					@Override
					public void run() {
						getListener().notifyProgress(progress);
					}
				});
			} catch (InterruptedException e) {
				Logger.error(e);
				return false;
			}
		}
		return true;
	}

	@Override
	public CDInfo retrieveDiscInformation() {
		CDInfo fakeCdInfo = new CDInfo();
		fakeCdInfo.setAlbum("Thriller");
		fakeCdInfo.setArtist("Michael Jackson");
		fakeCdInfo.setDuration("42:19");
		fakeCdInfo.setGenre("Pop");
		fakeCdInfo.setID("1");
		fakeCdInfo.setTracks(9);
		fakeCdInfo.setTitles(new ArrayList<String>());
		fakeCdInfo.setArtists(new ArrayList<String>());
		fakeCdInfo.setComposers(new ArrayList<String>());
		fakeCdInfo.setDurations(new ArrayList<String>());
		return fakeCdInfo;
	}
}
