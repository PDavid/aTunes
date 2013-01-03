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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.Dimension;

import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * Sizes of player controls
 * 
 * @author alex
 * 
 */
public final class PlayerControlsSize {

    /**
     * Size of main controls by standard layout (not Substance)
     */
    private static final Dimension PLAY_PREVIOUS_NEXT_BUTTONS_SIZE = new Dimension(
	    40, 40);

    /**
     * Size of main controls by standard layout (not Substance)
     */
    private static final Dimension DEFAULT_BUTTONS_SIZE = new Dimension(34, 34);

    private PlayerControlsSize() {
    }

    /**
     * Size of play / pause button
     * 
     * @param lookAndFeelManager
     * @return Size of play / pause button
     */
    public static final Dimension getPlayButtonSize(
	    final ILookAndFeelManager lookAndFeelManager) {
	return lookAndFeelManager.getCurrentLookAndFeel()
		.isCustomPlayerControlsSupported() ? new Dimension(45, 45)
		: PLAY_PREVIOUS_NEXT_BUTTONS_SIZE;
    }

    /**
     * Size of previous / next buttons
     * 
     * @param lookAndFeelManager
     * @return Size of previous / next buttons
     */
    public static final Dimension getPreviousNextButtonsSize(
	    final ILookAndFeelManager lookAndFeelManager) {
	return lookAndFeelManager.getCurrentLookAndFeel()
		.isCustomPlayerControlsSupported() ? new Dimension(62, 30)
		: PLAY_PREVIOUS_NEXT_BUTTONS_SIZE;
    }

    /**
     * Size of stop and mute buttons
     * 
     * @param lookAndFeelManager
     * @return Size of stop and mute buttons
     */
    public static final Dimension getStopMuteButtonsSize(
	    final ILookAndFeelManager lookAndFeelManager) {
	return lookAndFeelManager.getCurrentLookAndFeel()
		.isCustomPlayerControlsSupported() ? new Dimension(30, 26)
		: DEFAULT_BUTTONS_SIZE;
    }
}
