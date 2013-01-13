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

import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action enables or disables repeat mode
 * 
 * @author fleax
 * 
 */
public class RepeatModeAction extends ActionWithColorMutableIcon {

	private static final long serialVersionUID = 2032609750151412458L;

	private IIconFactory repeatIcon;

	private IStatePlayer statePlayer;

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param repeatIcon
	 */
	public void setRepeatIcon(final IIconFactory repeatIcon) {
		this.repeatIcon = repeatIcon;
	}

	/**
	 * Default constructor
	 */
	public RepeatModeAction() {
		super(I18nUtils.getString("REPEAT"));
	}

	@Override
	protected void initialize() {
		putValue(SELECTED_KEY, this.statePlayer.isRepeat());
		super.initialize();
	}

	@Override
	protected void executeAction() {
		this.statePlayer.setRepeat((Boolean) getValue(SELECTED_KEY));
	}

	@Override
	public IColorMutableImageIcon getIcon(final ILookAndFeel lookAndFeel) {
		return this.repeatIcon.getColorMutableIcon();
	}

	@Override
	protected void updateTooltip() {
		if ((Boolean) getValue(SELECTED_KEY)) {
			putValue(SHORT_DESCRIPTION, I18nUtils.getString("REPEAT_ENABLED"));
		} else {
			putValue(SHORT_DESCRIPTION, I18nUtils.getString("REPEAT_DISABLED"));
		}
	}
}
