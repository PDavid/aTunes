/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;

import javax.swing.JButton;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;

/*
 * based on code from Xtreme Media Player
 */
public final class PreviousButton extends JButton {

    private static final long serialVersionUID = -5415683019365261871L;

    /**
     * Instantiates a new previous button.
     * 
     * @param size
     */
    public PreviousButton(Dimension size) {
        super(Actions.getAction(PlayPreviousAudioObjectAction.class));
        // Force size
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);
        setIcon(Images.getImage(Images.PREVIOUS));
        setText(null);

        LookAndFeelSelector.getInstance().getCurrentLookAndFeel().putClientProperties(this);
    }

}
