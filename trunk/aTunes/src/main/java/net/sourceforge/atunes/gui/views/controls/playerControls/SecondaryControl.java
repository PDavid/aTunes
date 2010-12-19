/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.Action;
import javax.swing.JToggleButton;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;

public class SecondaryControl extends JToggleButton {

    /**
	 * 
	 */
    private static final long serialVersionUID = -124604413114002586L;

    public SecondaryControl(Action a) {
        super(a);
        setText(null);
        setPreferredSize(PlayerControlsPanel.OTHER_BUTTONS_SIZE);
        setFocusable(false);
        LookAndFeelSelector.getInstance().getCurrentLookAndFeel().putClientProperties(this);
    }

}
