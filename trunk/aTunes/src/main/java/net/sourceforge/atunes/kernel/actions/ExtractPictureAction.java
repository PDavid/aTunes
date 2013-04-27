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

package net.sourceforge.atunes.kernel.actions;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileSelectorDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.sanselan.ImageWriteException;

/**
 * Extracts a picture of an audio object
 * 
 * @author alex
 * 
 */
public class ExtractPictureAction extends
		AbstractActionOverSelectedObjects<ILocalAudioObject> {

	private static final long serialVersionUID = -8618297820141610193L;

	private IDialogFactory dialogFactory;

	private IOSManager osManager;

	private ILocalAudioObjectImageHandler localAudioObjectImageHandler;

	/**
	 * @param localAudioObjectImageHandler
	 */
	public void setLocalAudioObjectImageHandler(
			final ILocalAudioObjectImageHandler localAudioObjectImageHandler) {
		this.localAudioObjectImageHandler = localAudioObjectImageHandler;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * Default constructor
	 */
	public ExtractPictureAction() {
		super(I18nUtils.getString("EXTRACT_PICTURE"));
	}

	@Override
	protected void executeAction(final List<ILocalAudioObject> objects) {
		IFileSelectorDialog dialog = this.dialogFactory
				.newDialog(IFileSelectorDialog.class);
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(final File dir, final String name) {
				return name.toUpperCase().endsWith("PNG");
			}

			@Override
			public String toString() {
				return ".png";
			}
		};
		dialog.setFileFilter(filter);

		File selectedFile = dialog.saveFile(this.osManager.getUserHome(), null);
		if (!selectedFile.getName().toUpperCase().endsWith("PNG")) {
			selectedFile = new File(StringUtils.getString(
					FileUtils.getPath(selectedFile), ".png"));
		}

		// Export only first picture
		try {
			this.localAudioObjectImageHandler.saveInternalPictureToFile(
					objects.get(0), selectedFile);
		} catch (ImageWriteException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	@Override
	public boolean isEnabledForNavigationTableSelection(
			final List<IAudioObject> selection) {
		return selection.size() == 1
				&& selection.get(0) instanceof ILocalAudioObject
				&& ((ILocalAudioObject) selection.get(0)).hasInternalPicture();
	}
}
