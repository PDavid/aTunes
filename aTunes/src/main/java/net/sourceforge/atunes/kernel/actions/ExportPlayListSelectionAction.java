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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectExporter;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Calls export process
 * 
 * @author fleax
 * 
 */
public class ExportPlayListSelectionAction extends CustomAbstractAction {

	private static final long serialVersionUID = -6661702915765846089L;

	private IPlayListHandler playListHandler;

	private IAudioObjectExporter audioObjectExporter;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Constructor
	 */
	public ExportPlayListSelectionAction() {
		super(StringUtils.getString(
				I18nUtils.getString("EXPORT_SAVE_PLAYLIST_SELECTION"), "..."));
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param audioObjectExporter
	 */
	public void setAudioObjectExporter(
			final IAudioObjectExporter audioObjectExporter) {
		this.audioObjectExporter = audioObjectExporter;
	}

	@Override
	protected void executeAction() {
		// Get only LocalAudioObject objects of current play list
		this.audioObjectExporter.exportAudioObject(this.beanFactory.getBean(
				ILocalAudioObjectFilter.class).getLocalAudioObjects(
				this.playListHandler.getSelectedAudioObjects()));
	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		return !this.playListHandler.getVisiblePlayList().isEmpty();
	}
}
