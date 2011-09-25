/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;

import javax.swing.JButton;

import net.sourceforge.atunes.gui.images.PreviousImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;

public final class PreviousButton extends JButton implements ILookAndFeelChangeListener {

    private static final long serialVersionUID = -5415683019365261871L;

    private Dimension size;

    /**
     * Instantiates a new previous button.
     * 
     * @param size
     */
    public PreviousButton(Dimension size) {
        super(Actions.getAction(PlayPreviousAudioObjectAction.class));
        // Force size
        this.size = size;
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);
        setText(null);
        setIcon(PreviousImageIcon.getIcon(size));
        LookAndFeelSelector.getInstance().getCurrentLookAndFeel().putClientProperties(this);
        LookAndFeelSelector.getInstance().addLookAndFeelChangeListener(this);
    }

    @Override
    public void lookAndFeelChanged() {
        setIcon(PreviousImageIcon.getIcon(size));
    }
}
