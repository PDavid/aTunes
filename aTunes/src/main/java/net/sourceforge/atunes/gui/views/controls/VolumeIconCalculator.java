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

package net.sourceforge.atunes.gui.views.controls;

import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IStatePlayer;

/**
 * Returns volume icon according to volume level and look and feel
 * 
 * @author alex
 * 
 */
public class VolumeIconCalculator {

	private IIconFactory volumeMuteIcon;

	private IIconFactory volumeMaxIcon;

	private IIconFactory volumeMedIcon;

	private IIconFactory volumeMinIcon;

	private IIconFactory volumeZeroIcon;

	private IStatePlayer statePlayer;

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param volumeMaxIcon
	 */
	public void setVolumeMaxIcon(final IIconFactory volumeMaxIcon) {
		this.volumeMaxIcon = volumeMaxIcon;
	}

	/**
	 * @param volumeMedIcon
	 */
	public void setVolumeMedIcon(final IIconFactory volumeMedIcon) {
		this.volumeMedIcon = volumeMedIcon;
	}

	/**
	 * @param volumeMinIcon
	 */
	public void setVolumeMinIcon(final IIconFactory volumeMinIcon) {
		this.volumeMinIcon = volumeMinIcon;
	}

	/**
	 * @param volumeMuteIcon
	 */
	public void setVolumeMuteIcon(final IIconFactory volumeMuteIcon) {
		this.volumeMuteIcon = volumeMuteIcon;
	}

	/**
	 * @param volumeZeroIcon
	 */
	public void setVolumeZeroIcon(final IIconFactory volumeZeroIcon) {
		this.volumeZeroIcon = volumeZeroIcon;
	}

	/**
	 * Returns icon to use depending on volume and mute state
	 * 
	 * @return
	 */
	public IIconFactory getIcon() {
		if (this.statePlayer.isMuteEnabled()) {
			return this.volumeMuteIcon;
		} else {
			int volume = this.statePlayer.getVolume();
			if (volume > 80) {
				return this.volumeMaxIcon;
			} else if (volume > 40) {
				return this.volumeMedIcon;
			} else if (volume > 5) {
				return this.volumeMinIcon;
			} else {
				return this.volumeZeroIcon;
			}
		}
	}
}
