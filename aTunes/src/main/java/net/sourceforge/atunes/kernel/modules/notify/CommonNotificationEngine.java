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

package net.sourceforge.atunes.kernel.modules.notify;

import java.io.File;
import java.util.UUID;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectGenericImageFactory;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INotificationEngine;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITemporalDiskStorage;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.ImageUtils;

import org.apache.commons.io.FileUtils;

/**
 * Common code for notifications
 * 
 * @author alex
 * 
 */
public abstract class CommonNotificationEngine implements INotificationEngine {

	private Boolean available;

	private IAudioObjectGenericImageFactory audioObjectGenericImageFactory;

	private ITemporalDiskStorage temporalDiskStorage;

	private ILookAndFeelManager lookAndFeelManager;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param audioObjectGenericImageFactory
	 */
	public void setAudioObjectGenericImageFactory(
			final IAudioObjectGenericImageFactory audioObjectGenericImageFactory) {
		this.audioObjectGenericImageFactory = audioObjectGenericImageFactory;
	}

	/**
	 * @param temporalDiskStorage
	 */
	public void setTemporalDiskStorage(
			final ITemporalDiskStorage temporalDiskStorage) {
		this.temporalDiskStorage = temporalDiskStorage;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @return if engine is available
	 */
	@Override
	public final boolean isEngineAvailable() {
		if (this.available == null) {
			this.available = testEngineAvailable();
		}
		return this.available;
	}

	/**
	 * Stores audio object in a temporal folder so it can be used from
	 * third-party notification engines
	 * 
	 * @param audioObject
	 * @param osManager
	 * @return
	 */
	protected final String getTemporalImage(final IAudioObject audioObject,
			final IOSManager osManager) {
		ImageIcon imageForAudioObject = this.beanFactory.getBean(
				ILocalAudioObjectImageHandler.class).getImage(audioObject,
				ImageSize.SIZE_200);
		if (imageForAudioObject == null) {
			imageForAudioObject = this.audioObjectGenericImageFactory
					.getGenericImage(audioObject, GenericImageSize.MEDIUM)
					.getIcon(
							this.lookAndFeelManager.getCurrentLookAndFeel()
									.getPaintForSpecialControls());
		}
		return net.sourceforge.atunes.utils.FileUtils
				.getPath(this.temporalDiskStorage.addImage(ImageUtils
						.toBufferedImage(imageForAudioObject.getImage()), UUID
						.randomUUID().toString()));
	}

	protected final ILookAndFeelManager getLookAndFeelManager() {
		return this.lookAndFeelManager;
	}

	protected IBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Removes temporal image
	 * 
	 * @param path
	 */
	protected final void removeTemporalImage(final String path) {
		FileUtils.deleteQuietly(new File(path));
	}

	/**
	 * @return audio object generic image factory
	 */
	protected final IAudioObjectGenericImageFactory getAudioObjectGenericImageFactory() {
		return this.audioObjectGenericImageFactory;
	}

	/**
	 * Test if engine is available
	 * 
	 * @return
	 */
	protected abstract boolean testEngineAvailable();
}
