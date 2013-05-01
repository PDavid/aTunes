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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Stops player when current song finishes
 * 
 * @author alex
 * 
 */
public class StopAfterCurrentAudioObjectAction extends CustomAbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2425963483956204852L;

	private IPlayerHandler playerHandler;

	/**
	 * Default constructor
	 */
	public StopAfterCurrentAudioObjectAction() {
		super(I18nUtils.getString("STOP_AFTER_CURRENT_TRACK"));
	}

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	@Override
	protected void initialize() {
		super.initialize();
		putValue(SELECTED_KEY, false);
	}

	@Override
	protected void executeAction() {
		this.playerHandler
				.setStopAfterCurrentTrack((Boolean) getValue(SELECTED_KEY));
	}

	/**
	 * Called to change state of this action
	 * 
	 * @param stopAfterCurrentAudioObject
	 */
	public void setStopAfterCurrentAudioObject(
			final boolean stopAfterCurrentAudioObject) {
		// Set action to unselected again
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				putValue(SELECTED_KEY, stopAfterCurrentAudioObject);
			}
		});
	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		return true;
	}
}
