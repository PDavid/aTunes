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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.Dimension;

import net.sourceforge.atunes.Context;
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
    public static final Dimension PLAY_PREVIOUS_NEXT_BUTTONS_SIZE = new Dimension(
	    40, 40);

    /**
     * Size of main controls by standard layout (not Substance)
     */
    public static final Dimension DEFAULT_BUTTONS_SIZE = new Dimension(34, 34);

    private PlayerControlsSize() {
    }

    /** Size of play / pause button */
    public static final Dimension PLAY_BUTTON_SIZE = Context
	    .getBean(ILookAndFeelManager.class).getCurrentLookAndFeel()
	    .isCustomPlayerControlsSupported() ? new Dimension(45, 45)
	    : PLAY_PREVIOUS_NEXT_BUTTONS_SIZE;

    /** Size of previous and next buttons */
    public static final Dimension PREVIOUS_NEXT_BUTTONS_SIZE = Context
	    .getBean(ILookAndFeelManager.class).getCurrentLookAndFeel()
	    .isCustomPlayerControlsSupported() ? new Dimension(62, 30)
	    : PLAY_PREVIOUS_NEXT_BUTTONS_SIZE;

    /** Size of stop and mute buttons */
    public static final Dimension STOP_MUTE_BUTTONS_SIZE = Context
	    .getBean(ILookAndFeelManager.class).getCurrentLookAndFeel()
	    .isCustomPlayerControlsSupported() ? new Dimension(30, 26)
	    : DEFAULT_BUTTONS_SIZE;

    public static Dimension getPreviousNextButtonsSize() {
	return PREVIOUS_NEXT_BUTTONS_SIZE;
    }

    public static Dimension getStopMuteButtonsSize() {
	return STOP_MUTE_BUTTONS_SIZE;
    }
}
