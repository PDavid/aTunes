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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectExporter;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.LocalAudioObjectFilter;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Calls export process
 * 
 * @author fleax
 * 
 */
public class ExportPlayListAction extends CustomAbstractAction {

	private static final long serialVersionUID = -6661702915765846089L;

	private IPlayListHandler playListHandler;
	
	private IAudioObjectExporter audioObjectExporter;

	/**
	 * Constructor
	 */
	public ExportPlayListAction() {
		super(StringUtils.getString(I18nUtils.getString("EXPORT_PLAYLIST"), "..."));
		putValue(SHORT_DESCRIPTION, StringUtils.getString(I18nUtils.getString("EXPORT_PLAYLIST"), "..."));
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
	
	/**
	 * @param audioObjectExporter
	 */
	public void setAudioObjectExporter(IAudioObjectExporter audioObjectExporter) {
		this.audioObjectExporter = audioObjectExporter;
	}
	
	@Override
	protected void executeAction() {
		LocalAudioObjectFilter filter = new LocalAudioObjectFilter();
		// Get only LocalAudioObject objects of current play list
		audioObjectExporter.exportAudioObject(filter.getLocalAudioObjects(playListHandler.getCurrentPlayList(true).getAudioObjectsList()));
	}

	@Override
	public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
		return !playListHandler.getCurrentPlayList(true).isEmpty();
	}
}
