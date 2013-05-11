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

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;

/**
 * The Class FadeAwayRunnable.
 */
class FadeAwayRunnable implements Runnable {

	/** The command writer. */
	private final MPlayerCommandWriter commandWriter;

	/** The initial volume. */
	private final int initialVolume;

	/** Flag to interrupt this runnable */
	private boolean interrupted = false;

	/**
	 * MPlayerHandler instance
	 */
	private final MPlayerEngine handler;

	/**
	 * Instantiates a new fade away runnable.
	 * 
	 * @param process
	 * @param initialVolume
	 * @param handler
	 * @param osManager
	 */
	FadeAwayRunnable(final MPlayerProcess process, final int initialVolume,
			final MPlayerEngine handler, final IOSManager osManager) {
		this.initialVolume = initialVolume;
		this.handler = handler;
		this.commandWriter = process.newCommandWriter(osManager);
	}

	@Override
	public void run() {
		Logger.debug("Fade away runnable started");
		try {
			int fadeVolume = this.initialVolume;
			int fadeStep = 0;
			// Lower volume until it reaches 0 or 100 volume reductions
			// or object is interrupted
			while (!this.interrupted && fadeStep < 100 && fadeVolume > 0) {
				fadeVolume = fadeVolume - 1;
				if (!this.handler.isMute()) {
					this.commandWriter.sendVolumeCommand(fadeVolume);
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					Logger.error(e);
				}
				fadeStep++;
			}
		} finally {
			Logger.debug("Fade away runnable finished");
		}
		if (!this.interrupted) {
			// Notify finish to MPlayerHandler
			this.handler.finishedFadeAway();
		}
	}

	/**
	 * This method is called when fade away must stop immediately
	 */
	void finish() {
		this.interrupted = true;
		this.handler.finishedFadeAway();
	}
}
