/*
 * aTunes 2.0.0-SNAPSHOT
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

import javax.swing.JToggleButton;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.KaraokeModeAction;
import net.sourceforge.atunes.kernel.modules.player.PlayerEngineCapability;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

/*
 * based on code from Xtreme Media Player
 */
public final class KaraokeButton extends JToggleButton {

    private static final long serialVersionUID = 6007885049773560874L;

    /**
     * Instantiates a new karaoke button.
     */
    public KaraokeButton() {
        super(Actions.getAction(KaraokeModeAction.class));
        setText(null);
        setPreferredSize(PlayerControlsPanel.OTHER_BUTTONS_SIZE);
        setFocusable(false);
        LookAndFeelSelector.getCurrentLookAndFeel().putClientProperties(this);
        if (!PlayerHandler.getInstance().supportsCapability(PlayerEngineCapability.KARAOKE)) {
            setVisible(false);
        }
    }
}
