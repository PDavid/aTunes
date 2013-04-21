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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.model.ImageSize;

/**
 * Retrieves image to show in tooltip
 * 
 * @author alex
 * 
 */
public final class ExtendedToolTipGetAndSetImageBackgroundWorker extends
		BackgroundWorker<ImageIcon, Void> {

	private IWebServicesHandler webServicesHandler;

	private Object currentObject;

	private ExtendedTooltipContent extendedTooltipContent;

	/**
	 * @param extendedTooltipContent
	 */
	public void setExtendedTooltipContent(
			final ExtendedTooltipContent extendedTooltipContent) {
		this.extendedTooltipContent = extendedTooltipContent;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param currentObject
	 */
	public void setCurrentObject(final Object currentObject) {
		this.currentObject = currentObject;
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(final List<Void> chunks) {
	}

	@Override
	protected ImageIcon doInBackground() {
		// Get image for albums
		if (this.currentObject instanceof ITreeObject) {
			if (this.currentObject instanceof IArtist) {
				IArtist a = (IArtist) this.currentObject;
				return this.webServicesHandler.getArtistImage(a.getName());
			} else if (this.currentObject instanceof IAlbum) {
				return getBeanFactory().getBean(
						ILocalAudioObjectImageHandler.class).getImage(
						(IAlbum) this.currentObject, ImageSize.SIZE_MAX);
			}
		}
		return null;
	}

	@Override
	protected void done(final ImageIcon result) {
		// Set image in tooltip when done (tooltip can be not visible then)
		if (this.extendedTooltipContent.getCurrentExtendedToolTipContent() != null
				&& this.extendedTooltipContent
						.getCurrentExtendedToolTipContent().equals(
								this.currentObject)) {
			this.extendedTooltipContent.setImage(result);
		}
	}
}