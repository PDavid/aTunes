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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IAudioObject;

/**
 * Gets image and paints in a cover of full screen window
 * 
 * @author alex
 * 
 */
public final class PaintCoversBackgroundWorker extends
		BackgroundWorker<Void, Void> {

	private Cover cover;
	private IAudioObject audioObject;
	private int imageSize;

	private FullScreenCoverImageRetriever fullScreenCoverImageRetriever;

	/**
	 * @param fullScreenCoverImageRetriever
	 */
	public void setFullScreenCoverImageRetriever(
			final FullScreenCoverImageRetriever fullScreenCoverImageRetriever) {
		this.fullScreenCoverImageRetriever = fullScreenCoverImageRetriever;
	}

	/**
	 * Calls worker to retrieve cover
	 * 
	 * @param cover
	 * @param audioObject
	 * @param imageSize
	 */
	void getCover(final Cover cover, final IAudioObject audioObject,
			final int imageSize) {
		this.cover = cover;
		this.audioObject = audioObject;
		this.imageSize = imageSize;
		execute();
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected Void doInBackground() {
		ImageIcon image = this.fullScreenCoverImageRetriever
				.getPicture(this.audioObject);

		if (this.cover != null) {
			if (image == null) {
				this.cover.setImage(null, 0, 0);
			} else if (this.audioObject == null) {
				this.cover.setImage(Images.getImage(Images.APP_LOGO_300)
						.getImage(), this.imageSize, this.imageSize);
			} else {
				this.cover.setImage(image.getImage(), this.imageSize,
						this.imageSize);
			}
		}
		return null;
	}

	@Override
	protected void done(final Void result) {
		if (this.cover != null) {
			this.cover.repaint();
		}
	}
}