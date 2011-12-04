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

package net.sourceforge.atunes.kernel.modules.notify;

import java.io.File;
import java.util.UUID;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectGenericImageFactory;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INotificationEngine;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITemporalDiskStorage;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.ImageUtils;

import org.apache.commons.io.FileUtils;

public abstract class CommonNotificationEngine implements INotificationEngine {

	private Boolean available;
	
	private IAudioObjectGenericImageFactory audioObjectGenericImageFactory;
	
	private ITemporalDiskStorage diskStorage;
	
	private ILookAndFeelManager lookAndFeelManager;
	
	private IAudioObjectImageLocator audioObjectImageLocator;
	
	/**
	 * @param audioObjectGenericImageFactory
	 * @param diskStorage
	 * @param lookAndFeelManager
	 * @param audioObjectImageLocator
	 */
	public CommonNotificationEngine(IAudioObjectGenericImageFactory audioObjectGenericImageFactory, ITemporalDiskStorage diskStorage, ILookAndFeelManager lookAndFeelManager, IAudioObjectImageLocator audioObjectImageLocator) {
		this.audioObjectGenericImageFactory = audioObjectGenericImageFactory;
		this.diskStorage = diskStorage;
		this.lookAndFeelManager = lookAndFeelManager;
		this.audioObjectImageLocator = audioObjectImageLocator;
	}
	
	/**
	 * @return if engine is available
	 */
	public final boolean isEngineAvailable() {
		if (available == null) {
			available = testEngineAvailable();
		}
		return available;
	}
	
	/**
	 * Stores audio object in a temporal folder so it can be used from third-party notification engines
	 * @param audioObject
	 * @param osManager
	 * @return
	 */
	protected final String getTemporalImage(IAudioObject audioObject, IOSManager osManager) {
		ImageIcon imageForAudioObject = audioObjectImageLocator.getImage(audioObject, ImageSize.SIZE_200);
		if (imageForAudioObject == null) {
			imageForAudioObject = audioObjectGenericImageFactory.getGenericImage(audioObject, GenericImageSize.MEDIUM).getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls());
		}
		return diskStorage.addImage(ImageUtils.toBufferedImage(imageForAudioObject.getImage()), UUID.randomUUID().toString()).getAbsolutePath();
	}
	
	/**
	 * Removes temporal image
	 * @param path
	 */
	protected final void removeTemporalImage(String path) {
		FileUtils.deleteQuietly(new File(path));
	}
	
	/**
	 * @return audio object generic image factory
	 */
	protected final IAudioObjectGenericImageFactory getAudioObjectGenericImageFactory() {
		return audioObjectGenericImageFactory;
	}
	
	/**
	 * Test if engine is available
	 * @return
	 */
	protected abstract boolean testEngineAvailable();
}
