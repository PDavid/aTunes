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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.utils.Logger;

import org.apache.commons.io.FileUtils;

/**
 * Fake cd to wav converter used for simulation
 * 
 * @author alex
 * 
 */
public class FakeCDToWavConverter extends AbstractCdToWavConverter {

	@Override
	public boolean cdda2wav(final int track, final File file) {
		Logger.info("Converting with fake wav converter: Track ", track,
				" File ", net.sourceforge.atunes.utils.FileUtils.getPath(file));
		return waitConversion(file);
	}

	@Override
	public boolean cdda2wav(final int track, final File file,
			final boolean useParanoia) {
		Logger.info("Converting with fake wav converter: Track ", track,
				" File ", net.sourceforge.atunes.utils.FileUtils.getPath(file),
				" useParanoia ", useParanoia);
		return waitConversion(file);
	}

	/**
	 * @param file
	 * @return true
	 */
	private boolean waitConversion(final File file) {
		// Create file
		try {
			FileUtils.touch(file);
		} catch (IOException e) {
			Logger.error(e);
		}

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
		fakeCdInfo.setTracks(50); // Use a big number of tracks to simulate not
									// retrieving all titles
		fakeCdInfo.setTitles(getEmptyList(0));
		fakeCdInfo.setArtists(getEmptyList(9));
		fakeCdInfo.setComposers(getEmptyList(9));
		fakeCdInfo.setDurations(getEmptyList(9));
		return fakeCdInfo;
	}

	private List<String> getEmptyList(final int items) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < items; i++) {
			list.add("");
		}
		return list;
	}
}
