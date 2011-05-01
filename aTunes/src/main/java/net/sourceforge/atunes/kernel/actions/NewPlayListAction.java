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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Creates a new empty play list
 * 
 * @author fleax
 * 
 */
public class NewPlayListAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 3866441529401824151L;

    public NewPlayListAction() {
        super(I18nUtils.getString("NEW_PLAYLIST"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("NEW_PLAYLIST"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Create a new empty play list
        PlayListHandler.getInstance().newPlayList(null);
    }

}
