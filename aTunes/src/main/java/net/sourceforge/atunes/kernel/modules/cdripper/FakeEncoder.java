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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.utils.Logger;

import org.apache.commons.io.FileUtils;

/**
 * A fake encoder for simulation
 * @author alex
 *
 */
public class FakeEncoder extends AbstractEncoder {

	/**
	 * Default constructor
	 */
	public FakeEncoder() {
		super("fake", new String[] {"0"}, "0", "Fake");
	}

	@Override
	public boolean encode(final File originalFile, final File encodedFile) {
		Logger.info("Encoding with fake encoder");
		Logger.info("Original file: ", net.sourceforge.atunes.utils.FileUtils.getPath(originalFile));
		Logger.info("Encoded file: ", net.sourceforge.atunes.utils.FileUtils.getPath(encodedFile));

		// Create file
		try {
			FileUtils.touch(encodedFile);
		} catch (IOException e1) {
			Logger.error(e1);
		}

		// Simulate encoding to wav for an amount of time
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
	public boolean setTag(final File file, final int trackNumber, final CDMetadata metadata) {
		Logger.info("Title: ", metadata.getTitle(trackNumber));
		Logger.info("Track number: ", trackNumber);
		Logger.info("Artist: ", metadata.getArtist(trackNumber));
		Logger.info("Composer: ", metadata.getComposer(trackNumber));
		Logger.info("Disc number: ", metadata.getDisc());
		return true;
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean testEncoder() {
		return true;
	}
}
