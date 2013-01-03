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

import javax.swing.KeyStroke;

import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.utils.I18nUtils;


/**
 * This action lowers volume
 * 
 * @author fleax
 * 
 */
public class VolumeDownAction extends CustomAbstractAction {

    private static final long serialVersionUID = 8731458163463902477L;

    private IPlayerHandler playerHandler;
    
    /**
     * @param playerHandler
     */
    public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

    /**
     * Default constructor
     */
    public VolumeDownAction() {
        super(I18nUtils.getString("VOLUME_DOWN"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke('-'));
    }

    @Override
    protected void executeAction() {
        playerHandler.volumeDown();
    }
}
