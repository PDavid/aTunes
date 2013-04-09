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
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Copies audio objects to device
 * 
 * @author alex
 * 
 */
public class CopyToDeviceAction extends
		AbstractActionOverSelectedObjects<IAudioObject> {

	private static final long serialVersionUID = -7689483210176624995L;

	private INavigationHandler navigationHandler;

	private IDeviceHandler deviceHandler;

	private IPodcastFeedHandler podcastFeedHandler;

	private ILocalAudioObjectFactory localAudioObjectFactory;

	private INavigationView podcastNavigationView;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param podcastNavigationView
	 */
	public void setPodcastNavigationView(
			final INavigationView podcastNavigationView) {
		this.podcastNavigationView = podcastNavigationView;
	}

	/**
	 * @param localAudioObjectFactory
	 */
	public void setLocalAudioObjectFactory(
			final ILocalAudioObjectFactory localAudioObjectFactory) {
		this.localAudioObjectFactory = localAudioObjectFactory;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param deviceHandler
	 */
	public void setDeviceHandler(final IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}

	/**
	 * @param podcastFeedHandler
	 */
	public void setPodcastFeedHandler(
			final IPodcastFeedHandler podcastFeedHandler) {
		this.podcastFeedHandler = podcastFeedHandler;
	}

	/**
	 * Default constructor
	 */
	public CopyToDeviceAction() {
		super(I18nUtils.getString("COPY_TO_DEVICE"));
	}

	@Override
	protected boolean isPreprocessNeeded() {
		return true;
	}

	@Override
	protected IAudioObject preprocessObject(final IAudioObject audioObject) {
		if (audioObject instanceof ILocalAudioObject) {
			return audioObject;
		} else if (audioObject instanceof IPodcastFeedEntry
				&& ((IPodcastFeedEntry) audioObject).isDownloaded()) {
			String downloadPath = this.podcastFeedHandler
					.getDownloadPath((IPodcastFeedEntry) audioObject);
			return this.localAudioObjectFactory.getLocalAudioObject(new File(
					downloadPath));
		}
		return null;
	}

	@Override
	protected void executeAction(final List<IAudioObject> objects) {
		this.deviceHandler.copyFilesToDevice(this.beanFactory.getBean(
				ILocalAudioObjectFilter.class).getLocalAudioObjects(objects));
	}

	@Override
	public boolean isEnabledForNavigationTreeSelection(
			final boolean rootSelected, final List<ITreeNode> selection) {
		return this.deviceHandler.isDeviceConnected() && !rootSelected
				&& !selection.isEmpty();
	}

	@Override
	public boolean isEnabledForNavigationTableSelection(
			final List<IAudioObject> selection) {
		if (!this.deviceHandler.isDeviceConnected()) {
			return false;
		}

		if (this.navigationHandler.getCurrentView().equals(
				this.podcastNavigationView)) {
			for (IAudioObject ao : selection) {
				if (!((IPodcastFeedEntry) ao).isDownloaded()) {
					return false;
				}
			}
			return true;
		}

		return !selection.isEmpty();
	}
}
