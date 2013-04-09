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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.Logger;

final class FillPictureSwingWorker extends SwingWorker<ImageIcon, Void> {

	private final IBeanFactory beanFactory;

	private final JLabel pictureLabel;
	private final ILocalAudioObject file;

	/**
	 * @param beanFactort
	 * @param pictureLabel
	 * @param file
	 */
	FillPictureSwingWorker(final IBeanFactory beanFactory,
			final JLabel pictureLabel, final ILocalAudioObject file) {
		this.pictureLabel = pictureLabel;
		this.file = file;
		this.beanFactory = beanFactory;
	}

	@Override
	protected ImageIcon doInBackground() {
		return this.beanFactory.getBean(IAudioObjectImageLocator.class)
				.getImage(this.file, Constants.DIALOG_IMAGE_SIZE);
	}

	@Override
	protected void done() {
		ImageIcon cover;
		try {
			cover = get();
			this.pictureLabel.setIcon(cover);
			this.pictureLabel.setVisible(cover != null);
		} catch (InterruptedException e) {
			Logger.error(e);
		} catch (ExecutionException e) {
			Logger.error(e);
		}
	}
}