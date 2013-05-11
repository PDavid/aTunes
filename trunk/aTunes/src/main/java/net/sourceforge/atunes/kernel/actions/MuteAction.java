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

import net.sourceforge.atunes.gui.views.controls.VolumeIconCalculator;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Enables or disables mute
 * 
 * @author fleax
 * 
 */
public class MuteAction extends CustomAbstractAction {

	private static final long serialVersionUID = 306200192652324065L;

	private IPlayerHandler playerHandler;

	private VolumeIconCalculator volumeIconCalculator;

	private IStatePlayer statePlayer;

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param volumeIconCalculator
	 */
	public void setVolumeIconCalculator(
			final VolumeIconCalculator volumeIconCalculator) {
		this.volumeIconCalculator = volumeIconCalculator;
	}

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
	 * Default constructor
	 */
	public MuteAction() {
		super(I18nUtils.getString("MUTE"));
	}

	@Override
	protected void initialize() {
		super.initialize();
		putValue(SELECTED_KEY, this.statePlayer.isMuteEnabled());
		updateIcon();
	}

	@Override
	protected void executeAction() {
		this.statePlayer.setMuteEnabled((Boolean) getValue(SELECTED_KEY));
		this.playerHandler.applyMuteState(this.statePlayer.isMuteEnabled());
		updateIcon();
	}

	/**
	 * This method must be called when action code is fired from app, not from
	 * Swing
	 */
	public void switchState() {
		putValue(SELECTED_KEY, !(Boolean) getValue(SELECTED_KEY));
		executeAction();
	}

	/**
	 * Updates icon of mute
	 */
	private void updateIcon() {
		putValue(SMALL_ICON, this.volumeIconCalculator.getVolumeIcon());
	}
}
