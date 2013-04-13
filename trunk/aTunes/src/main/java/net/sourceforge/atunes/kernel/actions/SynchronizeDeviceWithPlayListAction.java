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

import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.ApplySynchronizationBetweenDeviceAndPlayListBackgroundWorker;
import net.sourceforge.atunes.kernel.modules.repository.CalculateSynchronizationBetweenDeviceAndPlayListBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerCallback;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Synchronizes play list and device: device is updated with play list content
 * First calculates sync needed and then calls another process to do synchronize
 * 
 * @author fleax
 */
public class SynchronizeDeviceWithPlayListAction extends CustomAbstractAction
		implements
		IBackgroundWorkerCallback<Map<String, List<ILocalAudioObject>>> {

	private static final long serialVersionUID = -1885495996370465881L;

	private IPlayListHandler playListHandler;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Default constructor
	 */
	public SynchronizeDeviceWithPlayListAction() {
		super(I18nUtils.getString("SYNCHRONIZE_DEVICE_WITH_PLAYLIST"));
		setEnabled(false);
	}

	@Override
	protected void executeAction() {
		CalculateSynchronizationBetweenDeviceAndPlayListBackgroundWorker worker = this.beanFactory
				.getBean(CalculateSynchronizationBetweenDeviceAndPlayListBackgroundWorker.class);
		worker.setPlayList(this.playListHandler.getVisiblePlayList());
		worker.execute(this);
	}

	@Override
	public void workerFinished(final Map<String, List<ILocalAudioObject>> result) {
		ApplySynchronizationBetweenDeviceAndPlayListBackgroundWorker worker = this.beanFactory
				.getBean(ApplySynchronizationBetweenDeviceAndPlayListBackgroundWorker.class);
		worker.setSyncData(result);
		worker.execute();
	}
}
