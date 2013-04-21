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

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;

/**
 * Fetches image to show in properties dialog
 * 
 * @author alex
 * 
 */
public final class FillPictureBackgroundWorker extends
		BackgroundWorker<ImageIcon, Void> {

	private JLabel pictureLabel;
	private ILocalAudioObject file;

	/**
	 * @param pictureLabel
	 */
	public void setPictureLabel(final JLabel pictureLabel) {
		this.pictureLabel = pictureLabel;
	}

	/**
	 * @param file
	 */
	public void setFile(final ILocalAudioObject file) {
		this.file = file;
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(final List<Void> chunks) {
	}

	@Override
	protected ImageIcon doInBackground() {
		return getBeanFactory().getBean(ILocalAudioObjectImageHandler.class)
				.getImage(FillPictureBackgroundWorker.this.file,
						Constants.DIALOG_IMAGE_SIZE);
	}

	@Override
	protected void done(final ImageIcon result) {
		this.pictureLabel.setIcon(result);
		this.pictureLabel.setVisible(result != null);
	}
}