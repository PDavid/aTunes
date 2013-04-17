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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;

/**
 * Gets internal picture of audio object
 * 
 * @author alex
 * 
 */
public final class GetInsidePictureBackgroundWorker extends
		BackgroundWorker<ImageIcon, Void> {

	private List<ILocalAudioObject> audioFiles;

	private EditTagDialogController controller;

	private ILocalAudioObjectImageHandler localAudioObjectImageHandler;

	/**
	 * @param localAudioObjectImageHandler
	 */
	public void setLocalAudioObjectImageHandler(
			final ILocalAudioObjectImageHandler localAudioObjectImageHandler) {
		this.localAudioObjectImageHandler = localAudioObjectImageHandler;
	}

	/**
	 * @param controller
	 */
	public void setController(final EditTagDialogController controller) {
		this.controller = controller;
	}

	/**
	 * @param audioFiles
	 */
	public void setAudioFiles(final List<ILocalAudioObject> audioFiles) {
		this.audioFiles = audioFiles;
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected ImageIcon doInBackground() {
		return this.localAudioObjectImageHandler.getInsidePicture(
				this.audioFiles.get(0), Constants.DIALOG_LARGE_IMAGE_WIDTH,
				Constants.DIALOG_LARGE_IMAGE_HEIGHT);
	}

	@Override
	protected void done(final ImageIcon cover) {
		// Check if it's the right dialog
		if (this.controller.audioFilesEditing.equals(this.audioFiles)) {
			this.controller.getEditTagDialog().getCover().setIcon(cover);
			this.controller.getEditTagDialog().getCoverButton()
					.setEnabled(true);
			this.controller.getEditTagDialog().getRemoveCoverButton()
					.setEnabled(true);
			this.controller.getEditTagDialog().getOkButton().setEnabled(true);
		}
	}
}